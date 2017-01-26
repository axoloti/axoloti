#Overview 
Axoloti consists of both hardware and software which work together to provide a virtual modular environment.
With the Axoloti software we can create 'patches' which are uploaded to the Axoloti hardware and then run on the hardware.

When we upload these patches to the Axoloti board, we are actually uploading native code for the hardware and not interpreting these patches on the board. 
This means it runs very efficiently, close to the efficiency you would get if you wrote the code specifically for the hardware!

Although this particular document concentrates on the Axoloti software, once the 'patch' is uploaded to the board, the Axoloti board can run completely independently, no computer or other controller is required.

You can find more information about the hardware in another post in this "User Guide" 

More topics from the user guide can be found in the [Axoloti User Guide Forum][1]


#Learning Axoloti
Axoloti is fairly simple to use, especially if you are used to graphical patching languages.
This topic covers the basic of how to use the computer side of axoloti 'the GUI', and an introduction into some of the aspects you will encounter.

After reading this, we would recommend you open each of the tutorial patches ( File -> Library-> Factory -> Tutorials)  to get an understanding of how to patch your own objects. From there, you can start looking at the factory demo which are more complex. 
This should give you a basic understanding of how to get started creating your own patches, from there you can look at community patches, and of course ask questions here on the forum.

Many users like to have a more 'tutorial book' approach, and we are lucky that such a book has been independently produced by a community member @janvantomme , which many users have commented is a great place to start - you can read more about this book, and feed back from users here [Getting started with Axoloti book](http://community.axoloti.com/t/getting-started-with-axoloti-the-book) . 
 

# Axoloti GUI
The main focus of the Axoloti GUI is to allow the user to connect to the Axoloti hardware, build patches and upload these patches to the hardware.
To do this there are two main windows, the console window and the patch window. 
There is always one console window, but you can have many patch windows open.

#Console Window
The console window shows information about the axoloti board that is connected, and various log messages.
Assuming your axoloti board is connected, the first thing you will want to do is to ensure it is connected.
(after the first time, it will automatically connect when you start Axoloti)

First, use the 'Board Menu' and select 'Select Device', and you will see one (or more) Axoloti boards that are available for connection (if not, check your USB cables). Select the device, and press ok.
Now you can use the 'connect' button on the Console window, it will print 'connected' in the log, as well as the current firmware version.
This console window also shows the current firmware revision on the board, this is covered in the "Firmware vs Patch" post in the user guide.

Note: see how connected was in RED, this shows an important log message, often errors that need the users attention.

We will be looking at the console window more, when we discuss transferring patches to the Axoloti board.

#Patch Window
This is where the fun starts, as this is where we create our synths, sequencers, or what ever else is in our imagination.
Lets start by looking at a patch to see what we can do....

Use the File menu, and you can see there are options to create a new patch, open patch... and also a Library menu, which contains tutorials and demos.
So lets start with a demo. 

##Loading a demo
in the console window's File menu, use:
File -> Library -> demos -> youtube ->tybett

This will open a patch with with the Tybett demo that has been shown on YouTube.
It might look complicated, but you will soon get accustomed to what is going on, and we are only here to see a few important things.

With this patch window we can see lots of objects all connected by wires, more on this later.
the most important thing about the patch window though is there are two modes.

Edit Mode - (light background) , allows you to edit the patch.
Live mode - (dark background) the patch is running on the Axoloti board, and you can control the parameters.

When you open a patch, you always start in Edit mode.

#Live Mode
Lets start with some sound!  (assuming you have the output jack connected to your speakers/mixer)

**Important Note** When you use a patch that you have not used before, or you have only just created **always** start with the gain on your headphones/mixer/speakers LOW and turn it up gradually. Different patches can have different volume levels and you don't want to damage your speakers/hearing!

ok, so to send this patch to the Axoloti board  - you can press the Live checkbox.
as this is a patch using a sequencer, you will instantly hear sound.

Importantly you will see the background has changed colour to dark grey, this clearly indicates the patch is Live, and running on the Axoloti board.

In Live mode, you can change the parameters that are on the screen.
have a go, in the top right, you will see a box labelled LFO/Square, this object is controlling the tempo of this patch, you can use the mouse to control the dial in the box,  by clicking on the dial with the mouse, and then moving up or down.  (This can also be controlled via MIDI, but more on that later!)

Also in Live mode you can use the preset recalls buttons at the top to change between different sets of saved presets, which are different groups of parameters values. (more later!)

Note: when in  LIVE mode, you cannot edit the patch in any way (including moving objects). (
as it is already loaded and running on the axoloti board) , to edit it you must leave LIVE mode.  
Also can only have one patch running on a particular board at a time.

Engaging live mode generates compatible code from the patch, compiles the code, uploads the binary and starts the binary on the board. In live mode, only parameters can be changed. Connections, execution order, location, attributes,... are all frozen. 
Currently midi mapping, and modulation can be adjusted in live mode, but those changes are only in effect until after engaging live mode again.


Leaving Live mode, easy... just click the Live checkbox again 
(you will see you can also press CTRL/CMD +E as a shortcut for entering/leaving Live mode)

#Edit Mode

Ok before we start editing lets get a little familiar with what a patch is...

The boxes are referred to as 'objects', and are like modules in a modular synth.
objects contain inlets to connect to, and outlets to connect to other objects by using  virtual wires or connections.

##Objects
Each objects has:

- titlebar, containing the kind of object
- instance name, the name you give it (a default is generated) Note: duplicate names in a patch are illegal.
- inlets: colored circles on the left side
- outlets colored squares on the right side
- attributes values that can only be set before running the patch.
- parameters values that can be set before loading the patch and also be modified during a "live" session. 

and some objects (called displays) also display various visualisations of live data from the patch.

Operations on objects:  (not in Live Mode):

- Objects can be moved by dragging the titlebar.
- Objects can be selected by clicking the titlebar, or dragging a rectange around a group.
- Selected objects can be deleted by pressing delete or backspace.
- Objects can be replaced with a different type by double-clicking on the title (or selecting replace in the context menu). Connections and parameters will be preserved. Attributes are not preserved (yet), this is especially useful when you wish to change inlet/outlet types (e.g. control rate to audio rate)
- Instance name can be changed (a double click on the instancename brings up the instance name editor)
- Attributes can be changed
- Parameters can be changed by:
 - mouse for units, + shift for sub-units, +shift+ctrl for fine units (e.g 0.5,0.05,0.01)
 - arrow-up/down. shift-arrow-up/down
 - page-up/down
 - home, end
 - typing the number followed by enter

**Parameters,  Attributes and Displays**
An object can have 3 types of user interface elments
Parameters - which can be changed at run time.  i.e. when Live  (and can also be modulated or controlled via midi CC)
Attributes - can only be configured whilst editing the patch.
Displays - show data coming from the axoloti board e.g. an oscilloscope (scope)

Attributes are used where there would be too great an overhead to change them at run-time, which could potentially disrupt the audio e.g. increasing a delay line's size at run time could cause audio glitches.
(the most common cases are for configuring buffer sizes/delay lines or configuring which tables/delays things are read from ... often they are text fields or drop down boxes - If you cant change it at run-time, its an attribute :) )

