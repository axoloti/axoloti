/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.attributedefinition.*;
import axoloti.inlets.*;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.*;
import axoloti.parameters.*;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
@Deprecated
public class GeneratedObjects extends gentools {

    static public void WriteAxoObjects() {

        Arithmetic.GenerateAll();
        Demux.GenerateAll();
        Constant.GenerateAll();
        Control.GenerateAll();
        Convert.GenerateAll();
        Delay.GenerateAll();
        Display.GenerateAll();
        Distortion.GenerateAll();
        Dynamics.GenerateAll();
        Env.GenerateAll();
        Filter.GenerateAll();
        Io.GenerateAll();
        Lfo.GenerateAll();
        Logic.GenerateAll();
        Math.GenerateAll();
        Midi.GenerateAll();
        Mixer.GenerateAll();
        Mux.GenerateAll();
        Osc.GenerateAll();
        Patch.GenerateAll();
        Script.GenerateAll();
        Sequencer.GenerateAll();
        Spectral.GenerateAll();
        Stochastics.GenerateAll();
        Table.GenerateAll();
        Trigonometry.GenerateAll();
        Wave.GenerateAll();
        Strings.GenerateAll();
        Impulses.GenerateAll();
        Timer.GenerateAll();
        Reverb.GenerateAll();
        brainwave.GenerateAll();
        LTC.GenerateAll();
        Spat.GenerateAll();
        USB.GenerateAll();
        Harmony.GenerateAll();

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_latch());
            c.add(Create_latchi());
            WriteAxoObject("logic", c);
        }

        WriteAxoObject("logic", Create_counter());
        WriteAxoObject("logic", Create_counter2());
        WriteAxoObject("logic", Create_countersat2());

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateWindow());
            c.add(CreateWindowTilde());
            WriteAxoObject("math", c);
        }

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_accu_sat());
            WriteAxoObject("math", c);
        }

        WriteAxoObject("gain", CreateVCA());

        WriteAxoObject("math", Create_smooth());
        WriteAxoObject("math", Create_smooth2());

        WriteAxoObject("math", Create_glide());

//        WriteAxoObject(unstable, Create_testanno());

