<patch-1.0>
   <obj type="dacconfig" sha="e47910fb3176fe2a8167aa211513bd0f0f20e741" name="dacconfig1" x="760" y="20">
      <params/>
      <attribs>
         <combo attributeName="headphones" selection="-54dB"/>
      </attribs>
   </obj>
   <obj type="keyb" sha="47bd98210bbdd2f31afd6baa726279bc5a8fc32a" name="keyb1" x="200" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="envd" sha="531c9ac204c2f9ac2fcf690f587fd986e998ec50" name="envd1" x="400" y="140">
      <params>
         <frac32.u.map name="d" onParent="true" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_1" x="160" y="240">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="6.72370986957111"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_2" x="160" y="380">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="18.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+1" x="520" y="380">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+2" x="520" y="460">
      <params/>
      <attribs/>
   </obj>
   <obj type="*c" sha="d36ecbd55095f4888a0ebda8efda68e015c5e72b" name="*c1" x="720" y="460">
      <params>
         <frac32.u.map name="amp" value="11.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_3" x="160" y="520">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="24.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+3" x="520" y="560">
      <params/>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="760" y="580">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="Wave" x="920" y="580">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_4" x="160" y="640">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="46.0"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="envd1 env"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="+1 out"/>
         <dest name="+2 in1"/>
      </net>
      <net>
         <source name="+2 out"/>
         <dest name="+3 in1"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envd1 trig"/>
      </net>
      <net>
         <source name="+3 out"/>
         <dest name="*c1 in"/>
      </net>
      <net>
         <source name="osc_1 wave"/>
         <dest name="+1 in1"/>
      </net>
      <net>
         <source name="osc_2 wave"/>
         <dest name="+1 in2"/>
      </net>
      <net>
         <source name="osc_3 wave"/>
         <dest name="+2 in2"/>
      </net>
      <net>
         <source name="osc_4 wave"/>
         <dest name="+3 in2"/>
      </net>
      <net>
         <source name="*c1 out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="Wave outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>