<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="14" y="42">
      <params/>
      <attribs/>
   </obj>
   <obj type="dist/inf" sha="3b7380de881bb6eafc05c60b35ae2351855d6c09" name="inf_1" x="98" y="42">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/eq4" sha="518b736fd39d7b359aad0043072946b6141c773b" name="eq" x="168" y="42">
      <params>
         <int32 name="lowmid" onParent="true" value="0"/>
         <int32 name="mid" onParent="true" value="-6"/>
         <int32 name="highmid" onParent="true" value="0"/>
         <int32 name="high" onParent="true" value="5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="level" x="280" y="42">
      <params>
         <frac32.u.map name="amp" onParent="true" value="5.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="392" y="42">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="inf_1 out"/>
         <dest name="eq in"/>
      </net>
      <net>
         <source name="eq out"/>
         <dest name="level in"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="inf_1 in"/>
      </net>
      <net>
         <source name="level out"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>