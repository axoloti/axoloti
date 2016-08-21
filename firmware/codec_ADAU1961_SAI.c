/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
#include "codec_ADAU1961.h"
#include "ch.h"
#include "hal.h"

#include "codec.h"
#include "stm32f4xx.h"
#include "axoloti_board.h"
#include "sysmon.h"

//#define STM_IS_I2S_MASTER 1

extern void computebufI(int32_t *inp, int32_t *outp);

#define STM32_SAI_A_DMA_STREAM STM32_DMA_STREAM_ID(2, 1)
#define STM32_SAI_B_DMA_STREAM STM32_DMA_STREAM_ID(2, 4)
#define SAI_A_DMA_CHANNEL 0
#define SAI_B_DMA_CHANNEL 1
#define STM32_SAI_A_DMA_PRIORITY 1
#define STM32_SAI_B_DMA_PRIORITY 1
#define STM32_SAI_A_IRQ_PRIORITY 2
#define STM32_SAI_B_IRQ_PRIORITY 2

const stm32_dma_stream_t* sai_a_dma;
const stm32_dma_stream_t* sai_b_dma;

int codec_interrupt_timestamp;

void codec_ADAU1961_hw_reset(void) {
}

/* I2C interface #3 */
/* SDA : PH8
 * SCL : PH7
 */
static const I2CConfig i2cfg2 = {OPMODE_I2C, 100000, STD_DUTY_CYCLE, };

static uint8_t i2crxbuf[8];
static uint8_t i2ctxbuf[8];
static systime_t tmo;

#define ADAU1961_I2C_ADDR (0x70>>1)

void CheckI2CErrors(void) {
  volatile i2cflags_t errors;
  errors = i2cGetErrors(&I2CD3);
  if (errors != 0){
    setErrorFlag(ERROR_CODEC_I2C);
  }
  (void)errors;
}

void ADAU1961_I2CStart(void) {
  palSetPadMode(
      GPIOH,
      7,
      PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN
          | PAL_STM32_PUDR_PULLUP);
  palSetPadMode(
      GPIOH,
      8,
      PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN
          | PAL_STM32_PUDR_PULLUP);
  i2cStart(&I2CD3, &i2cfg2);
}

void ADAU1961_I2CStop(void) {
  i2cStop(&I2CD3);
}

uint8_t ADAU1961_ReadRegister(uint16_t RegisterAddr) {
  msg_t status;
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  ADAU1961_I2CStart();
  i2cAcquireBus(&I2CD3);
  status = i2cMasterTransmitTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2ctxbuf, 2,
                                    i2crxbuf, 0, tmo);
  if (status != RDY_OK) {
    CheckI2CErrors();
  }
  status = i2cMasterReceiveTimeout(&I2CD3, ADAU1961_I2C_ADDR,
                                    i2crxbuf, 1, tmo);
  if (status != RDY_OK) {
    CheckI2CErrors();
  }
  i2cReleaseBus(&I2CD3);
  ADAU1961_I2CStop();
  chThdSleepMilliseconds(1);
  return i2crxbuf[0];
}

void ADAU1961_ReadRegister6(uint16_t RegisterAddr) {
  msg_t status;
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  ADAU1961_I2CStart();
  i2cAcquireBus(&I2CD3);
  status = i2cMasterTransmitTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2ctxbuf, 2,
                                    i2crxbuf, 0, tmo);
  if (status != RDY_OK) {
    CheckI2CErrors();
  }
  status = i2cMasterReceiveTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2crxbuf, 6, tmo);
  if (status != RDY_OK) {
    CheckI2CErrors();
  }
  i2cReleaseBus(&I2CD3);
  ADAU1961_I2CStop();
  chThdSleepMilliseconds(1);
}

