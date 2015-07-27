<patch-1.0>
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="noise.uniform_1" x="126" y="14">
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
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="env.decay.m_1" x="126" y="70">
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
   <obj type="filter/bp svf m" sha="24097930d951f375e0839b70f065d71a782d8b23" name="bp" x="392" y="70">
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
         <source obj="noise.uniform_1" outlet="wave"/>
         <dest obj="gain.vca~_1" inlet="a"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="gate"/>
         <dest obj="env.decay.m_1" inlet="trig"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="velocity"/>
         <dest obj="soft_1" inlet="a"/>
      </net>
      <net>
         <source obj="gain.vca~_1" outlet="o"/>
         <dest obj="bp" inlet="in"/>
      </net>
      <net>
         <source obj="bp" outlet="out"/>
         <dest obj="out" inlet="outlet"/>
      </net>
      <net>
         <source obj="keyb_1" outlet="note"/>
         <dest obj="bp" inlet="pitch"/>
      </net>
      <net>
         <source obj="env.decay.m_1" outlet="env"/>
         <dest obj="soft_1" inlet="b"/>
      </net>
      <net>
         <source obj="soft_1" outlet="result"/>
         <dest obj="gain.vca~_1" inlet="v"/>
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
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>814</width>
      <height>400</height>
   </windowPos>
</patch-1.0>