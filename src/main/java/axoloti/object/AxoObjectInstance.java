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
import axoloti.Patch;
import axoloti.PatchGUI;
import axoloti.attribute.*;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.datatypes.DataType;
import axoloti.datatypes.DataTypeBuffer;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletInstance;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.*;
import components.LabelComponent;
import components.PopupIcon;
import displays.Display;
import displays.DisplayInstance;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj")
public class AxoObjectInstance extends AxoObjectInstanceAbstract {

    public ArrayList<InletInstance> inletInstances;
    public ArrayList<OutletInstance> outletInstances;
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
        @ElementList(entry = "bin16", type = ParameterInstanceBin16.class, inline = true, required = false),
        @ElementList(entry = "bin32", type = ParameterInstanceBin32.class, inline = true, required = false),
        @ElementList(entry = "bool32.tgl", type = ParameterInstanceBin1.class, inline = true, required = false),
        @ElementList(entry = "bool32.mom", type = ParameterInstanceBin1Momentary.class, inline = true, required = false)})
    public ArrayList<ParameterInstance> parameterInstances;
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = "objref", type = AttributeInstanceObjRef.class, inline = true, required = false),
        @ElementList(entry = "table", type = AttributeInstanceTablename.class, inline = true, required = false),
        @ElementList(entry = "combo", type = AttributeInstanceComboBox.class, inline = true, required = false),
        @ElementList(entry = "int", type = AttributeInstanceInt32.class, inline = true, required = false),
        @ElementList(entry = "spinner", type = AttributeInstanceSpinner.class, inline = true, required = false),
        @ElementList(entry = "file", type = AttributeInstanceWavefile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AttributeInstanceTextEditor.class, inline = true, required = false)})
    ArrayList<AttributeInstance> attributeInstances;
    public ArrayList<DisplayInstance> displayInstances;
    LabelComponent IndexLabel;

    @Override
    public void refreshIndex() {
        if (patch != null) {
            IndexLabel.setText(" " + patch.objectinstances.indexOf(this));
        }
    }

    @Override
    public ArrayList<ParameterInstance> getParameterInstances() {
        return parameterInstances;
    }

    @Override
    public ArrayList<AttributeInstance> getAttributeInstances() {
        return attributeInstances;
    }
    public JPanel p_params;
    public JPanel p_displays;

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        if (this instanceof AxoObjectInstancePatcher) {
            ((AxoObjectInstancePatcher) this).updateObj1();
        }
        if (parameterInstances == null) {
            parameterInstances = new ArrayList<ParameterInstance>();
        }
        if (attributeInstances == null) {
            attributeInstances = new ArrayList<AttributeInstance>();
        }
        if (displayInstances == null) {
            displayInstances = new ArrayList<DisplayInstance>();
        }
        if (inletInstances == null) {
            inletInstances = new ArrayList<InletInstance>();
        }
        if (outletInstances == null) {
            outletInstances = new ArrayList<OutletInstance>();
        }
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        final PopupIcon popupIcon = new PopupIcon();
        popupIcon.setPopupIconListener(
                new PopupIcon.PopupIconListener() {
                    @Override
                    public void ShowPopup() {
                        if (popup.getParent() == null) {
                            popupIcon.add(popup);
                        }
                        popup.show(popupIcon,
                                0, popupIcon.getHeight());
                    }
                });
        Titlebar.add(popupIcon);

        LabelComponent idlbl = new LabelComponent(typeName);
        idlbl.setAlignmentX(LEFT_ALIGNMENT);
        Titlebar.add(idlbl);

        Titlebar.setToolTipText("<html>" + getType().sDescription
                + "<p>Author: " + getType().sAuthor
                + "<p>License: " + getType().sLicense
                + "<p>Path: " + getType().sPath);
        MenuItem popm_edit = new MenuItem("edit object definition");
        popm_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getType().OpenEditor();
            }
        });
        popup.add(popm_edit);
        MenuItem popm_editInstanceName = new MenuItem("edit instance name");
        popm_editInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addInstanceNameEditor();
            }
        });
        popup.add(popm_editInstanceName);
        MenuItem popm_substitute = new MenuItem("substitute");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((PatchGUI) patch).ShowClassSelector(AxoObjectInstance.this.getLocation(), AxoObjectInstance.this);
            }
        });
        popup.add(popm_substitute);

        /*
         h.add(Box.createHorizontalStrut(3));
         h.add(Box.createHorizontalGlue());
         h.add(new JSeparator(SwingConstants.VERTICAL));*/