void ADAU1961_WriteRegister(uint16_t RegisterAddr, uint8_t RegisterValue) {
  msg_t status;
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  i2ctxbuf[2] = RegisterValue;

  ADAU1961_I2CStart();
  i2cAcquireBus(&I2CD3);
  status = i2cMasterTransmitTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2ctxbuf, 3,
                                    i2crxbuf, 0, tmo);
  if (status != RDY_OK) {
    CheckI2CErrors();
    status = i2cMasterTransmitTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2ctxbuf, 3,
                                      i2crxbuf, 0, tmo);
    chThdSleepMilliseconds(1);
  }
  i2cReleaseBus(&I2CD3);
  ADAU1961_I2CStop();
  chThdSleepMilliseconds(1);

  static uint8_t rd;
  rd = ADAU1961_ReadRegister(RegisterAddr);
  if (rd != RegisterValue) {
//    setErrorFlag(ERROR_CODEC_I2C);
  } else {
  }
  chThdSleepMilliseconds(1);
}

void ADAU1961_WriteRegister6(uint16_t RegisterAddr, uint8_t * RegisterValues) {
  msg_t status;
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  i2ctxbuf[2] = RegisterValues[0];
  i2ctxbuf[3] = RegisterValues[1];
  i2ctxbuf[4] = RegisterValues[2];
  i2ctxbuf[5] = RegisterValues[3];
  i2ctxbuf[6] = RegisterValues[4];
  i2ctxbuf[7] = RegisterValues[5];
  ADAU1961_I2CStart();
  i2cAcquireBus(&I2CD3);
  status = i2cMasterTransmitTimeout(&I2CD3, ADAU1961_I2C_ADDR, i2ctxbuf, 8,
                                    i2crxbuf, 0, TIME_INFINITE);
  i2cReleaseBus(&I2CD3);
  ADAU1961_I2CStop();
  if (status != RDY_OK) {
    CheckI2CErrors();
  }

  chThdSleepMilliseconds(1);
}

void codec_ADAU1961_hw_init(uint16_t samplerate) {

  tmo = MS2ST(4);
  chThdSleepMilliseconds(5);

  /*
   * 1. Power down the PLL.
   * 2. Reset the PLL control register.
   * 3. Start the PLL.
   * 4. Poll the lock bit.
   * 5. Assert the core clock enable bit after the PLL lock is acquired.
   */

#ifdef STM_IS_I2S_MASTER
    ADAU1961_WriteRegister(ADAU1961_REG_R0_CLKC, 0x01); // 256FS
    chThdSleepMilliseconds(10);
#else

    uint8_t pllreg[6];

    if (samplerate == 48000) {
      // reg setting 0x007D 0012 3101
      pllreg[0] = 0x00;
      pllreg[1] = 0x7D;
      pllreg[2] = 0x00;
      pllreg[3] = 0x12;
      pllreg[4] = 0x31;
      pllreg[5] = 0x01;
    }
    else if (samplerate == 44100) {
      // reg setting 0x0271 0193 2901
      pllreg[0] = 0x02;
      pllreg[1] = 0x71;
      pllreg[2] = 0x01;
      pllreg[3] = 0x93;
      pllreg[4] = 0x29;
      pllreg[5] = 0x01;
    }
    else
      while (1) {
      }

    ADAU1961_WriteRegister6(ADAU1961_REG_R1_PLLC, &pllreg[0]);

    int i = 1000;
    while(i) {
      // wait for PLL
      ADAU1961_ReadRegister6(ADAU1961_REG_R1_PLLC);
      if (i2crxbuf[5] & 0x02)
        break;
      chThdSleepMilliseconds(1);
      i--;
    }
    if (!i){
      setErrorFlag(ERROR_CODEC_I2C);
    }

    ADAU1961_WriteRegister(ADAU1961_REG_R0_CLKC, 0x09); // PLL = clksrc

#endif
    // i2s2_sd (dac) is a confirmed connection, i2s2_ext_sd (adc) is not however
    // bclk and lrclk are ok too
    ADAU1961_WriteRegister(ADAU1961_REG_R2_DMICJ, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R3_RES, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R4_RMIXL0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R5_RMIXL1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R6_RMIXR0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R7_RMIXR1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R8_LDIVOL, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R9_RDIVOL, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R10_MICBIAS, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R11_ALC0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R12_ALC1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R13_ALC2, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R14_ALC3, 0x00);
#ifdef STM_IS_I2S_MASTER
    ADAU1961_WriteRegister(ADAU1961_REG_R15_SERP0,0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1,0x00);
#else
    ADAU1961_WriteRegister(ADAU1961_REG_R15_SERP0, 0x01); // codec is master
    ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1, 0x00); // 32bit samples
//ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1,0x60); // 64bit samples, spdif clock!
#endif
    ADAU1961_WriteRegister(ADAU1961_REG_R17_CON0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R18_CON1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R19_ADCC, 0x10);
    ADAU1961_WriteRegister(ADAU1961_REG_R20_LDVOL, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R21_RDVOL, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R22_PMIXL0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R23_PMIXL1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R24_PMIXR0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R25_PMIXR1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R26_PLRML, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R27_PLRMR, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R28_PLRMM, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R29_PHPLVOL, 0x02);
    ADAU1961_WriteRegister(ADAU1961_REG_R30_PHPRVOL, 0x02);
    ADAU1961_WriteRegister(ADAU1961_REG_R31_PLLVOL, 0x02);
    ADAU1961_WriteRegister(ADAU1961_REG_R32_PLRVOL, 0x02);
    ADAU1961_WriteRegister(ADAU1961_REG_R33_PMONO, 0x02);
    ADAU1961_WriteRegister(ADAU1961_REG_R34_POPCLICK, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R35_PWRMGMT, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R36_DACC0, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R37_DACC1, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R38_DACC2, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP, 0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R40_CPORTP0, 0xAA);
    ADAU1961_WriteRegister(ADAU1961_REG_R41_CPORTP1, 0xAA);
    ADAU1961_WriteRegister(ADAU1961_REG_R42_JACKDETP, 0x00);

    chThdSleepMilliseconds(10);


#if 0
  while(1) {
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0x00);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0x03);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0x0C);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0x30);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0xC0);
    ADAU1961_WriteRegister(ADAU1961_REG_R39_SERPP,0x00);
  }
#endif

  {
// ADC Enable
#ifdef STM_IS_I2S_MASTER
//  ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1, 0x20); // 32 bits per frame
    ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1, 0x00);// 32 bits per frame
#endif
    ADAU1961_WriteRegister(ADAU1961_REG_R19_ADCC, 0x03); // ADC enable
    ADAU1961_WriteRegister(ADAU1961_REG_R36_DACC0, 0x03); // DAC enable

    ADAU1961_WriteRegister(ADAU1961_REG_R31_PLLVOL, 0xE7); // Playback Line Output Left Volume
    ADAU1961_WriteRegister(ADAU1961_REG_R32_PLRVOL, 0xE7); // Playback Right Output Left Volume

    ADAU1961_WriteRegister(ADAU1961_REG_R26_PLRML, 0x05); // unmute Mixer5, 6dB gain
    ADAU1961_WriteRegister(ADAU1961_REG_R27_PLRMR, 0x11); // unmute Mixer6, 6dB gain
    ADAU1961_WriteRegister(ADAU1961_REG_R22_PMIXL0, 0x21); // unmute DAC, no aux mix
    ADAU1961_WriteRegister(ADAU1961_REG_R24_PMIXR0, 0x41); // unmute DAC, no aux mix

    ADAU1961_WriteRegister(ADAU1961_REG_R35_PWRMGMT, 0x03); //enable L&R

    ADAU1961_WriteRegister(ADAU1961_REG_R4_RMIXL0, 0x01); // mixer1 enable, mute LINP and LINR
    ADAU1961_WriteRegister(ADAU1961_REG_R5_RMIXL1, 0x08); // unmute PGA, aux mute, 0 dB boost
    ADAU1961_WriteRegister(ADAU1961_REG_R6_RMIXR0, 0x01); // mixer2 enable, mute LINP and LINR
    ADAU1961_WriteRegister(ADAU1961_REG_R7_RMIXR1, 0x08); // unmute PGA, aux mute, 0 dB boost

    ADAU1961_WriteRegister(ADAU1961_REG_R8_LDIVOL, 0x43); // 0dB gain
    ADAU1961_WriteRegister(ADAU1961_REG_R9_RDIVOL, 0x43); // 0dB gain

    // capless headphone config
    ADAU1961_WriteRegister(ADAU1961_REG_R33_PMONO, 0x03);   //MONOM+MOMODE
    ADAU1961_WriteRegister(ADAU1961_REG_R28_PLRMM, 0x01);  // MX7EN, COMMON MODE OUT
    ADAU1961_WriteRegister(ADAU1961_REG_R29_PHPLVOL, 0xC3);
    ADAU1961_WriteRegister(ADAU1961_REG_R30_PHPRVOL, 0xC3);
  }

  chThdSleepMilliseconds(10);
}

