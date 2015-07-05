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
   <obj type="env/adsr" sha="49cacd3004d35eb333d8c9004945061c0ce24b01" name="envf" x="378" y="168">
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
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_1" x="168" y="238">
      <params>
         <frac32.s.map name="pitch" value="-24.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="lfo" x="14" y="266">
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
   <obj type="osc/pwm" sha="a5f49fd39de0194bff6482e7b17ed3f35174578c" name="osc_2" x="168" y="350">
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
   <obj type="env/adsr" sha="49cacd3004d35eb333d8c9004945061c0ce24b01" name="enva" x="630" y="378">
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
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc1" x="14" y="448">
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
   <obj type="osc/saw" sha="1a5088484533a3633e3eb849de47b478f1599369" name="osc_3" x="266" y="448">
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
   <obj type="filter/vcf3~" sha="a4c7bb4270fc01be85be81c8f212636b9c54eaea" name="vcf3_1" x="448" y="574">
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
         <source name="keyb1 gate"/>
         <dest name="enva gate"/>
      </net>
      <net>
         <source name="pnoise out"/>
         <dest name="osc_2 pitchm"/>
         <dest name="mix11 bus_in"/>
         <dest name="mix22 in2"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="*c3 b"/>
      </net>
      <net>
         <source name="osc1 wave"/>
         <dest name="div321 in"/>
      </net>
      <net>
         <source name="div321 out"/>
         <dest name="mix11 in1"/>
      </net>
      <net>
         <source name="mix11 out"/>
         <dest name="osc_3 pitchm"/>
      </net>
      <net>
         <source name="pwm out"/>
         <dest name="osc_2 pwm"/>
      </net>
      <net>
         <source name="lfo wave"/>
         <dest name="pwm in1"/>
      </net>
      <net>
         <source name="c321 o"/>
         <dest name="pwm bus_in"/>
      </net>
      <net>
         <source name="keyb1 note"/>
         <dest name="pnoise bus_in"/>
         <dest name="osc_1 pitchm"/>
      </net>
      <net>
         <source name="div322 out"/>
         <dest name="pnoise in1"/>
      </net>
      <net>
         <source name="mix21 out"/>
         <dest name="vcf3_1 in"/>
      </net>
      <net>
         <source name="mix22 out"/>
         <dest name="vcf3_1 pitchm"/>
      </net>
      <net>
         <source name="*c3 result"/>
         <dest name="mix22 in1"/>
      </net>
      <net>
         <source name="pnoise2_1 out"/>
         <dest name="div322 in"/>
      </net>
      <net>
         <source name="keyb1 gate2"/>
         <dest name="envf gate"/>
      </net>
      <net>
         <source name="envf env"/>
         <dest name="*c3 a"/>
      </net>
      <net>
         <source name="osc_1 wave"/>
         <dest name="sub in1"/>
      </net>
      <net>
         <source name="vcf3_1 out"/>
         <dest name="sub bus_in"/>
      </net>
      <net>
         <source name="sub out"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="osc_2 wave"/>
         <dest name="mix21 in1"/>
      </net>
      <net>
         <source name="osc_3 wave"/>
         <dest name="mix21 in2"/>
      </net>
      <net>
         <source name="enva env"/>
         <dest name="div_1 in"/>
      </net>
      <net>
         <source name="div_1 out"/>
         <dest name="vca_1 v"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
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
</patch-1.0>