<patch-1.0>
   <obj type="toBool" sha="205e5c5ed2de35ec586e37ce247ac3b4a9d22cd5" name="toBool1" x="240" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="inlet1" x="120" y="40">
      <params/>
      <attribs/>
   </obj>
   <obj type="keyb" sha="c4bae6e941c9c85103725bc12ed6eec656d9852c" name="keyb1" x="120" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="+c" sha="4c5cd6eb0ec18a1bab81a4aa3b4f53834edeb10" name="+c1" x="240" y="80">
      <params>
         <frac32.u.map name="c" value="63.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="stringi" sha="729bdb1d434ebbff3539034a3805ca20fa2794ee" name="stringi1" x="440" y="80">
      <params/>
      <attribs>
         <table attributeName="prefix" table="pia_l"/>
         <table attributeName="suffix" table=".wav"/>
      </attribs>
   </obj>
   <obj type="envhd" sha="6f6efeeb3935a7d07a497e7bdade94c8560a8152" name="envhd1" x="640" y="100">
      <params>
         <frac32.u.map name="d" MidiCC="1" value="51.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="double" sha="c633387a1d72b98c2decbdfee1f60ab6893c82b" name="double1" x="760" y="100">
      <params/>
      <attribs/>
   </obj>
   <obj type="delta" sha="c1baa08776e10d0bfde6633563f02611494ab6cd" name="delta1" x="240" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="abs" sha="56b0e9ebf07d4a6d8f04db3682899c48c85cc389" name="abs1" x="320" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="&lt;c" sha="355de7092a37338e16e09397154948f860a9160c" name="&gt;c1" x="400" y="180">
      <params>
         <frac32.u.map name="c" value="0.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="playwave2stereo" sha="ebc10a15d80a71f6be3f998febca251713b47031" name="playwave21" x="600" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1" x="800" y="180">
      <params>
         <frac32.s.map name="pitch" MidiCC="2" value="0.0"/>
         <frac32.u.map name="reso" MidiCC="3" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="1000" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1_" x="800" y="320">
      <params>
         <frac32.s.map name="pitch" MidiCC="2" value="0.0"/>
         <frac32.u.map name="reso" MidiCC="3" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_2" x="1000" y="320">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="stringi1 out"/>
         <dest name="playwave21 filename"/>
      </net>
      <net>
         <source name="inlet1 inlet"/>
         <dest name="playwave21 pos"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="+c1 in"/>
         <dest name="delta1 a"/>
      </net>
      <net>
         <source name="+c1 out"/>
         <dest name="stringi1 index"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="toBool1 i"/>
         <dest name="envhd1 trig"/>
      </net>
      <net>
         <source name="delta1 d"/>
         <dest name="abs1 in"/>
      </net>
      <net>
         <source name="abs1 out"/>
         <dest name="&gt;c1 in"/>
      </net>
      <net>
         <source name="&gt;c1 out"/>
         <dest name="playwave21 trig"/>
      </net>
      <net>
         <source name="playwave21 outl"/>
         <dest name="vcf3_1 in"/>
      </net>
      <net>
         <source name="double1 out"/>
         <dest name="vcf3_1 pitchm"/>
         <dest name="vcf3_1_ pitchm"/>
      </net>
      <net>
         <source name="vcf3_1 out"/>
         <dest name="outlet_1 outlet"/>
      </net>
      <net>
         <source name="envhd1 env"/>
         <dest name="double1 in"/>
      </net>
      <net>
         <source name="playwave21 outr"/>
         <dest name="vcf3_1_ in"/>
      </net>
      <net>
         <source name="vcf3_1_ out"/>
         <dest name="outlet_2 outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>