Some parameters have real-world units, displayed left of the dial. For some, multiple conversions are meaningful. Clicking on the real-world unit to alternate between different units. Eg. frequency in Hertz or period time in milliseconds.
Parameter can be mapped to MIDI Continuous Controllers, by right clicking on the parameter to assign a MIDI controller. Mapped parameters have a "C" mark right of the dial.
Parameters can be modulated by other objects, right click on the parameter and select modulate, Modulated parameters have a "M" marked to right of the dial

#Connections

##Connecting wires
Just drag an inlet to outlet (or vice versa)
An outlet can connect to many inlets BUT an inlet can only be connected to one outlet.
If you wish to connect 2 outlets to an inlet you will need to mix/sum them.

##Disconnect a wire
- select it and press delete 
- right click on a inlet/outlet and select disconnect
- drag the connected inlet/outlet into space

##Changing inlet source
if you want to change the outlet an inlet is connected to, simply drag the new outlet to the inlet, and the connection will be replaced

##Connection network
a connection network is all of the wires that are connected to a particular outlet.
you can delete them all at once with delete network

## Connection (and Inlet/Outlet types)
Different data types are marked by different colors on the outlets, inlets and wires.

- Red connections are s-rate ( audio/sample rate - 48000 Hz). The normal range is -64 to 64 units.
- Blue connection points are k-rate (control-rate, 3000 Hz) fractional numbers. The normal range is -64 to 64 units, like control voltages on a modular synthesizer.
- Yellow connections are for k-rate booleans, like gate signals one a  modular synthesizer.
- Green connections are for k-rate integers (whole numbers). The range is a signed 32bit , e.g. -2147483648 to 2147483647. 
- Pink connections are for strings. Mostly useful for dynamic filenames.

