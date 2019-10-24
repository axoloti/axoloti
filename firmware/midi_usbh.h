#ifndef MIDI_USBH_H
#define MIDI_USBH_H

#include "stdint.h"
#include "midi_buffer.h"
#include "midi_routing.h"

extern midi_output_buffer_t midi_output_usbh1;
extern midi_output_buffer_t midi_output_usbh2;

extern midi_routing_t midi_inputmap_usbh1;
extern midi_routing_t midi_inputmap_usbh2;

extern midi_routing_t midi_outputmap_usbh1;
extern midi_routing_t midi_outputmap_usbh2;

int  usbh_MidiGetOutputBufferPending(void);
int  usbh_MidiGetOutputBufferAvailable(void);

typedef struct USBHMIDIConfig USBHMIDIConfig;

void usbmidi_disconnect(USBHMIDIConfig *midic);

typedef void (*usbhmidi_report_callback)(struct USBHMIDIConfig *midic,  uint32_t *buf, int len);
typedef void (*usbhmidi_disconnect_callback)(struct USBHMIDIConfig *midic);

struct USBHMIDIConfig {
       usbhmidi_report_callback cb_report;
       usbhmidi_disconnect_callback cb_disconnect;
};

typedef struct {
       USBHMIDIConfig config;
       midi_routing_t *in_mapping;
       midi_routing_t *out_mapping;
       midi_output_buffer_t out_buffer;
} USBHMIDIConfig_ext;

#define USBH_MIDI_CLASS_MAX_INSTANCES 2

extern USBHMIDIConfig_ext USBHMIDIC[USBH_MIDI_CLASS_MAX_INSTANCES];


#endif
