<patch-1.0>
   <obj type="patch/polyindex" sha="499a6acc6df4a405a04b6ad8df8e7ab489ee5234" name="polyindex1" x="14" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="polyIndexSum" x="140" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="midi/in/keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="14" y="100">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/modsource" sha="7c62ac4dc64da3f882068c7a4a4b5860cba293bf" name="velo" x="182" y="100">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/c 1" sha="ea3f53ea316f208a3ff2da087adce60e5e319c91" name="c11" x="56" y="220">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="oneSum" x="140" y="220">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="mod_by_velod" x="14" y="280">
      <params>
         <frac32.u.map name="value" value="0.0">
            <modulators>
               <modulation sourceName="velo" value="20.0"/>
            </modulators>
         </frac32.u.map>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="mod_by_velo" x="140" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="on_parent" x="14" y="380">
      <params>
         <frac32.u.map name="value" onParent="true" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="c_on_parent" x="140" y="380">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="polyindex1" outlet="index"/>
         <dest obj="polyIndexSum" inlet="outlet"/>
      </net>
      <net>
         <source obj="c11" outlet="o"/>
         <dest obj="oneSum" inlet="outlet"/>
      </net>
      <net>
         <source obj="keyb1" outlet="gate"/>
         <dest obj="velo" inlet="trig"/>
      </net>
      <net>
         <source obj="keyb1" outlet="velocity"/>
         <dest obj="velo" inlet="v"/>
      </net>
      <net>
         <source obj="on_parent" outlet="out"/>
         <dest obj="c_on_parent" inlet="outlet"/>
      </net>
      <net>
         <source obj="mod_by_velod" outlet="out"/>
         <dest obj="mod_by_velo" inlet="outlet"/>
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
   <windowPos>
      <x>1300</x>
      <y>460</y>
      <width>492</width>
      <height>690</height>
   </windowPos>
</patch-1.0>