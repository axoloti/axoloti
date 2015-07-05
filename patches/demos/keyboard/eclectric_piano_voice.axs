<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="velo" x="140" y="0">
      <params>
         <frac32.u.map name="amp" value="48.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/satp" sha="107e97e0797ac54d70617cbd5af301ac85ec58a8" name="satp1" x="224" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="294" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="q" x="350" y="0">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="1.0"/>
         <frac32.u.map name="gain2" onParent="true" value="0.024999618530273438"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1" x="434" y="56">
      <params>
         <frac32.s.map name="pitch" value="-12.0"/>
         <frac32.u.map name="reso" value="62.94999980926514"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m x" sha="a2e1da37932bdfc8056cd08cca74d2ebc6735f40" name="envdlinmx1" x="126" y="84">
      <params>
         <frac32.s.map name="d" value="-64.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="other/lfsr~" sha="94017314f1e9bdac75f41689a6df3c5a5b90c345" name="lfsr_1" x="0" y="98">
      <params/>
      <attribs>
         <combo attributeName="polynomial" selection="0x286"/>
      </attribs>
   </obj>
   <obj type="math/div 2" sha="7fee48a2d38604fd5504303cbccef61f687d1593" name="div1281" x="224" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*1" x="294" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/bipolar2unipolar" sha="38609fdcec231d6b21f80d961254b8abd0ecad61" name="bipolar2unipolar1" x="0" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="294" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="f1" x="434" y="196">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="0.0"/>
         <frac32.u.map name="reso" value="62.9399995803833"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="f2" x="434" y="336">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="12.0"/>
         <frac32.u.map name="reso" value="62.924999713897705"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 3" sha="3d37ac18e4393438042204df12d17aec499f09fb" name="mix21" x="574" y="336">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="8.5"/>
         <frac32.u.map name="gain2" onParent="true" value="10.5"/>
         <frac32.u.map name="gain3" onParent="true" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="dac_1" x="700" y="336">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 gate2"/>
         <dest name="envdlinmx1 trig"/>
         <dest name="q in1"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="vcf3_1 in"/>
         <dest name="f1 in"/>
         <dest name="f2 in"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="vcf3_1 pitchm"/>
         <dest name="f1 pitchm"/>
         <dest name="f2 pitchm"/>
         <dest name="satp1 in"/>
      </net>
      <net>
         <source name="q out"/>
         <dest name="vcf3_1 resom"/>
         <dest name="f1 resom"/>
         <dest name="f2 resom"/>
      </net>
      <net>
         <source name="vcf3_1 out"/>
         <dest name="mix21 in1"/>
      </net>
      <net>
         <source name="f1 out"/>
         <dest name="mix21 in2"/>
      </net>
      <net>
         <source name="f2 out"/>
         <dest name="mix21 in3"/>
      </net>
      <net>
         <source name="mix21 out"/>
         <dest name="dac_1 outlet"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="velo in"/>
         <dest name="div1281 in"/>
      </net>
      <net>
         <source name="lfsr_1 out"/>
         <dest name="bipolar2unipolar1 i"/>
      </net>
      <net>
         <source name="velo out"/>
         <dest name="envdlinmx1 dm"/>
      </net>
      <net>
         <source name="envdlinmx1 env"/>
         <dest name="*1 a"/>
      </net>
      <net>
         <source name="*1 result"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="div1281 out"/>
         <dest name="*1 b"/>
      </net>
      <net>
         <source name="bipolar2unipolar1 o"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="satp1 out"/>
         <dest name="inv1 in"/>
      </net>
      <net>
         <source name="inv1 out"/>
         <dest name="q in2"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <NPresets>2</NPresets>
      <NPresetEntries>2</NPresetEntries>
      <NModulationSources>2</NModulationSources>
      <NModulationTargetsPerSource>1</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>