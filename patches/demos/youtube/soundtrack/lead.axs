<patch-1.0>
   <obj type="midi/in/keyb zone lru" sha="b9b28cf52b3421368e367f3718404222910c57a5" name="keybzone_lru1" x="0" y="14">
      <params/>
      <attribs>
         <spinner attributeName="startNote" value="50"/>
         <spinner attributeName="endNote" value="120"/>
      </attribs>
   </obj>
   <obj type="math/smooth2" sha="7a49483fff392fd1c10fb15f40dfc8f92f942192" name="smooth1" x="126" y="14">
      <params>
         <frac32.u.map name="risetime" value="18.0"/>
         <frac32.u.map name="falltime" value="3.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c1" x="616" y="14">
      <params>
         <frac32.u.map name="value" MidiCC="1" value="56.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth1_" x="686" y="14">
      <params>
         <frac32.u.map name="time" value="58.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="266" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c3_" x="364" y="28">
      <params>
         <frac32.u.map name="amp" value="35.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd lin m" sha="1bff4be3aeae590d80327085f5f7c771667d8938" name="envahd1" x="462" y="28">
      <params>
         <frac32.s.map name="a" value="10.0"/>
         <frac32.s.map name="d" value="17.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c3__" x="266" y="70">
      <params>
         <frac32.u.map name="amp" value="16.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1_" x="364" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="lfo_flt" x="644" y="112">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-61.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="*c2_" x="742" y="112">
      <params>
         <frac32.u.map name="gain1" value="16.5"/>
         <frac32.u.map name="gain2" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="lfo_pitch" x="280" y="168">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 32" sha="41545586fbaebf68c4240a279a5619af09b5c1a1" name="div321" x="378" y="210">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="lfo_pitch_amt" x="280" y="252">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="4.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="saw_1" x="364" y="252">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/lp m" sha="c2224dc682842eae1af4496f3f94a6afc1525ee4" name="lpf_1" x="476" y="252">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-11.0"/>
         <frac32.u.map name="reso" onParent="true" MidiCC="11" value="60.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="dist/inf" sha="3b7380de881bb6eafc05c60b35ae2351855d6c09" name="infclip_1_" x="588" y="252">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="644" y="252">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="924" y="322">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="keybzone_lru1" outlet="note"/>
         <dest obj="smooth1" inlet="in"/>
      </net>
      <net>
         <source obj="lpf_1" outlet="out"/>
         <dest obj="infclip_1_" inlet="in"/>
      </net>
      <net>
         <source obj="envahd1" outlet="env"/>
         <dest obj="vca_1" inlet="v"/>
         <dest obj="*c2_" inlet="in2"/>
      </net>
      <net>
         <source obj="infclip_1_" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="*c2_" outlet="out"/>
         <dest obj="lpf_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="lfo_flt" outlet="wave"/>
         <dest obj="*c2_" inlet="in1"/>
      </net>
      <net>
         <source obj="lfo_pitch_amt" outlet="out"/>
         <dest obj="saw_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="smooth1" outlet="out"/>
         <dest obj="lfo_pitch_amt" inlet="bus_in"/>
      </net>
      <net>
         <source obj="lfo_pitch" outlet="wave"/>
         <dest obj="div321" inlet="in"/>
      </net>
      <net>
         <source obj="div321" outlet="out"/>
         <dest obj="lfo_pitch_amt" inlet="in1"/>
      </net>
      <net>
         <source obj="c1" outlet="out"/>
         <dest obj="smooth1_" inlet="in"/>
      </net>
      <net>
         <source obj="smooth1_" outlet="out"/>
         <dest obj="*c2_" inlet="bus_in"/>
      </net>
      <net>
         <source obj="keybzone_lru1" outlet="gate"/>
         <dest obj="envahd1" inlet="trig"/>
      </net>
      <net>
         <source obj="keybzone_lru1" outlet="velocity"/>
         <dest obj="inv1" inlet="in"/>
      </net>
      <net>
         <source obj="inv1" outlet="out"/>
         <dest obj="*c3_" inlet="in"/>
      </net>
      <net>
         <source obj="*c3_" outlet="out"/>
         <dest obj="envahd1" inlet="a"/>
      </net>
      <net>
         <source obj="*c3__" outlet="out"/>
         <dest obj="inv1_" inlet="in"/>
      </net>
      <net>
         <source obj="inv1_" outlet="out"/>
         <dest obj="envahd1" inlet="d"/>
      </net>
      <net>
         <source obj="saw_1" outlet="wave"/>
         <dest obj="lpf_1" inlet="in"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="out" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>4</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>1234</width>
      <height>632</height>
   </windowPos>
</patch-1.0>