##Connections between different types

- A red output (audio) can be connected to a blue input (float), this will sample the audio, 1 in 16 audio samples.
- A yellow output (boolean) can be connected to a blue input (float), this yields +64 units for true, 0 for false.
- A yellow output (boolean) can be connected to a green input (float), this yields 1 for true, 0 for false.
- A green output (integer) can be connected to a blue input(float).
- A green output (integer) can be connected to a yellow input (boolean), evaluates to true when the value is positive, or to false when zero or negative.
- A blue output (float) can be connected to a green input (integer) the value is rounded down.
- A blue output (float) can be connected to a yellow input (boolean), evaluates to true when the value is positive, or to false when zero or negative.
- A pink output must always be connected to a pink input. (strings)

#Execution order
Every object in the patch is executed once in the signal processing loop, at 3000Hz. 
These are processed in strict order, left to right, top to bottom.
(Feedback is allowed, and will be processed in the next processing loop)

#Documenting patches
Use comment objects and the patch notes (accessible from the menu) so you don't forget how your patch works, also changing the instance name (menu rename) helps when you have many objects of the same type

#Saving a patch
Once you have created a patch, you can then save it using the File menu, and choose Save or Save As. 

##Presets
A preset is a set of selected parameters and their new value. To include a parameter to a preset, select the preset index to edit in the toolbar. Then right-click on a parameter and select "include in current preset" in the popup-menu. The parameter will turn yellow. A yellow parameter is not updated live, but indicates that you are adjusting its value in the preset. Changes to presets are only updated after dis- and re-engaging the live checkbox! Presets in a sub-patch can be applied only with the "preset" object. A preset in a "normal" sub-patch only affects the sub-patch. A preset in a polyphonic sub-patch only affects one voice.

#Sub patching

Sub patches are an important building block in Axoloti.

There are lots of uses for sub patches but the main reasons are:

- creating 'utility' patches that you want to use in many patches
- to simplify a very complex patch
- for polyphonic voices 

Terminology, sometimes sub patches will be referred to as 'child patches' and the main patch is called the parent patch.

Sub patches can be create in two different ways, either embedded into the patch (i.e. saved in the AXP) or as a separate file (AXS) . Functionally they operate the same, the difference is embedded patch does not need to be saved separately, but cannot then be re-used on other patches. 
Most often embedded patches are used, especially during 'development' of a patch, and the subpatch files (AXS) are create if you wish to use the same functionality in other patches.
(note: you can copy and paste embedded patches, like other objects , but of course this means any change you want to make has to be made to individual copies)


##Embedded sub patches
To create an embedded sub-patch 

- create a new object of type 'patch/patcher'
- click the edit object, this will open a new patch window for you to add contents
- you can add inlet/outlet objects to communicate with main patch
- you can edit the patch settings, e.g. to create multiple voices
- you can add parameters to parent
- once you have finished editing, close the window **AND click update** on the patch/patcher

tip: remember you can rename the patch/patcher object to a more meaningful name.

using embedded patchers makes creating voices trivial, and keeps all of the patch in one file, which means its easy to share. you can even cut and paste embedded patchers to other patches to re-use them.

unless you have a particular reason to use sub-patch files, e.g. sharing in a library, you should use embedded patches.

##Sub patch files (AXS)

Sub patches are just like main patches, but are saved with as 'Axoloti Subpatch' with an extension of AXS, the difference is they are never used on their own... they are always added to a main patch. 
(we will also see later that sub-patches can often look like normal axoloti objects)

a few important notes:

- to use a subpatch it must be saved (to disk) before you can included it in a main patch.
- to edit a sub-patch used in a main patch, always use select in the main patch, and from the context menu select 'edit object defintition'

to create a sub-patch:

- Create a new patch, (this will be the sub patch)
- include inlet, and outlet objects allow data/audio to be passed to the main patch
- Save this patch (as an Axoloti Subpatch,  into the directory you are going to save the main patch) 
- Create a new patch (this will be the parent)
- Save this patch (as an Axoloti Patch, into the same directory as the sub patch above)
- Bring up the object search window ( space/N)
- Enter the patch filename in the object selector (without .axs extension), prefixed by "./"

If you want to modify the sub patch:

- In the main patch, select the sub patch object
- select "edit object definition" in the object popup menu
- the sub patch window opens
- make the changes
- save the sub-patch.

Note: If the main patch is LIVE, changes to a sub-patch will not be propagated until the main patch is sent again to the board (e.g. take it offline, then select live again)

