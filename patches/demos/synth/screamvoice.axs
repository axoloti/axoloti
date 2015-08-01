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
   <zombie type="env_old/ahd lin m x" uuid="zombie" name="envahd1" x="448" y="28"/>
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
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc1" x="616" y="112">
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
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc2" x="70" y="168">
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
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="saw_1" x="154" y="252">
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
   <obj type="filter/lp m" sha="c2224dc682842eae1af4496f3f94a6afc1525ee4" name="lpf_1" x="448" y="280">
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
   <obj type="noise/uniform" sha="117e0adca76d1dc3810e120a06d022ef06093103" name="rand_1" x="84" y="350">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="290a234e2f83eb072198d0158bcd5da02a3606c3" name="lowpass_1" x="168" y="350">
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
         <source obj="lpf_1" outlet="out"/>
         <dest obj="infclip_1_" inlet="in"/>
         <dest obj="clean" inlet="outlet"/>
      </net>
      <net>
         <source obj="saw_1" outlet="wave"/>
         <dest obj="mix21" inlet="in1"/>
      </net>
      <net>
         <source class="axoloti.outlets.OutletInstanceZombie" obj="envahd1" outlet="env"/>
         <dest obj="vca_1" inlet="v"/>
         <dest obj="*c2" inlet="in2"/>
         <dest obj="a" inlet="outlet"/>
      </net>
      <net>
         <source obj="infclip_1_" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="*c1" inlet="in"/>
      </net>
      <net>
         <source obj="mix21" outlet="out"/>
         <dest obj="lpf_1" inlet="in"/>
      </net>
      <net>
         <source obj="*c2" outlet="out"/>
         <dest obj="lpf_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="osc1" outlet="wave"/>
         <dest obj="*c2" inlet="in1"/>
      </net>
      <net>
         <source obj="mix11" outlet="out"/>
         <dest obj="saw_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="keyb1" outlet="note"/>
         <dest obj="mix11" inlet="bus_in"/>
      </net>
      <net>
         <source obj="osc2" outlet="wave"/>
         <dest obj="div321" inlet="in"/>
      </net>
      <net>
         <source obj="div321" outlet="out"/>
         <dest obj="mix11" inlet="in1"/>
      </net>
      <net>
         <source obj="c1" outlet="out"/>
         <dest obj="smooth1" inlet="in"/>
      </net>
      <net>
         <source obj="smooth1" outlet="out"/>
         <dest obj="*c2" inlet="bus_in"/>
      </net>
      <net>
         <source obj="rand_1" outlet="wave"/>
         <dest obj="lowpass_1" inlet="in"/>
      </net>
      <net>
         <source obj="lowpass_1" outlet="out"/>
         <dest obj="div161" inlet="in"/>
      </net>
      <net>
         <source obj="div161" outlet="out"/>
         <dest obj="mix21" inlet="in2"/>
      </net>
      <net>
         <source obj="keyb1" outlet="gate"/>
         <dest class="axoloti.inlets.InletInstanceZombie" obj="envahd1" inlet="trig"/>
      </net>
      <net>
         <source obj="keyb1" outlet="velocity"/>
         <dest obj="inv1" inlet="in"/>
      </net>
      <net>
         <source obj="inv1" outlet="out"/>
         <dest obj="*c3" inlet="in"/>
      </net>
      <net>
         <source obj="*c3" outlet="out"/>
         <dest class="axoloti.inlets.InletInstanceZombie" obj="envahd1" inlet="am"/>
      </net>
      <net>
         <source obj="keyb1" outlet="releaseVelocity"/>
         <dest obj="*c3_" inlet="in"/>
      </net>
      <net>
         <source obj="*c3_" outlet="out"/>
         <dest obj="inv1_" inlet="in"/>
      </net>
      <net>
         <source obj="inv1_" outlet="out"/>
         <dest class="axoloti.inlets.InletInstanceZombie" obj="envahd1" inlet="dm"/>
      </net>
      <net>
         <source obj="*c1" outlet="out"/>
         <dest obj="v" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>2</y>
      <width>1094</width>
      <height>674</height>
   </windowPos>
</patch-1.0>