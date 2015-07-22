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
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="speed" x="14" y="154">
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
   <obj type="delay/read interp" sha="6fda3a4b04cc8fc49e63240c2fff115695ec7a7" name="delread21" x="364" y="154">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="mix/xfade" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="mix" x="532" y="154">
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
         <source obj="speed" outlet="wave"/>
         <dest obj="bipolar2unipolar1" inlet="i"/>
      </net>
      <net>
         <source obj="bipolar2unipolar1" outlet="o"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="depth" outlet="out"/>
         <dest obj="vca_1" inlet="b"/>
      </net>
      <net>
         <source obj="vca_1" outlet="result"/>
         <dest obj="interp_1" inlet="i"/>
      </net>
      <net>
         <source obj="interp_1" outlet="o"/>
         <dest obj="delread21" inlet="time"/>
      </net>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="mix" inlet="i1"/>
         <dest obj="mux_1" inlet="i1"/>
      </net>
      <net>
         <source obj="delread21" outlet="out"/>
         <dest obj="mix" inlet="i2"/>
      </net>
      <net>
         <source obj="mix" outlet="o"/>
         <dest obj="dela" inlet="in"/>
         <dest obj="mux_1" inlet="i2"/>
      </net>
      <net>
         <source obj="fdbk" outlet="out"/>
         <dest obj="mix" inlet="c"/>
      </net>
      <net>
         <source obj="mux_1" outlet="o"/>
         <dest obj="out" inlet="outlet"/>
      </net>
      <net>
         <source obj="on" outlet="o"/>
         <dest obj="mux_1" inlet="s"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>4</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>2</NModulationSources>
      <NModulationTargetsPerSource>2</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>1010</width>
      <height>548</height>
   </windowPos>
   <helpPatch>flanger1.axh</helpPatch>
</patch-1.0>