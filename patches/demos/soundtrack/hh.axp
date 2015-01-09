<patch-1.0>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="noise.uniform_1" x="126" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb zone" sha="44dada96531ef6abd5c77f60bb92dbb2ec0d6d35" name="keyb_1" x="14" y="70">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="63"/>
         <spinner attributeName="endNote" value="127"/>
      </attribs>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="env.decay.m_1" x="126" y="70">
      <params>
         <frac32.s.map name="d" MidiCC="1" value="-39.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="soft_1" x="238" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="gain.vca~_1" x="308" y="70">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="561e56d24bf5c702564c7d043fda6d0d3003deec" name="bp" x="392" y="70">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
         <frac32.u.map name="reso" value="23.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="504" y="70">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="noise.uniform_1 wave"/>
         <dest name="gain.vca~_1 a"/>
      </net>
      <net>
         <source name="keyb_1 gate"/>
         <dest name="env.decay.m_1 trig"/>
      </net>
      <net>
         <source name="keyb_1 velocity"/>
         <dest name="soft_1 a"/>
      </net>
      <net>
         <source name="gain.vca~_1 o"/>
         <dest name="bp in"/>
      </net>
      <net>
         <source name="bp out"/>
         <dest name="out outlet"/>
      </net>
      <net>
         <source name="keyb_1 note"/>
         <dest name="bp pitchm"/>
      </net>
      <net>
         <source name="env.decay.m_1 env"/>
         <dest name="soft_1 b"/>
      </net>
      <net>
         <source name="soft_1 result"/>
         <dest name="gain.vca~_1 v"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>