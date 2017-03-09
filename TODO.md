This branch migrates from Chibios 2.6.x to 16.1.x
This heavily impacts the usb device stack, moving from byte queues to the synchronous buffer API.

* implement usb device midi
* remove "chibios_migration.h" dependencies in firmware (in a separate commit)
  objects/patches probably still need it.
* USB MSC sdcard mounter: does not restart into normal mode, when ejecting disk on host.
