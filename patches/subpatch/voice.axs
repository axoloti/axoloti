<patch-1.0>
   <obj type="osc~" sha="377a3586859b26132804d77a27e8734c2cf0fc69" name="osc1" x="360" y="20">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="480" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="26696a8af626bcd1d6434fe0b48564a3f33981af" name="randtrig1" x="40" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="inlet1" x="0" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="outlet_pass" x="280" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="inlet_pass" x="80" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c1" x="200" y="260">
      <params>
         <frac32.u.map name="value" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="outlet1" x="280" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="smooth2" sha="541a5712ecfe642fef80c9736fee487e3048600d" name="smooth21" x="120" y="20">
      <params>
         <frac32.u.map name="risetime" value="59.5"/>
         <frac32.u.map name="falltime" value="53.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="8041da25532c27ffaeaed170f3e9ca3b871804af" name="mix11" x="240" y="20">
      <params>
         <frac32.u.map name="gain1" value="10.5"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="osc1 wave"/>
         <dest name="outlet_1 outlet"/>
      </net>
      <net>
         <source name="inlet1 inlet"/>
         <dest name="randtrig1 trig"/>
      </net>
      <net>
         <source name="inlet_pass inlet"/>
         <dest name="outlet_pass outlet"/>
         <dest name="mix11 bus_in"/>
      </net>
      <net>
         <source name="c1 out"/>
         <dest name="outlet1 outlet"/>
      </net>
      <net>
         <source name="mix11 out"/>
         <dest name="osc1 pitchm"/>
      </net>
      <net>
         <source name="randtrig1 rand"/>
         <dest name="smooth21 in"/>
      </net>
      <net>
         <source name="smooth21 out"/>
         <dest name="mix11 in1"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>