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

/*
 * Adapted from codec_CS43L22.c
 * Created on: Jun 7, 2012
 * Author: Kumar Abhishek
 */

#include "codec.h"
#include "codec_CS43L22.h"

const stm32_dma_stream_t* i2sdma;
static uint32_t i2stxdmamode = 0;

static const I2CConfig i2cfg = {OPMODE_I2C, 100000, STD_DUTY_CYCLE, };

#define I2S3_TX_DMA_CHANNEL \
STM32_DMA_GETCHANNEL(STM32_SPI_SPI3_TX_DMA_STREAM, \
STM32_SPI3_TX_DMA_CHN)

static uint8_t txbuf[2] __attribute__ ((section (".sram2")));
static uint8_t rxbuf[2] __attribute__ ((section (".sram2")));

void codec_CS43L22_hw_init(void) {
  palSetPadMode(GPIOB, 6, PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN);
  palSetPadMode(GPIOB, 9, PAL_MODE_ALTERNATE(4) | PAL_STM32_OTYPE_OPENDRAIN);

// Start the i2c driver
  i2cStart(&CODEC_I2C, &i2cfg);

// Reset the codec
  codec_CS43L22_hw_reset();

// Write init sequence
// Keep codec powered down initially
  codec_CS43L22_pwrCtl(0);

  codec_CS43L22_muteCtl(0);

// Auto Detect Clock, MCLK/2
  codec_CS43L22_writeReg(0x05, 0x81);

// Slave Mode, I2S Data Format
  codec_CS43L22_writeReg(0x06, 0x04);

  codec_CS43L22_pwrCtl(1);

  codec_CS43L22_volCtl(200);

// Adjust PCM Volume
  codec_CS43L22_writeReg(0x1A, 0x0A);
  codec_CS43L22_writeReg(0x1B, 0x0A);

// Disable the analog soft ramp
  codec_CS43L22_writeReg(0x0A, 0x00);

// Disable the digital soft ramp
  codec_CS43L22_writeReg(0x0E, 0x04);

// Disable the limiter attack level
  codec_CS43L22_writeReg(0x27, 0x00);

  codec_CS43L22_writeReg(0x1C, 0x80);

//  i2cStop(&CODEC_I2C);

}

void codec_CS43L22_hw_reset(void) {
  palClearPad(GPIOD, 4); //GPIOD_RESET);
  halPolledDelay(MS2RTT(10));
  palSetPad(GPIOD, 4); //GPIOD_RESET);
}

static void dma_i2s_interrupt(void* dat, uint32_t flags) {
  (void)dat;
  (void)flags;

  if ((i2sdma)->stream->CR & STM32_DMA_CR_CT) {
    computebufI(rbuf, buf);
  }
  else {
    computebufI(rbuf2, buf2);
  }
  dmaStreamClearInterrupt(i2sdma);
}

static void codec_CS43L22_dma_init(void) {
  i2sdma = STM32_DMA_STREAM(STM32_SPI_SPI3_TX_DMA_STREAM);

  i2stxdmamode = STM32_DMA_CR_CHSEL(I2S3_TX_DMA_CHANNEL)
      | STM32_DMA_CR_PL(STM32_SPI_SPI3_DMA_PRIORITY) | STM32_DMA_CR_DIR_M2P
      | STM32_DMA_CR_TEIE | STM32_DMA_CR_TCIE | STM32_DMA_CR_DBM | // double buffer mode
      STM32_DMA_CR_PSIZE_HWORD | STM32_DMA_CR_MSIZE_WORD;

  dmaStreamAllocate(i2sdma, STM32_SPI_SPI3_IRQ_PRIORITY,
                    (stm32_dmaisr_t)dma_i2s_interrupt, (void *)&SPID3);

  dmaStreamSetPeripheral(i2sdma, &(SPI3->DR));
  dmaStreamSetMemory0(i2sdma, buf);
  dmaStreamSetMemory1(i2sdma, buf2);
  dmaStreamSetTransactionSize(i2sdma, 64);
  dmaStreamSetMode(i2sdma, i2stxdmamode | STM32_DMA_CR_MINC);
  dmaStreamClearInterrupt(i2sdma);
  dmaStreamEnable(i2sdma);
}

void codec_CS43L22_i2s_init_48k(void) {
  palSetPadMode(GPIOA, 4, PAL_MODE_OUTPUT_PUSHPULL);
  palSetPadMode(GPIOA, 4, PAL_MODE_ALTERNATE(6));
  palSetPadMode(GPIOC, 10, PAL_MODE_ALTERNATE(6));
  palSetPadMode(GPIOC, 12, PAL_MODE_ALTERNATE(6));

// SPI3 in I2S Mode, Master
  CODEC_I2S_ENABLE;
  CODEC_I2S->I2SCFGR = SPI_I2SCFGR_I2SMOD | SPI_I2SCFGR_I2SCFG_1
      | SPI_I2SCFGR_DATLEN_1;
  CODEC_I2S->I2SPR = SPI_I2SPR_MCKOE | SPI_I2SPR_ODD | 3;
  codec_CS43L22_dma_init();
  CODEC_I2S->CR2 = SPI_CR2_TXDMAEN;
  CODEC_I2S->I2SCFGR |= SPI_I2SCFGR_I2SE;
}

void codec_CS43L22_writeReg(uint8_t addr, uint8_t data) {
  txbuf[0] = addr;
  txbuf[1] = data;
  i2cMasterTransmitTimeout(&I2CD1, CS43L22_ADDR, txbuf, 2, NULL, 0, MS2ST(4));
}

uint8_t codec_CS43L22_readReg(uint8_t addr) {
  txbuf[0] = addr;
  i2cMasterTransmitTimeout(&I2CD1, CS43L22_ADDR, txbuf, 1, rxbuf, 2, MS2ST(4));
  return rxbuf[0];
}

void codec_CS43L22_pwrCtl(uint8_t pwr) {
  if (pwr)
    codec_CS43L22_writeReg(0x02, 0x9E);
  else
    codec_CS43L22_writeReg(0x02, 0x01);
}

void codec_CS43L22_muteCtl(uint8_t mute) {
  if (mute)
    codec_CS43L22_writeReg(0x04, 0xFF);
  else
    codec_CS43L22_writeReg(0x04, 0xAF);
}

void codec_CS43L22_volCtl(uint8_t vol) {
  if (vol > 0xE6) {
    codec_CS43L22_writeReg(0x20, vol - 0xE7);
    codec_CS43L22_writeReg(0x21, vol - 0xE7);
  }
  else {
    codec_CS43L22_writeReg(0x20, vol + 0x19);
    codec_CS43L22_writeReg(0x21, vol + 0x19);
  }
}
