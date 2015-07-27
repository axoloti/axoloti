<patch-1.0>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="level" x="182" y="14">
      <params>
         <frac32.u.map name="value" onParent="true" value="4.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="42" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/div 2" sha="5df68ad33aa1633cb7cb1724fcd41eee28932582" name="div_1" x="112" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="conv/nointerp" sha="77d868d4547e0e29a1aa7711f764a6d3957177f9" name="nointerp_1" x="252" y="56">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+c_1" x="112" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="dist/rectifier" sha="6a81663bcc6a29c922883f499193baff3d14c5d" name="rectifier_1" x="182" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/muls 2" sha="17100b9369a00a2265f0f754ff1c0ec87f9c6690" name="muls_1" x="252" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/+" sha="f21fcf9a2511404a296065f4ba87ab840e153161" name="+c_2" x="252" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="dist/rectifier" sha="6a81663bcc6a29c922883f499193baff3d14c5d" name="rectifier_2" x="322" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/-" sha="86190d21676ef888e72ad0ae4fde0d817119f21c" name="-_1" x="392" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/inv" sha="dd3d98b9ec6f2b9231cb1d00d0f9667152537120" name="inv_1" x="112" y="154">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/muls 8" sha="3b876043fb7aa6dff276407826cba102606eb254" name="muls_2" x="392" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="392" y="238">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="+c_1" outlet="out"/>
         <dest obj="rectifier_1" inlet="in"/>
      </net>
      <net>
         <source obj="div_1" outlet="out"/>
         <dest obj="+c_1" inlet="in1"/>
      </net>
      <net>
         <source obj="level" outlet="out"/>
         <dest obj="nointerp_1" inlet="i"/>
      </net>
      <net>
         <source obj="nointerp_1" outlet="o"/>
         <dest obj="+c_1" inlet="in2"/>
         <dest obj="muls_1" inlet="in"/>
         <dest obj="-_1" inlet="in2"/>
      </net>
      <net>
         <source obj="rectifier_1" outlet="out"/>
         <dest obj="inv_1" inlet="in"/>
      </net>
      <net>
         <source obj="inv_1" outlet="out"/>
         <dest obj="+c_2" inlet="in1"/>
      </net>
      <net>
         <source obj="+c_2" outlet="out"/>
         <dest obj="rectifier_2" inlet="in"/>
      </net>
      <net>
         <source obj="muls_1" outlet="out"/>
         <dest obj="+c_2" inlet="in2"/>
      </net>
      <net>
         <source obj="rectifier_2" outlet="out"/>
         <dest obj="-_1" inlet="in1"/>
      </net>
      <net>
         <source obj="in" outlet="inlet"/>
         <dest obj="div_1" inlet="in"/>
      </net>
      <net>
         <source obj="-_1" outlet="out"/>
         <dest obj="muls_2" inlet="in"/>
      </net>
      <net>
         <source obj="muls_2" outlet="out"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
      <MidiChannel>1</MidiChannel>
      <NPresets>0</NPresets>
      <NPresetEntries>0</NPresetEntries>
      <NModulationSources>0</NModulationSources>
      <NModulationTargetsPerSource>0</NModulationTargetsPerSource>
      <Author>Johannes Taelman</Author>
   </settings>
   <notes><![CDATA[Symmetrical hard clipping]]></notes>
   <windowPos>
      <x>525</x>
      <y>362</y>
      <width>702</width>
      <height>548</height>
   </windowPos>
</patch-1.0>