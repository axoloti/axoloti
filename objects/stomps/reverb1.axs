<patch-1.0>
   <obj type="ctrl/toggle" sha="a104f377191a424d537741cdfd7d5348bc16590c" name="on" x="308" y="14">
      <params>
         <bool32.tgl name="b" onParent="true" value="0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="inL" x="28" y="24">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="inR" x="28" y="84">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+_1" x="126" y="84">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 2" sha="5df68ad33aa1633cb7cb1724fcd41eee28932582" name="div_1" x="210" y="84">
      <params/>
      <attribs/>
   </obj>
   <obj type="mux/mux 2" sha="c6b90f8c9bc3d2f8632ce90fca7a738c7153eb2f" name="mux_1" x="308" y="84">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="amt" x="434" y="84">
      <params>
         <frac32.u.map name="amp" onParent="true" value="17.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="delay/write" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="d1" x="546" y="84">
      <params/>
      <attribs>
         <combo attributeName="size" selection="2048 (42.66ms)"/>
      </attribs>
   </obj>
   <obj type="delay/read" sha="5fca22dde504617cc3aec49fd5fcc1d7296290ca" name="read_1" x="140" y="224">
      <params>
         <frac32.u.map name="time" value="14.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="d1"/>
      </attribs>
   </obj>
   <obj type="delay/read" sha="5fca22dde504617cc3aec49fd5fcc1d7296290ca" name="read_2" x="140" y="322">
      <params>
         <frac32.u.map name="time" value="21.5"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="d1"/>
      </attribs>
   </obj>
   <obj type="math/inv" sha="dd3d98b9ec6f2b9231cb1d00d0f9667152537120" name="inv_1" x="294" y="322">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read" sha="5fca22dde504617cc3aec49fd5fcc1d7296290ca" name="read_3" x="140" y="420">
      <params>
         <frac32.u.map name="time" value="31.5"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="d1"/>
      </attribs>
   </obj>
   <obj type="mix/mix 3 g" sha="358c7ade2f67c53110afef97162e206e54c7edbb" name="mix_1" x="364" y="420">
      <params>
         <frac32.u.map name="gain1" value="34.5"/>
         <frac32.u.map name="gain2" value="33.0"/>
         <frac32.u.map name="gain3" value="16.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="reverb/fdn4" sha="13d669964bda5b57297a930109b40a6bc2f548e4" name="decay" x="574" y="420">
      <params>
         <frac32.u.map name="g" onParent="true" value="46.0"/>
      </params>
      <attribs>
         <spinner attributeName="d1" value="397"/>
         <spinner attributeName="d2" value="523"/>
         <spinner attributeName="d3" value="859"/>
         <spinner attributeName="d4" value="1289"/>
      </attribs>
   </obj>
   <obj type="math/+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+_2" x="700" y="420">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outL" x="812" y="462">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+_3" x="700" y="476">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outR" x="812" y="504">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="inL" outlet="inlet"/>
         <dest obj="+_1" inlet="in1"/>
         <dest obj="+_2" inlet="in1"/>
      </net>
      <net>
         <source obj="inR" outlet="inlet"/>
         <dest obj="+_1" inlet="in2"/>
         <dest obj="+_3" inlet="in2"/>
      </net>
      <net>
         <source obj="+_1" outlet="out"/>
         <dest obj="div_1" inlet="in"/>
      </net>
      <net>
         <source obj="amt" outlet="out"/>
         <dest obj="d1" inlet="in"/>
         <dest obj="mix_1" inlet="bus_in"/>
      </net>
      <net>
         <source obj="read_1" outlet="out"/>
         <dest obj="mix_1" inlet="in1"/>
      </net>
      <net>
         <source obj="read_3" outlet="out"/>
         <dest obj="mix_1" inlet="in3"/>
      </net>
      <net>
         <source obj="read_2" outlet="out"/>
         <dest obj="inv_1" inlet="in"/>
      </net>
      <net>
         <source obj="inv_1" outlet="out"/>
         <dest obj="mix_1" inlet="in2"/>
      </net>
      <net>
         <source obj="mix_1" outlet="out"/>
         <dest obj="decay" inlet="in1"/>
      </net>
      <net>
         <source obj="decay" outlet="out4"/>
         <dest obj="+_2" inlet="in2"/>
      </net>
      <net>
         <source obj="decay" outlet="out3"/>
         <dest obj="+_3" inlet="in1"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="mux_1" inlet="i2"/>
      </net>
      <net>
         <source obj="mux_1" outlet="o"/>
         <dest obj="amt" inlet="in"/>
      </net>
      <net>
         <source obj="on" outlet="o"/>
         <dest obj="mux_1" inlet="s"/>
      </net>
      <net>
         <source obj="+_2" outlet="out"/>
         <dest obj="outL" inlet="outlet"/>
      </net>
      <net>
         <source obj="+_3" outlet="out"/>
         <dest obj="outR" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>1</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>141</x>
      <y>0</y>
      <width>1122</width>
      <height>814</height>
   </windowPos>
</patch-1.0>