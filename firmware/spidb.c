/**
 * Copyright (C) 2016 Johannes Taelman
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

#include "ch.h"
#include "halconf.h"
#include "mcuconf.h"
#include "hal.h"
#include "stm32_dma.h"

#include "spidb.h"
#include <stdint.h>

unsigned int spidb_interrupt_timestamp;

//#define DEBUG_INT_ON_GPIO 1

void dmastream_slave_start(SPIDriver *spip) {
	dmaStreamSetMemory0(spip->dmarx, ((SPIDBConfig *) (spip->config))->rxbuf);
	dmaStreamSetTransactionSize(spip->dmarx,
			((SPIDBConfig *) (spip->config))->size * 2);
	dmaStreamSetMode(spip->dmarx,
			spip->rxdmamode | STM32_DMA_CR_MINC | STM32_DMA_CR_CIRC
					| STM32_DMA_CR_HTIE);

	dmaStreamSetMemory0(spip->dmatx, ((SPIDBConfig *) (spip->config))->txbuf);
	dmaStreamSetTransactionSize(spip->dmatx,
			((SPIDBConfig *) (spip->config))->size * 2);
	dmaStreamSetMode(spip->dmatx,
			spip->txdmamode | STM32_DMA_CR_MINC | STM32_DMA_CR_CIRC);
	chSysLock();
	// wait till not selected
	while(!palReadPad(spip->config->ssport,spip->config->sspad)){
		;
	}

	spip->spi->CR1 |= SPI_CR1_SPE;
	while (spip->spi->SR & SPI_SR_BSY) {
	}
	dmaStreamEnable(spip->dmarx);
	dmaStreamEnable(spip->dmatx);
	chSysUnlock();
}

static void dma_spidb_slave_interrupt(void* dat, uint32_t flags) {
	SPIDriver *spip = dat;
	spidb_interrupt_timestamp = stGetCounter();

	if (flags & STM32_DMA_ISR_TCIF) {
		chSysLockFromIsr();
#ifdef DEBUG_INT_ON_GPIO
	palSetPadMode(GPIOA, 1, PAL_MODE_OUTPUT_PUSHPULL);
	palSetPad(GPIOA, 1);
#endif
		chEvtSignalI(spip->thread, full_transfer_complete);
#ifdef DEBUG_INT_ON_GPIO
	palClearPad(GPIOA, 1);
#endif
#if 0
		dmaStreamDisable(spip->dmatx);
		dmaStreamSetTransactionSize(spip->dmatx, ((SPIDBConfig *)(spip->config))->size*2);
		dmaStreamEnable(spip->dmatx);
#endif
		chSysUnlockFromIsr();
	} else if (flags & STM32_DMA_ISR_HTIF) {
		chSysLockFromIsr();
#ifdef DEBUG_INT_ON_GPIO
	palSetPadMode(GPIOA, 1, PAL_MODE_OUTPUT_PUSHPULL);
	palSetPad(GPIOA, 1);
#endif
		chEvtSignalI(spip->thread, half_transfer_complete);
#ifdef DEBUG_INT_ON_GPIO
	palClearPad(GPIOA, 1);
#endif
		chSysUnlockFromIsr();
	} else if (flags & STM32_DMA_ISR_TEIF) {
//	    chSysHalt("spidb:TEIF");
	}
}

/**
 * @brief   Configures and activates the SPI peripheral
 * for slave mode, dual buffer-swapping
 *
 * @param[in] spip      pointer to the @p SPIDriver object
 * @param[in] config    pointer to the @p SPIDBConfig configuration
 *
 */
