<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/gain" sha="60c5fcc3503670dd02f8989bba108d80be59b518" name="gain" x="84" y="14">
      <params>
         <frac32.u.map name="amp" onParent="true" value="7.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="hardclip" sha="875adf7bae4bfe9677038fce3b9a7d98e1a1dcc0" name="hardclip_1" x="182" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="level" x="238" y="14">
      <params>
         <frac32.u.map name="amp" onParent="true" value="14.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/eq4" sha="518b736fd39d7b359aad0043072946b6141c773b" name="eq" x="336" y="14">
      <params>
         <int32 name="lowmid" onParent="true" value="5"/>
         <int32 name="mid" onParent="true" value="-8"/>
         <int32 name="highmid" onParent="true" value="2"/>
         <int32 name="high" onParent="true" value="7"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="434" y="14">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="gain out"/>
         <dest name="hardclip_1 in"/>
      </net>
      <net>
         <source name="hardclip_1 outlet_1"/>
         <dest name="level in"/>
      </net>
      <net>
         <source name="level out"/>
         <dest name="eq in"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="gain in"/>
      </net>
      <net>
         <source name="eq out"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>