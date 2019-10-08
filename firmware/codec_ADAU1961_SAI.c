/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
#include "stm32f4xx_hal_i2c.h"
#include "axoloti_board.h"
#include "sysmon.h"
#include "spilink.h"
#include "spilink_lld.h"

extern void computebufI(int32_t *inp, int32_t *outp);

#define STM32_SAI_A_DMA_STREAM STM32_DMA_STREAM_ID(2, 1)
#define STM32_SAI_B_DMA_STREAM STM32_DMA_STREAM_ID(2, 4)
#define SAI_A_DMA_CHANNEL 0
#define SAI_B_DMA_CHANNEL 1
#define STM32_SAI_A_DMA_PRIORITY 1
#define STM32_SAI_B_DMA_PRIORITY 1
#define STM32_SAI_A_IRQ_PRIORITY 2
#define STM32_SAI_B_IRQ_PRIORITY 2

#define SAI1_FS_PORT GPIOE
#define SAI1_FS_PAD 4
#define SAI1_SCK_PORT GPIOE
#define SAI1_SCK_PAD 5
#define SAI1_SD_A_PORT GPIOE
#define SAI1_SD_A_PAD 6
#define SAI1_SD_B_PORT GPIOE
#define SAI1_SD_B_PAD 3
#define MCO1_PORT GPIOA
#define MCO1_PAD 8
#define SPILINK_FSYNC_PORT GPIOA
#define SPILINK_FSYNC_PAD 15

//#define DEBUG_INT_ON_GPIO 1

const stm32_dma_stream_t* sai_a_dma;
const stm32_dma_stream_t* sai_b_dma;

unsigned int codec_interrupt_timestamp;

void codec_ADAU1961_hw_reset(void) {
}

typedef enum {
	falling=0,
	rising=1
} edge_t;

void waitSync(edge_t edge){
	 // sync on NSS
   palSetPadMode(SPILINK_FSYNC_PORT, SPILINK_FSYNC_PAD, PAL_MODE_INPUT);
	volatile int i,j;
	// wait till NSS is low (or already is)
	j=1000000;
	while(--j){
		if (edge ^ !palReadPad(SPILINK_FSYNC_PORT, SPILINK_FSYNC_PAD)) {
			break;
		}
	}
	i=1000000;
	// wait till NSS is high
	while(--i){
		if (edge ^ palReadPad(SPILINK_FSYNC_PORT, SPILINK_FSYNC_PAD)) {
			break;
		}
	}
	// wait till NSS is low
	j=1000000;
	while(--j){
		if (edge ^ !palReadPad(SPILINK_FSYNC_PORT, SPILINK_FSYNC_PAD)) {
			break;
		}
	}
	if ((j==0)||(i==0)) {
		// no pulse edge found, fail
		while(1);
	}
}

static uint8_t i2crxbuf[8];
static uint8_t i2ctxbuf[8];

#define ADAU1961_I2C_ADDR (0x70>>1)

// use STM32 HAL for I2C

static I2C_HandleTypeDef    ADAU1961_i2c_handle;

/******************************* I2C Routines**********************************/
/**
  * @brief  Configures I2C interface.
  */
static void ADAU_I2C_Init(void)
{
  if (HAL_I2C_GetState(&ADAU1961_i2c_handle) == HAL_I2C_STATE_RESET)
  {
    /* I2Cx peripheral configuration */
	ADAU1961_i2c_handle.Init.ClockSpeed = 100000;
    ADAU1961_i2c_handle.Init.DutyCycle = I2C_DUTYCYCLE_2;
    ADAU1961_i2c_handle.Init.OwnAddress1 = 0x33;
    ADAU1961_i2c_handle.Init.AddressingMode = I2C_ADDRESSINGMODE_7BIT;
    ADAU1961_i2c_handle.Instance = I2C3;

    /* Init the I2C */
    palSetPadMode(
        GPIOH,
        7,
        PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN
            | PAL_MODE_INPUT_PULLUP);
    palSetPadMode(
        GPIOH,
        8,
        PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN
            | PAL_MODE_INPUT_PULLUP);

    rccEnableI2C3(FALSE);
    nvicEnableVector(I2C3_EV_IRQn, STM32_I2C_I2C3_IRQ_PRIORITY);
    nvicEnableVector(I2C3_ER_IRQn, STM32_I2C_I2C3_IRQ_PRIORITY);

    HAL_I2C_Init(&ADAU1961_i2c_handle);
  }
}

