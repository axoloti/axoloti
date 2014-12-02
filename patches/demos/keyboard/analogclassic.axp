<patch-1.0>
   <obj type="osc" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="lfo" x="180" y="20">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="div16" sha="de53fa1ae8551aca115c5ab76dd646a20339668" name="div16_1" x="300" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb_1" x="40" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="lfop" x="180" y="120">
      <params>
         <frac32.u.map name="gain1" value="1.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="bendrange" x="280" y="120">
      <params>
         <frac32.u.map name="gain1" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="adsr" sha="49cacd3004d35eb333d8c9004945061c0ce24b01" name="enva" x="400" y="120">
      <params>
         <frac32.s.map name="a" onParent="true" value="-26.0"/>
         <frac32.s.map name="d" onParent="true" value="11.0"/>
         <frac32.u.map name="s" onParent="true" value="29.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="adsr" sha="49cacd3004d35eb333d8c9004945061c0ce24b01" name="envf" x="520" y="120">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
         <frac32.u.map name="s" onParent="true" value="0.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="ftrack" x="620" y="120">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="18.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="lfof" x="720" y="120">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="0.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="bendin" sha="84fc4df2ea385612e1294f33f4bfffbc8b501534" name="bendin_1" x="60" y="220">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc~_1" x="80" y="320">
      <params>
         <frac32.s.map name="pitch" value="-12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="f543e080bd2111cba525885443039f346703a594" name="sub" x="240" y="320">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="6.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca~_1" x="380" y="320">
      <params/>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3~_1" x="500" y="320">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="17.0"/>
         <frac32.u.map name="reso" onParent="true" value="61.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="700" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="saw~" sha="1a5088484533a3633e3eb849de47b478f1599369" name="saw~_1" x="80" y="440">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="bendrange out"/>
         <dest name="osc~_1 pitchm"/>
         <dest name="saw~_1 pitchm"/>
         <dest name="ftrack in1"/>
      </net>
      <net>
         <source name="osc~_1 wave"/>
         <dest name="sub in1"/>
      </net>
      <net>
         <source name="saw~_1 wave"/>
         <dest name="sub bus_in"/>
      </net>
      <net>
         <source name="sub out"/>
         <dest name="vca~_1 a"/>
      </net>
      <net>
         <source name="enva env"/>
         <dest name="vca~_1 v"/>
      </net>
      <net>
         <source name="keyb_1 gate2"/>
         <dest name="enva gate"/>
      </net>
      <net>
         <source name="envf env"/>
         <dest name="ftrack bus_in"/>
      </net>
      <net>
         <source name="vca~_1 o"/>
         <dest name="vcf3~_1 in"/>
      </net>
      <net>
         <source name="ftrack out"/>
         <dest name="lfof bus_in"/>
      </net>
      <net>
         <source name="lfo wave"/>
         <dest name="lfof in1"/>
         <dest name="div16_1 in"/>
      </net>
      <net>
         <source name="lfof out"/>
         <dest name="vcf3~_1 pitchm"/>
      </net>
      <net>
         <source name="keyb_1 note"/>
         <dest name="lfop bus_in"/>
      </net>
      <net>
         <source name="bendin_1 bend"/>
         <dest name="bendrange in1"/>
      </net>
      <net>
         <source name="lfop out"/>
         <dest name="bendrange bus_in"/>
      </net>
      <net>
         <source name="div16_1 out"/>
         <dest name="lfop in1"/>
      </net>
      <net>
         <source name="vcf3~_1 out"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>