<patch-1.0>
   <obj type="cbp" sha="589b835807a3b8c8b05793bc4bd9adaf853f9705" name="detune" x="500" y="0">
      <params>
         <frac32.s.map name="value" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="cbp" sha="589b835807a3b8c8b05793bc4bd9adaf853f9705" name="detunerange" x="600" y="0">
      <params>
         <frac32.s.map name="value" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_1" x="20" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="lspread" x="100" y="80">
      <params>
         <frac32.u.map name="amp" onParent="true" value="7.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="envdlinmx" sha="a2e1da37932bdfc8056cd08cca74d2ebc6735f40" name="length" x="200" y="80">
      <params>
         <frac32.s.map name="d" onParent="true" value="-44.0"/>
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
   <obj type="*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="ospread" x="500" y="80">
      <params>
         <frac32.u.map name="amp" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="randtrig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_3" x="420" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_2" x="560" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_1" x="620" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_3" x="700" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_2" x="760" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp~_2" x="860" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="delread2~~" sha="22a07dcbe5007bc4095bed25946486e7c98caf23" name="delread2~~_1" x="920" y="120">
      <params>
         <frac32.u.map name="time" value="2.0"/>
      </params>
      <attribs>
         <objref attributeName="delayname" obj="../d"/>
      </attribs>
   </obj>
   <obj type="*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="*_1" x="920" y="220">
      <params/>
      <attribs/>
   </obj>
   <obj type="window" sha="ff29ab0721db1b1238076400832e919d860fc38f" name="window_1" x="540" y="240">
      <params/>
      <attribs/>
   </obj>
   <obj type="interp~" sha="5a9175b8d44d830756d1599a86b4a6a49813a19b" name="interp~_1" x="600" y="240">
      <params/>
      <attribs/>
   </obj>
   <obj type="div8" sha="776c01564ea89f47347a594dcf67670e795e61f6" name="div8_1" x="980" y="240">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet~_1" x="1040" y="240">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="length env"/>
         <dest name="&lt;c_1 in"/>
         <dest name="window_1 phase"/>
         <dest name="*_3 b"/>
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
      </net>
      <net>
         <source name="randtrig_2 rand"/>
         <dest name="ospread in"/>
      </net>
      <net>
         <source name="delread2~~_1 out"/>
         <dest name="*_1 a"/>
      </net>
      <net>
         <source name="*_1 result"/>
         <dest name="div8_1 in"/>
      </net>
      <net>
         <source name="div8_1 out"/>
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
         <source name="interp~_2 o"/>
         <dest name="delread2~~_1 timem"/>
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
         <source name="+_2 out"/>
         <dest name="interp~_2 i"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>