unsigned int HAL_GetTick(void) {
	return stGetCounter();
}

int HAL_RCC_GetPCLK1Freq(void) {
	return STM32_PCLK1;
}

OSAL_IRQ_HANDLER(STM32_I2C3_EVENT_HANDLER) {
  OSAL_IRQ_PROLOGUE();
  HAL_I2C_EV_IRQHandler(&ADAU1961_i2c_handle);
  OSAL_IRQ_EPILOGUE();
}

OSAL_IRQ_HANDLER(STM32_I2C3_ERROR_HANDLER) {
  OSAL_IRQ_PROLOGUE();
  HAL_I2C_ER_IRQHandler(&ADAU1961_i2c_handle);
  OSAL_IRQ_EPILOGUE();
}

#define TIMEOUT 1000000

void ADAU1961_WriteRegister(uint16_t RegisterAddr, uint8_t RegisterValue) {
	// interrupt version
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  i2ctxbuf[2] = RegisterValue;
  chThdSleepMilliseconds(10);
  HAL_StatusTypeDef r = HAL_I2C_Master_Transmit_IT(&ADAU1961_i2c_handle, ADAU1961_I2C_ADDR<<1, i2ctxbuf,3);
  // should suspend thread and resume when transmit is done...
  if (r != HAL_OK) chSysHalt("CodecI2CWrite");
  // but for now we just sleep a little and assume transmission is finished after...
  chThdSleepMilliseconds(10);
}

void ADAU1961_WriteRegister6(uint16_t RegisterAddr, const uint8_t * RegisterValues) {
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  i2ctxbuf[2] = RegisterValues[0];
  i2ctxbuf[3] = RegisterValues[1];
  i2ctxbuf[4] = RegisterValues[2];
  i2ctxbuf[5] = RegisterValues[3];
  i2ctxbuf[6] = RegisterValues[4];
  i2ctxbuf[7] = RegisterValues[5];
  HAL_StatusTypeDef r = HAL_I2C_Master_Transmit(&ADAU1961_i2c_handle,ADAU1961_I2C_ADDR<<1,i2ctxbuf,8,TIMEOUT);
  if (r != HAL_OK) chSysHalt("CodecI2CWrite6");
  chThdSleepMilliseconds(10);
}

void ADAU1961_ReadRegister6(uint16_t RegisterAddr) {
  i2ctxbuf[0] = RegisterAddr >> 8;
  i2ctxbuf[1] = RegisterAddr;
  chThdSleepMilliseconds(1);
  HAL_I2C_Master_Transmit(&ADAU1961_i2c_handle,ADAU1961_I2C_ADDR<<1,i2ctxbuf,2,TIMEOUT);
  chThdSleepMilliseconds(1);
  HAL_StatusTypeDef r = HAL_I2C_Master_Receive(&ADAU1961_i2c_handle,(ADAU1961_I2C_ADDR<<1)+1,i2crxbuf,6,TIMEOUT);
  if (r != HAL_OK) chSysHalt("CodecI2CRead6");
}

void picosleep(void)
{
	volatile int i=100;
	while(i--){}
}

