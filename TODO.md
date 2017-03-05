This branch migrates from Chibios 2.6.x to 16.1.x
This heavily impacts the usb device stack, moving from byte queues to the synchronous buffer API.

* a microcontroller reset or crash causes a flood of "Control transfer failed: -9/-4/-99" in the GUI messages rather disconnecting
* implement usb device midi
* fix LogTextMessage, without sleeping in between, current implementation will just discard everything but the last string
* remove "chibios_migration.h" dependencies in firmware (in a separate commit)
  objects/patches probably still need it.
* USB MSC sdcard mounter: does not restart into normal mode, when ejecting disk on host.
