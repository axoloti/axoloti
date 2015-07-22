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
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="vcf3_1" x="434" y="56">
      <params>
         <frac32.s.map name="pitch" value="-12.0"/>
         <frac32.u.map name="reso" value="62.94999980926514"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m" sha="7cd630c1ecdc64542bf24aadc0f3114629fdf37d" name="envdlinmx1" x="126" y="84">
      <params>
         <frac32.s.map name="d" value="-64.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="other/lfsr" sha="94017314f1e9bdac75f41689a6df3c5a5b90c345" name="lfsr_1" x="0" y="98">
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
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="f1" x="434" y="196">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="0.0"/>
         <frac32.u.map name="reso" value="62.9399995803833"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="f2" x="434" y="336">
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
         <source obj="keyb1" outlet="gate2"/>
         <dest obj="envdlinmx1" inlet="trig"/>
         <dest obj="q" inlet="in1"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="vcf3_1" inlet="in"/>
         <dest obj="f1" inlet="in"/>
         <dest obj="f2" inlet="in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="note"/>
         <dest obj="vcf3_1" inlet="pitch"/>
         <dest obj="f1" inlet="pitch"/>
         <dest obj="f2" inlet="pitch"/>
         <dest obj="satp1" inlet="in"/>
      </net>
      <net>
         <source obj="q" outlet="out"/>
         <dest obj="vcf3_1" inlet="reso"/>
         <dest obj="f1" inlet="reso"/>
         <dest obj="f2" inlet="reso"/>
      </net>
      <net>
         <source obj="vcf3_1" outlet="out"/>
         <dest obj="mix21" inlet="in1"/>
      </net>
      <net>
         <source obj="f1" outlet="out"/>
         <dest obj="mix21" inlet="in2"/>
      </net>
      <net>
         <source obj="f2" outlet="out"/>
         <dest obj="mix21" inlet="in3"/>
      </net>
      <net>
         <source obj="mix21" outlet="out"/>
         <dest obj="dac_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="keyb1" outlet="velocity"/>
         <dest obj="velo" inlet="in"/>
         <dest obj="div1281" inlet="in"/>
      </net>
      <net>
         <source obj="lfsr_1" outlet="out"/>
         <dest obj="bipolar2unipolar1" inlet="i"/>
      </net>
      <net>
         <source obj="velo" outlet="out"/>
         <dest obj="envdlinmx1" inlet="d"/>
      </net>
      <net>
         <source obj="envdlinmx1" outlet="env"/>
         <dest obj="*1" inlet="a"/>
      </net>
      <net>
         <source obj="*1" outlet="result"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="div1281" outlet="out"/>
         <dest obj="*1" inlet="b"/>
      </net>
      <net>
         <source obj="bipolar2unipolar1" outlet="o"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="satp1" outlet="out"/>
         <dest obj="inv1" inlet="in"/>
      </net>
      <net>
         <source obj="inv1" outlet="out"/>
         <dest obj="q" inlet="in2"/>
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
   <windowPos>
      <x>312</x>
      <y>212</y>
      <width>1010</width>
      <height>646</height>
   </windowPos>
</patch-1.0>