<patch-1.0>
   <zombie type="comment" uuid="zombie" name="normal" x="80" y="40"/>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c1" x="80" y="60">
      <params>
         <frac32.u.map name="value" value="1.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="one" x="240" y="60">
      <params/>
      <attribs/>
   </obj>
   <zombie type="comment" uuid="zombie" name="this one : control on parent!" x="80" y="160"/>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c2" x="80" y="180">
      <params>
         <frac32.u.map name="value" onParent="true" value="2.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="two" x="240" y="180">
      <params/>
      <attribs/>
   </obj>
   <zombie type="comment" uuid="zombie" name="normal too" x="80" y="300"/>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c3" x="80" y="320">
      <params>
         <frac32.u.map name="value" value="3.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="three" x="240" y="320">
      <params/>
      <attribs/>
   </obj>
   <zombie type="comment" uuid="zombie" name="this one : control on parent again" x="80" y="420"/>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="c4" x="80" y="440">
      <params>
         <frac32.u.map name="value" onParent="true" value="4.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="four" x="240" y="440">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="c1" outlet="out"/>
         <dest obj="one" inlet="outlet"/>
      </net>
      <net>
         <source obj="c2" outlet="out"/>
         <dest obj="two" inlet="outlet"/>
      </net>
      <net>
         <source obj="c3" outlet="out"/>
         <dest obj="three" inlet="outlet"/>
      </net>
      <net>
         <source obj="c4" outlet="out"/>
         <dest obj="four" inlet="outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
      <NPresets>8</NPresets>
      <NPresetEntries>32</NPresetEntries>
      <NModulationSources>8</NModulationSources>
      <NModulationTargetsPerSource>8</NModulationTargetsPerSource>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>0</x>
      <y>2</y>
      <width>550</width>
      <height>750</height>
   </windowPos>
</patch-1.0>