#ifndef API_MIDI_LEGACY_H
#define API_MIDI_LEGACY_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>

// obsolete legacy Axoloti 1.x API wrapper, ignore port arg, using dev number as virtual port number,
// port argument is ignored

typedef int midi_device_t; // obsolete legacy api

#define MidiSend1(dev,port,b0)             midiSend1(dev,b0)
#define MidiSend2(dev,port,b0,b1)          midiSend2(dev,b0,b1,b2)
#define MidiSend3(dev,port,b0,b1,b2)       midiSend3(dev,b0,b1,b2)
#define MidiSendSysEx(dev,port,bytes,len)  midiSendSysEx(dev,bytes,len)
#define PatchMidiInHandler(dev,port,b0,b1,b2)   midiSend3(dev+8,b0,b1,b2)

typedef enum // obsolete
{
    MIDI_DEVICE_OMNI = -1,        // for filtering
    MIDI_DEVICE_DIN,             // MIDI_DIN
    MIDI_DEVICE_USB_DEVICE,      // Board acting as Midi device over MicroUSB
    MIDI_DEVICE_USB_HOST,        // Board hosting devices vid USB host port
    MIDI_DEVICE_DIGITAL_X1,      // x1 pins - not implemented
    MIDI_DEVICE_DIGITAL_X2,      // x2 pins - not implemented
    MIDI_DEVICE_INTERNAL = 0x10  // internal (to the board) midi
} obsolete_midi_device_t;

#ifdef __cplusplus
} // extern "C"
#endif

#endif // API_MIDI_LEGACY_H
