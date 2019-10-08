This branch migrates from Chibios 2.6.x to 18.x
This heavily impacts the usb device stack, moving from byte queues to the synchronous buffer API.

* Note: the MIDI sending API changed from
void MidiSend1(midi_device_t dev, int8_t port, uint8_t b0, uint8_t b1, uint8_t b2);
to 
void midiSend3(int port, uint8_t b0, uint8_t b1, uint8_t b2);
where port is an index of the virtual port. Midi port routing can be edited via the menu "Board->MIDI Routing"

* Note: the MIDI reception handler arguments changed:
"OMNI" port/device works as before as long as only port 1 is used 
of MIDI DIN, USB host and USB device, and if no input port mapping of any port 
has multiple targets. Otherwise duplicated midi messages will be processed. 
In the future "OMNI" port input should be avoided, that concept is broken when 
connecting a midi controller that uses all sorts of midi messages conflicting 
with "play". One virtual input port can be used to merge all regular midi ports 
for playing.

* TODO: test midi I/O extensively : help invited
* TODO: implement sysex rx/tx: help invited
* TODO: load/store midi routing config from sdcard: help invited
* remove "chibios_migration.h" dependencies in firmware (in a separate commit)
  objects/patches probably still need it.
* USB MSC sdcard mounter: does not restart into normal mode, when ejecting disk on host.
* TODO: midi clock: slaving, midi clock config in GUI

* Known broken: table of parent patch accessed in subpatch
