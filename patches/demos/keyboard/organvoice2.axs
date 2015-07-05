<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="envd1" x="112" y="14">
      <params>
         <frac32.s.map name="d" onParent="true" value="-48.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="excit" x="210" y="14">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="6.0"/>
         <frac32.u.map name="gain2" onParent="true" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="exci" x="294" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="378" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="reso" x="350" y="154">
      <params>
         <frac32.u.map name="amp" onParent="true" value="3.989999771118164"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="561e56d24bf5c702564c7d043fda6d0d3003deec" name="lpfm_1" x="448" y="154">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
         <frac32.u.map name="reso" value="55.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="561e56d24bf5c702564c7d043fda6d0d3003deec" name="lpfm_1_" x="448" y="294">
      <params>
         <frac32.s.map name="pitch" value="12.0"/>
         <frac32.u.map name="reso" value="55.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="561e56d24bf5c702564c7d043fda6d0d3003deec" name="lpfm_1__" x="448" y="434">
      <params>
         <frac32.s.map name="pitch" value="19.03000020980835"/>
         <frac32.u.map name="reso" value="55.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 3" sha="3d37ac18e4393438042204df12d17aec499f09fb" name="mix31" x="588" y="434">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="14.5"/>
         <frac32.u.map name="gain2" onParent="true" value="14.5"/>
         <frac32.u.map name="gain3" onParent="true" value="13.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="o" x="686" y="434">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="keyb1 note"/>
         <dest name="lpfm_1 pitchm"/>
         <dest name="lpfm_1_ pitchm"/>
         <dest name="lpfm_1__ pitchm"/>
      </net>
      <net>
         <source name="lpfm_1 out"/>
         <dest name="mix31 in1"/>
      </net>
      <net>
         <source name="lpfm_1_ out"/>
         <dest name="mix31 in2"/>
      </net>
      <net>
         <source name="lpfm_1__ out"/>
         <dest name="mix31 in3"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="lpfm_1 in"/>
         <dest name="lpfm_1_ in"/>
         <dest name="lpfm_1__ in"/>
      </net>
      <net>
         <source name="keyb1 gate2"/>
         <dest name="envd1 trig"/>
         <dest name="excit in1"/>
         <dest name="reso in"/>
      </net>
      <net>
         <source name="reso out"/>
         <dest name="lpfm_1 resom"/>
         <dest name="lpfm_1_ resom"/>
         <dest name="lpfm_1__ resom"/>
      </net>
      <net>
         <source name="envd1 env"/>
         <dest name="excit in2"/>
      </net>
      <net>
         <source name="excit out"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="mix31 out"/>
         <dest name="o outlet"/>
      </net>
      <net>
         <source name="exci inlet"/>
         <dest name="vca_1 a"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>2</NModulationSources>
      <NModulationTargetsPerSource>2</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>298</x>
      <y>149</y>
      <width>996</width>
      <height>744</height>
   </windowPos>
</patch-1.0>