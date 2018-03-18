This is derivative work from Mutable Instruments' Eurorack Modules
==================================================================

Modifications by Johannes Taelman to the Mutable Instruments' code
from https://github.com/pichenettes/eurorack/tree/6165129cd99416c762da3087594d9148e4c29222
and https://github.com/pichenettes/stmlib/tree/f8a4b46b7abb5d752a6a72932eeac1a03fe958fd
before initial commit:

* selected folders Braids, Elements, Rings, Stmlib, Warps, Clouds
* changed .cc file extension into .cpp
* removed "drivers", "hardware_design", "bootloader", "test" folders
* removed "linker_scripts", "midi", "programming", "system", "test", "third_party" from stmlib
* removed #include "warps/drivers/debug_pin.h" from warps/dsp/modulator.cpp
* removed #include "elements/drivers/debug_pin.h" from elements/dsp/resonator.cpp
* removed #include "clouds/drivers/debug_pin.h" from clouds/dsp/granular_processor.cpp 
* renamed xxx/resources.cpp -> xxx/xxx_resources.cpp (chibios makefile does digest overlapping object names)
* commented out the smp_sample_data in elements/elements_resources.cpp
* move duplicate lut_sine, lut_4_decades, lut_stiffness, lut_fm_frequency_quantizer
	from elements/elements_resources.cpp and rings/rings_resources.cpp 
	to mutable_resources.cpp
	Note that lut_sine in Elements is 360 degrees, while Rings uses 450 degrees, element 4096 changed from 0 to -2.449293598e-16
* define M_PI in stmlib/dsp/filter.h
* changed "#define xxx y" to "const int xxx=y;" in */resources.h 
* sample_data in elements/resources.h made non-const to allow dynamic loading
* sample_data initialised with new call loadElementsData in axoloti_mi.h/cpp

Original README
===============

* [Braids](http://mutable-instruments.net/modules/braids): Macro-oscillator.
* [Branches](http://mutable-instruments.net/modules/branches): Dual Bernoulli gate.
* [Clouds](http://mutable-instruments.net/modules/clouds): Texture synthesizer.
* [Edges](http://mutable-instruments.net/modules/edges): Quad chiptune digital oscillator.
* [Elements](http://mutable-instruments.net/modules/elements): Modal synthesizer.
* [Frames](http://mutable-instruments.net/modules/frames): Keyframer/mixer.
* [Grids](http://mutable-instruments.net/modules/grids): Topographic drum sequencer.
* [Links](http://mutable-instruments.net/modules/links): Utility module - buffer, mixer.
* [Peaks](http://mutable-instruments.net/modules/peaks): Dual trigger converter.
* [Rings](http://mutable-instruments.net/modules/rings): Resonator.
* [Ripples](http://mutable-instruments.net/modules/ripples): Liquid 2-pole BP, 2-pole LP and 4-pole LP filter.
* [Shades](http://mutable-instruments.net/modules/shades): Triple attenuverter.
* [Shelves](http://mutable-instruments.net/modules/shelves): EQ filter.
* [Streams](http://mutable-instruments.net/modules/streams): Dual dynamics gate.
* [Tides](http://mutable-instruments.net/modules/tides): Tidal modulator.
* [Volts](http://mutable-instruments.net/modules/volts): +5V power module.
* [Warps](http://mutable-instruments.net/modules/warps): Meta-modulator.
* [Yarns](http://mutable-instruments.net/modules/yarns): MIDI interface.

License
=======

Code (AVR projects): GPL3.0.

Code (STM32F projects): MIT license.

Hardware: cc-by-sa-3.0

By: Olivier Gillet (olivier@mutable-instruments.net)

Guidelines for derivative works
===============================

**Mutable Instruments is a registered trademark.**

The name "Mutable Instruments" should not be used on any of the derivative works you create from these files.

We do not recommend you to keep the original name of the Mutable Instruments module for your derivative works.

For example, your 5U adaptation of Mutable Instruments Clouds can be called "Foobar Modular - Particle Generator".
