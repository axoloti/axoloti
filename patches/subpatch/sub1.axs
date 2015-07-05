<patch-1.0>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="inlet_1" x="180" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="sat" sha="90497cb6406668eff34f70aea78bc62d638ee6fe" name="sat_1" x="240" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="outlet~~_1" x="320" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c_1" x="60" y="160">
      <params>
         <frac32.u.map name="value" onParent="true" value="13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca~_1" x="220" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet~_1" x="380" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet~" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="inlet~_1" x="80" y="280">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="inlet_1 inlet"/>
         <dest name="sat_1 in"/>
      </net>
      <net>
         <source name="sat_1 out"/>
         <dest name="outlet~~_1 outlet"/>
      </net>
      <net>
         <source name="c_1 out"/>
         <dest name="vca~_1 v"/>
      </net>
      <net>
         <source name="inlet~_1 inlet"/>
         <dest name="vca~_1 a"/>
      </net>
      <net>
         <source name="vca~_1 o"/>
         <dest name="outlet~_1 outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>