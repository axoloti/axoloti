<patch-1.0>
   <obj type="patch/inlet f" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="mod" x="504" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb zone lru" sha="b9b28cf52b3421368e367f3718404222910c57a5" name="keybzone_lru2" x="14" y="70">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="0"/>
         <spinner attributeName="endNote" value="50"/>
      </attribs>
   </obj>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="osc_1" x="168" y="70">
      <params>
         <frac32.s.map name="pitch" value="-11.99315881729126"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="c4000e3e6417d9d57283d66476b83f22f975ff09" name="env" x="378" y="70">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="rand/uniform f" sha="aefed121f296eb10eaa8ad5f85dbe647718f1044" name="rand_1" x="490" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix13" x="574" y="70">
      <params>
         <frac32.u.map name="gain1" value="7.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="mix23" x="658" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="osc_1_" x="168" y="154">
      <params>
         <frac32.s.map name="pitch" value="-12.14772891998291"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="67c325bf12e5b73ad58df89daf7899831777003c" name="mix12" x="266" y="154">
      <params>
         <frac32.u.map name="gain1" value="24.0"/>
         <frac32.u.map name="gain2" value="46.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth2" sha="7a49483fff392fd1c10fb15f40dfc8f92f942192" name="smooth21" x="56" y="252">
      <params>
         <frac32.u.map name="risetime" value="27.5"/>
         <frac32.u.map name="falltime" value="31.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="ftrack" x="140" y="252">
      <params>
         <frac32.u.map name="amp" onParent="true" value="36.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_2" x="434" y="252">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="vcf" x="504" y="252">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-30.0"/>
         <frac32.u.map name="reso" onParent="true" value="13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="616" y="252">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="keybzone_lru2" outlet="note"/>
         <dest obj="osc_1" inlet="pitch"/>
         <dest obj="osc_1_" inlet="pitch"/>
      </net>
      <net>
         <source obj="osc_1" outlet="wave"/>
         <dest obj="mix12" inlet="in1"/>
      </net>
      <net>
         <source obj="mix12" outlet="out"/>
         <dest obj="vca_2" inlet="a"/>
      </net>
      <net>
         <source obj="keybzone_lru2" outlet="gate"/>
         <dest obj="env" inlet="gate"/>
      </net>
      <net>
         <source obj="vca_2" outlet="o"/>
         <dest obj="vcf" inlet="in"/>
      </net>
      <net>
         <source obj="osc_1_" outlet="wave"/>
         <dest obj="mix12" inlet="in2"/>
      </net>
      <net>
         <source obj="env" outlet="env"/>
         <dest obj="mix23" inlet="b"/>
      </net>
      <net>
         <source obj="mix23" outlet="result"/>
         <dest obj="vca_2" inlet="v"/>
      </net>
      <net>
         <source obj="keybzone_lru2" outlet="velocity"/>
         <dest obj="smooth21" inlet="in"/>
      </net>
      <net>
         <source obj="smooth21" outlet="out"/>
         <dest obj="ftrack" inlet="in"/>
      </net>
      <net>
         <source obj="ftrack" outlet="out"/>
         <dest obj="vcf" inlet="pitch"/>
      </net>
      <net>
         <source obj="rand_1" outlet="wave"/>
         <dest obj="mix13" inlet="in1"/>
      </net>
      <net>
         <source obj="mix13" outlet="out"/>
         <dest obj="mix23" inlet="a"/>
      </net>
      <net>
         <source obj="vcf" outlet="out"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="mod" outlet="inlet"/>
         <dest obj="mix13" inlet="bus_in"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>968</width>
      <height>562</height>
   </windowPos>
</patch-1.0>