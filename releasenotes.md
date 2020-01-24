# New in Axoloti release 2.0.0

* Multiple patches can now run simultaneously

* MIDI routing

  Patches do not refer any more to specific MIDI devices and ports, but to virtual MIDI ports.
  Physical MIDI in- and outputs are now routed to virtual ports via the "Midi Routing" dialog in the
  board menu. 
  Midi routing settings can be stored on sdcard.
  
* MIDI input monitor

  Shows the content of the MIDI input buffer. 

* MIDI Sysex support

* New objects `patch/bus_mix` and `patch/bus_rcv`

  allow to route audio across running patches

* Improved undo/redo

* Introducing modules

  Modules are dynamically loaded libraries, loaded only once when used in multiple patches.
  They require an sdcard, and are located in /lib/libraryname.elf
  Compiling and uploading libraries happens automagically when running a patch.

* Audio input/output level meters

* Improved USB protocol handling

  Axoloti Core boards upgraded to v2.0 firmware can't connect to Axoloti v1.x software. 
  If you need to go back to 1.x after upgrading you need to downgrade to 1.0.12 firmware via the 
  board menu in 2.x, or do a "rescue flash".

# Changes in general behaviour:

* Compiled patches now carry an .elf extension (instead of .bin)

* "controller object" configuration is gone

  Since multiple patches can run, one "master" patch can be used to load other patches, for example in
  response to program changes. It can also contain your effects section, midi indirections, or other
  "utilities" you want to keep running.

* Old compiled patches do not run anymore, sorry

* MIDI i/o on gpio does not work anymore

* When dsp load exceeds 98%, patches are muted

* No more dsp load indicator in patch window

  There is a global dsp load indicator in the main window instead.

* Menubars are inside windows on Mac OSX, no useful menubar on top of the screen.
 
  The trick that puts the menubar on top of the screen unfortunately causes some menus to stop functioning correctly.

* Patches used to run in with a directory carriying the patch' name as working directory

  This is no longer the case, it conflicts with the concept of multiple running patches.

* Axoloti Runtime is no longer used, but installing gcc-arm-none-eabi manually is required.

  An error message with download/installation instructions is shown at startup when missing.

* Space characters in the installation path are not allowed on Mac OSX and Linux

  An error message is shown...

# Yet incomplete:

* Several MI clone objects do not work anymore:
	fx/lmnts/lmnts
	fx/lmnts/string
	fx/lmnts/tube
	fx/clds/clds
	fx/clds/pitchshifter
	fx/rngs/chorus
	fx/rngs/ensemble
	fx/rngs/reverb
	fx/strms/strms
	fx/wrps/wrps
	
  Those need to be adapted to use modules...

* A preliminary port of tinysoundfont library to play soundfonts need further work


# Object code transitions:

* Static memory allocations to use sdram are discouraged.

  Use dynamic memory allocation instead, cfr. `ax_malloc` in `api/axoloti_memory.h`
	
* Object init code now has an integer return value (error code).

  Return value 0 means "everything OK". Predefined error codes are defined in `api/error_codes.h`
  Other return values will cause the patch to abort and report the error code.
  
  Return statements without an argument will cause a compiler error.
  
* MidiSend1(), MidiSend2(), MidiSend3() API migration

  `MidiSend1(dev,port,b0)`, `MidiSend2(dev,port,b0,b1)`, `MidiSend3(dev,port,b0,b1,b2)` had a separate device
  and port argument, now we only have a virtual port number.
  Those calls need to be migrated to `midiSend1(vport, b0)`, `midiSend2(vport,b0,b1)`,
  `midiSend3(vport,b0,b1,b2)`.
  Notice the lowercase m!

* Objects that change a GPIO pin mode, must restore it to analog input on dispose

  via `palSetPadMode(..., ..., PAL_MODE_INPUT_ANALOG);`
  Since multiple patches can run, gpio pin modes are no longer initialized by firmware when starting a patch, 
  as that could disrupt a running patch.
