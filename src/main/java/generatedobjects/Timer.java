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

import axoloti.object.AxoObject;
import axoloti.object.inlet.InletBool32Rising;
import axoloti.object.inlet.InletFrac32Bipolar;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletBool32Pulse;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.ParameterFrac32SMapKLineTimeExp;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Timer extends GenTools {

    static void generateAll() {
        String catName = "timer";
        writeAxoObject(catName, createDelayedPulse());
        writeAxoObject(catName, createDelayedPulseM());
        writeAxoObject(catName, createDelayedPulseDuration());
        writeAxoObject(catName, createDelayedPulseDurationM());
        writeAxoObject(catName, createPulseLength());
        writeAxoObject(catName, createPulseLengthM());
        writeAxoObject(catName, createTimer());
    }

    static AxoObject createDelayedPulse() {
        AxoObject o = new AxoObject("delayedpulse", "Generates a single pulse after a delay after a rising edge on trigger input. A new trigger before the pulse arrives at the output, will cancel the previous trigger. The generated pulse is so small you won't notice it on a display! Extended range version.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletBool32Pulse("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 0;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) %pulse% = 1;\n"
                + "     else %pulse% = 0;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject createDelayedPulseM() {
        AxoObject o = new AxoObject("delayedpulsem", "Generates a single pulse after a delay after a rising edge on trigger input. A new trigger before the pulse arrives at the output, will cancel the previous trigger. The generated pulse is so small you won't notice it on a display! This version has a modulation input for the delay time. Extended range.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("delay", "delay modulation"));
        o.outlets.add(new OutletBool32Pulse("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 0;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay-inlet_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) %pulse% = 1;\n"
                + "     else %pulse% = 0;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }


    static AxoObject createDelayedPulseDuration() {
        AxoObject o = new AxoObject("delayedpulseduration", "Generates a pulse with a duration after a delay after a rising edge on trigger input. A new trigger before the pulse arrives at the output, will cancel the previous trigger. Extended range.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletBool32("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("pulselength"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 0;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) {\n"
                + "         %pulse% = 1;\n"
                + "         val = -1 << 30;\n"
                + "     } else %pulse% = 0;\n"
                + "  } else if (val<0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_pulselength,t);\n"
                + "     val += t>>3;\n"
                + "     if (val>=0) {"
                + "         %pulse% = 0;\n"
                + "         val = 0;\n"
                + "     } else %pulse% = 1;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject createDelayedPulseDurationM() {
        AxoObject o = new AxoObject("delayedpulsedurationm", "Generates a single pulse with a duration after a delay after a rising edge on trigger input. A new trigger before the pulse arrives at the output, will cancel the previous trigger. The generated pulse is so small you won't notice it on a display! This version has a modulation input for the delay time.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("delay", "delay time modulation"));
        o.inlets.add(new InletFrac32Bipolar("pulselength", "pulse length modulation"));
        o.outlets.add(new OutletBool32("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("pulselength"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 0;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay-inlet_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) {\n"
                + "         %pulse% = 1;\n"
                + "         val = -1 << 30;\n"
                + "     } else %pulse% = 0;\n"
                + "  } else if (val<0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_pulselength-inlet_pulselength,t);\n"
                + "     val += t>>3;\n"
                + "     if (val>=0) {"
                + "         %pulse% = 0;\n"
                + "         val = 0;\n"
                + "     } else %pulse% = 1;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }

    //TODO: check... this was PulseX but was not being called, not it also generates a 'delayedpulse' object
    static AxoObject createPulse() {
        AxoObject o = new AxoObject("delayedpulse", "Generates a single pulse on a rising edge on trigger input. A new trigger before the pulse arrives at the output, will cancel the previous trigger. The generated pulse is so small you won't notice it on a display! Extended range version.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletBool32Pulse("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 0;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) %pulse% = 1;\n"
                + "     else %pulse% = 0;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject createPulseLength() {
        AxoObject o = new AxoObject("pulselength", "Generates a single pulse after a rising edge on trigger input. A new trigger before the pulse finishes at the output, extends the pulse.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletBool32("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 1;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) %pulse% = 0;\n"
                + "     else %pulse% = 1;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";

        return o;
    }

    static AxoObject createPulseLengthM() {
        AxoObject o = new AxoObject("pulselengthm", "Generates a single pulse after after a rising edge on trigger input. A new trigger before the pulse ends at the output, will extend the pulse. This version has a modulation input for the delay time. Extended range.");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletFrac32Bipolar("delay", "delay"));
        o.outlets.add(new OutletBool32("pulse", "pulse output"));
        o.params.add(new ParameterFrac32SMapKLineTimeExp("delay"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig% > 0) && !ntrig) {\n"
                + "  val = 1 << 30;\n"
                + "  ntrig = 1;\n"
                + "  %pulse% = 1;\n"
                + "}\n"
                + "else {\n"
                + "  if (!(%trig% > 0))\n"
                + "    ntrig = 0;\n"
                + "  if (val>0) {\n"
                + "    int32_t t;\n"
                + "    MTOF(-param_delay-inlet_delay,t);\n"
                + "     val -= t>>3;\n"
                + "     if (val<=0) %pulse% = 0;\n"
                + "     else %pulse% = 1;\n"
                + "  } else %pulse% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject createTimer() {
        AxoObject o = new AxoObject("timeri", "measures the time interval between a rising edge on the start input and a rising edge on the stop input");
        o.inlets.add(new InletBool32Rising("start", "start trigger"));
        o.inlets.add(new InletBool32Rising("stop", "stop trigger"));
        o.outlets.add(new OutletInt32("t", "time interval in k-rate ticks (0.333ms)"));
        o.sLocalData = "int32_t trigstart;\n"
                + "int32_t trigstop;\n"
                + "int32_t tc;\n"
                + "int32_t tlatch;\n";
        o.sInitCode = "trigstart = 0;\n"
                + "trigstop = 0;\n"
                + "tlatch = 0;\n"
                + "tc = 0;\n";
        o.sKRateCode
                = "if ((%start% > 0) && !trigstart) {\n"
                + "  tc = 0;\n"
                + "  trigstart = 1;\n"
                + "} else if (!(%start% > 0)) {\n"
                + "    trigstart = 0;\n"
                + "}\n"
                + "if ((%stop% > 0) && !trigstop) {\n"
                + "  tlatch = tc;\n"
                + "  trigstop = 1;\n"
                + "} else if (!(%stop% > 0)) {\n"
                + "    trigstop = 0;\n"
                + "}\n"
                + "%t% = tlatch;\n"
                + "tc++;\n";
        return o;
    }

}
