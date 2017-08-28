#include "midi.h"
#include "midi_routing.h"
#include "midi_buffer.h"
#include "midi_usbh.h"


midi_output_buffer_t midi_output_usbh;

// map 16 usb host ports of usbh-midi device #1 to 16 virtual ports
midi_input_remap_t midi_inputmap_usbh1 = {
			.name = "not connected",
			.nports = 0,
			.portmap = {
			{0, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{1, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE}}
};

// map 16 usb host ports of usbh-midi device #2 to 16 virtual ports
midi_input_remap_t midi_inputmap_usbh2 = {
			.name = "not connected",
			.nports = 0,
			.portmap = {
			{2, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{3, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE},
			{MIDI_DEVICE_USB_HOST, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE, MIDI_DEVICE_INPUTMAP_NONE}}
};

midi_input_remap_t * midi_inputmap_usbh[2] = {
		&midi_inputmap_usbh1,
		&midi_inputmap_usbh2
};

#if 0
USB_Setup_TypeDef MIDI_Setup;
#endif

#define USBH_DbgLog(x)

static void notify(void * obj) {
	// TODO: we're currently polling for USBH transmission
	chSysHalt("usbh notify");
}

void usbh_midi_init(void)
{
  midi_output_buffer_objinit(&midi_output_usbh, notify);
  midi_output_buffer_deinit(&midi_output_usbh);
}

// pack header CN | CIN
inline uint8_t USBMidi_calcPH(uint8_t port, uint8_t b0) {
    uint8_t cin  = (b0 & 0xF0 ) >> 4;
    uint8_t ph = ((( port - 1) & 0x0F) << 4)  | cin;
    return ph;
}


void usbh_MidiSend1(uint8_t port, uint8_t b0) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = 0;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend1");
}

void usbh_MidiSend2(uint8_t port, uint8_t b0, uint8_t b1) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend2");
}

void usbh_MidiSend3(uint8_t port, uint8_t b0, uint8_t b1, uint8_t b2) {
	midi_message_t m;
	m.bytes.b0 = b0;
	m.bytes.b1 = b1;
	m.bytes.b2 = 0;
	m.bytes.ph = USBMidi_calcPH(port, b0);
	midi_output_buffer_put(&midi_output_usbh,m);
    USBH_DbgLog("usbh_MidiSend3");
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

int  usbh_MidiGetOutputBufferPending(void) {
	return midi_output_buffer_getpending(&midi_output_usbh);
}

int  usbh_MidiGetOutputBufferAvailable(void) {
	return midi_output_buffer_get_available(&midi_output_usbh);
}

void usbh_midi_dispatch(midi_message_t m, int8_t portmap[][MIDI_INPUT_REMAP_ENTRIES]) {
	int8_t *inputmap = portmap[m.fields.port];
	int i=0;
	for (i=0;i<MIDI_INPUT_REMAP_ENTRIES;i++) {
		int virtual_port = *inputmap;
		if (virtual_port == MIDI_DEVICE_INPUTMAP_NONE) break;
		m.fields.port = virtual_port;
		midi_input_buffer_put(&midi_input_buffer, m);
		inputmap++;
	}
}

