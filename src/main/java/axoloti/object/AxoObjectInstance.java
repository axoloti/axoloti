/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package axoloti.object;

import axoloti.MainFrame;
import axoloti.Net;
import axoloti.PatchModel;
import axoloti.PatchView;
import axoloti.PatchViewPiccolo;
import axoloti.PatchViewSwing;
import axoloti.SDFileReference;
import axoloti.Synonyms;
import axoloti.attribute.*;
import axoloti.datatypes.DataType;
import axoloti.datatypes.Frac32buffer;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.*;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj")
public class AxoObjectInstance extends AxoObjectInstanceAbstract implements ObjectModifiedListener {

    public ArrayList<InletInstance> inletInstances = new ArrayList<InletInstance>();
    public ArrayList<OutletInstance> outletInstances = new ArrayList<OutletInstance>();
    @Path("params")
    @ElementListUnion({
        @ElementList(entry = "frac32.u.map", type = ParameterInstanceFrac32UMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map", type = ParameterInstanceFrac32SMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.mapvsl", type = ParameterInstanceFrac32UMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.mapvsl", type = ParameterInstanceFrac32SMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "int32", type = ParameterInstanceInt32Box.class, inline = true, required = false),
        @ElementList(entry = "int32.small", type = ParameterInstanceInt32BoxSmall.class, inline = true, required = false),
        @ElementList(entry = "int32.hradio", type = ParameterInstanceInt32HRadio.class, inline = true, required = false),
        @ElementList(entry = "int32.vradio", type = ParameterInstanceInt32VRadio.class, inline = true, required = false),
        @ElementList(entry = "int2x16", type = ParameterInstance4LevelX16.class, inline = true, required = false),
        @ElementList(entry = "bin12", type = ParameterInstanceBin12.class, inline = true, required = false),
        @ElementList(entry = "bin16", type = ParameterInstanceBin16.class, inline = true, required = false),
        @ElementList(entry = "bin32", type = ParameterInstanceBin32.class, inline = true, required = false),
        @ElementList(entry = "bool32.tgl", type = ParameterInstanceBin1.class, inline = true, required = false),
        @ElementList(entry = "bool32.mom", type = ParameterInstanceBin1Momentary.class, inline = true, required = false)})
    public ArrayList<ParameterInstance> parameterInstances = new ArrayList<ParameterInstance>();
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = "objref", type = AttributeInstanceObjRef.class, inline = true, required = false),
        @ElementList(entry = "table", type = AttributeInstanceTablename.class, inline = true, required = false),
        @ElementList(entry = "combo", type = AttributeInstanceComboBox.class, inline = true, required = false),
        @ElementList(entry = "int", type = AttributeInstanceInt32.class, inline = true, required = false),
        @ElementList(entry = "spinner", type = AttributeInstanceSpinner.class, inline = true, required = false),
        @ElementList(entry = "file", type = AttributeInstanceSDFile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AttributeInstanceTextEditor.class, inline = true, required = false)})
    public ArrayList<AttributeInstance> attributeInstances = new ArrayList<AttributeInstance>();
    public ArrayList<DisplayInstance> displayInstances = new ArrayList<DisplayInstance>();

    public AxoObjectInstance() {
        super();
    }

    public AxoObjectInstance(AxoObject type, PatchModel patchModel, String InstanceName1, Point location) {
        super(type, patchModel, InstanceName1, location);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean result = super.setInstanceName(s);
        for (InletInstance i : inletInstances) {
            i.RefreshName();
        }
        for (OutletInstance i : outletInstances) {
            i.RefreshName();
        }
        return result;
    }

    @Override
    public InletInstance GetInletInstance(String n) {
        for (InletInstance o : inletInstances) {
            if (n.equals(o.GetLabel())) {
                return o;
            }
        }
        for (InletInstance o : inletInstances) {
            String s = Synonyms.instance().inlet(n);
            if (o.GetLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public OutletInstance GetOutletInstance(String n) {
        for (OutletInstance o : outletInstances) {
            if (n.equals(o.GetLabel())) {
                return o;
            }
        }
        for (OutletInstance o : outletInstances) {
            String s = Synonyms.instance().outlet(n);
            if (o.GetLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    public ParameterInstance GetParameterInstance(String n) {
        for (ParameterInstance o : parameterInstances) {
            if (n.equals(o.parameter.name)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public String GenerateInstanceDataDeclaration2() {
        String c = "";
        if (getType().sLocalData != null) {
            String s = getType().sLocalData;
            s = s.replaceAll("attr_parent", getCInstanceName());
            c += s + "\n";
        }
        return c;
    }

    @Override
    public boolean hasStruct() {
        if (getParameterInstances() != null && !(getParameterInstances().isEmpty())) {
            return true;
        }
        if (getType().sLocalData == null) {
            return false;
        }
        return getType().sLocalData.length() != 0;
    }

    @Override
    public boolean hasInit() {
        if (getType().sInitCode == null) {
            return false;
        }
        return getType().sInitCode.length() != 0;
    }

    public String GenerateInstanceCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
        for (ParameterInstance p : parameterInstances) {
            c += p.GenerateCodeDeclaration(classname);
        }
        c += GenerateInstanceDataDeclaration2();
        for (AttributeInstance p : attributeInstances) {
            if (p.CValue() != null) {
                c = c.replaceAll(p.GetCName(), p.CValue());
            }
        }
        return c;
    }

    @Override
    public String GenerateInitCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
//        if (hasStruct())
//            c = "  void " + GenerateInitFunctionName() + "(" + GenerateStructName() + " * x ) {\n";
//        else
//        if (!classname.equals("one"))
        c += "parent = _parent;\n";
        for (ParameterInstance p : parameterInstances) {
            if (p.parameter.PropagateToChild != null) {
                c += "// on Parent: propagate " + p.getName() + " " + enableOnParent + " " + getLegalName() + "" + p.parameter.PropagateToChild + "\n";
                c += p.PExName("parent->") + ".pfunction = PropagateToSub;\n";
                c += p.PExName("parent->") + ".finalvalue = (int32_t)(&(parent->instance"
                        + getLegalName() + "_i.PExch[instance" + getLegalName() + "::PARAM_INDEX_"
                        + p.parameter.PropagateToChild + "]));\n";

            } else {
                c += p.GenerateCodeInit("parent->", "");
            }
            c += p.GenerateCodeInitModulator("parent->", "");
            //           if ((p.isOnParent() && !enableOnParent)) {
            //c += "// on Parent: propagate " + p.name + "\n";
            //String parentparametername = classname.substring(8);
            //c += "// classname : " + classname + " : " + parentparametername + "\n";
            //c += "parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].pfunction = PropagateToSub;\n";
            //c += "parent->parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].finalvalue = (int32_t)(&(" + p.PExName("parent->") + "));\n";
            //         }
        }
        for (DisplayInstance p : displayInstances) {
            c += p.GenerateCodeInit("");
        }
        if (getType().sInitCode != null) {
            String s = getType().sInitCode;
            for (AttributeInstance p : attributeInstances) {
                s = s.replace(p.GetCName(), p.CValue());
            }
            c += s + "\n";
        }
        String d = "  public: void Init(" + classname + " * _parent";
        if (!displayInstances.isEmpty()) {
            for (DisplayInstance p : displayInstances) {
                if (p.display.getLength() > 0) {
                    d += ",\n";
                    if (p.display.getDatatype().isPointer()) {
                        d += p.display.getDatatype().CType() + " " + p.GetCName();
                    } else {
                        d += p.display.getDatatype().CType() + " & " + p.GetCName();
                    }
                }
            }
        }
        d += ") {\n" + c + "}\n";
        return d;
    }

    @Override
    public String GenerateDisposeCodePlusPlus(String classname) {
        String c = "";
        if (getType().sDisposeCode != null) {
            String s = getType().sDisposeCode;
            for (AttributeInstance p : attributeInstances) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            c += s + "\n";
        }
        c = "  public: void Dispose() {\n" + c + "}\n";
        return c;
    }

    public String GenerateKRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        String s = getType().sKRateCode;
        if (s != null) {
            for (AttributeInstance p : attributeInstances) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            s = s.replace("attr_name", getCInstanceName());
            s = s.replace("attr_legal_name", getLegalName());
            for (ParameterInstance p : parameterInstances) {
                if (p.isOnParent() && enableOnParent) {
//                    s = s.replace("%" + p.name + "%", OnParentAccess + p.variableName(vprefix, enableOnParent));
                } else {
//                    s = s.replace("%" + p.name + "%", p.variableName(vprefix, enableOnParent));
                }
            }
            for (DisplayInstance p : displayInstances) {
//                s = s.replace("%" + p.name + "%", p.valueName(vprefix));
            }
            return s + "\n";
        }
        return "";
    }

    public String GenerateSRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        if (getType().sSRateCode != null) {
            String s = "int buffer_index;\n"
                    + "for(buffer_index=0;buffer_index<BUFSIZE;buffer_index++) {\n"
                    + getType().sSRateCode
                    + "\n}\n";

            for (AttributeInstance p : attributeInstances) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            for (InletInstance i : inletInstances) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.GetCName(), i.GetCName() + "[buffer_index]");
                }
            }
            for (OutletInstance i : outletInstances) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.GetCName(), i.GetCName() + "[buffer_index]");
                }
            }

            s = s.replace("attr_name", getCInstanceName());
            s = s.replace("attr_legal_name", getLegalName());

            return s;
        }
        return "";
    }

    public String GenerateDoFunctionPlusPlus(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        String s;
        boolean comma = false;
        s = "  public: void dsp (";
        for (InletInstance i : inletInstances) {
            if (comma) {
                s += ",\n";
            }
            s += "const " + i.getDataType().CType() + " " + i.GetCName();
            comma = true;
        }
        for (OutletInstance i : outletInstances) {
            if (comma) {
                s += ",\n";
            }
            s += i.getDataType().CType() + " & " + i.GetCName();
            comma = true;
        }
        for (ParameterInstance i : parameterInstances) {
            if (i.parameter.PropagateToChild == null) {
                if (comma) {
                    s += ",\n";
                }
                s += i.parameter.CType() + " " + i.GetCName();
                comma = true;
            }
        }
        for (DisplayInstance i : displayInstances) {
            if (i.display.getLength() > 0) {
                if (comma) {
                    s += ",\n";
                }
                if (i.display.getDatatype().isPointer()) {
                    s += i.display.getDatatype().CType() + " " + i.GetCName();
                } else {
                    s += i.display.getDatatype().CType() + " & " + i.GetCName();
                }
                comma = true;
            }
        }
        s += "  ){\n";
        s += GenerateKRateCodePlusPlus("", enableOnParent, OnParentAccess);
        s += GenerateSRateCodePlusPlus("", enableOnParent, OnParentAccess);
        s += "}\n";
        return s;
    }

    public final static String MidiHandlerFunctionHeader = "void MidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n";

    @Override
    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        String s = "";
        s += "class " + getCInstanceName() + "{\n";
        s += "  public: // v1\n";
        s += "  " + ClassName + " *parent;\n";
        s += GenerateInstanceCodePlusPlus(ClassName, enableOnParent);
        s += GenerateInitCodePlusPlus(ClassName, enableOnParent);
        s += GenerateDisposeCodePlusPlus(ClassName);
        s += GenerateDoFunctionPlusPlus(ClassName, OnParentAccess, enableOnParent);
        {
            String d3 = GenerateCodeMidiHandler("");
            if (!d3.isEmpty()) {
                s += MidiHandlerFunctionHeader;
                s += d3;
                s += "}\n";
            }
        }
        s += "}\n;";
        return s;
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        String s = "";
        if (getType().sMidiCode != null) {
            s += getType().sMidiCode;
        }
        for (ParameterInstance i : parameterInstances) {
            s += i.GenerateCodeMidiHandler("");
        }
        for (AttributeInstance p : attributeInstances) {
            s = s.replaceAll(p.GetCName(), p.CValue());
        }
        s = s.replace("attr_name", getCInstanceName());
        s = s.replace("attr_legal_name", getLegalName());

        if (s.length() > 0) {
            return "{\n" + s + "}\n";
        } else {
            return "";
        }
    }

    @Override
    public String GenerateCallMidiHandler() {
        if ((getType().sMidiCode != null) && (!getType().sMidiCode.isEmpty())) {
            return getCInstanceName() + "_i.MidiInHandler(dev, port, status, data1, data2);\n";
        }
        for (ParameterInstance pi : getParameterInstances()) {
            if (!pi.GenerateCodeMidiHandler("").isEmpty()) {
                return getCInstanceName() + "_i.MidiInHandler(dev, port, status, data1, data2);\n";
            }
        }
        return "";
    }

    @Override
    public boolean providesModulationSource() {
        AxoObject atype = getType();
        if (atype == null) {
            return false;
        } else {
            return atype.providesModulationSource();
        }
    }

    @Override
    public AxoObject getType() {
        return (AxoObject) super.getType();
    }

    @Override
    public boolean PromoteToOverloadedObj() {
        if (getType() instanceof AxoObjectFromPatch) {
            return false;
        }
        if (getType() instanceof AxoObjectPatcher) {
            return false;
        }
        if (getType() instanceof AxoObjectPatcherObject) {
            return false;
        }
        String id = typeName;
        ArrayList<AxoObjectAbstract> candidates = MainFrame.axoObjects.GetAxoObjectFromName(id, getPatchModel().GetCurrentWorkingDirectory());
        if (candidates == null) {
            return false;
        }
        if (candidates.isEmpty()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "could not resolve any candidates {0}", id);
        }
        if (candidates.size() == 1) {
            return false;
        }

        int ranking[];
        ranking = new int[candidates.size()];
        // auto-choose depending on 1st connected inlet

        //      InletInstance i = null;// = GetInletInstances().get(0);
        for (InletInstance j : getInletInstances()) {
            Net n = getPatchModel().GetNet(j);
            if (n == null) {
                continue;
            }
            DataType d = n.getDataType();
            if (d == null) {
                continue;
            }
            String name = j.getInlet().getName();
            for (int i = 0; i < candidates.size(); i++) {
                AxoObjectAbstract o = candidates.get(i);
                Inlet i2 = o.GetInlet(name);
                if (i2 == null) {
                    continue;
                }
                if (i2.getDatatype().equals(d)) {
                    ranking[i] += 10;
                } else if (d.IsConvertableToType(i2.getDatatype())) {
                    ranking[i] += 2;
                }
            }
        }

        int max = -1;
        int maxi = 0;
        for (int i = 0; i < candidates.size(); i++) {
            if (ranking[i] > max) {
                max = ranking[i];
                maxi = i;
            }
        }
        AxoObjectAbstract selected = candidates.get(maxi);
        int rindex = candidates.indexOf(getType());
        if (rindex >= 0) {
            if (ranking[rindex] == max) {
                selected = getType();
            }
        }

        if (selected == null) {
            //Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO,"no promotion to null" + this + " to " + selected);
            return false;
        }
        if (selected != getType()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.FINE, "promoting " + this + " to " + selected);
            getPatchModel().ChangeObjectInstanceType(this, selected);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<SDFileReference> GetDependendSDFiles() {
        ArrayList<SDFileReference> files = getType().filedepends;
        if (files == null) {
            files = new ArrayList<SDFileReference>();
        } else {
            String p1 = getType().sPath;
            if (p1 == null) {
                // embedded object, reference path is of the patch
                p1 = getPatchModel().getFileNamePath();
                if (p1 == null) {
                    p1 = "";
                }
            }
            File f1 = new File(p1);
            java.nio.file.Path p = f1.toPath().getParent();
            for (SDFileReference f : files) {
                f.Resolve(p);
            }
        }
        for (AttributeInstance a : attributeInstances) {
            ArrayList<SDFileReference> f2 = a.GetDependendSDFiles();
            if (f2 != null) {
                files.addAll(f2);
            }
        }
        return files;
    }

    public void ConvertToPatchPatcher() {
        try {
            ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/patcher", null);
            assert (!ol.isEmpty());
            AxoObjectAbstract o = ol.get(0);
            AxoObjectInstancePatcher oi = (AxoObjectInstancePatcher) getPatchModel().AddObjectInstance(o, new Point(x, y));
            AxoObjectFromPatch ao = (AxoObjectFromPatch) getType();
            Strategy strategy = new AnnotationStrategy();
            Serializer serializer = new Persister(strategy);
            oi.setSubPatchModel(serializer.read(PatchModel.class, new File(ao.patchModel.getFileNamePath())));
            oi.initSubpatchFrame();
            oi.updateObj();
            getPatchModel().transferState(this, oi);
            getPatchModel().delete(this);
            getPatchModel().setDirty();
            oi.setInstanceName(getInstanceName());
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Failed to convert to patch/patcher", ex);
        }
    }

    public void ConvertToEmbeddedObj() {
        try {
            ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/object", null);
            assert (!ol.isEmpty());
            AxoObjectAbstract o = ol.get(0);
            String iname = getInstanceName();
            AxoObjectInstancePatcherObject oi = (AxoObjectInstancePatcherObject) getPatchModel().ChangeObjectInstanceType1(this, o);
            AxoObject ao = getType();
            oi.ao = new AxoObjectPatcherObject(ao.id, ao.sDescription);
            oi.ao.copy(ao);
            oi.ao.sPath = "";
            oi.ao.upgradeSha = null;
            oi.ao.CloseEditor();
            oi.setInstanceName(iname);
            getPatchModel().setDirty();
            getPatchModel().transferState(this, oi);
            getPatchModel().delete(this);
            getPatchModel().setDirty();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Persist
    public void Persist() {
        AxoObject o = getType();
        if (o != null) {
            if (o.uuid != null && !o.uuid.isEmpty()) {
                typeUUID = o.uuid;
                typeSHA = null;
            }
        }
    }

    @Override
    public IAxoObjectInstanceView getViewInstance(PatchView patchView) {
        if (patchView instanceof PatchViewSwing) {
            return new AxoObjectInstanceView(this, (PatchViewSwing) patchView);
        } else {
            return new PAxoObjectInstanceView(this, (PatchViewPiccolo) patchView);
        }
    }

    @Override
    public IAxoObjectInstanceView createView(PatchView patchView) {
        IAxoObjectInstanceView pi = getViewInstance(patchView);
        pi.PostConstructor();
        return pi;
    }

    @Override
    public void updateObj1() {
        getType().addObjectModifiedListener(this);
    }

    public Rectangle editorBounds;
    public Integer editorActiveTabIndex;
    public boolean deferredObjTypeUpdate = false;

    public void updateObj() {
        getPatchModel().ChangeObjectInstanceType(this, this.getType());
    }

    @Override
    public void ObjectModified(Object src) {
        if (getPatchModel() != null) {
            if (!getPatchModel().isLocked()) {
                updateObj();
            } else {
                deferredObjTypeUpdate = true;
            }
        }

        try {
            AxoObject o = (AxoObject) src;
            if (o.editor != null && o.editor.getBounds() != null) {
                editorBounds = o.editor.getBounds();
                editorActiveTabIndex = o.editor.getActiveTabIndex();
                this.getType().editorBounds = editorBounds;
                this.getType().editorActiveTabIndex = editorActiveTabIndex;
            }
        } catch (ClassCastException ex) {
        }
    }

    @Override
    public void Close() {
        super.Close();
        for (AttributeInstance a : attributeInstances) {
            a.Close();
        }
    }

    public ArrayList<InletInstance> getInletInstances() {
        return this.inletInstances;
    }

    @Override
    public ArrayList<OutletInstance> getOutletInstances() {
        return this.outletInstances;
    }

    @Override
    public ArrayList<ParameterInstance> getParameterInstances() {
        return this.parameterInstances;
    }

    @Override
    public ArrayList<AttributeInstance> getAttributeInstances() {
        return this.attributeInstances;
    }

    @Override
    public ArrayList<DisplayInstance> getDisplayInstances() {
        return this.displayInstances;
    }

    @Override
    public void setInletInstances(ArrayList<InletInstance> inletInstances) {
        this.inletInstances = inletInstances;
    }

    @Override
    public void setOutletInstances(ArrayList<OutletInstance> outletInstances) {
        this.outletInstances = outletInstances;
    }

    @Override
    public void setParameterInstances(ArrayList<ParameterInstance> parameterInstances) {
        this.parameterInstances = parameterInstances;
    }

    @Override
    public void setAttributeInstances(ArrayList<AttributeInstance> attributeInstances) {
        this.attributeInstances = attributeInstances;
    }

    @Override
    public void setDisplayInstances(ArrayList<DisplayInstance> displayInstances) {
        this.displayInstances = displayInstances;
    }
}
