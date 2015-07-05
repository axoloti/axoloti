<patch-1.0>
   <obj type="lfo/sine" sha="6215955d70f249301aa4141e75bdbc58d2782ae6" name="osc1" x="98" y="56">
      <params>
         <frac32.s.map name="pitch" value="-47.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="wave/flashplay" sha="fa9d96b0101e245814c5df3a60d5dd75583462ad" name="flashwaveplay_1" x="224" y="56">
      <params/>
      <attribs>
         <combo attributeName="sample" selection="rockwehrmann"/>
      </attribs>
   </obj>
   <comment type="patch/comment" name="test signal selection" x="546" y="98"/>
   <obj type="patch/inlet f" sha="ec45071db47e99aa672b4e8456c862acb1d95499" name="pitch" x="14" y="112">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="osc_2" x="224" y="112">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet i" sha="7a8c64d8041725644599af7ed4007b59beb57b1e" name="selection" x="476" y="112">
      <params/>
      <attribs/>
   </obj>
   <comment type="patch/comment" name="0 - audio sample" x="574" y="112"/>
   <comment type="patch/comment" name="1 - sine wave" x="574" y="126"/>
   <comment type="patch/comment" name="2 - sawtooth wave" x="574" y="140"/>
   <comment type="patch/comment" name="3 - noise" x="574" y="154"/>
   <obj type="saw~" sha="1a5088484533a3633e3eb849de47b478f1599369" name="saw_1" x="224" y="238">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="mux/mux 4" sha="4145bfd8751449238db95f24fb1cd2a69972d026" name="inmux32" x="462" y="238">
      <params/>
      <attribs/>
   </obj>
   <comment type="patch/comment" name="write to delayline" x="616" y="238"/>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="outlet_1" x="616" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="rand_1" x="336" y="294">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="osc1 wave"/>
         <dest name="flashwaveplay_1 trig"/>
      </net>
      <net>
         <source name="flashwaveplay_1 out"/>
         <dest name="inmux32 i0"/>
      </net>
      <net>
         <source name="osc_2 wave"/>
         <dest name="inmux32 i1"/>
      </net>
      <net>
         <source name="saw_1 wave"/>
         <dest name="inmux32 i2"/>
      </net>
      <net>
         <source name="rand_1 wave"/>
         <dest name="inmux32 i3"/>
      </net>
      <net>
         <source name="inmux32 o"/>
         <dest name="outlet_1 outlet"/>
      </net>
      <net>
         <source name="selection inlet"/>
         <dest name="inmux32 s"/>
      </net>
      <net>
         <source name="pitch inlet"/>
         <dest name="osc_2 pitchm"/>
         <dest name="saw_1 pitchm"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>normal</subpatchmode>
      <MidiChannel>0</MidiChannel>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>