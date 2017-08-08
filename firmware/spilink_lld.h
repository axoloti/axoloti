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

#include "spidb.h"


__STATIC_INLINE void spilink_master_process1(spilink_data_t *tx, spilink_data_t *rx){
	spilink_update_index++;

	// one in every 8 packets is an update of led data - 375Hz
	// but requires 2 packets for a full update, so 187.5Hz effective
	if ((spilink_update_index & 0x07) == 0) {
		if (spilink_update_index & 0x08) {
			// 4 encoder rings...
			tx->control_type = 0x200;
			int i;
			for(i=0;i<4;i++){
				((int32_t*) tx->control_data)[i] = leds[i].led_32b;
			}
		} else {
			// other leds...
			tx->control_type = 0x201;
			int i;
			for(i=0;i<2;i++){
				((int32_t*) tx->control_data)[i] = leds[i+4].led_32b;
			}
			// these do not exist
			((int32_t*) tx->control_data)[i++] = 0;
			((int32_t*) tx->control_data)[i++] = 0;
		}
	} else {
		lcd_update_index = (lcd_update_index+1)&0x3f;
		tx->control_type = 0x100 + lcd_update_index;
		int i;
		for(i=0;i<SPILINK_CTLDATASIZE;i++){
			tx->control_data[i]=lcd_buffer[i+(lcd_update_index<<4)];
		}
	}

	if (rx->control_type == 0x80) {
//		Btn_Nav_Or.word |= ((int32_t *)rx->control_data)[0];
//		Btn_Nav_And.word |= ((int32_t *)rx->control_data)[1];
		input_event evt;
		evt.word = ((int32_t *)rx->control_data)[0];
		if (evt.word)
			queueInputEventI(evt);
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
