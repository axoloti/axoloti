This branch migrates from Chibios 2.6.x to 16.1.x
This heavily impacts the usb device stack, moving from byte queues to the synchronous buffer API.

* firmware flashing in GUI is broken, need to flash via DFU!
* firmware CRC consistency fails, disable line 1360 in PatchModel.java to bypass verification
* a microcontroller reset or crash causes a flood of "Control transfer failed: -9/-4/-99" in the GUI messages rather disconnecting
* implement usb device midi
* fix LogTextMessage, without sleeping in between, current implementation will just discard everything but the last string
* fix flasher/sdcard mounter
* remove "chibios_migration.h" dependencies in firmware (in a separate commit)
  objects/patches probably still need it.
