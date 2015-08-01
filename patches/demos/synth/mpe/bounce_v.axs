<patch-1.0>
   <obj type="math/div 16" sha="de53fa1ae8551aca115c5ab76dd646a20339668" name="div_1" x="504" y="42">
      <params/>
      <attribs/>
   </obj>
   <obj type="lfo/square" sha="b4420b58ca2ae5ece53d53540bc91bc9ed7a4b83" name="bounce time" x="574" y="42">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="10.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m" sha="7cd630c1ecdc64542bf24aadc0f3114629fdf37d" uuid="4b3d3642bf931c1d20cbcbaa3b9d538e7cd861fa" name="decay time" x="770" y="98">
      <params>
         <frac32.s.map name="d" onParent="true" value="-42.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="midi/ctrl/mpe" sha="7d045bd3ec2368f42f90d2a142e3100fa2cf23c1" uuid="94323477e6476a10b9332922e5dfcd2705641af1" name="mpe_1" x="196" y="210">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="7b02dcb8eae6c8e1f4f1f9f532ad6cd7f0d9a69" name="inv_1" x="518" y="210">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="60143a29e35f452025e9edaa2dec6e660ecb9c6e" uuid="7d5ef61c3bcd571ee6bbd8437ef3612125dfb225" name="decay scale" x="588" y="210">
      <params>
         <frac32.u.map name="amp" onParent="true" value="46.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="b031e26920f6cf5c1a53377ee6021573c4e3ac02" name="*_1" x="952" y="238">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="1022" y="238">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="1106" y="238">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/square" sha="7cccf8a95bf312ecc084f11f532cf5fda00b8c58" name="square_1" x="616" y="350">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="filter/bp svf m" sha="24097930d951f375e0839b70f065d71a782d8b23" name="bp_1" x="770" y="350">
      <params>
         <frac32.s.map name="pitch" value="13.0"/>
         <frac32.u.map name="reso" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/div 2" sha="7fee48a2d38604fd5504303cbccef61f687d1593" name="div_3" x="574" y="462">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="81c2c147faf13ae4c2d00419326d0b6aec478b27" name="+_1" x="644" y="462">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="bounce time" outlet="wave"/>
         <dest obj="decay time" inlet="trig"/>
      </net>
      <net>
         <source obj="*_1" outlet="result"/>
         <dest obj="vca_1" inlet="v"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="bounce time" inlet="pitch"/>
      </net>
      <net>
         <source obj="inv_1" outlet="out"/>
         <dest obj="decay scale" inlet="in"/>
      </net>
      <net>
         <source obj="+_1" outlet="out"/>
         <dest obj="bp_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="square_1" outlet="wave"/>
         <dest obj="bp_1" inlet="in"/>
      </net>
      <net>
         <source obj="bp_1" outlet="out"/>
         <dest obj="vca_1" inlet="a"/>
      </net>
      <net>
         <source obj="div_3" outlet="out"/>
         <dest obj="+_1" inlet="in1"/>
      </net>
      <net>
         <source obj="vca_1" outlet="o"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="mpe_1" outlet="pitch"/>
         <dest obj="square_1" inlet="pitch"/>
         <dest obj="+_1" inlet="in2"/>
      </net>
      <net>
         <source obj="mpe_1" outlet="gate"/>
         <dest obj="bounce time" inlet="reset"/>
      </net>
      <net>
         <source obj="mpe_1" outlet="timbre"/>
         <dest obj="div_1" inlet="in"/>
         <dest obj="div_3" inlet="in"/>
         <dest obj="inv_1" inlet="in"/>
      </net>
      <net>
         <source obj="mpe_1" outlet="pressure"/>
         <dest obj="*_1" inlet="b"/>
      </net>
      <net>
         <source obj="decay time" outlet="env"/>
         <dest obj="*_1" inlet="a"/>
      </net>
      <net>
         <source obj="decay scale" outlet="out"/>
         <dest obj="decay time" inlet="d"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>polyexpression</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
      <Author>Mark Harris</Author>
      <License>GPL</License>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>1021</x>
      <y>432</y>
      <width>1514</width>
      <height>772</height>
   </windowPos>
</patch-1.0>