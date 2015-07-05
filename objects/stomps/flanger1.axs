<patch-1.0>
   <obj type="ctrl/toggle" sha="a104f377191a424d537741cdfd7d5348bc16590c" name="on" x="672" y="0">
      <params>
         <bool32.tgl name="b" onParent="true" value="0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="462" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="fdbk" x="462" y="56">
      <params>
         <frac32.u.map name="value" onParent="true" value="40.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="depth" x="14" y="70">
      <params>
         <frac32.u.map name="value" onParent="true" value="20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="speed" x="14" y="154">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-56.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="112" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="252" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="308" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread21" x="364" y="154">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="mix/xfade~" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="mix" x="532" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/write" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="dela" x="616" y="154">
      <params/>
      <attribs>
         <combo attributeName="size" selection="512 (10.66ms)"/>
      </attribs>
   </obj>
   <obj type="mux/mux 2" sha="c6b90f8c9bc3d2f8632ce90fca7a738c7153eb2f" name="mux_1" x="616" y="238">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="700" y="238">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="speed wave"/>
         <dest name="bipolar2unipolar1 i"/>
      </net>
      <net>
         <source name="bipolar2unipolar1 o"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="depth out"/>
         <dest name="vca_1 b"/>
      </net>
      <net>
         <source name="vca_1 result"/>
         <dest name="interp_1 i"/>
      </net>
      <net>
         <source name="interp_1 o"/>
         <dest name="delread21 timem"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="mix i1"/>
         <dest name="mux_1 i1"/>
      </net>
      <net>
         <source name="delread21 out"/>
         <dest name="mix i2"/>
      </net>
      <net>
         <source name="mix o"/>
         <dest name="dela in"/>
         <dest name="mux_1 i2"/>
      </net>
      <net>
         <source name="fdbk out"/>
         <dest name="mix c"/>
      </net>
      <net>
         <source name="mux_1 o"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="on o"/>
         <dest name="mux_1 s"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>4</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>2</NModulationSources>
      <NModulationTargetsPerSource>2</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>