void spidbSlaveStart(SPIDriver *spip, const SPIDBConfig *config, Thread * thread) {

	spiStart(spip, &config->spiconfig);

	spip->thread = thread;
	spip->spi->CR1 &= ~SPI_CR1_SPE;
	spip->spi->CR1 &= ~SPI_CR1_MSTR;
	spip->spi->CR1 &= ~SPI_CR1_SSM;
	spip->spi->CR1 &= ~SPI_CR1_SSI;

    dmaStreamRelease(spip->dmarx);
    dmaStreamRelease(spip->dmatx);

    bool_t b;
	int irq_priority = -1;
#if STM32_SPI_USE_SPI1
	if (spip == &SPID1) {
		irq_priority = STM32_SPI_SPI1_IRQ_PRIORITY;
	}
#endif
#if STM32_SPI_USE_SPI2
	if (spip == &SPID2) {
		irq_priority = STM32_SPI_SPI2_IRQ_PRIORITY;
	}
#endif
#if STM32_SPI_USE_SPI3
	if (spip == &SPID3) {
		irq_priority = STM32_SPI_SPI3_IRQ_PRIORITY;
	}
#endif
	if (irq_priority == -1) {
		osalSysHalt("IRQ Not found");
	}

	b = dmaStreamAllocate(spip->dmarx, irq_priority,
			(stm32_dmaisr_t) dma_spidb_slave_interrupt, (void *) spip);
	chDbgAssert(!b, "spi_lld_start(), #1 stream already allocated");
	b = dmaStreamAllocate(spip->dmatx, irq_priority,
			(stm32_dmaisr_t) 0, (void *) spip);
	chDbgAssert(!b, "spi_lld_start(), #2 stream already allocated");

	spiSelect(spip);

	dmastream_slave_start(spip);
}

void spidbSlaveResync(SPIDriver *spip) {
	dmaStreamDisable(spip->dmatx);
	while (!(spip->spi->SR & SPI_SR_TXE)) {
	}
	while (spip->spi->SR & SPI_SR_BSY) {
	}
	spip->spi->CR1 &= ~SPI_CR1_SPE;

	dmaStreamDisable(spip->dmarx);
	dmastream_slave_start(spip);
}

static void dma_spidb_master_interrupt(void* dat, uint32_t flags) {
	(void)flags;
	// assume it is a transfer ready interrupt
	SPIDriver *spip = dat;
	dmaStreamDisable(spip->dmarx);
	dmaStreamDisable(spip->dmatx);
	palSetPad(spip->config->ssport, spip->config->sspad);
}

void spidbMasterStart(SPIDriver *spip, const SPIDBConfig *config) {

	int i;
	for (i = 0; i < config->size * 2; i++) {
		config->rxbuf[i] = 0;
	}

	spiStart(spip, &config->spiconfig);

	spip->spi->CR1 &= ~SPI_CR1_SPE;
	spip->spi->CR1 &= ~SPI_CR1_SSM;
	spip->spi->CR1 &= ~SPI_CR1_SSI;
	spip->spi->CR1 |= SPI_CR1_SPE;


    dmaStreamRelease(spip->dmarx);
    dmaStreamRelease(spip->dmatx);

    bool_t b;
	int irq_priority = -1;
#if STM32_SPI_USE_SPI1
	if (spip == &SPID1) {
		irq_priority = STM32_SPI_SPI1_IRQ_PRIORITY;
	}
#endif
#if STM32_SPI_USE_SPI2
	if (spip == &SPID2) {
		irq_priority = STM32_SPI_SPI2_IRQ_PRIORITY;
	}
#endif
#if STM32_SPI_USE_SPI3
	if (spip == &SPID3) {
		irq_priority = STM32_SPI_SPI3_IRQ_PRIORITY;
	}
#endif
	chDbgCheck(irq_priority != -1);

	spip->rxdmamode |= STM32_DMA_CR_MINC;
	spip->txdmamode |= STM32_DMA_CR_MINC;

	b = dmaStreamAllocate(spip->dmarx, irq_priority,
			(stm32_dmaisr_t) dma_spidb_master_interrupt, (void *) spip);
	chDbgAssert(!b, "spi_lld_start() #1 stream already allocated");
	b = dmaStreamAllocate(spip->dmatx, irq_priority,
			(stm32_dmaisr_t) 0, (void *) spip);
	chDbgAssert(!b, "spi_lld_start() #2 stream already allocated");

	dmaStreamSetMemory0(spip->dmarx, ((SPIDBConfig *) (spip->config))->rxbuf);
	dmaStreamSetTransactionSize(spip->dmarx,
			((SPIDBConfig *) (spip->config))->size);
	dmaStreamSetMode(spip->dmarx,
			spip->rxdmamode );

	dmaStreamSetMemory0(spip->dmatx, ((SPIDBConfig *) (spip->config))->txbuf);
	dmaStreamSetTransactionSize(spip->dmatx,
			((SPIDBConfig *) (spip->config))->size);
	dmaStreamSetMode(spip->dmatx,
			spip->txdmamode );
}
