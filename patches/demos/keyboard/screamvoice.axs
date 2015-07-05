<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="0" y="0">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c1" x="588" y="14">
      <params>
         <frac32.u.map name="value" MidiCC="1" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth1" x="658" y="14">
      <params>
         <frac32.u.map name="time" value="41.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="224" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c3" x="336" y="28">
      <params>
         <frac32.u.map name="amp" value="35.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env_old/ahd lin m x" sha="62a6c82f56f3c5daa5fb4f269acb3a33cf34992b" name="envahd1" x="448" y="28">
      <params>
         <frac32.s.map name="a" value="10.0"/>
         <frac32.s.map name="d" value="17.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c3_" x="224" y="70">
      <params>
         <frac32.u.map name="amp" value="16.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1_" x="336" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc1" x="616" y="112">
      <params>
         <frac32.s.map name="pitch" value="-61.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="*c2" x="714" y="112">
      <params>
         <frac32.u.map name="gain1" value="4.5"/>
         <frac32.u.map name="gain2" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc2" x="70" y="168">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 32" sha="41545586fbaebf68c4240a279a5619af09b5c1a1" name="div321" x="168" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix11" x="70" y="252">
      <params>
         <frac32.u.map name="gain1" value="1.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/saw" sha="1a5088484533a3633e3eb849de47b478f1599369" name="saw_1" x="154" y="252">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="67c325bf12e5b73ad58df89daf7899831777003c" name="mix21" x="350" y="280">
      <params>
         <frac32.u.map name="gain1" value="40.0"/>
         <frac32.u.map name="gain2" value="1.5541162490844727"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/lp m" sha="649887a8ccb34e5928d77426b8db79bed3e57f0f" name="lpf_1" x="448" y="280">
      <params>
         <frac32.s.map name="pitch" value="-20.0"/>
         <frac32.u.map name="reso" MidiCC="11" value="59.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="dist/inf" sha="3b7380de881bb6eafc05c60b35ae2351855d6c09" name="infclip_1_" x="560" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="616" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="d36ecbd55095f4888a0ebda8efda68e015c5e72b" name="*c1" x="672" y="280">
      <params>
         <frac32.u.map name="amp" value="53.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="v" x="784" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="clean" x="784" y="322">
      <params/>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="rand_1" x="84" y="350">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="91e2ecaa66340906540043ea41ac3987ce0aef17" name="lowpass_1" x="168" y="350">
      <params>
         <frac32.s.map name="freq" value="60.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 32" sha="5c939ae8539de1dcd5808b75c6bba2de5f593827" name="div161" x="266" y="350">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="a" x="784" y="364">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="lpf_1 out"/>
         <dest name="infclip_1_ in"/>
         <dest name="clean outlet"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="mix21 in1"/>
      </net>
      <net>
         <source name="envahd1 env"/>
         <dest name="vca_1 v"/>
         <dest name="*c2 in2"/>
         <dest name="a outlet"/>
      </net>
      <net>
         <source name="infclip_1_ out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="*c1 in"/>
      </net>
      <net>
         <source name="mix21 out"/>
         <dest name="lpf_1 in"/>
      </net>
      <net>
         <source name="*c2 out"/>
         <dest name="lpf_1 pitchm"/>
      </net>
      <net>
         <source name="osc1 wave"/>
         <dest name="*c2 in1"/>
      </net>
      <net>
         <source name="mix11 out"/>
         <dest name="saw_1 pitchm"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="mix11 bus_in"/>
      </net>
      <net>
         <source name="osc2 wave"/>
         <dest name="div321 in"/>
      </net>
      <net>
         <source name="div321 out"/>
         <dest name="mix11 in1"/>
      </net>
      <net>
         <source name="c1 out"/>
         <dest name="smooth1 in"/>
      </net>
      <net>
         <source name="smooth1 out"/>
         <dest name="*c2 bus_in"/>
      </net>
      <net>
         <source name="rand_1 wave"/>
         <dest name="lowpass_1 in"/>
      </net>
      <net>
         <source name="lowpass_1 out"/>
         <dest name="div161 in"/>
      </net>
      <net>
         <source name="div161 out"/>
         <dest name="mix21 in2"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="envahd1 trig"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="inv1 in"/>
      </net>
      <net>
         <source name="inv1 out"/>
         <dest name="*c3 in"/>
      </net>
      <net>
         <source name="*c3 out"/>
         <dest name="envahd1 am"/>
      </net>
      <net>
         <source name="keyb1 releaseVelocity"/>
         <dest name="*c3_ in"/>
      </net>
      <net>
         <source name="*c3_ out"/>
         <dest name="inv1_ in"/>
      </net>
      <net>
         <source name="inv1_ out"/>
         <dest name="envahd1 dm"/>
      </net>
      <net>
         <source name="*c1 out"/>
         <dest name="v outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Author></Author>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>