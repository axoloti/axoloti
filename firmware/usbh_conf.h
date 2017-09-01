#ifndef USBH_CONF_H
#define USBH_CONF_H

#include "usbh_midi_class.h"

typedef struct {
	USBHMIDIConfig config;
	midi_input_remap_t *in_mapping;
	midi_output_routing_t *out_mapping;
	midi_output_buffer_t out_buffer;
} USBHMIDIConfig_ext;

extern USBHMIDIConfig_ext USBHMIDIC[USBH_MIDI_CLASS_MAX_INSTANCES];

#endif
