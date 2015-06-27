/**
 * Copyright (C) 2015 Johannes Taelman
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

import axoloti.inlets.InletBool32;
import axoloti.inlets.InletCharPtr32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.inlets.InletInt32Pos;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletCharPtr32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;

/**
 *
 * @author jtaelman
 */
public class Demux extends gentools {

    static void GenerateAll() {
        String catName = "demux";
        WriteAxoObject(catName, new AxoObject[]{Create_demux2(), Create_demux2Tilde(), Create_demux2I(), Create_demux2b()});

        for (int i = 3; i < 9; i++) {
            WriteAxoObject(catName, new AxoObject[]{Create_demuxn(i), Create_demuxni(i), Create_demuxntilde(i), Create_demuxnb(i)});
        }
    }

    static AxoObject Create_demux2() {
        AxoObject o = new AxoObject("demux 2", "Demultiplexer. Connects inlet i to outlet o0 when s is false, to outlet o1 when s is true.");
        o.inlets.add(new InletFrac32("i", "input"));
        o.inlets.add(new InletFrac32("d0", "default 0"));
        o.inlets.add(new InletFrac32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32("o0", "output 0"));
        o.outlets.add(new OutletFrac32("o1", "output 1"));
        o.sKRateCode = "   %o0%= (%s%)?%d0%:%i%;\n"
                + "   %o1%= (%s%)?%i%:%d1%;\n";
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject Create_demux2Tilde() {
        AxoObject o = new AxoObject("demux 2", "Demultiplexer. Connects inlet i to outlet o0 when s is false, to outlet o1 when s is true.");
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        o.inlets.add(new InletFrac32Buffer("d0", "default 0"));
        o.inlets.add(new InletFrac32Buffer("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32Buffer("o0", "output 0"));
        o.outlets.add(new OutletFrac32Buffer("o1", "output 1"));
        o.sSRateCode = "   %o0%= (%s%)?%d0%:%i%;\n"
                + "   %o1%= (%s%)?%i%:%d1%;\n";
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject Create_demux2I() {
        AxoObject o = new AxoObject("demux 2", "Demultiplexer. Connects inlet i to outlet o0 when s is false, to outlet o1 when s is true.");
        o.inlets.add(new InletInt32("i", "input"));
        o.inlets.add(new InletInt32("d0", "default 0"));
        o.inlets.add(new InletInt32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletInt32("o0", "output 0"));
        o.outlets.add(new OutletInt32("o1", "output 1"));
        o.sKRateCode = "   %o0%= (%s%)?%d0%:%i%;\n"
                + "   %o1%= (%s%)?%i%:%d1%;\n";
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject Create_demux2b() {
        AxoObject o = new AxoObject("demux 2", "Demultiplexer. Connects inlet i to outlet o0 when s is false, to outlet o1 when s is true.");
        o.inlets.add(new InletBool32("i", "input"));
        o.inlets.add(new InletBool32("d0", "default 0"));
        o.inlets.add(new InletBool32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.sKRateCode = "   %o0%= (%s%)?%d0%:%i%;\n"
                + "   %o1%= (%s%)?%i%:%d1%;\n";
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject Create_demuxn(int n) {
        AxoObject o = new AxoObject("demux " + n, "Demultiplexer. Connects inlet i to outlet number s. Other outlets copy their corresponding default inlets.");
        o.inlets.add(new InletFrac32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletFrac32("o" + i, "output " + i));
        }
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": \n";
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    o.sKRateCode += "         %o" + j + "% = %i%;\n";
                } else {
                    o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
                }
            }
            o.sKRateCode += "         break;\n";
        }
        o.sKRateCode += "      default:\n";
        for (int j = 0; j < n; j++) {
            o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
        }
        o.sKRateCode += "}\n";
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject Create_demuxntilde(int n) {
        AxoObject o = new AxoObject("demux " + n, "Demultiplexer. Connects inlet i to outlet number s. Other outlets copy their corresponding default inlets.");
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32Buffer("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletFrac32Buffer("o" + i, "output " + i));
        }
        o.sSRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sSRateCode += "      case " + i + ": \n";
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    o.sSRateCode += "         %o" + j + "% = %i%;\n";
                } else {
                    o.sSRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
                }
            }
            o.sSRateCode += "         break;\n";
        }
        o.sSRateCode += "      default:\n";
        for (int j = 0; j < n; j++) {
            o.sSRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
        }
        o.sSRateCode += "}\n";
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject Create_demuxni(int n) {
        AxoObject o = new AxoObject("demux " + n, "Demultiplexer. Connects inlet i to outlet number s. Other outlets copy their corresponding default inlets.");
        o.inlets.add(new InletInt32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletInt32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletInt32("o" + i, "output " + i));
        }
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": \n";
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    o.sKRateCode += "         %o" + j + "% = %i%;\n";
                } else {
                    o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
                }
            }
            o.sKRateCode += "         break;\n";
        }
        o.sKRateCode += "      default:\n";
        for (int j = 0; j < n; j++) {
            o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
        }
        o.sKRateCode += "}\n";
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject Create_demuxnb(int n) {
        AxoObject o = new AxoObject("demux " + n, "Demultiplexer. Connects inlet i to outlet number s. Other outlets copy their corresponding default inlets.");
        o.inlets.add(new InletBool32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletBool32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletBool32("o" + i, "output " + i));
        }
        o.sKRateCode = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o.sKRateCode += "      case " + i + ": \n";
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    o.sKRateCode += "         %o" + j + "% = %i%;\n";
                } else {
                    o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
                }
            }
            o.sKRateCode += "         break;\n";
        }
        o.sKRateCode += "      default:\n";
        for (int j = 0; j < n; j++) {
            o.sKRateCode += "         %o" + j + "% = %d"+ j +"%;\n";
        }
        o.sKRateCode += "}\n";
        o.helpPatch = "demux 3.axh";
        return o;
    }

}
