<patch-1.0>
   <obj type="c" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="interval" x="100" y="20">
      <params>
         <frac32.u.map name="value" onParent="true" value="12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="inlet" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="pitch" x="40" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_1" x="180" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc~" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="transpose" x="280" y="80">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="div4" sha="24e4544f0f846eb56ca3b8d30b635a5eb50caa29" name="div4_1" x="400" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="outlet~" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet~_1" x="480" y="80">
      <params/>
      <attribs/>
   </obj>
   <obj type="*" sha="c491f4fcc25792418020a0d98176129e3beac388" name="*_1" x="120" y="120">
      <params/>
      <attribs/>
   </obj>
   <obj type="polyindex" sha="d4abd919262b0b2a913b0aeb4ddf2dd44a6e39af" name="polyindex_1" x="20" y="140">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="transpose wave"/>
         <dest name="div4_1 in"/>
      </net>
      <net>
         <source name="div4_1 out"/>
         <dest name="outlet~_1 outlet"/>
      </net>
      <net>
         <source name="polyindex_1 index"/>
         <dest name="*_1 b"/>
      </net>
      <net>
         <source name="pitch inlet"/>
         <dest name="+_1 in1"/>
      </net>
      <net>
         <source name="*_1 result"/>
         <dest name="+_1 in2"/>
      </net>
      <net>
         <source name="+_1 out"/>
         <dest name="transpose pitchm"/>
      </net>
      <net>
         <source name="interval out"/>
         <dest name="*_1 a"/>
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
   <notes><![CDATA[makes an additive series of sine waves with intervals defined in semitones]]></notes>
</patch-1.0>