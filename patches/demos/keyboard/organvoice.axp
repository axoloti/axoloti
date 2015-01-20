<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="ahd_1" x="182" y="0">
      <params>
         <frac32.u.map name="a" onParent="true" value="6.5"/>
         <frac32.u.map name="d" onParent="true" value="37.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="p" x="322" y="28">
      <params>
         <frac32.s.map name="d" onParent="true" value="-14.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 4" sha="1b9659261cffe6011ae0ea5a26a8c2029d53828d" name="div_1" x="420" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="pressure" x="490" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="sine_1" x="126" y="126">
      <params>
         <frac32.s.map name="pitch" value="-11.989999771118164"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="pm1" x="28" y="154">
      <params>
         <frac32.u.map name="amp" onParent="true" value="12.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="d36ecbd55095f4888a0ebda8efda68e015c5e72b" name="pm2" x="28" y="238">
      <params>
         <frac32.u.map name="amp" onParent="true" value="10.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="sine_2" x="126" y="238">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet f" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="pressure_in" x="280" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/-" sha="27008b61438fd41bbc9a021b13c5eaad1cc101b5" name="-1" x="350" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/satp" sha="107e97e0797ac54d70617cbd5af301ac85ec58a8" name="satp1" x="420" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="3ade427ae7291fdf62058c4243fe718758187105" name="pm3" x="28" y="350">
      <params>
         <frac32.u.map name="amp" onParent="true" value="10.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="sine_3" x="126" y="350">
      <params>
         <frac32.s.map name="pitch" value="11.989999771118164"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="noise" x="28" y="476">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 4" sha="217ea56f47dd7397f64930ffcddab7c794ad4f5c" name="mix_1" x="322" y="476">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="12.5"/>
         <frac32.u.map name="gain2" onParent="true" value="19.0"/>
         <frac32.u.map name="gain3" onParent="true" value="21.5"/>
         <frac32.u.map name="gain4" onParent="true" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="462" y="476">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="518" y="476">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 gate"/>
         <dest name="p trig"/>
         <dest name="ahd_1 gate"/>
      </net>
      <net>
         <source name="sine_1 wave"/>
         <dest name="mix_1 in1"/>
      </net>
      <net>
         <source name="sine_2 wave"/>
         <dest name="mix_1 in2"/>
      </net>
      <net>
         <source name="sine_3 wave"/>
         <dest name="pm2 in"/>
         <dest name="mix_1 in3"/>
      </net>
      <net>
         <source name="pm2 out"/>
         <dest name="sine_2 pm"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="sine_1 pitchm"/>
         <dest name="sine_2 pitchm"/>
         <dest name="sine_3 pitchm"/>
      </net>
      <net>
         <source name="mix_1 out"/>
         <dest name="vca_1 a"/>
         <dest name="pm1 in"/>
         <dest name="pm3 in"/>
      </net>
      <net>
         <source name="pm1 out"/>
         <dest name="sine_1 pm"/>
      </net>
      <net>
         <source name="pm3 out"/>
         <dest name="sine_3 pm"/>
      </net>
      <net>
         <source name="p env"/>
         <dest name="div_1 in"/>
      </net>
      <net>
         <source name="div_1 out"/>
         <dest name="pressure outlet"/>
      </net>
      <net>
         <source name="ahd_1 env"/>
         <dest name="-1 in1"/>
      </net>
      <net>
         <source name="-1 out"/>
         <dest name="satp1 in"/>
      </net>
      <net>
         <source name="pressure_in inlet"/>
         <dest name="-1 in2"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="satp1 out"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="noise inlet"/>
         <dest name="mix_1 in4"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polychannel</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>