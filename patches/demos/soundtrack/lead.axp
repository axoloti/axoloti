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
   <obj type="env/ahd lin m x" sha="62a6c82f56f3c5daa5fb4f269acb3a33cf34992b" name="envahd1" x="462" y="28">
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
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="lfo_flt" x="644" y="112">
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
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="lfo_pitch" x="280" y="168">
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
   <obj type="osc/saw" sha="1a5088484533a3633e3eb849de47b478f1599369" name="saw_1" x="364" y="252">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/lp m" sha="649887a8ccb34e5928d77426b8db79bed3e57f0f" name="lpf_1" x="476" y="252">
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
         <source name="keybzone_lru1 note"/>
         <dest name="smooth1 in"/>
      </net>
      <net>
         <source name="lpf_1 out"/>
         <dest name="infclip_1_ in"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="vca_1 v"/>
         <dest name="*c2_ in2"/>
      </net>
      <net>
         <source name="infclip_1_ out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="*c2_ out"/>
         <dest name="lpf_1 pitchm"/>
      </net>
      <net>
         <source name="lfo_flt wave"/>
         <dest name="*c2_ in1"/>
      </net>
      <net>
         <source name="lfo_pitch_amt out"/>
         <dest name="saw_1 pitchm"/>
      </net>
      <net>
         <source name="smooth1 out"/>
         <dest name="lfo_pitch_amt bus_in"/>
      </net>
      <net>
         <source name="lfo_pitch wave"/>
         <dest name="div321 in"/>
      </net>
      <net>
         <source name="div321 out"/>
         <dest name="lfo_pitch_amt in1"/>
      </net>
      <net>
         <source name="c1 out"/>
         <dest name="smooth1_ in"/>
      </net>
      <net>
         <source name="smooth1_ out"/>
         <dest name="*c2_ bus_in"/>
      </net>
      <net>
         <source name="keybzone_lru1 gate"/>
         <dest name="envahd1 trig"/>
      </net>
      <net>
         <source name="keybzone_lru1 velocity"/>
         <dest name="inv1 in"/>
      </net>
      <net>
         <source name="inv1 out"/>
         <dest name="*c3_ in"/>
      </net>
      <net>
         <source name="*c3_ out"/>
         <dest name="envahd1 am"/>
      </net>
      <net>
         <source name="*c3__ out"/>
         <dest name="inv1_ in"/>
      </net>
      <net>
         <source name="inv1_ out"/>
         <dest name="envahd1 dm"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="lpf_1 in"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
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
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>