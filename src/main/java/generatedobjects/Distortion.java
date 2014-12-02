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

import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletFrac32BufferBipolar;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Distortion extends gentools {

    static void GenerateAll() {
        String catName = "dist";
        WriteAxoObject(catName, new AxoObject[]{Create_Softsat(), Create_SoftsatTilde()});
        WriteAxoObject(catName, Create_InfGain());
        WriteAxoObject(catName, Create_SchmittTriggerTilde());
        WriteAxoObject(catName, Create_SampleHoldBL());
        WriteAxoObject(catName, new AxoObject[]{Create_Slew(), Create_SlewTilde()});
    }

    static AxoObject Create_Softsat() {
        AxoObject o = new AxoObject("soft", "symetrical soft saturation distortion: y=1.5*x-0.5*x^3 for -1&lt;x&lt;1, y=-1 for x&lt;-1, y=1 for x&gt;1, no oversampling or anti-aliasing");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32Bipolar("out", "output"));
        o.sKRateCode = "int32_t ts = __SSAT(%in%,28);\n"
                + "int32_t tsq31 = ts<<3;\n"
                + "int32_t tsq31p3 = ___SMMUL(tsq31,___SMMUL(tsq31,tsq31));\n"
                + "%out% = ts + (ts>>1) - (tsq31p3);\n";
        return o;
    }

    static AxoObject Create_SoftsatTilde() {
        AxoObject o = new AxoObject("soft", "symetrical soft saturation distortion: y=1.5*x-0.5*x^3 for -1&lt;x&lt;1, y=-1 for x&lt;-1, y=1 for x&gt;1, no oversampling or anti-aliasing");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sSRateCode = "int32_t ts = __SSAT(%in%,28);\n"
                + "int32_t tsq31 = ts<<3;\n"
                + "int32_t tsq31p3 = ___SMMUL(tsq31,___SMMUL(tsq31,tsq31));\n"
                + "%out% = ts + (ts>>1) - (tsq31p3);\n";
        return o;
    }

    static AxoObject Create_InfGain() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("inf", "Infinite gain, hard clipping. Algorithm: linear interpolated zero-crossing detector and blit synthesis");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %in%[j]>>2;\n" // prevent overflow
                + "    int32_t sum=0;\n"
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    } else if ((i1<0)&&!(i0<0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((i0<<6)/(i0-i1));\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    i0 = i1;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    %out%[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject Create_SchmittTriggerTilde() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("schmitttrigger", "Schmitt trigger: hard clipping with hysteresis. Algorithm: linear interpolated zero-crossing detector and blit synthesis");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.params.add(new ParameterFrac32UMap("hysteresis"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %in%[j]>>2;\n" // prevent overflow
                + "    i1 += (nextvoice&1)?%hysteresis%:-%hysteresis%;\n"
                + "    int32_t sum=0;\n"
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    } else if ((i1<0)&&!(i0<0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((i0<<6)/(i0-i1));\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    i0 = i1;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    %out%[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject Create_SampleHoldBL() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("samplehold~", "high quality audio rate sample and hold using blit synthesis (bandwidth limited)");
        o.inlets.add(new InletFrac32Buffer("in", "level input"));
        o.inlets.add(new InletFrac32Buffer("trig", "trigger input, triggers on rising zero-crossing"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
//        o.outlets.add(new OutletFrac32BufferBipolar("out2", "audio output"));
        //o.outlets.add(new OutletInt32("xxx", "x debug"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  int32_t vgain[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n"
                + "  int32_t in0;\n"
                + "  int32_t acc;\n";
//                + "  int32_t xxxv;\n";
        o.sInitCode = "  int j;\n"
                + "  for(j=0;j<blepvoices;j++)\n"
                + "    oscp[j] = &blept[BLEPSIZE-1];\n"
                + "  nextvoice = 0;\n"
                + "  i0 = 0;\n"
                + "  in0 = 0;\n"
                + "  acc = 0;\n";
//                + "  xxxv = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %trig%[j]>>2;\n" // prevent overflow
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                //                + "      xxxv = x;\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "      int32_t val = (((64-x)*(%in%[j]>>2)) + (x*(in0>>2)))>>6;\n"
                //                + "      int32_t val = ((64-x)*(in0>>2) + (x*(%in%[j]>>2)))>>6;\n"
                + "      vgain[nextvoice] = (acc-val)<<2;\n"
                + "      acc = val;\n"
                + "    }\n"
                + "    int32_t sum=0;\n"
                //                + "    out2[j]=acc;\n"
                + "    i0 = i1;\n"
                + "    in0 = %in%[j];\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      sum =___SMMLA((16384-(*t))<<16,vgain[i],sum);\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) {"
                + "        t=lastblep;\n"
                + "        vgain[i] = 0;\n"
                + "      }\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    %out%[j]=sum+acc;\n"
                + "  }";
//                + "xxx = xxxv;\n";
        return o;
    }

    static AxoObject Create_Slew() {
        AxoObject o = new AxoObject("slew", "symetric slew rate limiter (not bandwidth limited)");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32UMap("slew"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sKRateCode = "if (%in%>acc){\n"
                + "  if ((%in%-acc)>%slew%)\n"
                + "    acc += %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "} else {\n"
                + "  if ((acc-%in%)>%slew%)\n"
                + "    acc -= %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "}\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject Create_SlewTilde() {
        AxoObject o = new AxoObject("slew", "symetric slew rate limiter (not bandwidth limited)");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32UMap("slew"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sSRateCode = "if (%in%>acc){\n"
                + "  if ((%in%-acc)>%slew%)\n"
                + "    acc += %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "} else {\n"
                + "  if ((acc-%in%)>%slew%)\n"
                + "    acc -= %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "}\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

}
