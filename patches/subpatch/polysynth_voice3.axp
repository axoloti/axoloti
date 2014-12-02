<patch-1.0>
   <obj type="keyb" sha="47bd98210bbdd2f31afd6baa726279bc5a8fc32a" name="keyb1" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="envd" sha="531c9ac204c2f9ac2fcf690f587fd986e998ec50" name="envd1" x="160" y="0">
      <params>
         <frac32.u.map name="d" onParent="true" value="19.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*1" x="320" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="p" x="440" y="0">
      <params>
         <frac32.u.map name="gain1" value="13.5"/>
         <frac32.u.map name="gain2" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="modsource" sha="45ebafea67ca2fe0720654bb75664ba8a032a332" name="velo" x="600" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="vibrato" x="0" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+1" x="120" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="saw~" sha="fbdd077f925e7d2b61a003ddca711d140c851f5" name="saw" x="240" y="140">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcfSaw" x="360" y="140">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="2.0">
            <modulators>
               <modulation sourceName="velo" value="30.0"/>
            </modulators>
         </frac32.s.map>
         <frac32.u.map name="reso" onParent="true" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="env" x="480" y="140">
      <params>
         <frac32.u.map name="a" onParent="true" value="54.0"/>
         <frac32.u.map name="d" onParent="true" value="52.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="volume" x="600" y="140">
      <params>
         <frac32.u.map name="amp" value="9.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="720" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outL" x="840" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="square~" sha="a9d95b3235bd2822873f85c661764b006f96b59e" name="square" x="240" y="280">
      <params>
         <frac32.s.map name="pitch" value="-0.09999990463256836"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcfSquare" x="360" y="280">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-12.0">
            <modulators>
               <modulation sourceName="velo" value="30.0"/>
            </modulators>
         </frac32.s.map>
         <frac32.u.map name="reso" onParent="true" value="17.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1_" x="720" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outR" x="840" y="300">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 note"/>
         <dest name="p in2"/>
         <dest name="+1 in1"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="env gate"/>
         <dest name="envd1 trig"/>
         <dest name="velo trig"/>
      </net>
      <net>
         <source name="env env"/>
         <dest name="volume in"/>
      </net>
      <net>
         <source name="volume out"/>
         <dest name="vca_1 v"/>
         <dest name="vca_1_ v"/>
      </net>
      <net>
         <source name="saw wave"/>
         <dest name="vcfSaw in"/>
      </net>
      <net>
         <source name="vcfSaw out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="outL outlet"/>
      </net>
      <net>
         <source name="vcfSquare out"/>
         <dest name="vca_1_ a"/>
      </net>
      <net>
         <source name="vca_1_ o"/>
         <dest name="outR outlet"/>
      </net>
      <net>
         <source name="square wave"/>
         <dest name="vcfSquare in"/>
      </net>
      <net>
         <source name="vibrato inlet"/>
         <dest name="+1 in2"/>
      </net>
      <net>
         <source name="+1 out"/>
         <dest name="saw pitchm"/>
         <dest name="square pitchm"/>
      </net>
      <net>
         <source name="p out"/>
         <dest name="vcfSaw pitchm"/>
         <dest name="vcfSquare pitchm"/>
      </net>
      <net>
         <source name="envd1 env"/>
         <dest name="*1 a"/>
      </net>
      <net>
         <source name="*1 result"/>
         <dest name="p in1"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="velo v"/>
         <dest name="*1 b"/>
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