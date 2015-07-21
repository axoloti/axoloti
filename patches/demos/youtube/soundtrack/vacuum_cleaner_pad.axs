<patch-1.0>
   <obj type="math/c 32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c321" x="126" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c321_" x="294" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc1" x="14" y="56">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix11" x="126" y="56">
      <params>
         <frac32.u.map name="gain1" value="14.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc1_" x="238" y="56">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="56.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix11_" x="336" y="56">
      <params>
         <frac32.u.map name="gain1" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="midi/in/bend" sha="84fc4df2ea385612e1294f33f4bfffbc8b501534" name="bendin1" x="28" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb zone" sha="44dada96531ef6abd5c77f60bb92dbb2ec0d6d35" name="keyb_1" x="14" y="196">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="0"/>
         <spinner attributeName="endNote" value="127"/>
      </attribs>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth1" x="126" y="196">
      <params>
         <frac32.u.map name="time" onParent="true" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="c4000e3e6417d9d57283d66476b83f22f975ff09" name="envd1" x="224" y="196">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="11.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="mix12" x="336" y="196">
      <params>
         <frac32.u.map name="gain1" value="14.5"/>
         <frac32.u.map name="gain2" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/pwm" sha="4f216b9a125822434f813198e9be4da0b5e8b042" name="pwm_1" x="434" y="196">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-12.185123443603516"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="mix21" x="658" y="252">
      <params>
         <frac32.u.map name="gain1" value="12.0"/>
         <frac32.u.map name="gain2" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth1_" x="756" y="252">
      <params>
         <frac32.u.map name="time" onParent="true" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="c4000e3e6417d9d57283d66476b83f22f975ff09" name="envahd1" x="546" y="280">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="45.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/pwm" sha="4f216b9a125822434f813198e9be4da0b5e8b042" name="pwm_1_" x="434" y="294">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-0.01004934310913086"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/pwm" sha="4f216b9a125822434f813198e9be4da0b5e8b042" name="pwm_1__" x="434" y="406">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-23.93801259994507"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 4" sha="217ea56f47dd7397f64930ffcddab7c794ad4f5c" name="mix31" x="546" y="406">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="12.0"/>
         <frac32.u.map name="gain2" onParent="true" value="12.0"/>
         <frac32.u.map name="gain3" onParent="true" value="12.0"/>
         <frac32.u.map name="gain4" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/lp m" sha="c2224dc682842eae1af4496f3f94a6afc1525ee4" name="lpf_1" x="644" y="420">
      <params>
         <frac32.s.map name="pitch" MidiCC="1" value="20.0"/>
         <frac32.u.map name="reso" onParent="true" value="19.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="756" y="420">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="826" y="420">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="keyb_1" outlet="note"/>
         <dest obj="smooth1" inlet="in"/>
         <dest obj="mix21" inlet="in1"/>
      </net>
      <net>
         <source obj="mix12" outlet="out"/>
         <dest obj="pwm_1" inlet="pitch"/>
         <dest obj="pwm_1_" inlet="pitch"/>
         <dest obj="pwm_1__" inlet="pitch"/>
      </net>
      <net>
         <source obj="pwm_1" outlet="wave"/>
         <dest obj="mix31" inlet="in1"/>
      </net>
      <net>
         <source obj="pwm_1_" outlet="wave"/>
         <dest obj="mix31" inlet="in2"/>
      </net>
      <net>
         <source obj="pwm_1__" outlet="wave"/>
         <dest obj="mix31" inlet="in3"/>
      </net>
      <net>
         <source obj="osc1" outlet="wave"/>
         <dest obj="mix11" inlet="in1"/>
      </net>
      <net>
         <source obj="c321" outlet="o"/>
         <dest obj="mix11" inlet="bus_in"/>
      </net>
      <net>
         <source obj="mix11" outlet="out"/>
         <dest obj="pwm_1_" inlet="pw"/>
         <dest obj="pwm_1__" inlet="pw"/>
      </net>
      <net>
         <source obj="smooth1" outlet="out"/>
         <dest obj="mix12" inlet="bus_in"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="gate"/>
         <dest obj="envahd1" inlet="gate"/>
         <dest obj="envd1" inlet="gate"/>
      </net>
      <net>
         <source obj="envahd1" outlet="env"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="c321_" outlet="o"/>
         <dest obj="mix11_" inlet="bus_in"/>
      </net>
      <net>
         <source obj="osc1_" outlet="wave"/>
         <dest obj="mix11_" inlet="in1"/>
      </net>
      <net>
         <source obj="mix11_" outlet="out"/>
         <dest obj="pwm_1" inlet="pw"/>
      </net>
      <net>
         <source obj="envd1" outlet="env"/>
         <dest obj="mix12" inlet="in1"/>
      </net>
      <net>
         <source obj="mix31" outlet="out"/>
         <dest obj="lpf_1" inlet="in"/>
      </net>
      <net>
         <source obj="lpf_1" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="bendin1" outlet="bend"/>
         <dest obj="mix12" inlet="in2"/>
      </net>
      <net>
         <source obj="mix21" outlet="out"/>
         <dest obj="smooth1_" inlet="in"/>
      </net>
      <net>
         <source obj="smooth1_" outlet="out"/>
         <dest obj="lpf_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="velocity"/>
         <dest obj="mix21" inlet="in2"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>1136</width>
      <height>730</height>
   </windowPos>
</patch-1.0>