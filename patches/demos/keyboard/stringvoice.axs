<patch-1.0>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="rand/pink" sha="8adc4b3e5aae39c856d02171f3d0b04aeec9625e" name="pnoise2_1" x="14" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 32" sha="41545586fbaebf68c4240a279a5619af09b5c1a1" name="div322" x="84" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix13" x="154" y="140">
      <params>
         <frac32.u.map name="gain1" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1" x="308" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c4" x="364" y="154">
      <params>
         <frac32.u.map name="amp" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/adsr" sha="2c4b16047d03b574d8a72b651f130895749eb670" name="envf" x="644" y="154">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
         <frac32.u.map name="s" onParent="true" value="0.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="osc_1" x="168" y="238">
      <params>
         <frac32.s.map name="pitch" value="-24.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv1_" x="308" y="238">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="*c4_" x="364" y="238">
      <params>
         <frac32.u.map name="amp" value="8.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/ahd m" sha="37f06c85b287c96369d67bcccbf212cea1ef68b" name="envahd21" x="434" y="238">
      <params>
         <frac32.u.map name="a" onParent="true" value="58.0"/>
         <frac32.u.map name="d" onParent="true" value="42.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc2" x="14" y="266">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-9.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*c3" x="742" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 32" sha="5797bce9fc4e770d9c14890b0fa899f126c5bc38" name="c321" x="0" y="350">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="pwm" x="70" y="350">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/pwm" sha="4f216b9a125822434f813198e9be4da0b5e8b042" name="osc_2" x="168" y="350">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="mix22" x="644" y="350">
      <params>
         <frac32.u.map name="gain1" value="26.0"/>
         <frac32.u.map name="gain2" value="23.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="rand/pink oct" sha="a591724f2d4958930ffd8ca14d025f0ce1d728c0" name="pnoise2_1_" x="434" y="378">
      <params/>
      <attribs>
         <combo attributeName="octaves" selection="1"/>
      </attribs>
   </obj>
   <obj type="math/smooth" sha="3a277a80f7590217e14fde92e834ace04d2b75cb" name="smooth1" x="518" y="378">
      <params>
         <frac32.u.map name="time" value="62.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="osc1" x="14" y="448">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 32" sha="41545586fbaebf68c4240a279a5619af09b5c1a1" name="div321" x="112" y="448">
      <params/>
      <attribs/>
   </obj>
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="mix11" x="182" y="448">
      <params>
         <frac32.u.map name="gain1" value="1.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/saw" sha="fe2c3c02396657dfbc225c73f9340ad0c4c3eea6" name="osc_3" x="266" y="448">
      <params>
         <frac32.s.map name="pitch" value="-12.020000457763672"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mix/mix 3" sha="3d37ac18e4393438042204df12d17aec499f09fb" name="mix21" x="364" y="574">
      <params>
         <frac32.u.map name="gain1" value="4.0"/>
         <frac32.u.map name="gain2" value="21.0"/>
         <frac32.u.map name="gain3" value="26.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/vcf3" sha="2a5cccf4517f54d2450ab7518925f49e4c41c837" name="vcf3_1" x="448" y="574">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="7.0"/>
         <frac32.u.map name="reso" onParent="true" value="46.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="672" y="588">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="d36ecbd55095f4888a0ebda8efda68e015c5e72b" name="*c1" x="728" y="588">
      <params>
         <frac32.u.map name="amp" value="20.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="784" y="588">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="*c1" inlet="in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="gate"/>
         <dest obj="envahd21" inlet="gate"/>
      </net>
      <net>
         <source obj="osc_1" outlet="wave"/>
         <dest obj="mix21" inlet="in1"/>
      </net>
      <net>
         <source obj="osc_2" outlet="wave"/>
         <dest obj="mix21" inlet="in2"/>
      </net>
      <net>
         <source obj="osc_3" outlet="wave"/>
         <dest obj="mix21" inlet="in3"/>
      </net>
      <net>
         <source obj="mix13" outlet="out"/>
         <dest obj="osc_1" inlet="pitch"/>
         <dest obj="osc_2" inlet="pitch"/>
         <dest obj="mix11" inlet="bus_in"/>
         <dest obj="mix22" inlet="bus_in"/>
      </net>
      <net>
         <source obj="*c1" outlet="out"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="keyb1" outlet="velocity"/>
         <dest obj="inv1" inlet="in"/>
         <dest obj="*c3" inlet="b"/>
      </net>
      <net>
         <source obj="inv1" outlet="out"/>
         <dest obj="*c4" inlet="in"/>
      </net>
      <net>
         <source obj="*c4" outlet="out"/>
         <dest obj="envahd21" inlet="a"/>
      </net>
      <net>
         <source obj="keyb1" outlet="releaseVelocity"/>
         <dest obj="inv1_" inlet="in"/>
      </net>
      <net>
         <source obj="inv1_" outlet="out"/>
         <dest obj="*c4_" inlet="in"/>
      </net>
      <net>
         <source obj="*c4_" outlet="out"/>
         <dest obj="envahd21" inlet="d"/>
      </net>
      <net>
         <source obj="envahd21" outlet="env"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="osc1" outlet="wave"/>
         <dest obj="div321" inlet="in"/>
      </net>
      <net>
         <source obj="div321" outlet="out"/>
         <dest obj="mix11" inlet="in1"/>
      </net>
      <net>
         <source obj="mix11" outlet="out"/>
         <dest obj="osc_3" inlet="pitch"/>
      </net>
      <net>
         <source obj="pwm" outlet="out"/>
         <dest obj="osc_2" inlet="pw"/>
      </net>
      <net>
         <source obj="osc2" outlet="wave"/>
         <dest obj="pwm" inlet="in1"/>
      </net>
      <net>
         <source obj="c321" outlet="o"/>
         <dest obj="pwm" inlet="bus_in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="note"/>
         <dest obj="mix13" inlet="bus_in"/>
      </net>
      <net>
         <source obj="div322" outlet="out"/>
         <dest obj="mix13" inlet="in1"/>
      </net>
      <net>
         <source obj="mix21" outlet="out"/>
         <dest obj="vcf3_1" inlet="in"/>
      </net>
      <net>
         <source obj="vcf3_1" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="pnoise2_1_" outlet="out"/>
         <dest obj="smooth1" inlet="in"/>
      </net>
      <net>
         <source obj="smooth1" outlet="out"/>
         <dest obj="mix22" inlet="in2"/>
      </net>
      <net>
         <source obj="mix22" outlet="out"/>
         <dest obj="vcf3_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="*c3" outlet="result"/>
         <dest obj="mix22" inlet="in1"/>
      </net>
      <net>
         <source obj="pnoise2_1" outlet="out"/>
         <dest obj="div322" inlet="in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="gate2"/>
         <dest obj="envf" inlet="gate"/>
      </net>
      <net>
         <source obj="envf" outlet="env"/>
         <dest obj="*c3" inlet="a"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyphonic</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <HasMidiChannelSelector>true</HasMidiChannelSelector>
      <NPresets>8</NPresets>
      <NPresetEntries>4</NPresetEntries>
      <NModulationSources>4</NModulationSources>
      <NModulationTargetsPerSource>4</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>23</y>
      <width>1094</width>
      <height>898</height>
   </windowPos>
</patch-1.0>