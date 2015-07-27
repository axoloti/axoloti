<patch-1.0>
   <obj type="midi/in/keyb zone lru" sha="b9b28cf52b3421368e367f3718404222910c57a5" name="keybzone_lru_1" x="28" y="0">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="0"/>
         <spinner attributeName="endNote" value="126"/>
      </attribs>
   </obj>
   <obj type="lfo/square" sha="b4420b58ca2ae5ece53d53540bc91bc9ed7a4b83" name="square_1" x="280" y="0">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="rand/uniform f trig" sha="926c3f305c1c8031d3cad3e29eb688cee124ab2e" name="randtrig_1" x="378" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="dist/slew" sha="521ab25a6a0aa5d12cbe59b5c1f9630a2f01e167" name="slew_1" x="490" y="0">
      <params>
         <frac32.u.map name="slew" value="0.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc_1" x="168" y="60">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="c4000e3e6417d9d57283d66476b83f22f975ff09" name="envahd_1" x="350" y="140">
      <params>
         <frac32.s.map name="a" value="0.0"/>
         <frac32.s.map name="d" value="54.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d" sha="d9f7cfe1295d7bcc550714a18126d4f73c7c8411" name="envd_1" x="140" y="180">
      <params>
         <frac32.s.map name="d" onParent="true" value="2.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="mix2_1" x="238" y="180">
      <params>
         <frac32.u.map name="gain1" value="33.5"/>
         <frac32.u.map name="gain2" value="20.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c_1" x="532" y="200">
      <params>
         <frac32.u.map name="amp" value="16.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="osc~_1" x="14" y="340">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="sawsync~" sha="4e614d245ac924f3f8f7ea02b2a4ba9e84769849" name="sawsync~_1" x="112" y="340">
      <params>
         <frac32.s.map name="pitch" value="11.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca~_1" x="252" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/hp1" sha="f9059d53a5af0890dc1a1268b5679aa335731857" name="hipass~_1" x="378" y="340">
      <params>
         <frac32.u.map name="freq" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="audio/out stereo" sha="b933bb91801a126126313c11f773158b5ca2face" name="dac~_1" x="658" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="disp/scope 128 b" sha="375509bcb23b2f6a69f9cd82349f406464c8295e" name="scope_128s_v2_1" x="252" y="440">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="hipass~_1" outlet="out"/>
         <dest obj="dac~_1" inlet="left"/>
         <dest obj="dac~_1" inlet="right"/>
      </net>
      <net>
         <source obj="osc~_1" outlet="wave"/>
         <dest obj="sawsync~_1" inlet="sync"/>
      </net>
      <net>
         <source obj="keybzone_lru_1" outlet="note"/>
         <dest obj="osc~_1" inlet="pitch"/>
         <dest obj="osc_1" inlet="pitch"/>
         <dest obj="mix2_1" inlet="bus_in"/>
      </net>
      <net>
         <source obj="keybzone_lru_1" outlet="gate"/>
         <dest obj="envahd_1" inlet="gate"/>
         <dest obj="envd_1" inlet="trig"/>
      </net>
      <net>
         <source obj="envd_1" outlet="env"/>
         <dest obj="mix2_1" inlet="in1"/>
      </net>
      <net>
         <source obj="mix2_1" outlet="out"/>
         <dest obj="sawsync~_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="envahd_1" outlet="env"/>
         <dest obj="*c_1" inlet="in"/>
      </net>
      <net>
         <source obj="*c_1" outlet="out"/>
         <dest obj="vca~_1" inlet="v"/>
      </net>
      <net>
         <source obj="sawsync~_1" outlet="wave"/>
         <dest obj="vca~_1" inlet="a"/>
         <dest obj="scope_128s_v2_1" inlet="in"/>
      </net>
      <net>
         <source obj="vca~_1" outlet="o"/>
         <dest obj="hipass~_1" inlet="in"/>
      </net>
      <net>
         <source obj="square_1" outlet="wave"/>
         <dest obj="randtrig_1" inlet="trig"/>
      </net>
      <net>
         <source obj="randtrig_1" outlet="rand"/>
         <dest obj="slew_1" inlet="in"/>
      </net>
      <net>
         <source obj="slew_1" outlet="out"/>
         <dest obj="mix2_1" inlet="in2"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>2</y>
      <width>968</width>
      <height>750</height>
   </windowPos>
</patch-1.0>