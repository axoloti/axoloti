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
   <obj type="mix/mix 1" sha="75de53c9e6783829b405b702a6e7feb5ccaa8b00" name="pnoise" x="154" y="140">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="13.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/adsr" sha="2c4b16047d03b574d8a72b651f130895749eb670" name="envf" x="378" y="168">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
         <frac32.u.map name="s" onParent="true" value="0.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*c3" x="476" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" name="osc_1" x="168" y="238">
      <params>
         <frac32.s.map name="pitch" value="-24.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="a2851b3d62ed0faceefc98038d9571422f0ce260" name="lfo" x="14" y="266">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-9.0"/>
      </params>
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
   <obj type="mix/mix 2" sha="90ac1a48634cb998bf3d0387eb5191531d6241fe" name="mix22" x="392" y="364">
      <params>
         <frac32.u.map name="gain1" value="63.5"/>
         <frac32.u.map name="gain2" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/adsr" sha="2c4b16047d03b574d8a72b651f130895749eb670" name="enva" x="630" y="378">
      <params>
         <frac32.s.map name="a" onParent="true" value="0.0"/>
         <frac32.s.map name="d" onParent="true" value="0.0"/>
         <frac32.u.map name="s" onParent="true" value="0.0"/>
         <frac32.s.map name="r" onParent="true" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 4" sha="1b9659261cffe6011ae0ea5a26a8c2029d53828d" name="div_1" x="728" y="378">
      <params/>
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
   <obj type="mix/mix 2" sha="67c325bf12e5b73ad58df89daf7899831777003c" name="mix21" x="364" y="574">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="7.5"/>
         <frac32.u.map name="gain2" onParent="true" value="8.0"/>
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
   <obj type="mix/mix 1" sha="f543e080bd2111cba525885443039f346703a594" name="sub" x="546" y="574">
      <params>
         <frac32.u.map name="gain1" onParent="true" value="11.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="630" y="574">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="686" y="574">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="keyb1" outlet="gate"/>
         <dest obj="enva" inlet="gate"/>
      </net>
      <net>
         <source obj="pnoise" outlet="out"/>
         <dest obj="osc_2" inlet="pitch"/>
         <dest obj="mix11" inlet="bus_in"/>
         <dest obj="mix22" inlet="in2"/>
      </net>
      <net>
         <source obj="keyb1" outlet="velocity"/>
         <dest obj="*c3" inlet="b"/>
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
         <source obj="lfo" outlet="wave"/>
         <dest obj="pwm" inlet="in1"/>
      </net>
      <net>
         <source obj="c321" outlet="o"/>
         <dest obj="pwm" inlet="bus_in"/>
      </net>
      <net>
         <source obj="keyb1" outlet="note"/>
         <dest obj="pnoise" inlet="bus_in"/>
         <dest obj="osc_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="div322" outlet="out"/>
         <dest obj="pnoise" inlet="in1"/>
      </net>
      <net>
         <source obj="mix21" outlet="out"/>
         <dest obj="vcf3_1" inlet="in"/>
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
      <net>
         <source obj="osc_1" outlet="wave"/>
         <dest obj="sub" inlet="in1"/>
      </net>
      <net>
         <source obj="vcf3_1" outlet="out"/>
         <dest obj="sub" inlet="bus_in"/>
      </net>
      <net>
         <source obj="sub" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="osc_2" outlet="wave"/>
         <dest obj="mix21" inlet="in1"/>
      </net>
      <net>
         <source obj="osc_3" outlet="wave"/>
         <dest obj="mix21" inlet="in2"/>
      </net>
      <net>
         <source obj="enva" outlet="env"/>
         <dest obj="div_1" inlet="in"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="out" inlet="outlet"/>
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
      <width>1038</width>
      <height>884</height>
   </windowPos>
</patch-1.0>