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
#include "hal.h"
#include "axoloti_board.h"
#include "spilink.h"
#include "spidb.h"

bool spilink_toggle;
thread_t *pThreadSpilink = 0;

spilink_data_t spilink_tx[2] DMA_MEM_FW;
spilink_data_t spilink_rx[2] DMA_MEM_FW;

spilink_channels_t *spilink_rx_samples;
spilink_channels_t *spilink_tx_samples;

uint32_t frameno = 0;

#define SPILINK SPID3

#define SPILINK_NSS_PORT GPIOA
#define SPILINK_NSS_PAD GPIOA_PIN15

bool spilink_master_active = 0;
int spilink_update_index;
int lcd_update_index;

/*
 * SPI configuration (10.5MHz, CPHA=0, CPOL=0, 16 bit).
 */
static const SPIDBConfig spidbcfg_slave = { { 0, (spicallback_t)NULL, SPILINK_NSS_PORT, SPILINK_NSS_PAD, SPI_CR1_DFF, 0 },
		(void *)&spilink_rx, (void *)&spilink_tx, sizeof(spilink_data_t) / 2 };
static const SPIDBConfig spidbcfg_master = { { 0, (spicallback_t)NULL, SPILINK_NSS_PORT, SPILINK_NSS_PAD, SPI_CR1_BR_0 | SPI_CR1_DFF, 0 },
		(void *)&spilink_rx, (void *)&spilink_tx, sizeof(spilink_data_t) / 2 };

static THD_WORKING_AREA(waThreadSpilink, 256);
//__attribute__ ((section (".ccmramend")));

static THD_FUNCTION(ThreadSpilinkSlave, arg) {
	(void) arg;
	chRegSetThreadName("spilink");
		// slave
		while (1) {
			/* Waiting for messages.*/
			msg_t m = chEvtWaitAnyTimeout(7, TIME_MS2I(50));
			if (!m) { // timeout
				int i;
				for (i = 0; i < 2; i++) {
					int j;
					for (j = 0; j < SPILINK_CHANNELS; j++) {
						int k;
						for (k = 0; k < SPILINK_BUFSIZE; k++) {
							spilink_rx[i].audio_io.channel[j].samples[k] = i
									+ 2;
						}
					}
				}
				spidbSlaveResync(&SPILINK);
//				LogTextMessage("spilink slave resync");
				continue;
			} else if (m & half_transfer_complete) {
				spilink_toggle = 0;
				if (spilink_rx[0].header != SPILINK_HEADER) {
					spidbSlaveResync(&SPILINK);
				}
			} else if (m & full_transfer_complete) {
				if (spilink_rx[0].header != SPILINK_HEADER) {
					spidbSlaveResync(&SPILINK);
				} /*else if (!(SAI1_Block_A->CR1 & SAI_xCR1_SAIEN)) {
					chSysLock();
				    SAI1_Block_A->CR1 |= SAI_xCR1_SAIEN;
				    SAI1_Block_B->CR1 |= SAI_xCR1_SAIEN;
				    chSysUnlock();
				}*/
				spilink_toggle = 1;
			} else if (m & (1 << 2)) {
				spidbSlaveResync(&SPILINK);
				continue;
			} else {
				//????
			}
		}
}

void spilink_clear_audio_tx(void) {
	int i;
	for (i = 0; i < 2; i++) {
		int j;
		for (j = 0; j < SPILINK_CHANNELS; j++) {
			int k;
			for (k = 0; k < SPILINK_BUFSIZE; k++) {
				spilink_tx[i].audio_io.channel[j].samples[k] = i + 1;
			}
		}
	}
}

void spilink_init(bool isMaster) {
	int i;
	for (i = 0; i < 2; i++) {
		spilink_tx[i].header = SPILINK_HEADER;
		spilink_tx[i].footer = SPILINK_FOOTER;
		int j;
		for (j = 0; j < SPILINK_CHANNELS; j++) {
			int k;
			for (k = 0; k < SPILINK_BUFSIZE; k++) {
				spilink_rx[i].audio_io.channel[j].samples[k] = i;
				spilink_tx[i].audio_io.channel[j].samples[k] = i + 1;
			}
		}
		for (j = 0; j < SPILINK_CTLDATASIZE; j++) {
			spilink_rx[i].control_data[j] = 0;
			spilink_tx[i].control_data[j] = 0;
		}
	}
	if (isMaster) {
		spilink_toggle = 0;
		spilink_rx_samples = &spilink_rx[0].audio_io;
		spilink_tx_samples = &spilink_tx[0].audio_io;
		palSetPadMode(GPIOA, 15, PAL_MODE_OUTPUT_PUSHPULL); // NSS
#if 0
		chMtxLock(&Mutex_DMAStream_1_7);
		spiStart(&SPILINK, &spilink_master_cfg); /* Setup transfer parameters.       */
		pThreadSpilink = chThdCreateStatic(waThreadSpilink,
				sizeof(waThreadSpilink), HIGHPRIO - 1,
				ThreadSpilinkMaster, NULL);
#else
		spidbMasterStart(&SPILINK, &spidbcfg_master);
		spilink_master_active = 1;
#endif
	} else { // slave
		spilink_rx_samples = &spilink_rx[0].audio_io;
		spilink_tx_samples = &spilink_tx[0].audio_io;
		thread_t *_pThreadSpilink = chThdCreateStatic(waThreadSpilink,
				sizeof(waThreadSpilink), HIGHPRIO - 1,
				ThreadSpilinkSlave, NULL);
		spidbSlaveStart(&SPILINK, &spidbcfg_slave, _pThreadSpilink);
		pThreadSpilink = _pThreadSpilink;
	}
}

