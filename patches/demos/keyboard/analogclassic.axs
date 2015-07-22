<patch-1.0>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="lfo" x="180" y="20">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 16" sha="de53fa1ae8551aca115c5ab76dd646a20339668" name="div16_1" x="300" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb_1" x="40" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="lfop" x="180" y="120">
      <params>
         <frac32.u.map name="gain1" value="1.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="bendrange" x="280" y="120">
      <params>
         <frac32.u.map name="gain1" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/adsr" sha="2c4b16047d03b574d8a72b651f130895749eb670" name="enva" x="400" y="120">
      <params>
         <frac32.s.map name="a" onParent="true" value="-26.0"/>
         <frac32.s.map name="d" onParent="true" value="11.0"/>
         <frac32.u.map name="s" onParent="true" value="29.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/adsr" sha="2c4b16047d03b574d8a72b651f130895749eb670" name="envf" x="520" y="120">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
         <frac32.u.map name="s" onParent="true" value="0.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="ftrack" x="620" y="120">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="18.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="lfof" x="720" y="120">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="0.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="midi/in/bend" sha="84fc4df2ea385612e1294f33f4bfffbc8b501534" name="bendin_1" x="60" y="220">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="osc~_1" x="80" y="320">
      <params>
         <frac32.s.map name="pitch" value="-12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="f543e080bd2111cba525885443039f346703a594" name="sub" x="240" y="320">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="6.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca~_1" x="380" y="320">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="vcf3~_1" x="500" y="320">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="17.0"/>
         <frac32.u.map name="reso" onParent="true" value="61.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="700" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="saw~_1" x="80" y="440">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="bendrange" outlet="out"/>
         <dest obj="osc~_1" inlet="pitch"/>
         <dest obj="saw~_1" inlet="pitch"/>
         <dest obj="ftrack" inlet="in1"/>
      </net>
      <net>
         <source obj="osc~_1" outlet="wave"/>
         <dest obj="sub" inlet="in1"/>
      </net>
      <net>
         <source obj="saw~_1" outlet="wave"/>
         <dest obj="sub" inlet="bus_in"/>
      </net>
      <net>
         <source obj="sub" outlet="out"/>
         <dest obj="vca~_1" inlet="a"/>
      </net>
      <net>
         <source obj="enva" outlet="env"/>
         <dest obj="vca~_1" inlet="v"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="gate2"/>
         <dest obj="enva" inlet="gate"/>
      </net>
      <net>
         <source obj="envf" outlet="env"/>
         <dest obj="ftrack" inlet="bus_in"/>
      </net>
      <net>
         <source obj="vca~_1" outlet="o"/>
         <dest obj="vcf3~_1" inlet="in"/>
      </net>
      <net>
         <source obj="ftrack" outlet="out"/>
         <dest obj="lfof" inlet="bus_in"/>
      </net>
      <net>
         <source obj="lfo" outlet="wave"/>
         <dest obj="lfof" inlet="in1"/>
         <dest obj="div16_1" inlet="in"/>
      </net>
      <net>
         <source obj="lfof" outlet="out"/>
         <dest obj="vcf3~_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="note"/>
         <dest obj="lfop" inlet="bus_in"/>
      </net>
      <net>
         <source obj="bendin_1" outlet="bend"/>
         <dest obj="bendrange" inlet="in1"/>
      </net>
      <net>
         <source obj="lfop" outlet="out"/>
         <dest obj="bendrange" inlet="bus_in"/>
      </net>
      <net>
         <source obj="div16_1" outlet="out"/>
         <dest obj="lfop" inlet="in1"/>
      </net>
      <net>
         <source obj="vcf3~_1" outlet="out"/>
         <dest obj="out" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>1030</width>
      <height>750</height>
   </windowPos>
</patch-1.0>