Parameters can be propagated to the main patch by right-click on the parameter and select "show on parent". "Show on parent" parameters are drawn in blue.

Sub-patch files (AXS) are useful were you wish to create a generic object that you can use in many different patches, with the advantage that if you update the AXS all patches using it will use the new implementation.
(this 'advantage' can be considered a disadvantage if you want consistency in old patches... in which case you may prefer embedded patches or will need to version the AXS) 

#Polyphonic Sub patching
An important use of Sub patches is to create polyphonic voices.
If you place an oscillator in a patch, then you have one oscillator (with one pitch), what we need for polyphony is to have many copies of that oscillator, one for each voice.
The way we achieve this is to create a sub patch, the sub patch is then used for each voice.
When we add the sub-patch to the main patch we can say how many voices are created.
Now when midi notes are played Axoloti will automatically allocate notes played simultaneously to different voices. 

Note: you can change some properties of how voices are allocated in the patch settings of the sub-patch, see "more on sub patching for details"

For sound design purposes, you can also obtain the index of the voice with the "voiceindex" object... useful to make voices have some variation.

#Patch Settings
With every patch you can store notes (View->notes) and also change the patches settings.
Patch settiings include:

- Author, who wrote the patch
- Licence, the license for using/sharing the patch
- Midi Channel, if you are using midi objects what channel they receive data on  (affected by patch mode)
- Number of presets - how many presets can be store on the patch
- Entries per presets - the number of parameters that can be store on the preset
- Number of modulation source - number of modulation source on the patch (patch/modsource*)
- Number of modulation targets - number of targets for sources
- Sub patch mode, how voices are handled, see 'more on sub patches'
- Has midi channel attribute - the midi channel is exposed on the parent patch, when used as a subpatch
- saturate audio - is the the audio output from this patch saturated

#Zombies
If a patch is loaded and an object cannot be found, a zombie will be created (its bright red!) , your patch will not work, so you need to replace it... simply double click, or use replace object. if you replace with something with compatible inlet/outlets it will remain connected!
(most likely to happen if you create your own subpatches and move them)

#File types
Axoloti has 4 file types:
AXO  - objects with functionality, found in search window 
AXP  - patch, which contains objects and can be compiled and sent to Axoloti board
AXS  - subpatch, a patch used by a main patch (see below)
AXH - help patch, shows how to use an object.
all except AXO, can be created by saving the patch with Save As...

#Object and Sub-patch libraries
By default axoloti will look in the objects sub-directory for objects files and sub-patches.
(if you start with ./ (as suggested in sub-patch section) this will also look in the same directory as the patch)

If you develop your own sub-patches that you wish to see as objects, or your own custom objects (axo) or third party objects, then this is possible, simply add them into a Library. 
if you want them privately then you can add then to the 'home' library,  or you can share them with the community by placing in the community library.

objects all have a unique id, that is allocated by the object editor.
however, currently (subject to change!) subpatches are uniquely identified by their name.
e.g midi/in/keyb
so what happens if you have multiple sub-patches with the same name?
axoloti will search using the following rules

- if it full path is given it will use this
- if a relative path is given , it will use this relative to the location the patch is saved (so you need to save your parent patch first) 
- the order of libraries, listed in the preferences dialog

Our recommendation is to use embedded patches and embedded objects as much as possible, or to place sub-patches/objects in library.

#Custom objects
Creating custom objects is beyond the scope of this user guide.
however, a few notes are useful...
a) axoloti features an object editor which can be used to create your own objects
b) you can either embed custom objects or save objects in the a share library (as an axo file) 
generally custom objects are useful where a graphical UI becomes cumbersome, i.e. its simpler to right an algorithm in lines of code, or where some structure are not available in patch e.g. looping.

will custom objects be more efficient? this depends, only if you can express your 'intent' in a simpler/more concise efficeint way. custom objects  are **not** par se quicker (as both custom objects and patching both generate C code, which is subject to the optimiser). a badly written custom object is likely to perform worst/have more unwanted side effects than a patch!

if you want to create custom objects , axoloti provides all the tools 'out of the box', 
but you will need a few additional skills, depending on what you want to achieve.
- some programming experience
- some C coding experience (not too much , just the basics)*
- some DSP knowledge if you want to do audio
- some understanding of axoloti (id recommend deep patching experience , before object coding!)
 
*personally, Id recommend you do some C programming on the desktop first, as you lack things like debuggers in axoloti, and even getting trace output is more cumbersome.


  [1]: http://community.axoloti.com/c/user-guide