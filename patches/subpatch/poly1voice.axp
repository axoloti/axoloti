<patch-1.0>
   <obj type="keyb" sha="c4bae6e941c9c85103725bc12ed6eec656d9852c" name="keyb1" x="80" y="60">
      <params/>
      <attribs/>
   </obj>
   <obj type="envd" sha="531c9ac204c2f9ac2fcf690f587fd986e998ec50" name="envd1" x="320" y="60">
      <params>
         <frac32.u.map name="d" value="6.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="envahd1" x="80" y="180">
      <params>
         <frac32.u.map name="a" value="24.0"/>
         <frac32.u.map name="d" value="36.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="saw~" sha="3445b02fa5289504183cffbb950cfe0e78671af" name="saw_1" x="200" y="200">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="360" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1" x="440" y="200">
      <params>
         <frac32.s.map name="pitch" MidiCC="17" value="1.0"/>
         <frac32.u.map name="reso" MidiCC="18" value="63.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="640" y="200">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*1" x="440" y="60">
      <params/>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c1" x="520" y="60">
      <params>
         <frac32.u.map name="amp" value="15.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="*c" sha="d36ecbd55095f4888a0ebda8efda68e015c5e72b" name="*c2" x="560" y="200">
      <params>
         <frac32.u.map name="amp" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 note"/>
         <dest name="saw_1 pitchm"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envd1 trig"/>
         <dest name="envahd1 gate"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="vcf3_1 in"/>
      </net>
      <net>
         <source name="envd1 env"/>
         <dest name="*1 a"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="*1 b"/>
      </net>
      <net>
         <source name="*1 result"/>
         <dest name="*c1 in"/>
      </net>
      <net>
         <source name="*c1 out"/>
         <dest name="vcf3_1 pitchm"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="vcf3_1 out"/>
         <dest name="*c2 in"/>
      </net>
      <net>
         <source name="*c2 out"/>
         <dest name="outlet_1 outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>