//        IndexLabel.setSize(IndexLabel.getMinimumSize());
        IndexLabel = new LabelComponent("");
        refreshIndex();
        //h.add(IndexLabel);
        //IndexLabel.setAlignmentX(RIGHT_ALIGNMENT);
        Titlebar.setAlignmentX(LEFT_ALIGNMENT);
        add(Titlebar);
        Titlebar.doLayout();
        InstanceLabel = new LabelComponent(getInstanceName());
        InstanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        InstanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        add(InstanceLabel);

        JPanel p_iolets = new JPanel();
        p_iolets.setLayout(new BoxLayout(p_iolets, BoxLayout.LINE_AXIS));
        p_iolets.setAlignmentX(LEFT_ALIGNMENT);
        JPanel p_inlets = new JPanel();
        p_inlets.setLayout(new BoxLayout(p_inlets, BoxLayout.PAGE_AXIS));
        p_inlets.setAlignmentX(LEFT_ALIGNMENT);
        JPanel p_outlets = new JPanel();
        p_outlets.setLayout(new BoxLayout(p_outlets, BoxLayout.PAGE_AXIS));
        p_outlets.setAlignmentX(RIGHT_ALIGNMENT);
        p_params = new JPanel();
        if (getType().getRotatedParams()) {
            p_params.setLayout(new BoxLayout(p_params, BoxLayout.LINE_AXIS));
        } else {
            p_params.setLayout(new BoxLayout(p_params, BoxLayout.PAGE_AXIS));
        }
        p_displays = new JPanel();
        if (getType().getRotatedParams()) {
            p_displays.setLayout(new BoxLayout(p_displays, BoxLayout.LINE_AXIS));
        } else {
            p_displays.setLayout(new BoxLayout(p_displays, BoxLayout.PAGE_AXIS));
        }
        p_displays.add(Box.createHorizontalGlue());
        p_params.add(Box.createHorizontalGlue());

//        inletInstances = new ArrayList<InletInstance>();
//        outletInstances =
        for (Inlet inl : getType().inlets) {
            InletInstance inlin = GetInletInstance(inl.name);
            if (inlin == null) {
                inlin = new InletInstance(inl, this);
                inletInstances.add(inlin);
            }
            inlin.setAlignmentX(LEFT_ALIGNMENT);
            p_inlets.add(inlin);
        }

        for (Outlet o : getType().outlets) {
            OutletInstance oin = GetOutletInstance(o.name);
            if (oin == null) {
                oin = new OutletInstance(o, this);
                outletInstances.add(oin);
            }
            oin.setAlignmentX(RIGHT_ALIGNMENT);
            p_outlets.add(oin);
        }/*
         if (p_inlets.getComponents().length == 0){
         p_inlets.add(Box.createHorizontalGlue());
         }
         if (p_outlets.getComponents().length == 0){
         p_outlets.add(Box.createHorizontalGlue());
         }*/

        p_iolets.add(p_inlets);
        p_iolets.add(Box.createHorizontalGlue());
        p_iolets.add(p_outlets);
        add(p_iolets);
