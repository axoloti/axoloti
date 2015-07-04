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

import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class brainwave extends gentools {

    static void GenerateAll() {
        String catName = "brainwave";
        WriteAxoObject(catName, Create_brainwave());
    }

    static AxoObject Create_brainwave() {
        AxoObject o = new AxoObject("read", "script with 2 inputs and 2 outputs, running in a separate thread, you must define \"void init(void){}\" and \"void loop(void)\"");
        o.outlets.add(new OutletFrac32("quality", "signal quality"));
        o.outlets.add(new OutletFrac32("attention", "attention"));
        o.outlets.add(new OutletFrac32("meditation", "meditation"));
        o.outlets.add(new OutletFrac32("low_alpha", "low alpha"));
        o.outlets.add(new OutletFrac32("high_alpha", "high_alpha"));
        o.outlets.add(new OutletFrac32("low_beta", "low beta"));
        o.outlets.add(new OutletFrac32("high_beta", "high_beta"));
        o.outlets.add(new OutletFrac32("low_gamma", "low gamma"));
        o.outlets.add(new OutletFrac32("high_gamma", "high_gamma"));
        o.outlets.add(new OutletFrac32("delta", "delta"));
        o.outlets.add(new OutletFrac32("theta", "theta"));
        o.attributes.add(new AxoAttributeTextEditor("script"));
        o.sLocalData = "int32_t _quality;\n"
                + "int32_t _attention;\n"
                + "int32_t _meditation;\n"
                + "int32_t _low_alpha;\n"
                + "int32_t _high_alpha;\n"
                + "int32_t _low_beta;\n"
                + "int32_t _high_beta;\n"
                + "int32_t _low_gamma;\n"
                + "int32_t _high_gamma;\n"
                + "int32_t _delta;\n"
                + "int32_t _theta;\n"
                + "%script%\n"
                + "msg_t ThreadX2(){\n"
                + "  setup();\n"
                + "  while(!chThdShouldTerminate()){\n"
                + "     loop();\n"
                + "     chThdYield();\n"
                + "  }\n"
                + "  chThdExit((msg_t)0);\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 1024);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "_quality = 0;\n"
                + "_attention = 0;\n"
                + "_meditation = 0;\n"
                + "_low_alpha = 0;\n"
                + "_high_alpha = 0;\n"
                + "_low_beta = 0;\n"
                + "_high_beta = 0;\n"
                + "_low_gamma = 0;\n"
                + "_high_gamma = 0;\n"
                + "_delta = 0;\n"
                + "_theta = 0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n"
                + "chThdWait(Thd);\n";
        o.sKRateCode
                = "%quality% = this->_quality;\n"
                + "%attention% = this->_attention;\n"
                + "%meditation% = this->_meditation;\n"
                + "%low_alpha% = this->_low_alpha;\n"
                + "%high_alpha% = this->_high_alpha;\n"
                + "%low_beta% = this->_low_beta;\n"
                + "%high_beta% = this->_high_beta;\n"
                + "%low_gamma% = this->_low_gamma;\n"
                + "%high_gamma% = this->_high_gamma;\n"
                + "%delta% = this->_delta;\n"
                + "%theta% = this->_theta;\n";
        return o;
    }
}
