/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package generatedobjects;

import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32SMapRatio;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Reverb extends gentools {

    static void GenerateAll() {
        String catName = "filter";
        WriteAxoObject(catName, Create_Allpass());
        WriteAxoObject(catName, Create_FeedbackComb());
        catName = "reverb";
        WriteAxoObject(catName, Create_FDN4());
    }

    static AxoObject Create_Allpass() {
        AxoObject o = new AxoObject("allpass", "allpass reverb section");
        o.inlets.add(new InletFrac32Buffer("in", "in"));
        o.outlets.add(new OutletFrac32Buffer("out", "out"));
        o.params.add(new ParameterFrac32SMapRatio("g"));
        o.attributes.add(new AxoAttributeSpinner("delay", 1, 10000, 1000));
        o.sLocalData = "int16_t d[%delay%];\n"
                + "int dpos;\n";
        o.sInitCode = "int i;\n"
                + "for (i=0;i<%delay%;i++)\n"
                + "   d[i] = 0;\n"
                + "dpos = 0;\n";
        o.sKRateCode = "int32_t g2 = %g%<<4;\n"
                + "int32_t g2c = ((1<<31)-1)-g2;\n";
//                + "int32_t g2c_inv = ((1<<31)-1)-g2;\n";
        o.sSRateCode = "int32_t dout =  d[dpos]<<16;\n"
                + "int32_t din = ___SMMLA(g2,dout,%in%>>1);\n"
                + "d[dpos++]=din>>15;\n"
                + "%out% = ___SMMLS(g2,din<<1,dout>>1)<<1;\n"
                + "if (dpos == %delay%) dpos = 0;\n";
        /*
         o.sSRateCode = "int32_t dout =  d[dpos]<<15;\n"
         + "int32_t din = ___SMMUL(g2c,%in%);\n"
         + "din = ___SMMLA(g2,dout,din);\n"
         + "d[dpos++]=din>>14;\n"
         + "%out% = ___SMMLS(g2,din<<2,dout);\n"
         + "if (dpos == %delay%) dpos = 0;\n";
    
         */
        return o;
    }

    static AxoObject Create_FeedbackComb() {
        AxoObject o = new AxoObject("fdbkcomb", "feedback comb filter, y(n) = b*x(n)+a*y(n-D)");
        o.inlets.add(new InletFrac32Buffer("in", "in"));
        o.outlets.add(new OutletFrac32Buffer("out", "out"));
        o.params.add(new ParameterFrac32SMapRatio("a"));
        o.params.add(new ParameterFrac32SMapRatio("b"));
        o.attributes.add(new AxoAttributeSpinner("delay", 1, 10000, 1000));
        o.sLocalData = "int16_t d[%delay%];\n"
                + "int dpos;\n";
        o.sInitCode = "int i;\n"
                + "for (i=0;i<%delay%;i++)\n"
                + "   d[i] = 0;\n"
                + "dpos = 0;\n";
        o.sKRateCode = "int32_t a2 = %a%<<4;\n"
                + "int32_t b2 = %b%<<4;\n";
        o.sSRateCode = "int32_t dout =  d[dpos]<<16;\n"
                + "int32_t din = ___SMMUL(b2,%in%);\n"
                + "din = ___SMMLA(a2,dout,din);\n"
                + "d[dpos++]=din>>15;\n"
                + "%out% = din;\n"
                + "if (dpos == %delay%) dpos = 0;\n";
        return o;
    }

    static AxoObject Create_FDN4() {
        AxoObject o = new AxoObject("fdn4", "Feedback delay network with 4 delay lines. High quality 32bit.");
        o.inlets.add(new InletFrac32Buffer("in1", "in1"));
        o.inlets.add(new InletFrac32Buffer("in2", "in2"));
        o.inlets.add(new InletFrac32Buffer("in3", "in3"));
        o.inlets.add(new InletFrac32Buffer("in4", "in4"));
        o.attributes.add(new AxoAttributeSpinner("d1", 2, 4096, 128));
        o.attributes.add(new AxoAttributeSpinner("d2", 2, 4096, 128));
        o.attributes.add(new AxoAttributeSpinner("d3", 2, 4096, 128));
        o.attributes.add(new AxoAttributeSpinner("d4", 2, 4096, 128));
        o.outlets.add(new OutletFrac32Buffer("out1", "out1"));
        o.outlets.add(new OutletFrac32Buffer("out2", "out2"));
        o.outlets.add(new OutletFrac32Buffer("out3", "out3"));
        o.outlets.add(new OutletFrac32Buffer("out4", "out4"));
        o.params.add(new ParameterFrac32UMap("g"));
        o.sLocalData = "int32_t d1d[%d1%];\n"
                + "int32_t d2d[%d2%];\n"
                + "int32_t d3d[%d3%];\n"
                + "int32_t d4d[%d4%];\n"
                + "uint32_t d1p;\n"
                + "uint32_t d2p;\n"
                + "uint32_t d3p;\n"
                + "uint32_t d4p;\n";
        o.sInitCode = "int i;"
                + "for(i=0;i<%d1%;i++) d1d[i]=0;\n"
                + "for(i=0;i<%d2%;i++) d2d[i]=0;\n"
                + "for(i=0;i<%d3%;i++) d3d[i]=0;\n"
                + "for(i=0;i<%d4%;i++) d4d[i]=0;\n"
                + "d1p = 0;\n"
                + "d2p = 0;\n"
                + "d3p = 0;\n"
                + "d4p = 0;\n";
        o.sKRateCode = "int32_t g2 = 755299123+%g%;\n";
        o.sSRateCode = "int32_t i1 = %in1% + d1d[d1p];\n"
                + "int32_t i2 = %in2% + d2d[d2p];\n"
                + "int32_t i3 = %in3% + d3d[d3p];\n"
                + "int32_t i4 = %in4% + d4d[d4p];\n"
                + "int32_t t1 = i2 + i3;\n"
                + "int32_t t2 = -i1 - i4;\n"
                + "int32_t t3 = i1 - i4;\n"
                + "int32_t t4 = i2 - i3;\n"
                + "int32_t o1 = ___SMMLA(t1,g2,t1>>1);\n"
                + "int32_t o2 = ___SMMLA(t2,g2,t2>>1);\n"
                + "int32_t o3 = ___SMMLA(t3,g2,t3>>1);\n"
                + "int32_t o4 = ___SMMLA(t4,g2,t4>>1);\n"
                + "d1d[d1p] = o1;\n"
                + "d2d[d2p] = o2;\n"
                + "d3d[d3p] = o3;\n"
                + "d4d[d4p] = o4;\n"
                + "d1p++;\n"
                + "d2p++;\n"
                + "d3p++;\n"
                + "d4p++;\n"
                + "if (d1p == %d1%) d1p = 0;\n"
                + "if (d2p == %d2%) d2p = 0;\n"
                + "if (d3p == %d3%) d3p = 0;\n"
                + "if (d4p == %d4%) d4p = 0;\n"
                + "%out1% = o1;\n"
                + "%out2% = o2;\n"
                + "%out3% = o3;\n"
                + "%out4% = o4;\n";
        return o;
    }
}
