<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="460" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="depth" x="40" y="60">
      <params>
         <frac32.u.map name="value" onParent="true" value="20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="fdbk" x="500" y="60">
      <params>
         <frac32.u.map name="value" onParent="true" value="40.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="speed" x="40" y="140">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-56.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="140" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="240" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="300" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="6fda3a4b04cc8fc49e63240c2fff115695ec7a7" name="delread21" x="360" y="140">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="mix/xfade" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="mix" x="520" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/write" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="dela" x="600" y="140">
      <params/>
      <attribs>
         <combo attributeName="size" selection="512 (10.66ms)"/>
      </attribs>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="620" y="220">
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
      </net>
      <net>
         <source obj="delread21" outlet="out"/>
         <dest obj="mix" inlet="i2"/>
      </net>
      <net>
         <source obj="mix" outlet="o"/>
         <dest obj="dela" inlet="in"/>
         <dest obj="out" inlet="outlet"/>
      </net>
      <net>
         <source obj="fdbk" outlet="out"/>
         <dest obj="mix" inlet="c"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>930</width>
      <height>530</height>
   </windowPos>
</patch-1.0>