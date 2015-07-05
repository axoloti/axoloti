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
   <obj type="osc/saw" sha="1a5088484533a3633e3eb849de47b478f1599369" name="osc_1" x="168" y="70">
      <params>
         <frac32.s.map name="pitch" value="-11.99315881729126"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="env" x="378" y="70">
      <params>
         <frac32.u.map name="a" onParent="true" value="0.0"/>
         <frac32.u.map name="d" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="rand/uniform f" sha="a5949c689afce5a3097108fd569d13ffda3895f6" name="rand_1" x="490" y="70">
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
   <obj type="osc/saw" sha="1a5088484533a3633e3eb849de47b478f1599369" name="osc_1_" x="168" y="154">
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
   <obj type="filter/vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf" x="504" y="252">
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
         <source name="keybzone_lru2 note"/>
         <dest name="osc_1 pitchm"/>
         <dest name="osc_1_ pitchm"/>
      </net>
      <net>
         <source name="osc_1 wave"/>
         <dest name="mix12 in1"/>
      </net>
      <net>
         <source name="mix12 out"/>
         <dest name="vca_2 a"/>
      </net>
      <net>
         <source name="keybzone_lru2 gate"/>
         <dest name="env gate"/>
      </net>
      <net>
         <source name="vca_2 o"/>
         <dest name="vcf in"/>
      </net>
      <net>
         <source name="osc_1_ wave"/>
         <dest name="mix12 in2"/>
      </net>
      <net>
         <source name="env env"/>
         <dest name="mix23 b"/>
      </net>
      <net>
         <source name="mix23 result"/>
         <dest name="vca_2 v"/>
      </net>
      <net>
         <source name="keybzone_lru2 velocity"/>
         <dest name="smooth21 in"/>
      </net>
      <net>
         <source name="smooth21 out"/>
         <dest name="ftrack in"/>
      </net>
      <net>
         <source name="ftrack out"/>
         <dest name="vcf pitchm"/>
      </net>
      <net>
         <source name="rand_1 wave"/>
         <dest name="mix13 in1"/>
      </net>
      <net>
         <source name="mix13 out"/>
         <dest name="mix23 a"/>
      </net>
      <net>
         <source name="vcf out"/>
         <dest name="outlet_1 outlet"/>
      </net>
      <net>
         <source name="mod inlet"/>
         <dest name="mix13 bus_in"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>