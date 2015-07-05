<patch-1.0>
   <obj type="patch/inlet b" sha="e98d5f4c7b741588feaffc8629026f8d8e59e3ef" name="trig" x="14" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/d" sha="61669c0e3c33c6cb64ed388d75b8e756d064e5a4" name="d1" x="112" y="14">
      <params>
         <frac32.s.map name="d" onParent="true" value="-16.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="noise/uniform" sha="545caca792c6b8c27225590dd0240ef2d351a645" name="noise.uniform_2" x="224" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/abs" sha="d9aef12fddc085a0a8a7a9868f1c845c538a9209" name="arithmetic.abs_1" x="322" y="14">
      <params/>
      <attribs/>
   </obj>
   <obj type="filter/lp1" sha="91e2ecaa66340906540043ea41ac3987ce0aef17" name="lp1" x="406" y="14">
      <params>
         <frac32.s.map name="freq" onParent="true" value="32.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="env/d lin m x" sha="a2e1da37932bdfc8056cd08cca74d2ebc6735f40" name="d2" x="112" y="98">
      <params>
         <frac32.s.map name="d" onParent="true" value="-12.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*c" sha="1ea155bb99343babad87e3ff0de80e6bf568e8da" name="amt" x="210" y="98">
      <params>
         <frac32.u.map name="amp" onParent="true" value="63.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="57fd153c89df1299ed1ecbe27c961ac52732ab5" name="sine_1" x="308" y="98">
      <params>
         <frac32.s.map name="pitch" onParent="true" value="-44.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/*" sha="d67b6c172dd96232df67e96baf19e3062e880e68" name="*_1" x="406" y="98">
      <params/>
      <attribs/>
   </obj>
   <obj type="math/*" sha="c47ceb7366785e0103cf880ce3450321491949f1" name="*_2" x="406" y="168">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="490" y="168">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="noise.uniform_2 wave"/>
         <dest name="arithmetic.abs_1 in"/>
      </net>
      <net>
         <source name="d2 env"/>
         <dest name="*_2 b"/>
      </net>
      <net>
         <source name="sine_1 wave"/>
         <dest name="*_1 b"/>
      </net>
      <net>
         <source name="*_1 result"/>
         <dest name="*_2 a"/>
      </net>
      <net>
         <source name="amt out"/>
         <dest name="sine_1 pitchm"/>
      </net>
      <net>
         <source name="arithmetic.abs_1 out"/>
         <dest name="lp1 in"/>
      </net>
      <net>
         <source name="lp1 out"/>
         <dest name="*_1 a"/>
      </net>
      <net>
         <source name="trig inlet"/>
         <dest name="d1 trig"/>
         <dest name="d2 trig"/>
      </net>
      <net>
         <source name="d1 env"/>
         <dest name="amt in"/>
      </net>
      <net>
         <source name="*_2 result"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>