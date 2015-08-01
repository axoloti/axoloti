<patch-1.0>
   <comment type="patch/comment" x="56" y="14" text="Simple voice for a MPE synth. simply load in parent patch, set voices and connect to audio"/>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="saw_1" x="546" y="70">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 1 g" sha="9837ebd6f7c0b2b3853ea475d91c943144e2273b" name="sub_osc_mix" x="672" y="70">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="63.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="midi/ctrl/mpe" sha="df6476c993ae8899573dbd0c881e717e5eec19ea" name="keyb_1" x="42" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp m" sha="c2224dc682842eae1af4496f3f94a6afc1525ee4" name="lp_1" x="784" y="196">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="0.0"/>
         <frac32.u.map name="reso" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="924" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="1022" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 64" sha="23bcd526229a9199a165fe7a57c62168cb21de0d" name="div_1" x="252" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 128" sha="a04562d4c5dad7454500fb8bc6383a802aef8f25" name="div_2" x="336" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_1" x="434" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/square" sha="7cccf8a95bf312ecc084f11f532cf5fda00b8c58" name="sub_osc" x="546" y="294">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="pitch"/>
         <dest obj="saw_1" inlet="pitch"/>
         <dest obj="+_1" inlet="in1"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="pressure"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="timbre"/>
         <dest obj="div_1" inlet="in"/>
         <dest obj="lp_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="lp_1" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="div_2" inlet="in"/>
      </net>
      <net>
         <source obj="div_2" outlet="out"/>
         <dest obj="+_1" inlet="in2"/>
      </net>
      <net>
         <source obj="saw_1" outlet="wave"/>
         <dest obj="sub_osc_mix" inlet="bus_in"/>
      </net>
      <net>
         <source obj="sub_osc" outlet="wave"/>
         <dest obj="sub_osc_mix" inlet="in1"/>
      </net>
      <net>
         <source obj="sub_osc_mix" outlet="out"/>
         <dest obj="lp_1" inlet="in"/>
      </net>
      <net>
         <source obj="+_1" outlet="out"/>
         <dest obj="sub_osc" inlet="pitch"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyexpression</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Saturate>false</Saturate>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>148</x>
      <y>160</y>
      <width>1579</width>
      <height>636</height>
   </windowPos>
</patch-1.0>