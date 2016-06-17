/*
 * spidb.h
 *
 *  Created on: 06 Jun 2016
 *      Author: Johannes Taelman
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

void spidbMasterStart(SPIDriver *spip, const SPIDBConfig *config, Thread * thread);
void spidbSlaveStart(SPIDriver *spip, const SPIDBConfig *config, Thread * thread);
void spidbSlaveResync(SPIDriver *spip);

// inline functions

__STATIC_INLINE void spidbMasterExchangeI(SPIDriver *spip, bool_t toggle) {
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
