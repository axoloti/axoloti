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

import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletBool32Pulse;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.parameters.ParameterFrac32SMapLFOPitch;

/**
 *
 * @author Johannes Taelman
 */
public class Lfo extends gentools {

    static void GenerateAll() {
        String catName = "lfo";
        WriteAxoObject(catName, CreateSineLFO());
        WriteAxoObject(catName, CreateKRateSineOsc1());
        WriteAxoObject(catName, CreateKRateSineOscReset());
        WriteAxoObject(catName, CreateSquare());
        WriteAxoObject(catName, CreateSaw());
        WriteAxoObject(catName, CreateSaw2());
        WriteAxoObject(catName, CreateSaw3());
        WriteAxoObject(catName, CreateSawDown());
        WriteAxoObject(catName, CreateSawDown2());
        //WriteAxoObject(catName, CreateTaptempo());
    }

    static AxoObject CreateSineLFO() {
        AxoObject o = new AxoObject("sine lin", "Cheapest sine wave LFO, non-interpolated table");
        o.outlets.add(new OutletFrac32Bipolar("out", "sine wave LFO, cheap and bad quality"));
        o.inlets.add(new InletFrac32("freq", "phase increment"));
        o.sLocalData = "uint32_t Phase;";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "Phase += inlet_freq;\n"
                + "%out%= sinet[Phase>>22]<<12;";
        return o;
    }

    static AxoObject CreateKRateSineOsc1() {
        AxoObject o = new AxoObject("sine", "sine wave LFO, linear interpolated table, pitch input");
        o.outlets.add(new OutletFrac32Bipolar("wave", "sine wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "uint32_t Phase;";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "   {"
                + "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "      Phase += freq>>2;\n"
                + "      int32_t r;\n"
                + "      SINE2TINTERP(Phase,r)\n"
                + "      %wave%= (r>>4);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateKRateSineOscReset() {
        AxoObject o = new AxoObject("sine r", "sine wave LFO, linear interpolated table, pitch input, reset input");
        o.outlets.add(new OutletFrac32Bipolar("wave", "sine wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.inlets.add(new InletFrac32Bipolar("phase", "phase for reset"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "uint32_t Phase;"
                + "uint32_t r;\n";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "      if (%reset% && r) {\n"
                + "         Phase = %phase% << 4;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "     }"
                + "   {"
                + "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "      Phase += freq>>2;\n"
                + "      int32_t r;\n"
                + "      SINE2TINTERP(Phase,r)\n"
                + "      %wave%= (r>>4);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSquare() {
        AxoObject o = new AxoObject("square", "square wave LFO, boolean output, frequency input");
        o.outlets.add(new OutletBool32("wave", "square wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "int32_t Phase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "   Phase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         int32_t freq;\n"
                + "         MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "         Phase += freq>>2;\n"
                + "      }\n"
                + "      %wave%= (Phase>0)?1:0;\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSaw() {
        AxoObject o = new AxoObject("saw lin", "saw wave LFO, rising slope, frequency input");
        o.outlets.add(new OutletFrac32Pos("wave", "saw wave"));
        o.inlets.add(new InletFrac32("freq", "frequency"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.sLocalData = "uint32_t Phase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "   Phase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         Phase += %freq%>>2;\n"
                + "      }\n"
                + "      %wave%= (Phase>>5);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSaw2() {
        AxoObject o = new AxoObject("saw", "saw wave LFO, rising slope, pitch input");
        o.outlets.add(new OutletFrac32Pos("wave", "saw wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "uint32_t Phase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "Phase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         int32_t freq;\n"
                + "         MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "         Phase += freq>>2;\n"
                + "      }\n"
                + "      %wave%= (Phase>>5);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSaw3() {
        AxoObject o = new AxoObject("saw r", "saw wave LFO, rising slope, pitch input, phase reset");
        o.outlets.add(new OutletFrac32Pos("wave", "saw wave"));
        o.outlets.add(new OutletBool32Pulse("sync", "sync output"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "uint32_t Phase;\n"
                + "int32_t pPhase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "Phase = 0;\n"
                + "pPhase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         int32_t freq;\n"
                + "         MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "         Phase += freq>>2;\n"
                + "      }\n"
                + "      %sync% = (((int32_t)Phase)>=0)&&(pPhase<0);\n"
                + "      %wave%= (Phase>>5);\n"
                + "      pPhase = Phase;\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSawDown() {
        AxoObject o = new AxoObject("saw down lin", "saw wave LFO, falling slope, frequency input");
        o.outlets.add(new OutletFrac32Pos("wave", "saw wave"));
        o.inlets.add(new InletFrac32("freq", "frequency"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.sLocalData = "uint32_t Phase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "   Phase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         Phase -= %freq%>>2;\n"
                + "      }\n"
                + "      %wave%= (Phase>>5);\n"
                + "   }";
        return o;
    }

    static AxoObject CreateSawDown2() {
        AxoObject o = new AxoObject("saw down", "saw wave LFO, falling slope, pitch input");
        o.outlets.add(new OutletFrac32Pos("wave", "saw wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletBool32Rising("reset", "reset phase"));
        o.params.add(new ParameterFrac32SMapLFOPitch("pitch"));
        o.sLocalData = "uint32_t Phase;\n"
                + "uint32_t r;\n";
        o.sInitCode = "Phase = 0;\n"
                + "   r = 1;\n";
        o.sKRateCode = "   {\n"
                + "      if (%reset% && r) {\n"
                + "         Phase = 0;\n"
                + "         r = 0;\n"
                + "      } else {\n"
                + "         if (!%reset%) r = 1;\n"
                + "         int32_t freq;\n"
                + "         MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "         Phase -= freq>>2;\n"
                + "      }\n"
                + "      %wave%= (Phase>>5);\n"
                + "   }";
        return o;
    }

    //broken
    static AxoObject CreateTaptempo() {
        AxoObject o = new AxoObject("taptempo", "taptempo follower");
        o.inlets.add(new InletBool32Rising("tap", "tap tap tap"));
        o.outlets.add(new OutletFrac32Pos("phasor", "phasor"));
        o.outlets.add(new OutletBool32Pulse("24ppq", "24ppq"));
        o.outlets.add(new OutletBool32Pulse("index", "index"));
        o.outlets.add(new OutletFrac32Pos("int1", ""));
        o.outlets.add(new OutletFrac32Pos("int2", ""));
        o.sLocalData = "int32_t trigtap;\n"
                + "int32_t tc;\n"
                + "int32_t tlatch;\n";
        o.sInitCode = "trigtap = 0;\n"
                + "tlatch = 0;\n"
                + "tc = 0;\n";
        o.sKRateCode
                = "int tol = 0x10000000;\n" +
"if ((%tap% > 0) && !trigtap) {\n" +
"  tlatch = tc;\n" +
"  if (tlatch<4096) {\n" +
"     frql = 0xFFFFFFFF/(tlatch+1);\n" +
"  }\n" +
"  tc = 0;\n" +
"  trigtap = 1;\n" +
"  if ((acc>-tol)&&(acc<tol)){\n" +
"     // low deviation: catch\n" +
"     acc = 0;\n" +
"  } else {\n" +
"     // high deviation: flywheel\n" +
"     acc = acc-(acc>>5);\n" +
"  }\n" +
"} else if (!(%tap% > 0)){\n" +
"  trigtap = 0;\n" +
"}\n" +
"tc++;\n" +
"acc += frql;\n" +
"%int1% = tlatch;\n" +
"%phasor% = acc>>4;\n";
        return o;
    }

}
