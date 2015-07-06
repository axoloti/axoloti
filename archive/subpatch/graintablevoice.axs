<patch-1.0>
   <obj type="cbp" sha="589b835807a3b8c8b05793bc4bd9adaf853f9705" name="detunerange" x="500" y="0">
      <params>
         <frac32.s.map name="value" onParent="true" value="0.1"/>
      </params>
      <attribs/>
   </obj>
   <obj type="cbp" sha="589b835807a3b8c8b05793bc4bd9adaf853f9705" name="detune" x="560" y="0">
      <params>
         <frac32.s.map name="value" onParent="true" value="16.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_1" x="20" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="lspread" x="100" y="80">
      <params>
         <frac32.u.map name="amp" onParent="true" value="4.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envdlinmx" sha="a2e1da37932bdfc8056cd08cca74d2ebc6735f40" name="length" x="200" y="80">
      <params>
         <frac32.s.map name="d" onParent="true" value="-4.299999999999999"/>
      </params>
      <attribs/>
   </obj>
   <obj type="&lt;c" sha="355de7092a37338e16e09397154948f860a9160c" name="&lt;c_1" x="320" y="80">
      <params>
         <frac32.u.map name="c" value="0.04999971389770508"/>
      </params>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_2" x="420" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="bipolar2unipolar" sha="b80b299df9cb5523b1c4c0c7fe09941a1c682112" name="bipolar2unipolar_1" x="480" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="ospread" x="600" y="80">
      <params>
         <frac32.u.map name="amp" onParent="true" value="5.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_3" x="420" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="mtof" sha="a620f86cc7234a9fa26043819d068b779dd852f" name="mtof_1" x="420" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_4" x="480" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_2" x="580" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_1" x="640" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_3" x="720" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_2" x="780" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="muls4" sha="a8753c6d47c9c5bf9066fd09779fa04b6d5f192" name="muls4_1" x="880" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp~_2" x="960" y="160">
      <params/>
      <attribs/>
   </obj>
   <obj type="tabread2" sha="b0519acd115f068f0851ae0be434a57888454c06" name="tabread2_1" x="1020" y="160">
      <params/>
      <attribs>
         <objref attributeName="table" obj="../t"/>
      </attribs>
   </obj>
   <obj type="window" sha="ff29ab0721db1b1238076400832e919d860fc38f" name="window_1" x="440" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp~_1" x="520" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="*_1" x="940" y="260">
      <params/>
      <attribs/>
   </obj>
   <obj type="div4" sha="24e4544f0f846eb56ca3b8d30b635a5eb50caa29" name="div4_1" x="1000" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet~_1" x="1060" y="280">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="length env"/>
         <dest name="&lt;c_1 in"/>
         <dest name="window_1 phase"/>
         <dest name="*_4 b"/>
      </net>
      <net>
         <source name="&lt;c_1 out"/>
         <dest name="length trig"/>
         <dest name="randtrig_1 trig"/>
         <dest name="randtrig_2 trig"/>
         <dest name="randtrig_3 trig"/>
      </net>
      <net>
         <source name="randtrig_1 rand"/>
         <dest name="lspread in"/>
      </net>
      <net>
         <source name="lspread out"/>
         <dest name="length dm"/>
         <dest name="mtof_1 pitch"/>
      </net>
      <net>
         <source name="div4_1 out"/>
         <dest name="outlet~_1 outlet"/>
      </net>
      <net>
         <source name="window_1 win"/>
         <dest name="interp~_1 i"/>
      </net>
      <net>
         <source name="interp~_1 o"/>
         <dest name="*_1 b"/>
      </net>
      <net>
         <source name="detunerange out"/>
         <dest name="*_2 a"/>
      </net>
      <net>
         <source name="randtrig_3 rand"/>
         <dest name="*_2 b"/>
      </net>
      <net>
         <source name="*_2 result"/>
         <dest name="+_1 in2"/>
      </net>
      <net>
         <source name="detune out"/>
         <dest name="+_1 in1"/>
      </net>
      <net>
         <source name="+_1 out"/>
         <dest name="*_3 a"/>
      </net>
      <net>
         <source name="ospread out"/>
         <dest name="+_2 in1"/>
      </net>
      <net>
         <source name="*_3 result"/>
         <dest name="+_2 in2"/>
      </net>
      <net>
         <source name="interp~_2 o"/>
         <dest name="tabread2_1 a"/>
      </net>
      <net>
         <source name="tabread2_1 o"/>
         <dest name="*_1 a"/>
      </net>
      <net>
         <source name="mtof_1 frequency"/>
         <dest name="*_4 a"/>
      </net>
      <net>
         <source name="*_4 result"/>
         <dest name="*_3 b"/>
      </net>
      <net>
         <source name="+_2 out"/>
         <dest name="muls4_1 in"/>
      </net>
      <net>
         <source name="muls4_1 out"/>
         <dest name="interp~_2 i"/>
      </net>
      <net>
         <source name="randtrig_2 rand"/>
         <dest name="bipolar2unipolar_1 i"/>
      </net>
      <net>
         <source name="bipolar2unipolar_1 o"/>
         <dest name="ospread in"/>
      </net>
      <net>
         <source name="*_1 result"/>
         <dest name="div4_1 in"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>0</NPresets>
      <NPresetEntries>0</NPresetEntries>
      <NModulationSources>0</NModulationSources>
      <NModulationTargetsPerSource>0</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>