static void dma_sai_a_interrupt(void* dat, uint32_t flags) {
  (void)dat;
  (void)flags;
  codec_interrupt_timestamp = hal_lld_get_counter_value();
  if ((sai_a_dma)->stream->CR & STM32_DMA_CR_CT) {
    computebufI(rbuf2, buf);
  }
  else {
    computebufI(rbuf, buf2);
  }
  dmaStreamClearInterrupt(sai_a_dma);
}

volatile SAI_Block_TypeDef *sai_a;
volatile SAI_Block_TypeDef *sai_b;
const stm32_dma_stream_t* sai_a_dma;
const stm32_dma_stream_t* sai_b_dma;

void codec_ADAU1961_i2s_init(uint16_t sampleRate) {
  sai_a = SAI1_Block_A;
  sai_b = SAI1_Block_B;
//configure MCO
  palSetPadMode(GPIOA, 8, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPadMode(GPIOA, 8, PAL_MODE_ALTERNATE(0));
  chThdSleepMilliseconds(10);
// release SAI
  palSetPadMode(GPIOE, 3, PAL_MODE_INPUT);
  palSetPadMode(GPIOE, 4, PAL_MODE_INPUT);
  palSetPadMode(GPIOE, 5, PAL_MODE_INPUT);
  palSetPadMode(GPIOE, 6, PAL_MODE_INPUT);
// configure SAI
  RCC->APB2ENR |= RCC_APB2ENR_SAI1EN;
  chThdSleepMilliseconds(1);
  SAI1_Block_A->CR2 = SAI_xCR2_FTH_1;
  SAI1_Block_B->CR2 = SAI_xCR2_FTH_1;
  SAI1_Block_A->FRCR = /*SAI_xFRCR_FSDEF |*/ SAI_xFRCR_FRL_0 | SAI_xFRCR_FRL_1
      | SAI_xFRCR_FRL_2 | SAI_xFRCR_FRL_3 | SAI_xFRCR_FRL_4 | SAI_xFRCR_FRL_5
      | SAI_xFRCR_FSALL_0 | SAI_xFRCR_FSALL_1 | SAI_xFRCR_FSALL_2
      | SAI_xFRCR_FSALL_3 | SAI_xFRCR_FSALL_4 | SAI_xFRCR_FSOFF;
  SAI1_Block_B->FRCR = /*SAI_xFRCR_FSDEF |*/ SAI_xFRCR_FRL_0 | SAI_xFRCR_FRL_1
      | SAI_xFRCR_FRL_2 | SAI_xFRCR_FRL_3 | SAI_xFRCR_FRL_4 | SAI_xFRCR_FRL_5
      | SAI_xFRCR_FSALL_0 | SAI_xFRCR_FSALL_1 | SAI_xFRCR_FSALL_2
      | SAI_xFRCR_FSALL_3 | SAI_xFRCR_FSALL_4 | SAI_xFRCR_FSOFF;
  SAI1_Block_A->SLOTR = (3 << 16) | SAI_xSLOTR_NBSLOT_0;
  SAI1_Block_B->SLOTR = (3 << 16) | SAI_xSLOTR_NBSLOT_0;
// SAI1_A is slave transmitter
// SAI1_B is synchronous slave receiver
  SAI1_Block_A->CR1 = SAI_xCR1_DS_0 | SAI_xCR1_DS_1 | SAI_xCR1_DS_2
      | SAI_xCR1_MODE_1 | SAI_xCR1_DMAEN | SAI_xCR1_CKSTR;
  SAI1_Block_B->CR1 = SAI_xCR1_DS_0 | SAI_xCR1_DS_1 | SAI_xCR1_DS_2
      | SAI_xCR1_SYNCEN_0 | SAI_xCR1_MODE_1 | SAI_xCR1_MODE_0 | SAI_xCR1_DMAEN | SAI_xCR1_CKSTR;
  chThdSleepMilliseconds(1);
  palSetPadMode(GPIOE, 3, PAL_MODE_ALTERNATE(6));
  palSetPadMode(GPIOE, 4, PAL_MODE_ALTERNATE(6));
  palSetPadMode(GPIOE, 5, PAL_MODE_ALTERNATE(6));
  palSetPadMode(GPIOE, 6, PAL_MODE_ALTERNATE(6));

  // initialize DMA
  sai_a_dma = STM32_DMA_STREAM(STM32_SAI_A_DMA_STREAM);
  sai_b_dma = STM32_DMA_STREAM(STM32_SAI_B_DMA_STREAM);

  uint32_t sai_a_dma_mode = STM32_DMA_CR_CHSEL(SAI_A_DMA_CHANNEL)
      | STM32_DMA_CR_PL(STM32_SAI_A_DMA_PRIORITY) | STM32_DMA_CR_DIR_M2P
      | STM32_DMA_CR_TEIE | STM32_DMA_CR_TCIE | STM32_DMA_CR_DBM | // double buffer mode
      STM32_DMA_CR_PSIZE_WORD | STM32_DMA_CR_MSIZE_WORD;
  uint32_t sai_b_dma_mode = STM32_DMA_CR_CHSEL(SAI_B_DMA_CHANNEL)
      | STM32_DMA_CR_PL(STM32_SAI_B_DMA_PRIORITY) | STM32_DMA_CR_DIR_P2M
      | STM32_DMA_CR_TEIE | STM32_DMA_CR_TCIE | STM32_DMA_CR_DBM | // double buffer mode
      STM32_DMA_CR_PSIZE_WORD | STM32_DMA_CR_MSIZE_WORD;

  bool_t b = dmaStreamAllocate(sai_a_dma, STM32_SAI_A_IRQ_PRIORITY,
                               (stm32_dmaisr_t)dma_sai_a_interrupt,
                               (void *)0);

  dmaStreamSetPeripheral(sai_a_dma, &(sai_a->DR));
  dmaStreamSetMemory0(sai_a_dma, buf);
  dmaStreamSetMemory1(sai_a_dma, buf2);
  dmaStreamSetTransactionSize(sai_a_dma, 32);
  dmaStreamSetMode(sai_a_dma, sai_a_dma_mode | STM32_DMA_CR_MINC);

  b |= dmaStreamAllocate(sai_b_dma, STM32_SAI_B_IRQ_PRIORITY,
                               (stm32_dmaisr_t)0,
                               (void *)0);

  if (b){
    setErrorFlag(ERROR_CODEC_I2C);
  }

  dmaStreamSetPeripheral(sai_b_dma, &(sai_b->DR));
  dmaStreamSetMemory0(sai_b_dma, rbuf);
  dmaStreamSetMemory1(sai_b_dma, rbuf2);
  dmaStreamSetTransactionSize(sai_b_dma, 32);
  dmaStreamSetMode(sai_b_dma, sai_b_dma_mode | STM32_DMA_CR_MINC);


  dmaStreamClearInterrupt(sai_b_dma);
  dmaStreamEnable(sai_b_dma);

  dmaStreamClearInterrupt(sai_a_dma);
  dmaStreamEnable(sai_a_dma);

  SAI1_Block_A->CR1 |= SAI_xCR1_SAIEN;
  SAI1_Block_B->CR1 |= SAI_xCR1_SAIEN;

}

void codec_ADAU1961_Stop(void) {
}
