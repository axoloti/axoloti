#include "midi.h"
#include "midi_routing.h"
#include "midi_buffer.h"
#include "midi_usbh.h"

// map 16 usb host ports of usbh-midi device #1 to 16 virtual ports
midi_routing_t midi_inputmap_usbh1 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001,
				0b0000000000000001
			}
};

midi_routing_t midi_outputmap_usbh1 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010,
					0b0000000000000010
			}
};

// map 16 usb host ports of usbh-midi device #2 to 16 virtual ports
midi_routing_t midi_inputmap_usbh2 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001,
					0b0000000000000001
			}
};

midi_routing_t midi_outputmap_usbh2 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100,
					0b0000000000000100
			}
};

#define USBH_DbgLog(x)

static void usbh_midi_dispatch(midi_message_t m, int32_t portmap[]) {
	int32_t inputmap = portmap[m.fields.port];
	  int v;
	  for (v=0;v<16;v++) {
		  if (inputmap&1) {
			  m.fields.port = v;
			  midi_input_buffer_put(&midi_input_buffer, m);
		  }
		  inputmap = inputmap>>1;
	  }
}

static void usbhmidi_cb(USBHMIDIConfig *midic, uint32_t *buf, int len) {
        USBHMIDIConfig_ext *midic_ext = (USBHMIDIConfig_ext *)midic;
        int i;
        for (i = 0; i < len; i ++) {
                if (*buf) {
                        midi_message_t m;
                        m.word = *buf;
                        usbh_midi_dispatch(m, midic_ext->in_mapping->bmvports);
                        buf++;
                        //usbDbgPuts("cb!");
                }
        }
}

void usbmidi_disconnect(USBHMIDIConfig *midic) {
        USBHMIDIConfig_ext *midic_ext = (USBHMIDIConfig_ext *)midic;
        midic_ext->in_mapping->nports=0;
        midic_ext->out_mapping->nports=0;
}


USBHMIDIConfig_ext USBHMIDIC[USBH_MIDI_CLASS_MAX_INSTANCES] = {
    {.config = {usbhmidi_cb,usbmidi_disconnect}, .in_mapping = &midi_inputmap_usbh1, .out_mapping = &midi_outputmap_usbh1},
    {.config = {usbhmidi_cb,usbmidi_disconnect}, .in_mapping = &midi_inputmap_usbh2, .out_mapping = &midi_outputmap_usbh2},
};
