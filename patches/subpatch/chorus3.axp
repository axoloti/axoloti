<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/write" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="del" x="70" y="0">
      <params/>
      <attribs>
         <combo attributeName="size" selection="1024 (21.33ms)"/>
      </attribs>
   </obj>
   <obj type="ctrl/toggle" sha="a104f377191a424d537741cdfd7d5348bc16590c" name="on" x="532" y="14">
      <params>
         <bool32.tgl name="b" onParent="true" value="0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="depth" x="70" y="70">
      <params>
         <frac32.u.map name="value" onParent="true" value="7.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="speed" x="70" y="154">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-41.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="224" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="350" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="406" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread21" x="476" y="154">
      <params>
         <frac32.u.map name="time" value="1.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="del"/>
      </attribs>
   </obj>
   <obj type="mux/mux 2" sha="c6b90f8c9bc3d2f8632ce90fca7a738c7153eb2f" name="mux_1" x="630" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="L" x="700" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="168" y="224">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1_" x="224" y="224">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1_" x="350" y="224">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/interp" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_2" x="406" y="224">
      <params/>
      <attribs/>
   </obj>
   <obj type="delay/read interp" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread22" x="476" y="252">
      <params>
         <frac32.u.map name="time" value="1.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="del"/>
      </attribs>
   </obj>
   <obj type="mux/mux 2" sha="c6b90f8c9bc3d2f8632ce90fca7a738c7153eb2f" name="mux_2" x="630" y="266">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="R" x="700" y="266">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="in inlet"/>
         <dest name="del in"/>
         <dest name="mux_1 i1"/>
         <dest name="mux_2 i1"/>
      </net>
      <net>
         <source name="speed wave"/>
         <dest name="bipolar2unipolar1 i"/>
         <dest name="inv1 in"/>
      </net>
      <net>
         <source name="bipolar2unipolar1 o"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="inv1 out"/>
         <dest name="bipolar2unipolar1_ i"/>
      </net>
      <net>
         <source name="bipolar2unipolar1_ o"/>
         <dest name="vca_1_ a"/>
      </net>
      <net>
         <source name="depth out"/>
         <dest name="vca_1 b"/>
         <dest name="vca_1_ b"/>
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
         <source name="vca_1_ result"/>
         <dest name="interp_2 i"/>
      </net>
      <net>
         <source name="interp_2 o"/>
         <dest name="delread22 timem"/>
      </net>
      <net>
         <source name="mux_1 o"/>
         <dest name="L outlet"/>
      </net>
      <net>
         <source name="delread21 out"/>
         <dest name="mux_1 i2"/>
      </net>
      <net>
         <source name="delread22 out"/>
         <dest name="mux_2 i2"/>
      </net>
      <net>
         <source name="mux_2 o"/>
         <dest name="R outlet"/>
      </net>
      <net>
         <source name="on o"/>
         <dest name="mux_1 s"/>
         <dest name="mux_2 s"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>