#ifndef MIDI_USBH_H
#define MIDI_USBH_H

#include "stdint.h"
#include "midi_buffer.h"
#include "midi_routing.h"

extern midi_output_buffer_t midi_output_usbh1;
extern midi_output_buffer_t midi_output_usbh2;

extern midi_input_remap_t midi_inputmap_usbh1;
extern midi_input_remap_t midi_inputmap_usbh2;

extern midi_output_routing_t midi_outputmap_usbh1;
extern midi_output_routing_t midi_outputmap_usbh2;

int  usbh_MidiGetOutputBufferPending(void);
int  usbh_MidiGetOutputBufferAvailable(void);

extern void usbh_midi_dispatch(midi_message_t m, int32_t portmap[]);

#endif