// approx 1Hz drift...
static const unsigned char pll48k_pullup[6] = {0x1F,0x40,0x04,0x81,0x31,0x01};
static const unsigned char pll48k_exact[6] = {0x1F,0x40,0x04,0x80,0x31,0x01};
static const unsigned char pll48k_pulldown[6] = {0x1F,0x40,0x04,0x7F,0x31,0x01};

void codec_ADAU1961_hw_init(uint16_t samplerate, bool isMaster) {

  ADAU_I2C_Init();
  chThdSleepMilliseconds(5);

  /*
   * 1. Power down the PLL.
   * 2. Reset the PLL control register.
   * 3. Start the PLL.
   * 4. Poll the lock bit.
   * 5. Assert the core clock enable bit after the PLL lock is acquired.
   */

    ADAU1961_WriteRegister(ADAU1961_REG_R0_CLKC, 0x00); // disable core

    unsigned char pllreg[6];
    if (samplerate == 48000) {
      // reg setting 0x007D 0012 3101
      pllreg[0] = 0x1F;
      pllreg[1] = 0x40;
      pllreg[2] = 0x04;
      pllreg[3] = 0x81;
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

    int i = 100;
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
      while(1){
      }
    }

    ADAU1961_WriteRegister(ADAU1961_REG_R0_CLKC, 0x09); // PLL = clksrc

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

    if (isMaster) {
        ADAU1961_WriteRegister(ADAU1961_REG_R15_SERP0, 0x01); // codec is master
    } else {
        ADAU1961_WriteRegister(ADAU1961_REG_R15_SERP0, 0x01); // codec is master too
    	palSetPadMode(SAI1_FS_PORT, SAI1_FS_PAD, PAL_MODE_INPUT);
        ADAU1961_WriteRegister6(ADAU1961_REG_R1_PLLC, &pll48k_pulldown[0]);
    	chThdSleepMilliseconds(100);
    	chSysLock();
        while(1){
        	// wait for spi frame
        	waitSync(falling);
        	int i = 0;
        	// now count time to FS edge
        	while(palReadPad(SAI1_FS_PORT, SAI1_FS_PAD)){
        		i++;
        	}
        	if ((i>1) && (i<5)) {
        		// lock found
        		break;
        	}
        }
        chSysUnlock();
        ADAU1961_WriteRegister6(ADAU1961_REG_R1_PLLC, &pllreg[0]);
        dmaStreamClearInterrupt(sai_b_dma);
        dmaStreamEnable(sai_b_dma);

        dmaStreamClearInterrupt(sai_a_dma);
        dmaStreamEnable(sai_a_dma);
	    SAI1_Block_A->CR1 |= SAI_xCR1_SAIEN;
	    SAI1_Block_B->CR1 |= SAI_xCR1_SAIEN;
    }


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


    ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1, 0x00); // 32bit samples
//  ADAU1961_WriteRegister(ADAU1961_REG_R16_SERP1,0x60); // 64bit samples, spdif clock!

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

  chThdSleepMilliseconds(10);
}

#ifdef DEBUG_INT_ON_GPIO
static void dma_sai_b_interrupt(void* dat, uint32_t flags) {
	(void) dat;
	(void) flags;
	palSetPadMode(GPIOA, GPIOA_PIN3, PAL_MODE_OUTPUT_PUSHPULL);
	palSetPad(GPIOA, GPIOA_PIN3);
	asm("nop");	asm("nop");	asm("nop");	asm("nop");
	palClearPad(GPIOA, GPIOA_PIN3);
}
#endif

static void dma_sai_a_interrupt_spilink_master(void* dat, uint32_t flags) {
	(void) dat;
	(void) flags;
#ifdef DEBUG_INT_ON_GPIO
	palSetPadMode(GPIOA, 0, PAL_MODE_OUTPUT_PUSHPULL);
	palSetPad(GPIOA, 0);
#endif
	chSysLockFromISR();
	codec_interrupt_timestamp = stGetCounter();
	spilink_master_process();
	chSysUnlockFromISR();
	if ((sai_a_dma)->stream->CR & STM32_DMA_CR_CT) {
		computebufI(rbuf2, buf);
	} else {
		computebufI(rbuf, buf2);
	}
#ifdef DEBUG_INT_ON_GPIO
	palClearPad(GPIOA, 0);
#endif
}

