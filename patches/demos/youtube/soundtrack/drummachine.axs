<patch-1.0>
   <obj type="midi/in/keyb note" sha="5d623ad73ade2280421069a67ee3f30595794c72" name="keybnote1" x="14" y="14">
      <params/>
      <attribs>
         <spinner attributeName="note" value="60"/>
      </attribs>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="d_1" x="210" y="14">
      <params>
         <frac32.s.map name="d" onParent="true" value="-20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="noise.uniform_2" x="322" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/abs" sha="d9aef12fddc085a0a8a7a9868f1c845c538a9209" name="arithmetic.abs_1" x="420" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="91e2ecaa66340906540043ea41ac3987ce0aef17" name="filter.lowpass~_1" x="504" y="14">
      <params>
         <frac32.s.map name="freq" onParent="true" value="-29.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m x" sha="a2e1da37932bdfc8056cd08cca74d2ebc6735f40" name="env.envdlinmx_1" x="210" y="98">
      <params>
         <frac32.s.map name="d" onParent="true" value="-13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="arithmetic.*c_2" x="308" y="98">
      <params>
         <frac32.u.map name="amp" onParent="true" value="24.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc.sine_1" x="406" y="98">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-30.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="arithmetic.*_1" x="504" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="c47ceb7366785e0103cf880ce3450321491949f1" name="arithmetic.*_2" x="504" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="bd" x="588" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 64" sha="69b493f3d94607d38df5ab951d27622bec162349" name="c_1" x="126" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="noise.uniform_1" x="308" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb note" sha="5d623ad73ade2280421069a67ee3f30595794c72" name="keybnote1_" x="14" y="322">
      <params/>
      <attribs>
         <spinner attributeName="note" value="62"/>
      </attribs>
   </obj>
   <obj type="math/-" sha="27008b61438fd41bbc9a021b13c5eaad1cc101b5" name="-_1" x="126" y="322">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 2" sha="7fee48a2d38604fd5504303cbccef61f687d1593" name="div_1" x="210" y="336">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d m" sha="50cab575d33b76fbf6279e2c0fa381124d3f1032" name="env.decay.m_1" x="294" y="336">
      <params>
         <frac32.u.map name="d" onParent="true" value="11.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="gain.vca~_1" x="406" y="336">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/bp svf" sha="64eb414253ad09d2d8c5945dc48b8e1d2ac5b321" name="filter.bpfsvf~_1" x="490" y="336">
      <params>
         <frac32.s.map name="pitch" value="55.0"/>
         <frac32.u.map name="reso" value="23.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="snr" x="602" y="336">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="noise.uniform_2 wave"/>
         <dest name="arithmetic.abs_1 in"/>
      </net>
      <net>
         <source name="env.envdlinmx_1 env"/>
         <dest name="arithmetic.*_2 b"/>
      </net>
      <net>
         <source name="osc.sine_1 wave"/>
         <dest name="arithmetic.*_1 b"/>
      </net>
      <net>
         <source name="arithmetic.*_1 result"/>
         <dest name="arithmetic.*_2 a"/>
      </net>
      <net>
         <source name="arithmetic.*c_2 out"/>
         <dest name="osc.sine_1 pitchm"/>
      </net>
      <net>
         <source name="arithmetic.abs_1 out"/>
         <dest name="filter.lowpass~_1 in"/>
      </net>
      <net>
         <source name="filter.lowpass~_1 out"/>
         <dest name="arithmetic.*_1 a"/>
      </net>
      <net>
         <source name="keybnote1 gate"/>
         <dest name="d_1 trig"/>
         <dest name="env.envdlinmx_1 trig"/>
      </net>
      <net>
         <source name="d_1 env"/>
         <dest name="arithmetic.*c_2 in"/>
      </net>
      <net>
         <source name="keybnote1 velocity"/>
         <dest name="env.envdlinmx_1 dm"/>
      </net>
      <net>
         <source name="env.decay.m_1 env"/>
         <dest name="gain.vca~_1 v"/>
      </net>
      <net>
         <source name="noise.uniform_1 wave"/>
         <dest name="gain.vca~_1 a"/>
      </net>
      <net>
         <source name="gain.vca~_1 o"/>
         <dest name="filter.bpfsvf~_1 in"/>
      </net>
      <net>
         <source name="keybnote1_ gate"/>
         <dest name="env.decay.m_1 trig"/>
      </net>
      <net>
         <source name="keybnote1_ velocity"/>
         <dest name="-_1 in2"/>
      </net>
      <net>
         <source name="c_1 o"/>
         <dest name="-_1 in1"/>
      </net>
      <net>
         <source name="-_1 out"/>
         <dest name="div_1 in"/>
      </net>
      <net>
         <source name="div_1 out"/>
         <dest name="env.decay.m_1 dm"/>
      </net>
      <net>
         <source name="arithmetic.*_2 result"/>
         <dest name="bd outlet"/>
      </net>
      <net>
         <source name="filter.bpfsvf~_1 out"/>
         <dest name="snr outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>