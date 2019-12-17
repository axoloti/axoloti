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

void spilink_init(bool isMaster);
void spilink_clear_audio_tx(void);

extern thread_t *pThreadSpilink;

extern spilink_channels_t *spilink_rx_samples;
extern spilink_channels_t *spilink_tx_samples;
extern spilink_data_t spilink_rx[2];
extern spilink_data_t spilink_tx[2];
extern uint32_t frameno;
extern bool spilink_master_active;
extern bool spilink_toggle;

#define SPILINK_HEADER (('A' << 8) | ('x') | ('o' << 24) | ('<' << 16))
#define SPILINK_FOOTER (('A' << 8) | ('x') | ('o' << 24) | ('>' << 16))

extern int spilink_update_index;
extern int lcd_update_index;

#endif /* SPILINK_H_ */
