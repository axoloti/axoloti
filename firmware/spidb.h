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

#ifndef SPIDB_H_
#define SPIDB_H_

/*
 *
 * Double buffered periodic spi exchange
 *
 */

typedef struct {
	SPIConfig spiconfig;
	char *rxbuf;
	char *txbuf;
	int size;
} SPIDBConfig;

enum spidb_signal {
    half_transfer_complete=1,
    full_transfer_complete=2
};

extern unsigned int spidb_interrupt_timestamp;

void spidbMasterStart(SPIDriver *spip, const SPIDBConfig *config);
void spidbSlaveStart(SPIDriver *spip, const SPIDBConfig *config, thread_t * thread);
void spidbSlaveResync(SPIDriver *spip);

// inline functions

__STATIC_INLINE void spidbMasterExchangeI(SPIDriver *spip, bool toggle) {
	SPIDBConfig *config = (SPIDBConfig *)spip->config;
	palClearPad(config->spiconfig.ssport, config->spiconfig.sspad);
	int offset = toggle?0:2*config->size; // assumes 16 bit xfer

	dmaStreamSetMemory0(spip->dmarx, config->rxbuf + offset);
	dmaStreamSetTransactionSize(spip->dmarx, config->size);
	dmaStreamSetMode(spip->dmarx, spip->rxdmamode);

	dmaStreamSetMemory0(spip->dmatx, config->txbuf + offset);
	dmaStreamSetTransactionSize(spip->dmatx, config->size);
	dmaStreamSetMode(spip->dmatx, spip->txdmamode);

	dmaStreamEnable(spip->dmarx);
	dmaStreamEnable(spip->dmatx);
}

#endif /* SPIDB_H_ */
