<patch-1.0>
   <obj type="keyb" sha="c4bae6e941c9c85103725bc12ed6eec656d9852c" name="keyb1" x="40" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="saw~" sha="8c87c37d5169a8ac979b310b57ee0ec517ec94b9" name="saw_1" x="160" y="80">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envahd" sha="b1e9c61cfd44f50159ce86d89126495aeb8b9679" name="envahd1" x="280" y="80">
      <params>
         <frac32.u.map name="a" value="27.0"/>
         <frac32.u.map name="d" value="31.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vca~" sha="d3cc755f1897e939978d5fff117339327a4b3a52" name="vca_1" x="600" y="100">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="680" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix1" sha="8041da25532c27ffaeaed170f3e9ca3b871804af" name="mix11" x="360" y="80">
      <params>
         <frac32.u.map name="gain1" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf2_1" x="480" y="140">
      <params>
         <frac32.s.map name="pitch" MidiCC="1" value="12.0"/>
         <frac32.u.map name="reso" MidiCC="2" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc" sha="7224f0f8080ebb101b837d5823024ae068903724" name="osc1" x="400" y="0">
      <params>
         <frac32.s.map name="pitch" MidiCC="4" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix1" sha="8041da25532c27ffaeaed170f3e9ca3b871804af" name="mix12" x="520" y="0">
      <params>
         <frac32.u.map name="gain1" MidiCC="3" value="18.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="analogRead" sha="7d96dca616c6a2c27e7cc6f57bd170d8ded36e1e" name="analogRead1" x="200" y="220">
      <params/>
      <attribs>
         <combo attributeName="channel" selection="PA0 (ADC1_IN0)"/>
      </attribs>
   </obj>
   <nets>
      <net>
         <source name="keyb1 note"/>
         <dest name="saw_1 pitchm"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envahd1 gate"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="mix11 in1"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="vcf2_1 in"/>
      </net>
      <net>
         <source name="vcf2_1 out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="osc1 wave"/>
         <dest name="mix12 in1"/>
      </net>
      <net>
         <source name="mix12 out"/>
         <dest name="vcf2_1 pitchm"/>
      </net>
      <net>
         <source name="mix11 out"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="analogRead1 out"/>
         <dest name="mix12 bus_in"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>