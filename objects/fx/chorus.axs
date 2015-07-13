<patch-1.0>
   <obj type="inlet~" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="delwrite~" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="dela" x="40" y="0">
      <params/>
      <attribs>
         <combo attributeName="size" selection="2048 (42.66ms)"/>
      </attribs>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="depth" x="0" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="speed" x="0" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc_1" x="40" y="140">
      <params>
         <frac32.s.map name="pitch" value="-64.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="200" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="300" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="360" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delread2~~" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread21" x="420" y="140">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="L" x="600" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="140" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1_" x="200" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1_" x="300" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_2" x="360" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="delread2~~" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread22" x="420" y="240">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="R" x="600" y="240">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="in inlet"/>
         <dest name="dela in"/>
      </net>
      <net>
         <source name="delread21 out"/>
         <dest name="L outlet"/>
      </net>
      <net>
         <source name="delread22 out"/>
         <dest name="R outlet"/>
      </net>
      <net>
         <source name="osc_1 wave"/>
         <dest name="bipolar2unipolar1 i"/>
         <dest name="inv1 in"/>
      </net>
      <net>
         <source name="speed inlet"/>
         <dest name="osc_1 pitchm"/>
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
         <source name="depth inlet"/>
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
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>