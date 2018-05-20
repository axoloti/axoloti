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

import axoloti.object.AxoObject;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.inlet.InletInt32Pos;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;

/**
 *
 * @author jtaelman
 */
class Demux extends GenTools {

    static void generateAll() {
        String catName = "demux";
        writeAxoObject(catName, new AxoObject[]{create_demux2(), create_demux2Tilde(), create_demux2I(), create_demux2b()});

        for (int i = 3; i < 9; i++) {
            writeAxoObject(catName, new AxoObject[]{create_demuxn(i), create_demuxni(i), create_demuxntilde(i), create_demuxnb(i)});
        }
    }

    private static final String descriptionDemux2 = "Demultiplexer. Connects inlet i to outlet o0 when s is false, to outlet o1 when s is true.";
    private static final String descriptionDemuxN = "Demultiplexer. Connects inlet i to outlet number s. Other outlets copy their corresponding default inlets.";
    private static final String demux2code = "   %o0%= (%s%)?%d0%:%i%;\n"
            + "   %o1%= (%s%)?%i%:%d1%;\n";

    static AxoObject create_demux2() {
        AxoObject o = new AxoObject("demux 2", descriptionDemux2);
        o.inlets.add(new InletFrac32("i", "input"));
        o.inlets.add(new InletFrac32("d0", "default 0"));
        o.inlets.add(new InletFrac32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32("o0", "output 0"));
        o.outlets.add(new OutletFrac32("o1", "output 1"));
        o.sKRateCode = demux2code;
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject create_demux2Tilde() {
        AxoObject o = new AxoObject("demux 2", descriptionDemux2);
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        o.inlets.add(new InletFrac32Buffer("d0", "default 0"));
        o.inlets.add(new InletFrac32Buffer("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32Buffer("o0", "output 0"));
        o.outlets.add(new OutletFrac32Buffer("o1", "output 1"));
        o.sSRateCode = demux2code;
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject create_demux2I() {
        AxoObject o = new AxoObject("demux 2", descriptionDemux2);
        o.inlets.add(new InletInt32("i", "input"));
        o.inlets.add(new InletInt32("d0", "default 0"));
        o.inlets.add(new InletInt32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletInt32("o0", "output 0"));
        o.outlets.add(new OutletInt32("o1", "output 1"));
        o.sKRateCode = demux2code;
        o.helpPatch = "demux 2.axh";
        return o;
    }

    static AxoObject create_demux2b() {
        AxoObject o = new AxoObject("demux 2", descriptionDemux2);
        o.inlets.add(new InletBool32("i", "input"));
        o.inlets.add(new InletBool32("d0", "default 0"));
        o.inlets.add(new InletBool32("d1", "default 1"));
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.sKRateCode = demux2code;
        o.helpPatch = "demux 2.axh";
        return o;
    }

    private static String generateDemuxCode(int n) {
        String o;
        o = "   switch(%s%>0?%s%:0){\n";
        for (int i = 0; i < n; i++) {
            o += "      case " + i + ": \n";
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    o += "         %o" + j + "% = %i%;\n";
                } else {
                    o += "         %o" + j + "% = %d" + j + "%;\n";
                }
            }
            o += "         break;\n";
        }
        o += "      default:\n";
        for (int j = 0; j < n; j++) {
            o += "         %o" + j + "% = %d" + j + "%;\n";
        }
        o += "}\n";
        return o;
    }

    static AxoObject create_demuxn(int n) {
        AxoObject o = new AxoObject("demux " + n, descriptionDemuxN);
        o.inlets.add(new InletFrac32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletFrac32("o" + i, "output " + i));
        }
        o.sKRateCode = generateDemuxCode(n);
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject create_demuxntilde(int n) {
        AxoObject o = new AxoObject("demux " + n, descriptionDemuxN);
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletFrac32Buffer("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletFrac32Buffer("o" + i, "output " + i));
        }
        o.sSRateCode = generateDemuxCode(n);
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject create_demuxni(int n) {
        AxoObject o = new AxoObject("demux " + n, descriptionDemuxN);
        o.inlets.add(new InletInt32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletInt32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletInt32("o" + i, "output " + i));
        }
        o.sKRateCode = generateDemuxCode(n);
        o.helpPatch = "demux 3.axh";
        return o;
    }

    static AxoObject create_demuxnb(int n) {
        AxoObject o = new AxoObject("demux " + n, descriptionDemuxN);
        o.inlets.add(new InletBool32("i", "input"));
        for (int i = 0; i < n; i++) {
            o.inlets.add(new InletBool32("d" + i, "default " + i));
        }
        o.inlets.add(new InletInt32Pos("s", "select"));
        for (int i = 0; i < n; i++) {
            o.outlets.add(new OutletBool32("o" + i, "output " + i));
        }
        o.sKRateCode = generateDemuxCode(n);
        o.helpPatch = "demux 3.axh";
        return o;
    }

}
