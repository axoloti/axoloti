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

/*
 * spilink = SPI bus link to interconnect Axoloti Cores and Axoloti Control
 *
 */

#ifndef SPILINK_H_
#define SPILINK_H_

#include "ch.h"
#include "stdint.h"
#include "spidb.h"
#include "axoloti_control.h"
#include "ui.h"

#define SPILINK_BUFSIZE 16
#define SPILINK_CHANNELS 4
#define SPILINK_CTLDATASIZE 16

typedef struct {
	int32_t samples[SPILINK_BUFSIZE];
} spilink_samples_t;

typedef struct {
	spilink_samples_t channel[SPILINK_CHANNELS];
} spilink_channels_t;

typedef struct {
	int32_t header;
	int32_t frameno;
	spilink_channels_t audio_io;
	int32_t control_type;
	uint8_t control_data[SPILINK_CTLDATASIZE];
	int32_t midi1;
	int32_t midi2;
	int32_t footer;
} spilink_data_t;

void spilink_init(bool_t isMaster);
void spilink_clear_audio_tx(void);

extern Thread *pThreadSpilink;

extern spilink_channels_t *spilink_rx_samples;
extern spilink_channels_t *spilink_tx_samples;
extern spilink_data_t spilink_rx[2];
extern spilink_data_t spilink_tx[2];
extern uint32_t frameno;
extern bool_t spilink_master_active;
extern bool_t spilink_toggle;

#define SPILINK_HEADER (('A' << 8) | ('x') | ('o' << 24) | ('<' << 16))
#define SPILINK_FOOTER (('A' << 8) | ('x') | ('o' << 24) | ('>' << 16))

extern int lcd_update_index;

__STATIC_INLINE void spilink_master_process1(spilink_data_t *tx, spilink_data_t *rx){
	lcd_update_index = (lcd_update_index+1)&0x3f;
	tx->control_type = lcd_update_index;
	int i;
	for(i=0;i<SPILINK_CTLDATASIZE;i++){
		tx->control_data[i]=lcd_buffer[i+(lcd_update_index<<4)];
	}
	if (rx->control_type == 0x80) {
	    Btn_Nav_Or.word |= ((int32_t *)rx->control_data)[0];
	    Btn_Nav_And.word &= ((int32_t *)rx->control_data)[1];
	    EncBuffer[0] += rx->control_data[8];
	    EncBuffer[1] += rx->control_data[9];
	    EncBuffer[2] += rx->control_data[10];
	    EncBuffer[3] += rx->control_data[11];
	}

}

__STATIC_INLINE void spilink_master_process(void) {
	if (spilink_master_active) {
		spilink_toggle = !spilink_toggle;
		spidbMasterExchangeI(&SPID3, spilink_toggle);
		if (spilink_toggle) {
			spilink_tx[0].frameno = frameno++;
			if ((spilink_rx[0].header == SPILINK_HEADER)
					&& (spilink_rx[0].footer == SPILINK_FOOTER)) {
				spilink_rx_samples = &spilink_rx[0].audio_io;
			} else {
				spilink_rx_samples = (spilink_channels_t *) 0x080F0000;
			}
			spilink_tx_samples = &spilink_tx[0].audio_io;
			spilink_master_process1(&spilink_tx[0],&spilink_rx[0]);
		} else {
			spilink_tx[1].frameno = frameno++;
			if ((spilink_rx[0].header == SPILINK_HEADER)
					&& (spilink_rx[0].footer == SPILINK_FOOTER)) {
				spilink_rx_samples = &spilink_rx[1].audio_io;
			} else {
				spilink_rx_samples = (spilink_channels_t *) 0x080F0000;
			}
			spilink_tx_samples = &spilink_tx[1].audio_io;
			spilink_master_process1(&spilink_tx[1],&spilink_rx[1]);
		}
	}
}

__STATIC_INLINE void spilink_slave_process(void) {
//	spilink_rx_samples = &spilink_rx[0].audio_io;
	spilink_data_t *r = &spilink_rx[spilink_toggle ? 0 : 1];
	if ((r->header == SPILINK_HEADER) && (r->footer == SPILINK_FOOTER)) {
		spilink_rx_samples = &r->audio_io;
	} else {
		//spilink_rx_samples = (spilink_channels_t *) 0x080F000;
	}
	spilink_tx_samples = &spilink_tx[spilink_toggle ? 0 : 1].audio_io;
}

#endif /* SPILINK_H_ */
