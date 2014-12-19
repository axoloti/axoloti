<patch-1.0>
   <obj type="patch/polyindex" sha="d4abd919262b0b2a913b0aeb4ddf2dd44a6e39af" name="polyindex1" x="14" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="polyIndexSum" x="140" y="20">
      <params/>
      <attribs/>
   </obj>
   <obj type="keyb" sha="b8deb97637e54be31fcb62e849e4fa406e72256e" name="keyb1" x="14" y="100">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/modsource" sha="45ebafea67ca2fe0720654bb75664ba8a032a332" name="velo" x="182" y="100">
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
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="mod_by_velo" x="14" y="280">
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
         <source name="polyindex1 index"/>
         <dest name="polyIndexSum outlet"/>
      </net>
      <net>
         <source name="c11 o"/>
         <dest name="oneSum outlet"/>
      </net>
      <net>
         <source name="keyb1 gate"/>
         <dest name="velo trig"/>
      </net>
      <net>
         <source name="keyb1 velocity"/>
         <dest name="velo v"/>
      </net>
      <net>
         <source name="on_parent out"/>
         <dest name="c_on_parent outlet"/>
      </net>
      <net>
         <source name="mod_by_velo out"/>
         <dest name="mod_by_velo outlet"/>
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
</patch-1.0>