#ifndef MIDI_USBH_H
#define MIDI_USBH_H

#include "stdint.h"
#include "midi_buffer.h"
#include "midi_routing.h"

extern midi_output_buffer_t midi_output_usbh;
extern midi_input_remap_t midi_inputmap_usbh[16];

// external midi interface
void usbh_midi_init(void);
void usbh_midi_reset_buffer(void);
void usbh_MidiSend1(uint8_t port, uint8_t b0);
void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1);
void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len);

int  usbh_MidiGetOutputBufferPending(void);
int  usbh_MidiGetOutputBufferAvailable(void);



#endif
