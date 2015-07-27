<patch-1.0>
   <obj type="patch/inlet b" sha="e98d5f4c7b741588feaffc8629026f8d8e59e3ef" name="trig" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="d1" x="112" y="14">
      <params>
         <frac32.s.map name="d" onParent="true" value="-16.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="noise.uniform_2" x="224" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/abs" sha="d9aef12fddc085a0a8a7a9868f1c845c538a9209" name="arithmetic.abs_1" x="322" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="290a234e2f83eb072198d0158bcd5da02a3606c3" name="lp1" x="406" y="14">
      <params>
         <frac32.s.map name="freq" onParent="true" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m" sha="7cd630c1ecdc64542bf24aadc0f3114629fdf37d" name="d2" x="112" y="98">
      <params>
         <frac32.s.map name="d" onParent="true" value="-12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="amt" x="210" y="98">
      <params>
         <frac32.u.map name="amp" onParent="true" value="63.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="sine_1" x="308" y="98">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-44.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="*_1" x="406" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="c47ceb7366785e0103cf880ce3450321491949f1" name="*_2" x="406" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="490" y="168">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="noise.uniform_2" outlet="wave"/>
         <dest obj="arithmetic.abs_1" inlet="in"/>
      </net>
      <net>
         <source obj="d2" outlet="env"/>
         <dest obj="*_2" inlet="b"/>
      </net>
      <net>
         <source obj="sine_1" outlet="wave"/>
         <dest obj="*_1" inlet="b"/>
      </net>
      <net>
         <source obj="*_1" outlet="result"/>
         <dest obj="*_2" inlet="a"/>
      </net>
      <net>
         <source obj="amt" outlet="out"/>
         <dest obj="sine_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="arithmetic.abs_1" outlet="out"/>
         <dest obj="lp1" inlet="in"/>
      </net>
      <net>
         <source obj="lp1" outlet="out"/>
         <dest obj="*_1" inlet="a"/>
      </net>
      <net>
         <source obj="trig" outlet="inlet"/>
         <dest obj="d1" inlet="trig"/>
         <dest obj="d2" inlet="trig"/>
      </net>
      <net>
         <source obj="d1" outlet="env"/>
         <dest obj="amt" inlet="in"/>
      </net>
      <net>
         <source obj="*_2" outlet="result"/>
         <dest obj="out" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>800</width>
      <height>478</height>
   </windowPos>
</patch-1.0>