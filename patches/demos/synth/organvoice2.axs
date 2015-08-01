<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="envd1" x="112" y="14">
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
   <obj type="filter/bp svf m" sha="24097930d951f375e0839b70f065d71a782d8b23" name="lpfm_1" x="448" y="154">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
         <frac32.u.map name="reso" value="55.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="24097930d951f375e0839b70f065d71a782d8b23" name="lpfm_1_" x="448" y="294">
      <params>
         <frac32.s.map name="pitch" value="12.0"/>
         <frac32.u.map name="reso" value="55.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="24097930d951f375e0839b70f065d71a782d8b23" name="lpfm_1__" x="448" y="434">
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
         <source obj="keyb1" outlet="note"/>
         <dest obj="lpfm_1" inlet="pitch"/>
         <dest obj="lpfm_1_" inlet="pitch"/>
         <dest obj="lpfm_1__" inlet="pitch"/>
      </net>
      <net>
         <source obj="lpfm_1" outlet="out"/>
         <dest obj="mix31" inlet="in1"/>
      </net>
      <net>
         <source obj="lpfm_1_" outlet="out"/>
         <dest obj="mix31" inlet="in2"/>
      </net>
      <net>
         <source obj="lpfm_1__" outlet="out"/>
         <dest obj="mix31" inlet="in3"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="lpfm_1" inlet="in"/>
         <dest obj="lpfm_1_" inlet="in"/>
         <dest obj="lpfm_1__" inlet="in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="gate2"/>
         <dest obj="envd1" inlet="trig"/>
         <dest obj="excit" inlet="in1"/>
         <dest obj="reso" inlet="in"/>
      </net>
      <net>
         <source obj="reso" outlet="out"/>
         <dest obj="lpfm_1" inlet="reso"/>
         <dest obj="lpfm_1_" inlet="reso"/>
         <dest obj="lpfm_1__" inlet="reso"/>
      </net>
      <net>
         <source obj="envd1" outlet="env"/>
         <dest obj="excit" inlet="in2"/>
      </net>
      <net>
         <source obj="excit" outlet="out"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="mix31" outlet="out"/>
         <dest obj="o" inlet="outlet"/>
      </net>
      <net>
         <source obj="exci" outlet="inlet"/>
         <dest obj="vca_1" inlet="a"/>
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
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>298</x>
      <y>149</y>
      <width>996</width>
      <height>744</height>
   </windowPos>
</patch-1.0>