//        objs.add(Create_FlashTableGranularPlay());
        //WriteAxoObject("util",modsource());

        /*        
         for(AxoObject o:objs){
         String cn = o.id;
         }
         //        Objects.add(CreateSScope2());

         for(AxoObject o:objs){
 
         }
         */
    }

    static AxoObject CreateWindow() {
        AxoObject o = new AxoObject("window", "hanning window function, input 0..64");
        o.inlets.add(new InletFrac32Pos("phase", "phase"));
        o.outlets.add(new OutletFrac32Pos("win", "w(phase)"));
        o.sKRateCode = "   {\n"
                + "       int32_t r;\n"
                + "       HANNING2TINTERP(%phase%<<5,r)\n"
                + "       %win%= (r>>4);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateWindowTilde() {
        AxoObject o = new AxoObject("window", "hanning window function, input 0..64");
        o.inlets.add(new InletFrac32Buffer("phase", "phase"));
        o.outlets.add(new OutletFrac32Buffer("win", "w(phase)"));
        o.sSRateCode = "   {\n"
                + "       int32_t r;\n"
                + "       HANNING2TINTERP(%phase%<<5,r)\n"
                + "       %win%= (r>>4);\n"
                + "   }";
        return o;
    }

    static AxoObject Create_smooth() {
        AxoObject o = new AxoObject("smooth", "exponential smooth");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32UMap("time"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   val = ___SMMLA(val-%in%,(-1<<26)+(%time%>>1),val);\n"
                + "   %out%= val;\n";
        return o;
    }

    static AxoObject Create_smooth2() {
        AxoObject o = new AxoObject("smooth2", "exponential smooth, separate rise and fall time");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32UMap("risetime"));
        o.params.add(new ParameterFrac32UMap("falltime"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   if (%in%>val)"
                + "      val = ___SMMLA(val-%in%, (-1<<26)+(%risetime%>>1),val);\n"
                + "      else val = ___SMMLA(val-%in%,(-1<<26)+(%falltime%>>1),val);\n"
                + "   %out% = val;\n";
        return o;
    }

    static AxoObject Create_glide() {
        AxoObject o = new AxoObject("glide", "exponential smooth with enable");
        o.inlets.add(new InletFrac32("in", "input"));
        o.inlets.add(new InletBool32("en", "enable"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32UMap("time"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sKRateCode = "   "
                + "   if (%en%>0) val = ___SMMLA(val-%in%,(-1<<26)+(%time%>>1),val);\n"
                + "   else val = %in%;\n"
                + "   %out%= val;\n";
        return o;
    }

    static AxoObject Create_counter() {
        AxoObject o = new AxoObject("counter", "cyclic up counter");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletBool32Rising("r", "reset"));
        o.params.add(new ParameterInt32Box("maximum", 0, 1 << 16));
        o.outlets.add(new OutletInt32("o", "output"));
        o.outlets.add(new OutletBool32Pulse("c", "carry pulse"));
        o.sLocalData = "   int ntrig;\n"
                + "   int rtrig;\n"
                + "   int count;\n";
        o.sInitCode = "    count=0;\n"
                + "   ntrig = 0;\n"
                + "   rtrig = 0;\n";
        o.sKRateCode = "%c%=0;\n"
                + "if ((%trig%>0) && !ntrig) {\n"
                + "   count += 1; if (count>=%maximum%) {count = 0; %c% = 1;}\n"
                + "   ntrig=1;\n"
                + "}\n"
                + "else if (!(%trig%>0)) ntrig=0;\n"
                + "if ((%r%>0) && !rtrig) {count=0; rtrig = 1;}\n"
                + "else if (!(%r%>0)) rtrig=0;\n"
                + "%o%= count;\n";
        return o;
    }

    static AxoObject Create_counter2() {
        AxoObject o = new AxoObject("counter2", "cyclic up/down counter");
        o.inlets.add(new InletBool32Rising("inc", "increment trigger"));
        o.inlets.add(new InletBool32Rising("dec", "decrement trigger"));
        o.inlets.add(new InletBool32Rising("r", "reset"));
        o.params.add(new ParameterInt32Box("maximum", 0, 1 << 16));
        o.outlets.add(new OutletInt32("o", "output"));
        o.outlets.add(new OutletBool32Pulse("c", "carry pulse"));
        o.sLocalData = "   int ntrig;\n"
                + "   int rtrig;\n"
                + "   int dtrig;\n"
                + "   int count;\n";
        o.sInitCode = "    count=0;\n"
                + "   ntrig = 0;\n"
                + "   dtrig = 0;\n"
                + "   rtrig = 0;\n";
        o.sKRateCode = "%c%=0;\n"
                + "if ((%inc%>0) && !ntrig) {\n"
                + "   count++; if (count>=%maximum%) {count = 0; %c% = 1;}\n"
                + "   ntrig=1;\n"
                + "}\n"
                + "else if (!(%inc%>0)) ntrig=0;\n"
                + "if ((%dec%>0) && !dtrig) {\n"
                + "   count--; if (count<0) {count = %maximum%-1; %c% = 1;}\n"
                + "   dtrig=1;\n"
                + "}\n"
                + "else if (!(%dec%>0)) dtrig=0;\n"
                + "if ((%r%>0) && !rtrig) {count=0; rtrig = 1;}\n"
                + "else if (!(%r%>0)) rtrig=0;\n"
                + "%o%= count;\n";
        return o;
    }

    static AxoObject Create_countersat2() {
        AxoObject o = new AxoObject("countersat2", "saturating up/down counter");
        o.inlets.add(new InletBool32Rising("inc", "increment trigger"));
        o.inlets.add(new InletBool32Rising("dec", "decrement trigger"));
        o.inlets.add(new InletBool32Rising("r", "reset"));
        o.params.add(new ParameterInt32Box("maximum", 0, 1 << 16));
        o.outlets.add(new OutletInt32("o", "output"));
        o.sLocalData = "   int ntrig;\n"
                + "   int rtrig;\n"
                + "   int dtrig;\n"
                + "   int count;\n";
        o.sInitCode = "    count=0;\n"
                + "   ntrig = 0;\n"
                + "   dtrig = 0;\n"
                + "   rtrig = 0;\n";
        o.sKRateCode = "if ((%inc%>0) && !ntrig) {\n"
                + "   count++; if (count>=%maximum%) {count = %maximum%-1; }\n"
                + "   ntrig=1;\n"
                + "}\n"
                + "else if (!(%inc%>0)) ntrig=0;\n"
                + "if ((%dec%>0) && !dtrig) {\n"
                + "   count--; if (count<0) {count = 0;}\n"
                + "   dtrig=1;\n"
                + "}\n"
                + "else if (!(%dec%>0)) dtrig=0;\n"
                + "if ((%r%>0) && !rtrig) {count=0; rtrig = 1;}\n"
                + "else if (!(%r%>0)) rtrig=0;\n"
                + "%o%= count;\n";
        return o;
    }

    static AxoObject Create_accu_sat() {
        AxoObject o = new AxoObject("accu_sat", "Saturating accumulator. Adds input to accumulator on trigger. Reset clears the accumulator to zero.");
        o.inlets.add(new InletFrac32("i", "input"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletBool32Rising("rst", "reset"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sLocalData = "   int ntrig;"
                + "   int rtrig;"
                + "   int accu;\n";
        o.sInitCode = "ntrig = 0;\n"
                + "rtrig = 0;\n"
                + "accu = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {accu += %i%; ntrig=1; accu = __SSAT(accu,28);}\n"
                + "if (!(%trig%>0)) ntrig=0;\n"
                + "if ((%rst%>0) && !rtrig) {accu = 0; rtrig=1;}\n"
                + "if (!(%rst%>0)) rtrig=0;\n"
                + "%o%= accu;\n";
        return o;
    }

    static AxoObject Create_latch() {
        AxoObject o = new AxoObject("latch", "Copies the input to the output at the rising edge of the trigger input. Keeps the output otherwise.");
        o.inlets.add(new InletFrac32("i", "input"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sLocalData = "   int ntrig;"
                + "   int latch;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {latch = %i%; ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n"
                + "%o%= latch;\n";
        return o;
    }

    static AxoObject Create_latchi() {
        AxoObject o = new AxoObject("latch", "Copies the input to the output at the rising edge of the trigger input. Keeps the output otherwise.");
        o.inlets.add(new InletInt32("i", "input"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.sLocalData = "   int ntrig;"
                + "   int latch;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {latch = %i%; ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n"
                + "%o%= latch;\n";
        return o;
    }

    static AxoObject CreateVCA() {
        AxoObject o = new AxoObject("vca", "\"voltage controlled amplifier\", multiplies v and a inputs, with linear interpolation from k- to s-rate");
        o.inlets.add(new InletFrac32("v", "gain input"));
        o.inlets.add(new InletFrac32Buffer("a", "audio input"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sLocalData = "   int32_t prev;\n "
                + "   int32_t step;\n";
        o.sKRateCode = "   step = (%v% - prev)>>4;\n"
                + "   int32_t i = prev;\n"
                + "   prev = %v%;\n";
        o.sSRateCode = "   %o% =  ___SMMUL(%a%,i)<<5;\n"
                + "   i += step;\n";
        return o;
    }

    static AxoObject Create_FlashTableGranularPlay() {
        AxoObject o = new AxoObject("flashtable_granular~", "granular playback of a sound sample in flash memory, UNSTABLE, pspread input unused!");
        o.inlets.add(new InletFrac32("position", "grain start position"));
        o.inlets.add(new InletFrac32("pspread", "grain start position spread"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.attributes.add(new AxoAttributeTablename("tablename"));
        o.attributes.add(new AxoAttributeSpinner("voices", 1, 8, 1));
        o.sLocalData = "    int32_t pos_sample[%voices%];\n"
                + "   int32_t pos_window[%voices%];\n";
        o.sInitCode = "    for(i=0;i<%voices%;i++){\n"
                + "      pos_window[i] = i*((1<<30)/%voices%);\n"
                + "   }\n";
        o.sKRateCode = ""
                + "   {\n"
                + "      int j;\n"
                + "      for(j=0;j<%voices%;j++) {\n"
                + "         if (pos_window[j] > (1<<30)) {\n"
                + "               pos_sample[j] = %position%>>14;\n"
                + "               pos_window[j] = 0;\n"
                + "         }\n"
                + "      }\n"
                + "   }\n";
        o.sSRateCode = ""
                + "      int j;\n"
                + "      int32_t a=0;\n"
                + "      for(j=0;j<%voices%;j++) {\n"
                //                + "         a += t_%tablename%samples[pos_sample[j]++]<<12;\n"
                + "         int32_t tmpw;\n"
                + "         HANNING2TINTERP(pos_window[j]<<2,tmpw);\n"
                + "         a = ___SMMLA(t_%tablename%samples[pos_sample[j]++]<<11,tmpw,a);\n"
                + "         pos_window[j] += 1<<17;\n"
                + "      }\n"
                + "      %o%= a;\n";
        return o;
    }

    static AxoObject Create_testanno() {
        AxoObject o = new AxoObject("test_annotations", "shows various annotations on inputs and outputs");
        o.inlets.add(new InletFrac32Pos("pos", "positive fractional"));
        o.inlets.add(new InletFrac32Bipolar("bip", "bipolar fractional"));
        o.inlets.add(new InletFrac32BufferPos("pos_b", "positive fractional buffer"));
        o.inlets.add(new InletFrac32BufferBipolar("bip_b", "bipolar fractional input buffer"));
        o.inlets.add(new InletBool32("bool", "bool"));
        o.inlets.add(new InletBool32Rising("bool_r", "bool rising"));
        o.inlets.add(new InletBool32RisingFalling("bool_rf", "bool rising/falling"));
        o.inlets.add(new InletInt32("int", "integer"));
        o.inlets.add(new InletInt32Pos("int_pos", "positive integer"));
        o.inlets.add(new InletInt32Bipolar("int_bip", "bipolar integer"));

        o.outlets.add(new OutletFrac32Pos("o_pos", "positive fractional"));
        o.outlets.add(new OutletFrac32Bipolar("o_bip", "bipolar fractional"));
        o.outlets.add(new OutletFrac32BufferPos("o_pos_b", "positive fractional buffer"));
        o.outlets.add(new OutletFrac32BufferBipolar("o_bip_b", "bipolar fractional input buffer"));
        o.outlets.add(new OutletBool32("o_bool", "bool"));
        o.outlets.add(new OutletBool32Pulse("o_bool_p", "boolean pulse"));
        o.outlets.add(new OutletInt32("o_int", "integer"));
        o.outlets.add(new OutletInt32Pos("o_int_pos", "positive integer"));
        o.outlets.add(new OutletInt32Bipolar("o_int_bip", "bipolar integer"));
        return o;
    }
}
