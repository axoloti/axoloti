/**
 * Copyright (C) 2017 Johannes Taelman
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

#include "firmware_chunks.h"

#include "fourcc.h"
#include "chunks/midi_input_routing.h"
#include "chunks/midi_output_routing.h"
#include "chunks/lcd_framebuffer.h"
#include "chunks/gpio_adc.h"

#include "midi_routing.h"
#include "serial_midi.h"
#include "usbh_midi_core.h"
#include "midi_usb.h"
#include "axoloti_control.h"
#include "axoloti_board.h"

// ------ firmware chunks ---------------------------------------------
typedef struct {
	chunk_midi_input_routing_t midi_input_routing_din;
	chunk_midi_input_routing_t midi_input_routing_usbh;
	chunk_midi_input_routing_t midi_input_routing_usbd;
	chunk_midi_output_routing_t midi_output_routing;
	chunk_gpio_adc_t gpio_adc;
	chunk_lcd_framebuffer_t lcd_framebuffer;
} chunk_firmware_chunks_t;

#define fourcc_firmware_chunks  FOURCC('A','X','R','1')

typedef struct {
	chunk_header_t header;
	chunk_firmware_chunks_t fw_chunks;
} chunk_fw_root_t;

const chunk_fw_root_t chunk_fw_root = {
	.header = CHUNK_HEADER(firmware_chunks),
	.fw_chunks = {
		.midi_input_routing_din = {
			.header = CHUNK_HEADER(midi_input_routing),
			.name = "DIN",
			.nports = 1,
			.routing_table = &midi_inputmap_serial
		},
		.midi_input_routing_usbh = {
			.header = CHUNK_HEADER(midi_input_routing),
			.name = "USBH",
			.nports = 16,
			.routing_table = &midi_inputmap_usbh[0]
		},
		.midi_input_routing_usbd = {
			.header = CHUNK_HEADER(midi_input_routing),
			.name = "USBD",
			.nports = 1,
			.routing_table = &midi_inputmap_usbd
		},
		.midi_output_routing = {
			.header = CHUNK_HEADER(midi_output_routing),
			.ntargets = MIDI_TARGETS_PER_VPORT,
			.routing_table = &midi_output_routing_table[0][0]
		},
		.gpio_adc = {
			.header = CHUNK_HEADER(gpio_adc),
			.datatype = 0,
			.channels = 15,
			.data = &adcvalues[0]
		},
		.lcd_framebuffer = {
			.header = CHUNK_HEADER(lcd_framebuffer),
			.width = LCDWIDTH,
			.height = LCDHEIGHT,
			.pixeltype = 0,
			.data = (uint8_t *)lcd_buffer
		}
	}
};

const void *chunk_fw_root_data = &chunk_fw_root;
const int chunk_fw_root_size = sizeof(chunk_fw_root);
