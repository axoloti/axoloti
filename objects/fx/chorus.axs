<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/write" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="dela" x="84" y="0">
      <params/>
      <attribs>
         <combo attributeName="size" selection="2048 (42.66ms)"/>
      </attribs>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="depth" x="84" y="56">
      <params>
         <frac32.u.map name="value" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="speed" x="84" y="140">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-64.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="266" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="406" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="476" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="6fda3a4b04cc8fc49e63240c2fff115695ec7a7" name="delread21" x="560" y="140">
      <params>
         <frac32.u.map name="time" value="1.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="L" x="728" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="196" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1_" x="266" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1_" x="406" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_2" x="476" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="6fda3a4b04cc8fc49e63240c2fff115695ec7a7" name="delread22" x="560" y="238">
      <params>
         <frac32.u.map name="time" value="1.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="R" x="728" y="238">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="dela" inlet="in"/>
      </net>
      <net>
         <source obj="delread21" outlet="out"/>
         <dest obj="L" inlet="outlet"/>
      </net>
      <net>
         <source obj="delread22" outlet="out"/>
         <dest obj="R" inlet="outlet"/>
      </net>
      <net>
         <source obj="speed" outlet="wave"/>
         <dest obj="bipolar2unipolar1" inlet="i"/>
         <dest obj="inv1" inlet="in"/>
      </net>
      <net>
         <source obj="bipolar2unipolar1" outlet="o"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="inv1" outlet="out"/>
         <dest obj="bipolar2unipolar1_" inlet="i"/>
      </net>
      <net>
         <source obj="bipolar2unipolar1_" outlet="o"/>
         <dest obj="vca_1_" inlet="a"/>
      </net>
      <net>
         <source obj="depth" outlet="out"/>
         <dest obj="vca_1" inlet="b"/>
         <dest obj="vca_1_" inlet="b"/>
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
         <source obj="vca_1_" outlet="result"/>
         <dest obj="interp_2" inlet="i"/>
      </net>
      <net>
         <source obj="interp_2" outlet="o"/>
         <dest obj="delread22" inlet="time"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Author>jt</Author>
      <License>viral!</License>
   </settings>
   <notes><![CDATA[A simple stereo chorus, based on two out-of-phase sine-wave modulated linear interpolating delayline readers.
]]></notes>
   <windowPos>
      <x>451</x>
      <y>302</y>
      <width>910</width>
      <height>550</height>
   </windowPos>
</patch-1.0>