<patch-1.0>
   <obj type="ctrl/toggle" sha="a104f377191a424d537741cdfd7d5348bc16590c" name="on" x="322" y="0">
      <params>
         <bool32.tgl name="b" onParent="true" value="0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="fdbk" x="126" y="28">
      <params>
         <frac32.u.map name="value" onParent="true" value="16.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="time" x="196" y="28">
      <params>
         <frac32.u.map name="value" onParent="true" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="42" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="mux/mux 2" sha="804f2e81d6fe3e310497e5cfa0fec8560435c2ac" name="mux_1" x="126" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/xfade" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="xfade_1" x="196" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="378" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/write sdram" sha="aa55d7ae111ced1fafde9a6f6386d746292dc8d1" uuid="5ae03f8d7b815edcfc40585d8bbac2ed48460fba" name="d1" x="154" y="210">
      <params/>
      <attribs>
         <combo attributeName="size" selection="131072 (2.37s)"/>
      </attribs>
   </obj>
   <obj type="delay/read" sha="5fca22dde504617cc3aec49fd5fcc1d7296290ca" uuid="739f69bf3dae8db57f1412d0d15cb37bbae3f4c" name="read_1" x="154" y="266">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="d1"/>
      </attribs>
   </obj>
   <nets>
      <net>
         <source obj="time" outlet="out"/>
         <dest obj="read_1" inlet="time"/>
      </net>
      <net>
         <source obj="xfade_1" outlet="o"/>
         <dest obj="out" inlet="outlet"/>
         <dest obj="d1" inlet="in"/>
      </net>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="xfade_1" inlet="i1"/>
      </net>
      <net>
         <source obj="on" outlet="o"/>
         <dest obj="mux_1" inlet="s"/>
      </net>
      <net>
         <source obj="mux_1" outlet="o"/>
         <dest obj="xfade_1" inlet="c"/>
      </net>
      <net>
         <source obj="fdbk" outlet="out"/>
         <dest obj="mux_1" inlet="i2"/>
      </net>
      <net>
         <source obj="read_1" outlet="out"/>
         <dest obj="xfade_1" inlet="i2"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>8</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>310</x>
      <y>300</y>
      <width>688</width>
      <height>506</height>
   </windowPos>
</patch-1.0>