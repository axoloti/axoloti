<patch-1.0>
   <obj type="ctrl/dial p" sha="1f21216639bb798a4ea7902940999a5bcfd0de90" name="dial_1" x="182" y="14">
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
         <source name="+c_1 out"/>
         <dest name="rectifier_1 in"/>
      </net>
      <net>
         <source name="div_1 out"/>
         <dest name="+c_1 in1"/>
      </net>
      <net>
         <source name="dial_1 out"/>
         <dest name="nointerp_1 i"/>
      </net>
      <net>
         <source name="nointerp_1 o"/>
         <dest name="+c_1 in2"/>
         <dest name="muls_1 in"/>
         <dest name="-_1 in2"/>
      </net>
      <net>
         <source name="rectifier_1 out"/>
         <dest name="inv_1 in"/>
      </net>
      <net>
         <source name="inv_1 out"/>
         <dest name="+c_2 in1"/>
      </net>
      <net>
         <source name="+c_2 out"/>
         <dest name="rectifier_2 in"/>
      </net>
      <net>
         <source name="muls_1 out"/>
         <dest name="+c_2 in2"/>
      </net>
      <net>
         <source name="rectifier_2 out"/>
         <dest name="-_1 in1"/>
      </net>
      <net>
         <source name="in inlet"/>
         <dest name="div_1 in"/>
      </net>
      <net>
         <source name="-_1 out"/>
         <dest name="muls_2 in"/>
      </net>
      <net>
         <source name="muls_2 out"/>
         <dest name="outlet_1 outlet"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
</patch-1.0>