//        p_iolets.setBackground(Color.red);

        for (AxoAttribute p : getType().attributes) {
            AttributeInstance attri = p.CreateInstance(this);
            attri.setAlignmentX(LEFT_ALIGNMENT);
            add(attri);
            attri.doLayout();
            attributeInstances.add(attri);
        }

        for (Parameter p : getType().params) {
            ParameterInstance pin = p.CreateInstance(this);
            pin.setAlignmentX(RIGHT_ALIGNMENT);
            pin.doLayout();
            parameterInstances.add(pin);
        }
        boolean cont;
        do {
            cont = false;
            for (ParameterInstance pi : parameterInstances) {
                if (pi.axoObj == null) {
                    parameterInstances.remove(pi);
                    Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "Unresolved parameter " + getInstanceName() + ":" + pi.name);
                    cont = true;
                    break;
                }
            }
        } while (cont);
        do {
            cont = false;
            for (AttributeInstance pi : attributeInstances) {
                if (pi.axoObj == null) {
                    attributeInstances.remove(pi);
                    Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "Unresolved attribute " + getInstanceName() + ":" + pi.getAttributeName());
                    cont = true;
                    break;
                }
            }
        } while (cont);

        for (Display p : getType().displays) {
            System.out.println(p.toString());
            DisplayInstance pin = p.CreateInstance(this);
            pin.setAlignmentX(RIGHT_ALIGNMENT);
            pin.doLayout();
            displayInstances.add(pin);
        }
