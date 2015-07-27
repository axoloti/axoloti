<patch-1.0>
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="noise.uniform_1" x="98" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/inlet b" sha="e98d5f4c7b741588feaffc8629026f8d8e59e3ef" name="trig" x="14" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="env" x="98" y="56">
      <params>
         <frac32.s.map name="d" onParent="true" value="13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="gain.vca~_1" x="196" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/bp svf" sha="ba4ac02f48ae93c7ecc9fac9b0e212cac7c21539" name="bpf" x="266" y="56">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="41.0"/>
         <frac32.u.map name="reso" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="364" y="56">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="env" outlet="env"/>
         <dest obj="gain.vca~_1" inlet="v"/>
      </net>
      <net>
         <source obj="noise.uniform_1" outlet="wave"/>
         <dest obj="gain.vca~_1" inlet="a"/>
      </net>
      <net>
         <source obj="gain.vca~_1" outlet="o"/>
         <dest obj="bpf" inlet="in"/>
      </net>
      <net>
         <source obj="trig" outlet="inlet"/>
         <dest obj="env" inlet="trig"/>
      </net>
      <net>
         <source obj="bpf" outlet="out"/>
         <dest obj="out" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>2</y>
      <width>674</width>
      <height>400</height>
   </windowPos>
</patch-1.0>