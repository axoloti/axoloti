<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="0" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/toggle" sha="a104f377191a424d537741cdfd7d5348bc16590c" name="on" x="266" y="42">
      <params>
         <bool32.tgl name="b" onParent="true" value="0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="dist/rectifier" sha="6a81663bcc6a29c922883f499193baff3d14c5d" name="rectifier_2" x="0" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="freq" x="0" y="126">
      <params>
         <frac32.u.map name="value" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth_1" x="70" y="126">
      <params>
         <frac32.u.map name="time" value="18.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="flt" x="154" y="126">
      <params>
         <frac32.s.map name="pitch" value="-13.0"/>
         <frac32.u.map name="reso" onParent="true" value="58.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mux/mux 2" sha="c6b90f8c9bc3d2f8632ce90fca7a738c7153eb2f" name="mux_1" x="266" y="126">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="350" y="126">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="freq out"/>
         <dest name="smooth_1 in"/>
      </net>
      <net>
         <source name="smooth_1 out"/>
         <dest name="flt pitchm"/>
      </net>
      <net>
         <source name="rectifier_2 out"/>
         <dest name="flt in"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="rectifier_2 in"/>
         <dest name="mux_1 i1"/>
      </net>
      <net>
         <source name="flt out"/>
         <dest name="mux_1 i2"/>
      </net>
      <net>
         <source name="mux_1 o"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="on o"/>
         <dest name="mux_1 s"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>