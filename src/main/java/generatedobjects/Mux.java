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
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.inlet.InletInt32Pos;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletCharPtr32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Mux extends GenTools {

    static void generateAll() {
        String catName = "mux";
        writeAxoObject(catName, new AxoObject[]{create_inmux2(), create_inmux2Tilde(), create_inmux2I(), create_inmux2b(), create_inmux2s()});

        for (int i = 3; i < 9; i++) {
            writeAxoObject(catName, new AxoObject[]{create_inmuxn(i), create_inmuxni(i), create_inmuxntilde(i), create_inmuxnb(i), create_inmuxns(i)});
        }
    }

    static AxoObject create_inmux2() {
        AxoObject o = new AxoObject("mux 2", "input multiplexer. Output is i1 when s is false, i2 otherwise.");
        o.inlets.add(new InletFrac32("i1", "input 1"));
        o.inlets.add(new InletFrac32("i2", "input 2"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   %o%= (%s%)?%i2%:%i1%;\n";
        return o;
    }

    static AxoObject create_inmux2Tilde() {
        AxoObject o = new AxoObject("mux 2", "input multiplexer. Output is i1 when s is false, i2 otherwise.");
        o.inlets.add(new InletFrac32Buffer("i1", "input 1"));
        o.inlets.add(new InletFrac32Buffer("i2", "input 2"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   %o%= (%s%)?%i2%:%i1%;\n";
        return o;
    }

    static AxoObject create_inmux2I() {
        AxoObject o = new AxoObject("mux 2", "input multiplexer. Output is i1 when s is false, i2 otherwise.");
        o.inlets.add(new InletInt32("i1", "input 1"));
        o.inlets.add(new InletInt32("i2", "input 2"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "   %o%= (%s%)?%i2%:%i1%;\n";
        return o;
    }

    static AxoObject create_inmux2b() {
        AxoObject o = new AxoObject("mux 2", "input multiplexer. Output is i1 when s is false, i2 otherwise.");
        o.inlets.add(new InletBool32("i1", "input 1"));
        o.inlets.add(new InletBool32("i2", "input 2"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "   %o%= (%s%)?%i2%:%i1%;\n";
        return o;
    }

    static AxoObject create_inmux2s() {
        AxoObject o = new AxoObject("mux 2", "input multiplexer. Output is i1 when s is false, i2 otherwise.");
        o.inlets.add(new InletCharPtr32("i1", "input 1"));
        o.inlets.add(new InletCharPtr32("i2", "input 2"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletCharPtr32("o", "output"));
        o.sKRateCode = "   %o%= (%s%)?(char *)%i2%:(char *)%i1%;\n";
        return o;
    }

    static AxoObject create_inmuxn(int n) {
        AxoObject o = new AxoObject("mux " + n, "input multiplexer. Output is i1 when s < 1, i[i] when....");
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32("i" + i, "input " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": %o%= %i" + i + "%;break;\n";
        }
        o.sKRateCode += "      default: %o%= %i" + (n - 1) + "%;break;\n";
        o.sKRateCode += "}\n";
        return o;
    }

    static AxoObject create_inmuxntilde(int n) {
        AxoObject o = new AxoObject("mux " + n, "input multiplexer. Output is i1 when s < 1, i[i] when....");
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32Buffer("i" + i, "input " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sSRateCode += "      case " + i + ": %o%= %i" + i + "%;break;\n";
        }
        o.sSRateCode += "      default: %o%= %i" + (n - 1) + "%;break;\n";
        o.sSRateCode += "}\n";
        return o;
    }

    static AxoObject create_inmuxni(int n) {
        AxoObject o = new AxoObject("mux " + n, "input multiplexer. Output is i1 when s < 1, i[i] when....");
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletInt32("i" + i, "input " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": %o%= %i" + i + "%;break;\n";
        }
        o.sKRateCode += "      default: %o%= %i" + (n - 1) + "%;break;\n";
        o.sKRateCode += "}\n";
        return o;
    }

    static AxoObject create_inmuxnb(int n) {
        AxoObject o = new AxoObject("mux " + n, "input multiplexer. Output is i1 when s < 1, i[i] when....");
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletBool32("i" + i, "input " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": %o%= %i" + i + "%;break;\n";
        }
        o.sKRateCode += "      default: %o%= %i" + (n - 1) + "%;break;\n";
        o.sKRateCode += "}\n";
        return o;
    }

    static AxoObject create_inmuxns(int n) {
        AxoObject o = new AxoObject("mux " + n, "input multiplexer. Output is i1 when s < 1, i[i] when....");
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletCharPtr32("i" + i, "input " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        o.outlets.add(new OutletCharPtr32("o", "output"));
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": %o%= (char *)%i" + i + "%;break;\n";
        }
        o.sKRateCode += "      default: %o%= (char *)%i" + (n - 1) + "%;break;\n";
        o.sKRateCode += "}\n";
        return o;
    }

}
