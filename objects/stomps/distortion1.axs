<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/gain" sha="60c5fcc3503670dd02f8989bba108d80be59b518" name="gain" x="84" y="14">
      <params>
         <frac32.u.map name="amp" onParent="true" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="dist/hardclip" uuid="df65230b84185ca57a2701f06c4650436139e1f5" name="hardclip_1" x="196" y="14">
      <params>
         <frac32.u.map name="level" value="4.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="level" x="294" y="14">
      <params>
         <frac32.u.map name="amp" onParent="true" value="14.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/eq4" sha="3f3b6079e9b4ea602f1fe88d3d565e698162bcff" name="eq" x="392" y="14">
      <params>
         <int32 name="lowmid" onParent="true" value="5"/>
         <int32 name="mid" onParent="true" value="-8"/>
         <int32 name="highmid" onParent="true" value="2"/>
         <int32 name="high" onParent="true" value="7"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="490" y="14">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="gain" outlet="out"/>
         <dest obj="hardclip_1" inlet="in"/>
      </net>
      <net>
         <source obj="level" outlet="out"/>
         <dest obj="eq" inlet="in"/>
      </net>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="gain" inlet="in"/>
      </net>
      <net>
         <source obj="eq" outlet="out"/>
         <dest obj="out" inlet="outlet"/>
      </net>
      <net>
         <source obj="hardclip_1" outlet="outlet_1"/>
         <dest obj="level" inlet="in"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>297</x>
      <y>448</y>
      <width>744</width>
      <height>400</height>
   </windowPos>
</patch-1.0>