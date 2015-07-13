<patch-1.0>
   <obj type="keyb" sha="47bd98210bbdd2f31afd6baa726279bc5a8fc32a" name="keyb1" x="40" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="saw~" sha="fbdd077f925e7d2b61a003ddca711d140c851f5" name="saw_1" x="160" y="80">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1" x="280" y="80">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="2.0"/>
         <frac32.u.map name="reso" onParent="true" value="41.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="envahd1" x="400" y="80">
      <params>
         <frac32.u.map name="a" onParent="true" value="54.0"/>
         <frac32.u.map name="d" onParent="true" value="52.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="k" x="520" y="80">
      <params>
         <frac32.u.map name="amp" value="16.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="640" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="760" y="80">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 note"/>
         <dest name="saw_1 pitchm"/>
         <dest name="vcf3_1 pitchm"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envahd1 gate"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="k in"/>
      </net>
      <net>
         <source name="k out"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="vcf3_1 in"/>
      </net>
      <net>
         <source name="vcf3_1 out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>0</MidiChannel>
      <NPresets>4</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>