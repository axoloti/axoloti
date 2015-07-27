<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="140" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c321_" x="56" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="speed" x="14" y="70">
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
         <source obj="c321_" outlet="o"/>
         <dest obj="intesity" inlet="bus_in"/>
      </net>
      <net>
         <source obj="speed" outlet="wave"/>
         <dest obj="intesity" inlet="in1"/>
      </net>
      <net>
         <source obj="intesity" outlet="out"/>
         <dest obj="vca_2_" inlet="v"/>
         <dest obj="cv" inlet="outlet"/>
      </net>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="vca_2_" inlet="a"/>
      </net>
      <net>
         <source obj="vca_2_" outlet="o"/>
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
      <width>646</width>
      <height>436</height>
   </windowPos>
</patch-1.0>