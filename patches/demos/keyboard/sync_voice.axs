<patch-1.0>
   <obj type="keybzone_lru" sha="b9b28cf52b3421368e367f3718404222910c57a5" name="keybzone_lru_1" x="28" y="0">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="0"/>
         <spinner attributeName="endNote" value="126"/>
      </attribs>
   </obj>
   <obj type="lfo/square" sha="2619a1d94a07bf82a1e47e4e34485e9c4916cc18" name="square_1" x="280" y="0">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="rand/uniform f trig" sha="7c693e3fcb8abe7dc3908628ef0eb911a4a19ce1" name="randtrig_1" x="378" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="dist/slew" sha="521ab25a6a0aa5d12cbe59b5c1f9630a2f01e167" name="slew_1" x="490" y="0">
      <params>
         <frac32.u.map name="slew" value="0.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc_1" x="168" y="60">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd" sha="ce83118fedc4aa5d92661fa45a38dcece91fbee4" name="envahd_1" x="350" y="140">
      <params>
         <frac32.u.map name="a" value="0.0"/>
         <frac32.u.map name="d" value="54.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d" sha="531c9ac204c2f9ac2fcf690f587fd986e998ec50" name="envd_1" x="140" y="180">
      <params>
         <frac32.u.map name="d" onParent="true" value="2.5"/>
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
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc~_1" x="14" y="340">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="sawsync~" sha="4173669d858d9547874f581b85f4e60313401651" name="sawsync~_1" x="112" y="340">
      <params>
         <frac32.s.map name="pitch" value="11.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca~_1" x="252" y="340">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/hp1" sha="77d00a776a68c62ae84817f1fbdd50fd8102dc54" name="hipass~_1" x="378" y="340">
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
         <source name="hipass~_1 out"/>
         <dest name="dac~_1 left"/>
         <dest name="dac~_1 right"/>
      </net>
      <net>
         <source name="osc~_1 wave"/>
         <dest name="sawsync~_1 sync"/>
      </net>
      <net>
         <source name="keybzone_lru_1 note"/>
         <dest name="osc~_1 pitchm"/>
         <dest name="osc_1 pitchm"/>
         <dest name="mix2_1 bus_in"/>
      </net>
      <net>
         <source name="keybzone_lru_1 gate"/>
         <dest name="envahd_1 gate"/>
         <dest name="envd_1 trig"/>
      </net>
      <net>
         <source name="envd_1 env"/>
         <dest name="mix2_1 in1"/>
      </net>
      <net>
         <source name="mix2_1 out"/>
         <dest name="sawsync~_1 pitchm"/>
      </net>
      <net>
         <source name="envahd_1 env"/>
         <dest name="*c_1 in"/>
      </net>
      <net>
         <source name="*c_1 out"/>
         <dest name="vca~_1 v"/>
      </net>
      <net>
         <source name="sawsync~_1 wave"/>
         <dest name="vca~_1 a"/>
         <dest name="scope_128s_v2_1 in"/>
      </net>
      <net>
         <source name="vca~_1 o"/>
         <dest name="hipass~_1 in"/>
      </net>
      <net>
         <source name="square_1 wave"/>
         <dest name="randtrig_1 trig"/>
      </net>
      <net>
         <source name="randtrig_1 rand"/>
         <dest name="slew_1 in"/>
      </net>
      <net>
         <source name="slew_1 out"/>
         <dest name="mix2_1 in2"/>
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
</patch-1.0>