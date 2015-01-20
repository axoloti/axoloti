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
   <obj type="mix/xfade~" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="xfade_1" x="196" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="wave/longdelay" sha="8660dbb4d820f8208ceb44c7101ba7760fb50d1d" name="longdelay_1" x="196" y="196">
      <params/>
      <attribs>
         <table attributeName="fn" table="test.raw"/>
      </attribs>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="378" y="196">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="time out"/>
         <dest name="longdelay_1 delay"/>
      </net>
      <net>
         <source name="longdelay_1 out"/>
         <dest name="xfade_1 i2"/>
      </net>
      <net>
         <source name="xfade_1 o"/>
         <dest name="longdelay_1 in"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="xfade_1 i1"/>
      </net>
      <net>
         <source name="on o"/>
         <dest name="mux_1 s"/>
      </net>
      <net>
         <source name="mux_1 o"/>
         <dest name="xfade_1 c"/>
      </net>
      <net>
         <source name="fdbk out"/>
         <dest name="mux_1 i2"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>8</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>