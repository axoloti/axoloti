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

import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectComment;
import axoloti.object.AxoObjectPatcher;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import axoloti.outlets.OutletInt32Pos;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Patch extends gentools {

    static void GenerateAll() {
        String catName = "patch";
        WriteAxoObject(catName, Create_inlet());
        WriteAxoObject(catName, Create_inlet_b());
        WriteAxoObject(catName, Create_inlet_i());
        WriteAxoObject(catName, Create_inlet_tilde());
        WriteAxoObject(catName, Create_outlet());
        WriteAxoObject(catName, Create_outlet_b());
        WriteAxoObject(catName, Create_outlet_i());
        WriteAxoObject(catName, Create_outlet_tilde());
        WriteAxoObject(catName, CreatePreset());
        WriteAxoObject(catName, Create_comment());
        WriteAxoObject(catName, modsource_cc());
        WriteAxoObject(catName, modsource());
        WriteAxoObject(catName, Create_send());
        WriteAxoObject(catName, Create_sendi());
        WriteAxoObject(catName, Create_sendb());
        WriteAxoObject(catName, Create_recv());
        WriteAxoObject(catName, Create_recvi());
        WriteAxoObject(catName, Create_recvb());
        WriteAxoObject(catName, CreateLoadPatch());
//        WriteAxoObject("patch", CreateInitMsg());
        WriteAxoObject(catName, CreatePolyIndex());

        WriteAxoObject(catName, CreatePatcher());

    }

    static AxoObject Create_inlet() {
        AxoObject o = new AxoObject("inlet f", "Fractional inlet. The inlet object becomes an inlet connector when this patch is used as an object (subpatch)");
        o.outlets.add(new OutletFrac32("inlet", "inlet"));
        o.sLocalData = "int32_t _inlet;\n";
        o.sKRateCode = "  %inlet% = _inlet;\n";
        return o;
    }

    static AxoObject Create_inlet_b() {
        AxoObject o = new AxoObject("inlet b", "Boolean inlet. The inlet object becomes an inlet connector when this patch is used as an object (subpatch)");
        o.outlets.add(new OutletBool32("inlet", "inlet"));
        o.sLocalData = "int32_t _inlet;\n";
        o.sKRateCode = "  %inlet% = _inlet;\n";
        return o;
    }

    static AxoObject Create_inlet_i() {
        AxoObject o = new AxoObject("inlet i", "Integer inlet. The inlet object becomes an inlet connector when this patch is used as an object (subpatch)");
        o.outlets.add(new OutletInt32("inlet", "inlet"));
        o.sLocalData = "int32_t _inlet;\n";
        o.sKRateCode = "  %inlet% = _inlet;\n";
        return o;
    }

    static AxoObject Create_inlet_tilde() {
        AxoObject o = new AxoObject("inlet a", "Fractional buffer (audio) inlet. The inlet object becomes an inlet connector when this patch is used as an object (subpatch)");
        o.outlets.add(new OutletFrac32Buffer("inlet", "inlet"));
        o.sLocalData = "int32buffer _inlet;\n";
        o.sSRateCode = "   %inlet% = _inlet[buffer_index];";
        return o;
    }

    static AxoObject Create_send() {
        AxoObject o = new AxoObject("send f", "send (to recv object), fractional type");
        o.inlets.add(new InletFrac32("v", "v"));
        o.sLocalData = "int32_t _v;\n";
        o.sKRateCode = "  _v = %v%;\n";
        return o;
    }

    static AxoObject Create_sendi() {
        AxoObject o = new AxoObject("send i", "send (to recv object), integer type");
        o.inlets.add(new InletInt32("v", "v"));
        o.sLocalData = "int32_t _vi;\n";
        o.sKRateCode = "  _vi = %v%;\n";
        return o;
    }

    static AxoObject Create_sendb() {
        AxoObject o = new AxoObject("send b", "send (to recv object), boolean type");
        o.inlets.add(new InletBool32("v", "v"));
        o.sLocalData = "int32_t _vb;\n";
        o.sKRateCode = "  _vb = %v%;\n";
        return o;
    }

    static AxoObject Create_recv() {
        AxoObject o = new AxoObject("recv f", "receive (from send), fractional type");
        o.attributes.add(new AxoAttributeObjRef("sender"));
        o.outlets.add(new OutletFrac32("v", "v"));
        o.sKRateCode = "%v% = %sender%._v;\n";
        return o;
    }

    static AxoObject Create_recvi() {
        AxoObject o = new AxoObject("recv i", "receive (from send), integer type");
        o.attributes.add(new AxoAttributeObjRef("sender"));
        o.outlets.add(new OutletInt32("v", "v"));
        o.sKRateCode = "%v% = %sender%._vi;\n";
        return o;
    }

    static AxoObject Create_recvb() {
        AxoObject o = new AxoObject("recv b", "receive (from send), boolean type");
        o.attributes.add(new AxoAttributeObjRef("sender"));
        o.outlets.add(new OutletBool32("v", "v"));
        o.sKRateCode = "%v% = %sender%._vb;\n";
        return o;
    }

    static AxoObject Create_outlet() {
        AxoObject o = new AxoObject("outlet f", "Fractional outlet. The outlet object becomes an outlet connector when this patch is used as an object (subpatch)");
        o.inlets.add(new InletFrac32("outlet", "outlet"));
        o.sLocalData = "int32_t _outlet;\n";
        o.sKRateCode = "  _outlet = %outlet%;\n";
        return o;
    }

    static AxoObject Create_outlet_b() {
        AxoObject o = new AxoObject("outlet b", "Boolean outlet. The outlet object becomes an outlet connector when this patch is used as an object (subpatch)");
        o.inlets.add(new InletBool32("outlet", "outlet"));
        o.sLocalData = "int32_t _outlet;\n";
        o.sKRateCode = "  _outlet = %outlet%;\n";
        return o;
    }

    static AxoObject Create_outlet_i() {
        AxoObject o = new AxoObject("outlet i", "Integer outlet. The outlet object becomes an outlet connector when this patch is used as an object (subpatch)");
        o.inlets.add(new InletInt32("outlet", "outlet"));
        o.sLocalData = "int32_t _outlet;\n";
        o.sKRateCode = "  _outlet = %outlet%;\n";
        return o;
    }

    static AxoObject Create_outlet_tilde() {
        AxoObject o = new AxoObject("outlet a", "Fractional buffer (audio) outlet. The outlet object becomes an outlet connector when this patch is used as an object (subpatch)");
        o.inlets.add(new InletFrac32Buffer("outlet", "outlet"));
        o.sLocalData = "int32buffer _outlet;\n";
        o.sSRateCode = "   _outlet[buffer_index] = %outlet%;\n";
        return o;
    }

    static AxoObject modsource_cc() {
        AxoObject o = new AxoObject("modsource_cc", "midi cc modulation source");
        o.attributes.add(new AxoAttributeInt32("MidiCC", 0, 127, 0));
        o.SetProvidesModulationSource();
        //o.sInstanceData = "PExModulationTargets_t ;\n";
        o.sMidiCode = "        if ((status == MIDI_CONTROL_CHANGE + %midichannel%)&&(data1 == %MidiCC%)) {\n"
                + "            PExModulationSourceChange(&PExModulationSources[MODULATOR_%name%],data2<<20);\n"
                + "        }\n";
        return o;
    }

    static AxoObject modsource() {
        AxoObject o = new AxoObject("modsource", "generic modulation source");
        o.inlets.add(new InletFrac32("v", "value"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.SetProvidesModulationSource();
        //o.sInstanceData = "PExModulationTargets_t ;\n";
        o.sLocalData = "int ntrig;\n";
//        o.sInitCode = "int i;\n"
//                + "for(i=0;i<NMODULATIONTARGETS;i++)\n"
//                + "   parent2->PExModulationSources[MODULATOR_%name%][i].PEx = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {PExModulationSourceChange(&parent->PExModulationSources[MODULATOR_%name%][0],NMODULATIONTARGETS,%v%);  ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObjectAbstract Create_comment() {
        AxoObjectComment o = new AxoObjectComment("comment", "free text comment");
        return o;
    }

    static AxoObject CreatePreset() {
        AxoObject o = new AxoObject("preset", "apply preset, preset zero = init, and will reset ALL parameters, not just the presets");
        o.inlets.add(new InletInt32("preset", "preset number"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.sLocalData = "int ntrig;\n";
        o.sInitCode = "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) {parent->ApplyPreset(%preset%) ; ntrig=1;}\n"
                + "   else if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject CreateLoadPatch() {
        AxoObject o = new AxoObject("load", "load a patch from sdcard");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.attributes.add(new AxoAttributeTablename("filename"));
        o.sLocalData = "int ntrig;\n";
        o.sInitCode = "ntrig = 1;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) {LoadPatch(\"%filename%\"); ntrig=1;}\n"
                + "   else if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject CreateInitMsg() {
        AxoObject o = new AxoObject("initmsg", "prints a message on patch init");
        o.attributes.add(new AxoAttributeTablename("message"));
        o.sInitCode = "TransmitTextMessage(\"%message%\");\n";
        return o;
    }

    static AxoObject CreatePolyIndex() {
        AxoObject o = new AxoObject("polyindex", "Outputs the voice index number from 0 to n-1. Only works in a polyphonic sub-patch!");
        o.outlets.add(new OutletInt32Pos("index", "index from 0 to n-1"));
        o.sKRateCode = "%index% = parent->polyIndex;\n";
        return o;
    }

    static AxoObject CreatePatcher() {
        AxoObject o = new AxoObjectPatcher("patcher", "Subpatch object stored in the patch document (IN DEVELOPMENT!)");
        return o;
    }
}
