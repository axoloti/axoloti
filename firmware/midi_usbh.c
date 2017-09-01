#include "midi.h"
#include "midi_routing.h"
#include "midi_buffer.h"
#include "midi_usbh.h"

// map 16 usb host ports of usbh-midi device #1 to 16 virtual ports
midi_input_remap_t midi_inputmap_usbh1 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
				0b00000011,
				0b00000011,
				0b00000011,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001
			}
};

midi_output_routing_t midi_outputmap_usbh1 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {4,8,0,0,
					0,0,0,0,
					0,0,0,0,
					0,0,0,0
			}
};

// map 16 usb host ports of usbh-midi device #2 to 16 virtual ports
midi_input_remap_t midi_inputmap_usbh2 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {
				0b00000011,
				0b00000011,
				0b00000011,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001,
				0b00000001
			}
};

midi_output_routing_t midi_outputmap_usbh2 = {
			.name = "not connected",
			.nports = 0,
			.bmvports = {0,0,0,0,
					0,0,0,0,
					0,0,0,0,
					0,0,0,0
			}
};

#define USBH_DbgLog(x)


// pack header CN | CIN
inline uint8_t USBMidi_calcPH(uint8_t port, uint8_t b0) {
    uint8_t cin  = (b0 & 0xF0 ) >> 4;
    uint8_t ph = ((( port - 1) & 0x0F) << 4)  | cin;
    return ph;
}

#define CIN_SYSEX_START 0x04
#define CIN_SYSEX_END_1 0x05
#define CIN_SYSEX_END_2 0x06
#define CIN_SYSEX_END_3 0x07

void usbh_MidiSendSysEx(uint8_t port, uint8_t bytes[], uint8_t len) {
#if 0 // TODO: needs re-implementation
	if (send_ring_buffer.write_ptr + 1 == 0) return;
    USBH_DbgLog("usbh_MidiSysEx %i",len);
    uint8_t next = send_ring_buffer.write_ptr;

    uint8_t cn = ((( port - 1) & 0x0F) << 4);
    uint8_t cin = CIN_SYSEX_START;
    uint8_t ph = cin | cn;
    int i = 0;
    for(i = 0; i< (len - 3); i += 3) {
        next = (next + 1) % RING_BUFFER_SIZE;
        // later do this up front... but read_ptr may be changing
        if(next == send_ring_buffer.read_ptr) {
            report_usbh_midi_ringbuffer_overflow();
            return;
        }

        USBH_DbgLog("usbh_MidiSysEx start %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    int res = len - i;

    // end the sysex message, also handles special cases 2/3 bytes
    next = (next + 1) % RING_BUFFER_SIZE;
    if(next == send_ring_buffer.read_ptr) {
        report_usbh_midi_ringbuffer_overflow();
        return;
    }

    if (res == 1) {
        cin = CIN_SYSEX_END_1;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 1 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = 0;
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 2) {
        cin = CIN_SYSEX_END_2;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 2 %i,%i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = 0;
    } else if (res == 3) {
        cin = CIN_SYSEX_END_3;
        ph = cin | cn;
        USBH_DbgLog("usbh_MidiSysEx end 3 %i, %i", next,i);
        send_ring_buffer.event[next].data[0] = ph;
        send_ring_buffer.event[next].data[1] = bytes[i];
        send_ring_buffer.event[next].data[2] = bytes[i + 1];
        send_ring_buffer.event[next].data[3] = bytes[i + 2];
    }

    send_ring_buffer.write_ptr=next;
#endif
}

void usbh_midi_dispatch(midi_message_t m, int32_t portmap[]) {
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

