<patch-1.0>
   <obj type="c32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c641" x="280" y="40">
      <params/>
      <attribs/>
   </obj>
   <obj type="keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="120" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+1" x="280" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="+c" sha="4c5cd6eb0ec18a1bab81a4aa3b4f53834edeb10" name="+c1" x="400" y="80">
      <params>
         <frac32.u.map name="c" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="stringi" sha="4c1c90721e5f530e9fbdcfa373edaffe908e605b" name="stringi1" x="480" y="80">
      <params/>
      <attribs>
         <table attributeName="prefix" table="pia_l"/>
         <table attributeName="suffix" table=".raw"/>
      </attribs>
   </obj>
   <obj type="delayedpulsedurationx" sha="55f69bc6153344c6d5bed526ec91502b83708ce3" name="delayedpulsedurationx_1" x="280" y="140">
      <params>
         <frac32.s.map name="delay" value="-64.0"/>
         <frac32.s.map name="pulselength" value="63.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="offset" x="520" y="180">
      <params>
         <frac32.u.map name="value" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="div128" sha="a04562d4c5dad7454500fb8bc6383a802aef8f25" name="div1281" x="600" y="180">
      <params/>
      <attribs/>
   </obj>
   <obj type="envahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="envahd1" x="240" y="260">
      <params>
         <frac32.u.map name="a" value="8.0"/>
         <frac32.u.map name="d" onParent="true" value="44.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="&gt;c" sha="aa245f90aec358415dbbc12409c90065cda73d3e" name="&gt;c1" x="340" y="260">
      <params>
         <frac32.u.map name="c" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="and2" sha="1938611ef1b57af3c7b1965081b17bf8e194d9a6" name="and21" x="400" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="playwave2stereopoly" sha="ac6b5b7bdb42c8bda0a612a463885008a12b9160" name="playwave21" x="680" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="800" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="920" y="300">
      <params/>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1_" x="800" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_2" x="920" y="340">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="stringi1 out"/>
         <dest name="playwave21 filename"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envahd1 gate"/>
      </net>
      <net>
         <source name="playwave21 outl"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="outlet_1 outlet"/>
      </net>
      <net>
         <source name="playwave21 outr"/>
         <dest name="vca_1_ a"/>
      </net>
      <net>
         <source name="vca_1_ o"/>
         <dest name="outlet_2 outlet"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="&gt;c1 in"/>
         <dest name="vca_1 v"/>
         <dest name="vca_1_ v"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="+1 in2"/>
      </net>
      <net>
         <source name="c641 o"/>
         <dest name="+1 in1"/>
      </net>
      <net>
         <source name="+1 out"/>
         <dest name="+c1 in"/>
      </net>
      <net>
         <source name="+c1 out"/>
         <dest name="stringi1 index"/>
      </net>
      <net>
         <source name="offset out"/>
         <dest name="div1281 in"/>
      </net>
      <net>
         <source name="div1281 out"/>
         <dest name="playwave21 pos"/>
      </net>
      <net>
         <source name="keyb1 gate2"/>
         <dest name="and21 i1"/>
         <dest name="delayedpulsedurationx_1 trig"/>
      </net>
      <net>
         <source name="&gt;c1 out"/>
         <dest name="and21 i2"/>
      </net>
      <net>
         <source name="delayedpulsedurationx_1 pulse"/>
         <dest name="playwave21 trig"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>