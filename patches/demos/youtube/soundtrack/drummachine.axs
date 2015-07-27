<patch-1.0>
   <obj type="midi/in/keyb note" sha="5d623ad73ade2280421069a67ee3f30595794c72" name="keybnote1" x="14" y="14">
      <params/>
      <attribs>
         <spinner attributeName="note" value="60"/>
      </attribs>
   </obj>
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="d_1" x="210" y="14">
      <params>
         <frac32.s.map name="d" onParent="true" value="-20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="noise.uniform_2" x="322" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/abs" sha="d9aef12fddc085a0a8a7a9868f1c845c538a9209" name="arithmetic.abs_1" x="420" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="290a234e2f83eb072198d0158bcd5da02a3606c3" name="filter.lowpass~_1" x="504" y="14">
      <params>
         <frac32.s.map name="freq" onParent="true" value="-29.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m x" sha="7cd630c1ecdc64542bf24aadc0f3114629fdf37d" name="env.envdlinmx_1" x="210" y="98">
      <params>
         <frac32.s.map name="d" onParent="true" value="-13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="arithmetic.*c_2" x="308" y="98">
      <params>
         <frac32.u.map name="amp" onParent="true" value="24.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="osc.sine_1" x="406" y="98">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-30.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="arithmetic.*_1" x="504" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="c47ceb7366785e0103cf880ce3450321491949f1" name="arithmetic.*_2" x="504" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="bd" x="588" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 64" sha="69b493f3d94607d38df5ab951d27622bec162349" name="c_1" x="126" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="noise.uniform_1" x="308" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb note" sha="5d623ad73ade2280421069a67ee3f30595794c72" name="keybnote1_" x="14" y="322">
      <params/>
      <attribs>
         <spinner attributeName="note" value="62"/>
      </attribs>
   </obj>
   <obj type="math/-" sha="27008b61438fd41bbc9a021b13c5eaad1cc101b5" name="-_1" x="126" y="322">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 2" sha="7fee48a2d38604fd5504303cbccef61f687d1593" name="div_1" x="210" y="336">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d m" sha="3f6e6c6081782177f0dc9dfe9e50a99b54fe41f6" name="env.decay.m_1" x="294" y="336">
      <params>
         <frac32.s.map name="d" onParent="true" value="11.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="gain.vca~_1" x="406" y="336">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/bp svf" sha="ba4ac02f48ae93c7ecc9fac9b0e212cac7c21539" name="filter.bpfsvf~_1" x="490" y="336">
      <params>
         <frac32.s.map name="pitch" value="55.0"/>
         <frac32.u.map name="reso" value="23.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="snr" x="602" y="336">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="noise.uniform_2" outlet="wave"/>
         <dest obj="arithmetic.abs_1" inlet="in"/>
      </net>
      <net>
         <source obj="env.envdlinmx_1" outlet="env"/>
         <dest obj="arithmetic.*_2" inlet="b"/>
      </net>
      <net>
         <source obj="osc.sine_1" outlet="wave"/>
         <dest obj="arithmetic.*_1" inlet="b"/>
      </net>
      <net>
         <source obj="arithmetic.*_1" outlet="result"/>
         <dest obj="arithmetic.*_2" inlet="a"/>
      </net>
      <net>
         <source obj="arithmetic.*c_2" outlet="out"/>
         <dest obj="osc.sine_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="arithmetic.abs_1" outlet="out"/>
         <dest obj="filter.lowpass~_1" inlet="in"/>
      </net>
      <net>
         <source obj="filter.lowpass~_1" outlet="out"/>
         <dest obj="arithmetic.*_1" inlet="a"/>
      </net>
      <net>
         <source obj="keybnote1" outlet="gate"/>
         <dest obj="d_1" inlet="trig"/>
         <dest obj="env.envdlinmx_1" inlet="trig"/>
      </net>
      <net>
         <source obj="d_1" outlet="env"/>
         <dest obj="arithmetic.*c_2" inlet="in"/>
      </net>
      <net>
         <source obj="keybnote1" outlet="velocity"/>
         <dest obj="env.envdlinmx_1" inlet="d"/>
      </net>
      <net>
         <source obj="env.decay.m_1" outlet="env"/>
         <dest obj="gain.vca~_1" inlet="v"/>
      </net>
      <net>
         <source obj="noise.uniform_1" outlet="wave"/>
         <dest obj="gain.vca~_1" inlet="a"/>
      </net>
      <net>
         <source obj="gain.vca~_1" outlet="o"/>
         <dest obj="filter.bpfsvf~_1" inlet="in"/>
      </net>
      <net>
         <source obj="keybnote1_" outlet="gate"/>
         <dest obj="env.decay.m_1" inlet="trig"/>
      </net>
      <net>
         <source obj="keybnote1_" outlet="velocity"/>
         <dest obj="-_1" inlet="in2"/>
      </net>
      <net>
         <source obj="c_1" outlet="o"/>
         <dest obj="-_1" inlet="in1"/>
      </net>
      <net>
         <source obj="-_1" outlet="out"/>
         <dest obj="div_1" inlet="in"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="env.decay.m_1" inlet="d"/>
      </net>
      <net>
         <source obj="arithmetic.*_2" outlet="result"/>
         <dest obj="bd" inlet="outlet"/>
      </net>
      <net>
         <source obj="filter.bpfsvf~_1" outlet="out"/>
         <dest obj="snr" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
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
      <y>2</y>
      <width>912</width>
      <height>646</height>
   </windowPos>
</patch-1.0>