## Roadmap

* doubleclick on param to reset to default value
* disconnect wires by "dragging out": DnD cursor change
* USB host for HID
* move some non-essential functionality out of firmware into objects (gpio pwm, filters,...)
* Store patch xml along binary executable on SDCard, so patches stored on sdcard can load into the patcher
* preferences in flash memory: device name, input/output gain
* Patch librarian: compile a directory of patches on PC, dump the binaries on target SDcard
* DSP: automatically insert saturation if range can exceed, avoid inserting saturation when there is no need. Split frac32 into frac32 saturated and frac32 unsaturated types
* DSP: reduce (right shift on output) (left shift on input)
* object creation arguments - javascript defined object (in addition to static objects). Useful for mix, sel, maybe static filter design...
* dependencies/requirements in objects. For instance :
    * if the firmware doesn't have fatfs: refuse objects that use fatfs.
    * compiler library linkage
    * managed references to sample files on sdcard
    * if the board does not have audio-input, refuse audio input objects
    * if the target CPU is not ARM Cortex-M4: refuse objects that use its intrinsics.               
* midi monitor, midi cc auto-assign
* multi-level undo
* cable datatype for MIDI queues?
* easier rescaling and controller mapping of control values
* Popup menus to select object variants
* object editor: layout, code editor with syntax coloring, show function prototypes...
* presentation mode?
* installers for OSX, Windows, Ubuntu, file extension associations...
* GCC/LD/OBJCOPY invoke with pipes or switch to libClang java bindings?                
* Other targets: PortAudio standalone, VST, AudioUnits, LV2...