static void dma_sai_a_interrupt_spilink_slave(void* dat, uint32_t flags) {
  (void)dat;
  (void)flags;
#ifdef DEBUG_INT_ON_GPIO
  palSetPadMode(GPIOA, 0, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPad(GPIOA, 0);
#endif
  codec_interrupt_timestamp = stGetCounter();
  spilink_slave_process();

  if ((sai_a_dma)->stream->CR & STM32_DMA_CR_CT) {
    computebufI(rbuf2, buf);
  }
  else {
    computebufI(rbuf, buf2);
  }
#ifdef DEBUG_INT_ON_GPIO
  palClearPad(GPIOA, 0);
#endif
}

const stm32_dma_stream_t* sai_a_dma;
const stm32_dma_stream_t* sai_b_dma;

void check_clock_is_present(void) {
	while (1) {
		volatile int i,j;
		chSysLock();
		j = 100;
		while (--j) {
			if (!palReadPad(MCO1_PORT, MCO1_PAD)) {
				break;
			}
		}
		i = 100; // 1000 iterations
		// wait till clock is high
		while (--i) {
			if (palReadPad(MCO1_PORT, MCO1_PAD)) {
				break;
			}
		}
		// wait till clock is low
		j = 1000;
		while (--j) {
			if (!palReadPad(MCO1_PORT, MCO1_PAD)) {
				break;
			}
		}
		chSysUnlock();
		if ((j == 0) || (i == 0)) {
			// no pulse edge found, fail
			chSysHalt("CodecSyncFail");
		} else
			break; // clock found
	}
	sysmon_blink_pattern(BLINK_BOOT);
}

void codec_ADAU1961_i2s_init(uint16_t sampleRate, bool isMaster) {
  volatile SAI_Block_TypeDef *sai_a = SAI1_Block_A;
  volatile SAI_Block_TypeDef *sai_b = SAI1_Block_B;
//configure MCO
  if (isMaster) {
	// master
    palSetPadMode(MCO1_PORT, MCO1_PAD, PAL_MODE_OUTPUT_PUSHPULL);
    palSetPadMode(MCO1_PORT, MCO1_PAD, PAL_MODE_ALTERNATE(0));
    chThdSleepMilliseconds(10);
  } else {
	// slave
    palSetPadMode(MCO1_PORT, MCO1_PAD, PAL_MODE_INPUT);
    chThdSleepMilliseconds(1000);
    // verify clock is present
    check_clock_is_present();
  }
// release SAI
  palSetPadMode(SAI1_FS_PORT, SAI1_FS_PAD, PAL_MODE_INPUT);
  palSetPadMode(SAI1_SD_A_PORT, SAI1_SD_A_PAD, PAL_MODE_INPUT);
  palSetPadMode(SAI1_SD_B_PORT, SAI1_SD_B_PAD, PAL_MODE_INPUT);
  palSetPadMode(SAI1_SCK_PORT, SAI1_SCK_PAD, PAL_MODE_INPUT);


  codec_ADAU1961_hw_init(sampleRate, isMaster);


// configure SAI
  RCC->APB2ENR |= RCC_APB2ENR_SAI1EN;
  chThdSleepMilliseconds(1);
  SAI1_Block_A->CR2 = 0;//SAI_xCR2_FTH_1;
  SAI1_Block_B->CR2 = 0;//SAI_xCR2_FTH_0;
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

  palSetPadMode(SAI1_FS_PORT, SAI1_FS_PAD, PAL_MODE_ALTERNATE(6));
  palSetPadMode(SAI1_SD_A_PORT, SAI1_SD_A_PAD, PAL_MODE_ALTERNATE(6));
  palSetPadMode(SAI1_SD_B_PORT, SAI1_SD_B_PAD, PAL_MODE_ALTERNATE(6));
  palSetPadMode(SAI1_SCK_PORT, SAI1_SCK_PAD, PAL_MODE_ALTERNATE(6));

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

  if  (isMaster){
    sai_a_dma = dmaStreamAlloc(STM32_SAI_A_DMA_STREAM,
                               STM32_SAI_A_IRQ_PRIORITY,
                               (stm32_dmaisr_t)dma_sai_a_interrupt_spilink_master,
                               (void *)0);
  } else {
    sai_a_dma = dmaStreamAlloc(STM32_SAI_A_DMA_STREAM,
                               STM32_SAI_A_IRQ_PRIORITY,
                               (stm32_dmaisr_t)dma_sai_a_interrupt_spilink_slave,
                               (void *)0);
  }
  dmaStreamSetPeripheral(sai_a_dma, &(sai_a->DR));
  dmaStreamSetMemory0(sai_a_dma, buf);
  dmaStreamSetMemory1(sai_a_dma, buf2);
  dmaStreamSetTransactionSize(sai_a_dma, 32);
  dmaStreamSetMode(sai_a_dma, sai_a_dma_mode | STM32_DMA_CR_MINC);


#ifndef DEBUG_INT_ON_GPIO
  sai_b_dma = dmaStreamAlloc(STM32_SAI_B_DMA_STREAM,
                             STM32_SAI_B_IRQ_PRIORITY,
                             (stm32_dmaisr_t)0,
                             (void *)0);
#else
  sai_b_dma = dmaStreamAlloc(STM32_SAI_B_DMA_STREAM,
                             STM32_SAI_B_IRQ_PRIORITY,
                             dma_sai_b_interrupt,
                             (void *)0);
#endif
  if (!sai_b_dma){
    setErrorFlag(ERROR_CODEC_I2C);
  }

  dmaStreamSetPeripheral(sai_b_dma, &(sai_b->DR));
  dmaStreamSetMemory0(sai_b_dma, rbuf);
  dmaStreamSetMemory1(sai_b_dma, rbuf2);
  dmaStreamSetTransactionSize(sai_b_dma, 32);
  dmaStreamSetMode(sai_b_dma, sai_b_dma_mode | STM32_DMA_CR_MINC);


  dmaStreamClearInterrupt(sai_b_dma);
  dmaStreamClearInterrupt(sai_a_dma);
  if (isMaster) {
	  chSysLock();
      SAI1_Block_A->CR2 |= SAI_xCR2_FFLUSH;
	  SAI1_Block_B->CR2 |= SAI_xCR2_FFLUSH;
  	  SAI1_Block_A->DR=0;
  	  SAI1_Block_A->DR=0;
	  dmaStreamEnable(sai_b_dma);
	  dmaStreamEnable(sai_a_dma);
      SAI1_Block_B->CR1 |= SAI_xCR1_SAIEN;
      SAI1_Block_A->CR1 |= SAI_xCR1_SAIEN;
      // 2.25 us offset between dmarx and dmatx
      chSysUnlock();
  } else {
	  chSysLock();
      SAI1_Block_A->CR2 |= SAI_xCR2_FFLUSH;
	  SAI1_Block_B->CR2 |= SAI_xCR2_FFLUSH;
  	  SAI1_Block_A->DR=0;
  	  SAI1_Block_A->DR=0;
  	  waitSync(rising);
	  dmaStreamEnable(sai_b_dma);
	  dmaStreamEnable(sai_a_dma);
      SAI1_Block_A->CR1 |= SAI_xCR1_SAIEN;
      SAI1_Block_B->CR1 |= SAI_xCR1_SAIEN;
      chSysUnlock();
  }
}

void codec_ADAU1961_Stop(void) {
}
