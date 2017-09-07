This branch migrates from Chibios 2.6.x to 16.1.x
This heavily impacts the usb device stack, moving from byte queues to the synchronous buffer API.

* Note: the MIDI function call API changed from
int8_t dev, int8_t port, ...
to 
int8_t unused, int8_t virtual_port, ...
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
