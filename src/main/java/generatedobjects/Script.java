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

import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32SMap;

/**
 *
 * @author Johannes Taelman
 */
public class Script extends gentools {

    static void GenerateAll() {
        String catName = "script";
        WriteAxoObject(catName, Create_KOneliner());
        WriteAxoObject(catName, Create_SOneliner());
        WriteAxoObject(catName, Create_Script());
        WriteAxoObject(catName, Create_Script2());
    }

    static AxoObject Create_KOneliner() {
        AxoObject o = new AxoObject("oneliner_k", "k-rate c one-liner");
        o.inlets.add(new InletFrac32("in", "in"));
        o.outlets.add(new OutletFrac32("out", "out"));
        o.params.add(new ParameterFrac32SMap("c"));
        o.attributes.add(new AxoAttributeTablename("line"));
        o.sKRateCode = "%line%;\n";
        return o;
    }

    static AxoObject Create_SOneliner() {
        AxoObject o = new AxoObject("oneliner_s", "s-rate c one-liner");
        o.inlets.add(new InletFrac32Buffer("in", "in"));
        o.outlets.add(new OutletFrac32Buffer("out", "out"));
        o.params.add(new ParameterFrac32SMap("c"));
        o.attributes.add(new AxoAttributeTablename("line"));
        o.sSRateCode = "%line%;\n";
        return o;
    }

    static AxoObject Create_Script() {
        AxoObject o = new AxoObject("script", "script with 2 inputs and 2 outputs, running in a separate thread");
        o.inlets.add(new InletFrac32("in1_", "in1"));
        o.inlets.add(new InletFrac32("in2_", "in2"));
        o.outlets.add(new OutletFrac32("out1_", "out1"));
        o.outlets.add(new OutletFrac32("out2_", "out2"));
        o.attributes.add(new AxoAttributeTextEditor("script"));
        o.sLocalData = "int32_t in1,in2;\n"
                + "int32_t out1,out2;\n"
                + "msg_t ThreadX2(){\n"
                + "%script%\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 1024);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "in1=0;in2=0;out1=0;out2=0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n"
                + "chThdWait(Thd);\n";
        o.sKRateCode = "%out1_% = this->out1;\n"
                + "%out2_% = this->out2;\n"
                + "this->in1 = %in1_%;\n"
                + "this->in2 = %in2_%;\n";
        return o;
    }

    static AxoObject Create_Script2() {
        AxoObject o = new AxoObject("script2", "script with 2 inputs and 2 outputs, running in a separate thread, you must define \"void setup(void){}\" and \"void loop(void)\"");
        o.inlets.add(new InletFrac32("in1_", "in1"));
        o.inlets.add(new InletFrac32("in2_", "in2"));
        o.outlets.add(new OutletFrac32("out1_", "out1"));
        o.outlets.add(new OutletFrac32("out2_", "out2"));
        o.attributes.add(new AxoAttributeTextEditor("script"));
        o.sLocalData = "int32_t in1,in2;\n"
                + "int32_t out1,out2;\n"
                + "%script%\n"
                + "msg_t ThreadX2(){\n"
                + "  setup();\n"
                + "  while(!chThdShouldTerminate()){\n"
                + "     loop();\n"
                + "     chThdSleepMilliseconds(1);\n"
                + "  }\n"
                + "  chThdExit((msg_t)0);\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 1024);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "in1=0;in2=0;out1=0;out2=0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n"
                + "chThdWait(Thd);\n";
        o.sKRateCode = "%out1_% = this->out1;\n"
                + "%out2_% = this->out2;\n"
                + "this->in1 = %in1_%;\n"
                + "this->in2 = %in2_%;\n";
        return o;
    }

}
