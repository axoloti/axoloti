<patch-1.0>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="noise.uniform_1" x="98" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/inlet b" sha="e98d5f4c7b741588feaffc8629026f8d8e59e3ef" name="trig" x="14" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="env" x="98" y="56">
      <params>
         <frac32.s.map name="d" onParent="true" value="13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="gain.vca~_1" x="196" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/bp svf" sha="64eb414253ad09d2d8c5945dc48b8e1d2ac5b321" name="bpf" x="266" y="56">
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
         <source name="env env"/>
         <dest name="gain.vca~_1 v"/>
      </net>
      <net>
         <source name="noise.uniform_1 wave"/>
         <dest name="gain.vca~_1 a"/>
      </net>
      <net>
         <source name="gain.vca~_1 o"/>
         <dest name="bpf in"/>
      </net>
      <net>
         <source name="trig inlet"/>
         <dest name="env trig"/>
      </net>
      <net>
         <source name="bpf out"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>