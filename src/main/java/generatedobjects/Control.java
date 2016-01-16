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

import axoloti.inlets.InletFrac32Pos;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.outlets.OutletInt32Pos;
import axoloti.parameters.ParameterBin1;
import axoloti.parameters.ParameterBin16;
import axoloti.parameters.ParameterBin1Momentary;
import axoloti.parameters.ParameterFrac32SMap;
import axoloti.parameters.ParameterFrac32UMap;
import axoloti.parameters.ParameterInt32Box;
import axoloti.parameters.ParameterInt32HRadio;
import axoloti.parameters.ParameterInt32VRadio;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Control extends gentools {

    static void GenerateAll() {
        String catName = "ctrl";
        WriteAxoObject(catName, CreateKConstBP());
        WriteAxoObject(catName, CreateKConstPos());
        WriteAxoObject(catName, CreateB1());
        WriteAxoObject(catName, CreateB1Mom());
        WriteAxoObject(catName, CreateCB16());
        WriteAxoObject(catName, CreateI());
        WriteAxoObject(catName, CreateHook());
        WriteAxoObject(catName, CreateIRadioH(2));
        WriteAxoObject(catName, CreateIRadioH(4));
        WriteAxoObject(catName, CreateIRadioH(8));
        WriteAxoObject(catName, CreateIRadioH(16));
        WriteAxoObject(catName, CreateIRadioV(2));
        WriteAxoObject(catName, CreateIRadioV(4));
        WriteAxoObject(catName, CreateIRadioV(8));
        WriteAxoObject(catName, CreateIRadioV(16));
    }

    static AxoObject CreateKConstPos() {
        AxoObject o = new AxoObject("dial p", "positive constant value dial");
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.params.add(new ParameterFrac32UMap("value"));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }

    static AxoObject CreateKConstBP() {
        AxoObject o = new AxoObject("dial b", "bipolar constant value dial");
        o.outlets.add(new OutletFrac32Bipolar("out", "output"));
        o.params.add(new ParameterFrac32SMap("value"));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }

    static AxoObject CreateB1() {
        AxoObject o = new AxoObject("toggle", "constant boolean, toggle control");
//        o.params.add(new ParameterBin16("b16"));
        o.params.add(new ParameterBin1("b"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o%=%b%;\n";
        return o;
    }

    static AxoObject CreateB1Mom() {
        AxoObject o = new AxoObject("button", "constant boolean, momentary control");
//        o.params.add(new ParameterBin16("b16"));
        o.params.add(new ParameterBin1Momentary("b"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o%=%b%;\n";
        return o;
    }

    static AxoObject CreateCB16() {
        AxoObject o = new AxoObject("cb16", "constant from 16 flags");
        o.params.add(new ParameterBin16("b16"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "%o%=%b16%;\n";
        return o;
    }

    static AxoObject CreateI() {
        AxoObject o = new AxoObject("i", "positive integer control");
        o.outlets.add(new OutletInt32Pos("out", "output"));
        o.params.add(new ParameterInt32Box("value", 0, 65536));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }

    static AxoObject CreateHook() {
        AxoObject o = new AxoObject("hook", "inlet value passed through after hitting control value");
        o.inlets.add(new InletFrac32Pos("in", "input"));
        o.outlets.add(new OutletFrac32Pos("out", "output"));
        o.outlets.add(new OutletBool32("hooked", "hooked"));
        o.params.add(new ParameterFrac32UMap("value"));
        o.sLocalData = "int32_t nhooked; //0:hooked, 1:gt, 2:lt, 4:unhooked\n"
                + "int32_t param_cache;\n";
        o.sInitCode = "nhooked = 4;\n";
        o.sKRateCode = "if (nhooked) {\n"
                + "	outlet_out = param_value;\n"
                + "	if (param_value > inlet_in){\n"
                + "		nhooked |= 1;\n"
                + "		if (nhooked == 7) {\n"
                + "			nhooked = 0;\n"
                + "			param_cache = param_value;\n"
                + "		}\n"
                + "	} else {\n"
                + "		nhooked |= 2;\n"
                + "		if (nhooked == 7) {\n"
                + "			nhooked = 0;\n"
                + "			param_cache = param_value;\n"
                + "		}\n"
                + "	}\n"
                + "} else {\n"
                + "	outlet_out = inlet_in;\n"
                + "	if (param_cache != param_value) \n"
                + "		nhooked = 4;\n"
                + "	else\n"
                + "		PExParameterChange(&parent->PExch[PARAM_INDEX_attr_legal_name_value],inlet_in,0xFFFD);\n"
                + "		param_cache = inlet_in;\n"
                + "}\n"
                + "outlet_hooked = !nhooked;\n";
        return o;
    }

    static AxoObject CreateIRadioH(int i) {
        AxoObject o = new AxoObject("i radio " + i + " h", "positive integer control, horizontal radio buttons");
        o.outlets.add(new OutletInt32Pos("out", "output"));
        o.params.add(new ParameterInt32HRadio("value", 0, i));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }

    static AxoObject CreateIRadioV(int i) {
        AxoObject o = new AxoObject("i radio " + i + " v", "positive integer control, vertical radio buttons");
        o.outlets.add(new OutletInt32Pos("out", "output"));
        o.params.add(new ParameterInt32VRadio("value", 0, i));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }

}
