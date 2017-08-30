/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
#ifndef __PCONNECTION_H
#define __PCONNECTION_H

typedef struct {
	uint32_t header;
	uint32_t version;
	uint32_t dspload;
	uint32_t patchID;
	uint32_t voltage;
	uint32_t loadPatchIndex;
	uint32_t fs_ready;
	float vu_input[2];
	float vu_output[2];
	uint32_t underruns;
} tx_pckt_ack_v2_t;

extern tx_pckt_ack_v2_t tx_pckt_ack_v2;

void InitPConnection(void);
extern void BootLoaderInit(void);
void LogTextMessage(const char* format, ...);
int GetFirmwareID(void);

#endif
