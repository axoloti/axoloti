<patch-1.0>
   <obj type="inlet~" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="460" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="depth" x="40" y="60">
      <params>
         <frac32.u.map name="value" onParent="true" value="20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="fdbk" x="500" y="60">
      <params>
         <frac32.u.map name="value" onParent="true" value="40.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="speed" x="40" y="140">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-56.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar1" x="140" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="vca_1" x="240" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp_1" x="300" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delread2~~" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread21" x="360" y="140">
      <params>
         <frac32.u.map name="time" value="0.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="dela"/>
      </attribs>
   </obj>
   <obj type="xfade~" sha="7c7bb910e14c9ba3614f189bb924c86ca0a86890" name="mix" x="520" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="delwrite~" sha="c573b27a5ebc2efb2d1e8ec173ff4793a2acbae2" name="dela" x="600" y="140">
      <params/>
      <attribs>
         <combo attributeName="size" selection="512 (10.66ms)"/>
      </attribs>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="620" y="220">
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
      </net>
      <net>
         <source name="delread21 out"/>
         <dest name="mix i2"/>
      </net>
      <net>
         <source name="mix o"/>
         <dest name="dela in"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="fdbk out"/>
         <dest name="mix c"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>