//        p_displays.add(Box.createHorizontalGlue());
//        p_params.add(Box.createHorizontalGlue());
        add(p_params);
        add(p_displays);
        p_params.setAlignmentX(LEFT_ALIGNMENT);
        p_displays.setAlignmentX(LEFT_ALIGNMENT);
        resizeToGrid();
    }

    public AxoObjectInstance() {
        inletInstances = new ArrayList<InletInstance>();
        outletInstances = new ArrayList<OutletInstance>();
        displayInstances = new ArrayList<DisplayInstance>();
        parameterInstances = new ArrayList<ParameterInstance>();
        attributeInstances = new ArrayList<AttributeInstance>();
    }

    public AxoObjectInstance(AxoObject type, Patch patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
        inletInstances = new ArrayList<InletInstance>();
        outletInstances = new ArrayList<OutletInstance>();
        displayInstances = new ArrayList<DisplayInstance>();
        parameterInstances = new ArrayList<ParameterInstance>();
        attributeInstances = new ArrayList<AttributeInstance>();
    }

    @Override
    public void setInstanceName(String s) {
        super.setInstanceName(s);
        for (InletInstance i : inletInstances) {
            i.RefreshName();
        }
        for (OutletInstance i : outletInstances) {
            i.RefreshName();
        }
    }

    @Override
    public InletInstance GetInletInstance(String n) {
        for (InletInstance o : inletInstances) {
            if (n.equals(o.GetLabel())) {
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
    public void Lock() {
        super.Lock();
        for (AttributeInstance a : attributeInstances) {
            a.Lock();
        }
    }

    @Override
    public void Unlock() {
        super.Unlock();
        for (AttributeInstance a : attributeInstances) {
            a.UnLock();
        }
    }

    @Override
    public ArrayList<InletInstance> GetInletInstances() {
        return inletInstances;
    }

    @Override
    public ArrayList<OutletInstance> GetOutletInstances() {
        return outletInstances;
    }

    @Override
    public String GenerateInstanceDataDeclaration2() {
        String c = "";
        if (getType().sLocalData != null) {
            String s = getType().sLocalData;
            s = s.replace("%name%", getCInstanceName());
            s = s.replace("%parent%", getCInstanceName());
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
        if (getType().sLocalData.length() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasInit() {
        if (getType().sInitCode == null) {
            return false;
        }
        if (getType().sInitCode.length() == 0) {
            return false;
        }
        return true;
    }

    public String GenerateInstanceCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
        for (ParameterInstance p : parameterInstances) {
            c += p.GenerateCodeDeclaration(classname);
        }
        c += GenerateInstanceDataDeclaration2();
        for (AttributeInstance p : attributeInstances) {
            if (p.CValue() != null) {
                c = c.replace("%" + p.getAttributeName() + "%", p.CValue());
            }
        }
        for (ParameterInstance p : parameterInstances) {
            c = c.replace("%" + p.name + "%", p.variableName("", enableOnParent));
        }
        for (DisplayInstance p : displayInstances) {
            c = c.replace("%" + p.name + "%", p.valueName(""));
        }
        c = c.replace("%name%", getCInstanceName());
        c = c.replace("%class%", classname);
        return c;
    }

    @Override
    public String GenerateInitCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
//        if (hasStruct())
//            c = "  void " + GenerateInitFunctionName() + "(" + GenerateStructName() + " * x ) {\n";
//        else
//        if (!classname.equals("one"))
        c += "parent2 = parent;\n";
        for (ParameterInstance p : parameterInstances) {
            if (!((p.isOnParent() && enableOnParent))) {
                c += p.GenerateCodeInit("parent2->", "");
            }
        }
        for (DisplayInstance p : displayInstances) {
            c += p.GenerateCodeInit("");
        }
        if (getType().sInitCode != null) {
            String s = getType().sInitCode;
            for (AttributeInstance p : attributeInstances) {
                s = s.replace("%" + p.getAttributeName() + "%", p.CValue());
            }
            for (ParameterInstance p : parameterInstances) {
                s = s.replace("%" + p.name + "%", p.variableName("", enableOnParent));
            }
            for (DisplayInstance p : displayInstances) {
                s = s.replace("%" + p.name + "%", p.valueName(""));
            }
            c += s + "\n";
        }
        c = c.replace("%class%", classname);
        c = c.replace("%name%", getCInstanceName());
        c = "  public: void Init(" + classname + " * parent) {\n" + c + "}\n";
        return c;
    }

    @Override
    public String GenerateDisposeCodePlusPlus(String classname) {
        String c = "";
        if (getType().sDisposeCode != null) {
            String s = getType().sDisposeCode;
            for (AttributeInstance p : attributeInstances) {
                s = s.replace("%" + p.getAttributeName() + "%", p.CValue());
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
                s = s.replace("%" + p.getAttributeName() + "%", p.CValue());
            }
            s = s.replace("%name%", getCInstanceName());
            for (InletInstance i : inletInstances) {
                Net n = patch.GetNet(i);
                s = s.replace("%" + i.GetLabel() + "%", i.GetCName());
            }
            for (OutletInstance i : outletInstances) {
                s = s.replace("%" + i.GetLabel() + "%", i.GetCName());
            }
            for (ParameterInstance p : parameterInstances) {
                if (p.isOnParent() && enableOnParent) {
                    s = s.replace("%" + p.name + "%", OnParentAccess + p.variableName(vprefix, enableOnParent));
                } else {
                    s = s.replace("%" + p.name + "%", p.variableName(vprefix, enableOnParent));
                }
            }
            for (DisplayInstance p : displayInstances) {
                s = s.replace("%" + p.name + "%", p.valueName(vprefix));
            }
            return s + "\n";
        }
        return "";
    }

    public String GenerateSRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        if (getType().sSRateCode != null) {
            String s = "int buffer_index;\n"
                    + "for(buffer_index=0;buffer_index<BUFSIZE;buffer_index++) {\n" + getType().sSRateCode;
            for (AttributeInstance p : attributeInstances) {
                if (s.contains("%" + p.getAttributeName() + "%")) {
                    s = s.replace("%" + p.getAttributeName() + "%", p.CValue());
                }
            }
            s = s.replace("%name%", getCInstanceName());
            for (InletInstance i : inletInstances) {
                if (i.GetDataType() instanceof DataTypeBuffer) {
                    s = s.replace("%" + i.GetLabel() + "%", i.GetCName() + ((DataTypeBuffer) i.GetDataType()).GetIndex("buffer_index"));
                } else {
                    s = s.replace("%" + i.GetLabel() + "%", i.GetCName());
                }
            }
            for (OutletInstance i : outletInstances) {
                if (i.GetDataType() instanceof DataTypeBuffer) {
                    s = s.replace("%" + i.GetLabel() + "%", i.GetCName() + ((DataTypeBuffer) i.GetDataType()).GetIndex("buffer_index"));
                } else {
                    s = s.replace("%" + i.GetLabel() + "%", i.GetCName());
                }
            }
            for (ParameterInstance p : parameterInstances) {
                if (p.isOnParent() && enableOnParent) {
                    s = s.replace("%" + p.name + "%", OnParentAccess + p.variableName(vprefix, enableOnParent));
                } else {
                    s = s.replace("%" + p.name + "%", p.variableName(vprefix, enableOnParent));
                }
            }
            for (DisplayInstance p : displayInstances) {
                s = s.replace("%" + p.name + "%", p.valueName(vprefix));
            }
//            for(Parameter p:type.params) {
//                s=s.replace("%" + p.name + "%", "x_" + InstanceName + "_" + p.name);
//            }
            s += "\n}\n";
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
                s += ",\n    ";
            }
            s += "const " + i.GetDataType().CType() + " " + i.GetCName();
            comma = true;
        }
        for (OutletInstance i : outletInstances) {
            if (comma) {
                s += ",\n    ";
            }
            s += i.GetDataType().CType() + " & " + i.GetCName();
            comma = true;
        }
        s += "  ){\n";
        s += GenerateKRateCodePlusPlus("", enableOnParent, OnParentAccess);
        s += GenerateSRateCodePlusPlus("", enableOnParent, OnParentAccess);
        s += "}\n";
        return s;
    }

    @Override
    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        String s = "";
        s += "class " + getCInstanceName() + "{\n";
        s += "  public: // v1\n";
        s += "  " + ClassName + " *parent2;\n";
        s += GenerateInstanceCodePlusPlus(ClassName, enableOnParent);
        s += GenerateInitCodePlusPlus(ClassName, enableOnParent);
        s += GenerateDisposeCodePlusPlus(ClassName);
        s += GenerateDoFunctionPlusPlus(ClassName, OnParentAccess, enableOnParent);
        {
            String d3 = GenerateCodeMidiHandler("");
            if (!d3.isEmpty()) {
                s += "void MidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2){\n";
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
            if (s.contains("%" + p.getAttributeName() + "%")) {
                s = s.replace("%" + p.getAttributeName() + "%", p.CValue());
            }
        }
        s = s.replace("%name%", vprefix + getCInstanceName());
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
        AxoObject type = getType();
        if (type == null) {
            return false;
        } else {
            return type.providesModulationSource();
        }
    }

    @Override
    public AxoObject getType() {
        return (AxoObject) super.getType();
    }

    @Override
    public void PromoteToOverloadedObj() {
        if (getType() instanceof AxoObjectFromPatch) {
            return;
        }
        String id = typeName;
        ArrayList<AxoObjectAbstract> candidates = MainFrame.mainframe.axoObjects.GetAxoObjectFromName(id, patch.GetCurrentWorkingDirectory());
        if (candidates == null) {
            return;
        }
        if (candidates.isEmpty()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "could not resolve any candidates" + id);
        }
        if (candidates.size() == 1) {
            return;
        }

        int ranking[];
        ranking = new int[candidates.size()];
        // auto-choose depending on 1st connected inlet

        //      InletInstance i = null;// = GetInletInstances().get(0);
        for (InletInstance j : GetInletInstances()) {
            Net n = patch.GetNet(j);
            if (n == null) {
                continue;
            }
            DataType d = n.GetDataType();
            if (d == null) {
                continue;
            }
            String name = j.getInlet().name;
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
            return;
        }
        if (selected != getType()) {
            //Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO,"promoting " + this + " to " + selected);            
            patch.ChangeObjectInstanceType(this, selected);
        } else {
            //Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO,"no promotion");            
        }
    }

    @Override
    public ArrayList<DisplayInstance> GetDisplayInstances() {
        return displayInstances;
    }
}
