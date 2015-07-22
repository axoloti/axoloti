<patch-1.0>
   <obj type="wave/play fn" sha="60926d7b3ffb8a3a9d5b9d72347f2797f413f2fc" uuid="25910f7130532e4934eed3cf2a934324790d0f00" name="play_1" x="140" y="0">
      <params/>
      <attribs>
         <table attributeName="fn" table=""/>
      </attribs>
   </obj>
   <obj type="patch/inlet f" sha="8e69e1ab7ccd8afaefdc23146c50149809b64955" uuid="5c585d2dcd9c05631e345ac09626a22a639d7c13" name="pitch" x="28" y="28">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/sine" sha="edec4a9d5f533ea748cd564ce8c69673dd78742f" uuid="556d54977a43597bf97427ca6c13e3d446859933" name="sine_1" x="182" y="98">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/outlet a" sha="9e7e04867e1d37837b0924c9bf18c44ac68602e6" uuid="abd8c5fd3b0524a6630f65cad6dc27f6c58e2a3e" name="outlet_1" x="490" y="140">
      <params/>
      <attribs/>
   </obj>
   <obj type="mux/mux 4" sha="9f7f3b7a0abf760b335371219c835086f87c62b0" uuid="e511105cf5630d1a0b4a144dc3fabb3cc7c07bd" name="mux_1" x="350" y="196">
      <params/>
      <attribs/>
   </obj>
   <obj type="osc/phasor" sha="343e0dfbaa48c69032d959ee1e7398e45000e0bf" uuid="db03c099c44cbefe218b88b702004bcfd6ba87d6" name="phasor_1" x="182" y="210">
      <params>
         <frac32.s.map name="pitch" value="0.0"/>
      </params>
      <attribs/>
   </obj>
   <obj type="patch/inlet i" sha="525f64aba3d51dde5253cccedd116ec84bf5d5d1" uuid="f11927f00c59219df0c50f73056aa19f125540b7" name="selection" x="28" y="294">
      <params/>
      <attribs/>
   </obj>
   <obj type="noise/pink" sha="73a919bf86dac4805c4300760b5052e1ec2453c6" uuid="72c03a2468ee865f248733fcf9b12d4cf42b5a61" name="pink_1" x="210" y="308">
      <params/>
      <attribs/>
   </obj>
   <nets>
      <net>
         <source obj="pitch" outlet="inlet"/>
         <dest obj="phasor_1" inlet="pitch"/>
         <dest obj="sine_1" inlet="pitch"/>
      </net>
      <net>
         <source obj="play_1" outlet="out"/>
         <dest obj="mux_1" inlet="i0"/>
      </net>
      <net>
         <source obj="sine_1" outlet="wave"/>
         <dest obj="mux_1" inlet="i1"/>
      </net>
      <net>
         <source obj="pink_1" outlet="out"/>
         <dest obj="mux_1" inlet="i3"/>
      </net>
      <net>
         <source obj="selection" outlet="inlet"/>
         <dest obj="mux_1" inlet="s"/>
      </net>
      <net>
         <source obj="mux_1" outlet="o"/>
         <dest obj="outlet_1" inlet="outlet"/>
      </net>
      <net>
         <source obj="phasor_1" outlet="phasor"/>
         <dest obj="mux_1" inlet="i2"/>
      </net>
   </nets>
   <settings>
      <subpatchmode>no</subpatchmode>
   </settings>
   <notes><![CDATA[]]></notes>
   <windowPos>
      <x>702</x>
      <y>580</y>
      <width>668</width>
      <height>535</height>
   </windowPos>
</patch-1.0>