<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="140" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c321_" x="56" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="speed" x="14" y="70">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="10.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="intesity" x="140" y="70">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="3.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_2_" x="266" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="336" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="cv" x="266" y="126">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="c321_ o"/>
         <dest name="intesity bus_in"/>
      </net>
      <net>
         <source name="speed wave"/>
         <dest name="intesity in1"/>
      </net>
      <net>
         <source name="intesity out"/>
         <dest name="vca_2_ v"/>
         <dest name="cv outlet"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="vca_2_ a"/>
      </net>
      <net>
         <source name="vca_2_ o"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>