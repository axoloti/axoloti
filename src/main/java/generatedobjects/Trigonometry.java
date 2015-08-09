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
import axoloti.outlets.OutletFrac32Buffer;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Trigonometry extends gentools {

    static void GenerateAll() {
        String catName = "math";
        WriteAxoObject(catName, new AxoObject[]{CreateSin(), CreateSinTilde()});
        WriteAxoObject(catName, new AxoObject[]{CreateCos(), CreateCosTilde()});
    }

    static AxoObject CreateSin() {
        AxoObject o = new AxoObject("sin", "sine function, -64..64 phase corresponds to -180 to 180 degrees");
        o.inlets.add(new InletFrac32("phase", "phase"));
        o.outlets.add(new OutletFrac32("out", "sin(phase)"));
        o.sKRateCode = "       int32_t r;\n"
                + "       SINE2TINTERP(%phase%<<5,r)\n"
                + "       %out%= (r>>4);\n";
        return o;
    }

    static AxoObject CreateSinTilde() {
        AxoObject o = new AxoObject("sin", "sine function, -64..64 corresponds to -360 to 360 degrees");
        o.inlets.add(new InletFrac32Buffer("phase", "phase"));
        o.outlets.add(new OutletFrac32Buffer("out", "sin(phase)"));
        o.sSRateCode = "       int32_t r;\n"
                + "       SINE2TINTERP(%phase%<<5,r)\n"
                + "       %out%= (r>>4);\n";
        return o;
    }

    static AxoObject CreateCos() {
        AxoObject o = new AxoObject("cos", "cosine function, -64..64 phase corresponds to -360 to 360 degrees");
        o.inlets.add(new InletFrac32("phase", "phase"));
        o.outlets.add(new OutletFrac32("out", "cos(phase)"));
        o.sKRateCode = "       int32_t r;\n"
                + "       SINE2TINTERP((%phase%<<5)+(1<<30),r)\n"
                + "       %out%= (r>>4);\n";
        return o;
    }

    static AxoObject CreateCosTilde() {
        AxoObject o = new AxoObject("cos", "cosine function, -64..64 corresponds to -360 to 360 degrees");
        o.inlets.add(new InletFrac32Buffer("phase", "phase"));
        o.outlets.add(new OutletFrac32Buffer("out", "cos(phase)"));
        o.sSRateCode = "       int32_t r;\n"
                + "       SINE2TINTERP((%phase%<<5)+(1<<30),r)\n"
                + "       %out%= (r>>4);\n";
        return o;
    }

}
