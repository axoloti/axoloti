<patch-1.0>
   <obj type="patch/inlet a" sha="2944bdbaeb2a8a42d5a97163275d052f75668a86" name="in" x="14" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="env/follower" sha="8074c80ff135ec9b250e19c7a6671f8369b45ae4" name="follower_1" x="112" y="140">
      <params/>
      <attribs>
         <combo attributeName="time" selection="5.3ms"/>
      </attribs>
   </obj>
   <obj type="dyn/comp" sha="c8cc4a0549bb80df85534096de4eac1df8874dc" name="comp_1" x="196" y="140">
      <params>
         <frac32.u.map name="tresh" onParent="true" value="1.5"/>
         <frac32.u.map name="ratio" onParent="true" value="63.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="math/smooth2" sha="7a49483fff392fd1c10fb15f40dfc8f92f942192" name="smooth2_1" x="308" y="140">
      <params>
         <frac32.u.map name="risetime" onParent="true" value="52.5"/>
         <frac32.u.map name="falltime" onParent="true" value="45.5"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet f" sha="aac48d98f5fc2318197fd0a8587cf5f3e3ef4902" name="cv" x="490" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="gain/vca" sha="6bbeaeb94e74091879965461ad0cb043f2e7f6cf" name="vca_1" x="406" y="280">
      <params/>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="72226293648dde4dd4739bc1b8bc46a6bf660595" name="out" x="490" y="280">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source name="in inlet"/>
         <dest name="follower_1 in"/>
         <dest name="vca_1 a"/>
      </net>
      <net>
         <source name="follower_1 amp"/>
         <dest name="comp_1 in"/>
      </net>
      <net>
         <source name="comp_1 out"/>
         <dest name="smooth2_1 in"/>
      </net>
      <net>
         <source name="smooth2_1 out"/>
         <dest name="vca_1 v"/>
         <dest name="cv outlet"/>
      </net>
      <net>
         <source name="vca_1 o"/>
         <dest name="out outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>