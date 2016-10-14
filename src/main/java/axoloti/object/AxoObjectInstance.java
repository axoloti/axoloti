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
import axoloti.PatchFrame;
import axoloti.PatchGUI;
import axoloti.SDFileReference;
import axoloti.Synonyms;
import axoloti.Theme;
import axoloti.attribute.*;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.datatypes.DataType;
import axoloti.datatypes.Frac32buffer;
import axoloti.displays.Display;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletInstance;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.*;
import components.LabelComponent;
import components.PopupIcon;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj")
public class AxoObjectInstance extends AxoObjectInstanceAbstract implements ObjectModifiedListener {

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
        @ElementList(entry = "bin12", type = ParameterInstanceBin12.class, inline = true, required = false),
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
        @ElementList(entry = "file", type = AttributeInstanceSDFile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AttributeInstanceTextEditor.class, inline = true, required = false)})
    public ArrayList<AttributeInstance> attributeInstances;
    public ArrayList<DisplayInstance> displayInstances;
    LabelComponent IndexLabel;

    boolean deferredObjTypeUpdate = false;

    @Override
    public void refreshIndex() {
        if (patch != null && IndexLabel != null) {
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

    public final JPanel p_params = new JPanel();
    public final JPanel p_displays = new JPanel();
    public final JPanel p_iolets = new JPanel();
    public final JPanel p_inlets = new JPanel();
    public final JPanel p_outlets = new JPanel();

    void updateObj1() {
        getType().addObjectModifiedListener(this);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        updateObj1();
        ArrayList<ParameterInstance> pParameterInstances = parameterInstances;
        ArrayList<AttributeInstance> pAttributeInstances = attributeInstances;
        ArrayList<InletInstance> pInletInstances = inletInstances;
        ArrayList<OutletInstance> pOutletInstances = outletInstances;
        parameterInstances = new ArrayList<ParameterInstance>();
        attributeInstances = new ArrayList<AttributeInstance>();
        displayInstances = new ArrayList<DisplayInstance>();
        inletInstances = new ArrayList<InletInstance>();
        outletInstances = new ArrayList<OutletInstance>();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        final PopupIcon popupIcon = new PopupIcon();
        popupIcon.setPopupIconListener(new PopupIcon.PopupIconListener() {
            @Override
            public void ShowPopup() {
                JPopupMenu popup = CreatePopupMenu();
                popupIcon.add(popup);
                popup.show(popupIcon,
                        0, popupIcon.getHeight());
            }
        });
        popupIcon.setAlignmentX(LEFT_ALIGNMENT);
        Titlebar.add(popupIcon);
        LabelComponent idlbl = new LabelComponent(typeName);
        idlbl.setForeground(Theme.getCurrentTheme().Object_TitleBar_Foreground);
        idlbl.setAlignmentX(LEFT_ALIGNMENT);
        Titlebar.add(idlbl);

        String tooltiptxt = "<html>";
        if ((getType().sDescription != null) && (!getType().sDescription.isEmpty())) {
            tooltiptxt += getType().sDescription;
        }
        if ((getType().sAuthor != null) && (!getType().sAuthor.isEmpty())) {
            tooltiptxt += "<p>Author: " + getType().sAuthor;
        }
        if ((getType().sLicense != null) && (!getType().sLicense.isEmpty())) {
            tooltiptxt += "<p>License: " + getType().sLicense;
        }
        if ((getType().sPath != null) && (!getType().sPath.isEmpty())) {
            tooltiptxt += "<p>Path: " + getType().sPath;
        }
        Titlebar.setToolTipText(tooltiptxt);


        /*
         h.add(Box.createHorizontalStrut(3));
         h.add(Box.createHorizontalGlue());
         h.add(new JSeparator(SwingConstants.VERTICAL));*/
        ////IndexLabel not shown, maybe useful later...
        //IndexLabel.setSize(IndexLabel.getMinimumSize());
        //IndexLabel = new LabelComponent("");
        //refreshIndex();
        //h.add(IndexLabel);
        //IndexLabel.setAlignmentX(RIGHT_ALIGNMENT);
        Titlebar.setAlignmentX(LEFT_ALIGNMENT);
        add(Titlebar);
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

        p_iolets.removeAll();
        p_inlets.removeAll();
        p_outlets.removeAll();
        p_params.removeAll();

        if (getType().getRotatedParams()) {
            p_params.setLayout(new BoxLayout(p_params, BoxLayout.LINE_AXIS));
        } else {
            p_params.setLayout(new BoxLayout(p_params, BoxLayout.PAGE_AXIS));
        }

        p_displays.removeAll();

        if (getType().getRotatedParams()) {
            p_displays.setLayout(new BoxLayout(p_displays, BoxLayout.LINE_AXIS));
        } else {
            p_displays.setLayout(new BoxLayout(p_displays, BoxLayout.PAGE_AXIS));
        }
        p_displays.add(Box.createHorizontalGlue());
        p_params.add(Box.createHorizontalGlue());

        for (Inlet inl : getType().inlets) {
            InletInstance inlinp = null;
            for (InletInstance inlin1 : pInletInstances) {
                if (inlin1.GetLabel().equals(inl.getName())) {
                    inlinp = inlin1;
                }
            }
            InletInstance inlin = new InletInstance(inl, this);
            if (inlinp != null) {
                Net n = getPatch().GetNet(inlinp);
                if (n != null) {
                    n.connectInlet(inlin);
                }
            }
            inletInstances.add(inlin);
            inlin.setAlignmentX(LEFT_ALIGNMENT);
            p_inlets.add(inlin);
        }
        // disconnect stale inlets from nets
        for (InletInstance inlin1 : pInletInstances) {
            getPatch().disconnect(inlin1);
        }

        for (Outlet o : getType().outlets) {
            OutletInstance oinp = null;
            for (OutletInstance oinp1 : pOutletInstances) {
                if (oinp1.GetLabel().equals(o.getName())) {
                    oinp = oinp1;
                }
            }
            OutletInstance oin = new OutletInstance(o, this);
            if (oinp != null) {
                Net n = getPatch().GetNet(oinp);
                if (n != null) {
                    n.connectOutlet(oin);
                }
            }
            outletInstances.add(oin);
            oin.setAlignmentX(RIGHT_ALIGNMENT);
            p_outlets.add(oin);
        }
        // disconnect stale outlets from nets
        for (OutletInstance oinp1 : pOutletInstances) {
            getPatch().disconnect(oinp1);
        }

        /*
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

        for (AxoAttribute p : getType().attributes) {
            AttributeInstance attrp1 = null;
            for (AttributeInstance attrp : pAttributeInstances) {
                if (attrp.getName().equals(p.getName())) {
                    attrp1 = attrp;
                }
            }
            AttributeInstance attri = p.CreateInstance(this, attrp1);
            attri.setAlignmentX(LEFT_ALIGNMENT);
            add(attri);
            attributeInstances.add(attri);
        }

        for (Parameter p : getType().params) {
            ParameterInstance pin = p.CreateInstance(this);
            for (ParameterInstance pinp : pParameterInstances) {
                if (pinp.getName().equals(pin.getName())) {
                    pin.CopyValueFrom(pinp);
                }
            }
            pin.PostConstructor();
            pin.setAlignmentX(RIGHT_ALIGNMENT);
            parameterInstances.add(pin);
        }

        for (Display p : getType().displays) {
            DisplayInstance pin = p.CreateInstance(this);
            pin.setAlignmentX(RIGHT_ALIGNMENT);
            displayInstances.add(pin);
        }
//        p_displays.add(Box.createHorizontalGlue());
//        p_params.add(Box.createHorizontalGlue());
        add(p_params);
        add(p_displays);

        getType().addObjectModifiedListener(this);

        synchronized (getTreeLock()) {
            validateTree();
        }
        setLocation(x, y);
        resizeToGrid();
    }

    @Override
    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = super.CreatePopupMenu();
        JMenuItem popm_edit = new JMenuItem("edit object definition");
        popm_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                OpenEditor();
            }
        });
        popup.add(popm_edit);
        JMenuItem popm_editInstanceName = new JMenuItem("edit instance name");
        popm_editInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addInstanceNameEditor();
            }
        });
        popup.add(popm_editInstanceName);
        JMenuItem popm_substitute = new JMenuItem("replace");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((PatchGUI) patch).ShowClassSelector(AxoObjectInstance.this.getLocation(), AxoObjectInstance.this, null);
            }
        });
        popup.add(popm_substitute);
        if (getType().GetHelpPatchFile() != null) {
            JMenuItem popm_help = new JMenuItem("help");
            popm_help.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    PatchGUI.OpenPatch(getType().GetHelpPatchFile());
                }
            });
            popup.add(popm_help);
        }
        if (MainFrame.prefs.getExpertMode()) {
            JMenuItem popm_adapt = new JMenuItem("adapt homonym");
            popm_adapt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    PromoteToOverloadedObj();
                }
            });
            popup.add(popm_adapt);
        }

        if (type instanceof AxoObjectFromPatch) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/patcher");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ConvertToPatchPatcher();
                }
            });
            popup.add(popm_embed);
        } else if (!(this instanceof AxoObjectInstancePatcherObject)) {
            JMenuItem popm_embed = new JMenuItem("embed as patch/object");
            popm_embed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ConvertToEmbeddedObj();
                }
            });
            popup.add(popm_embed);
        }
        return popup;
    }

    final void init1() {
        inletInstances = new ArrayList<InletInstance>();
        outletInstances = new ArrayList<OutletInstance>();
        displayInstances = new ArrayList<DisplayInstance>();
        parameterInstances = new ArrayList<ParameterInstance>();
        attributeInstances = new ArrayList<AttributeInstance>();

        p_iolets.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_iolets.setLayout(new BoxLayout(p_iolets, BoxLayout.LINE_AXIS));
        p_iolets.setAlignmentX(LEFT_ALIGNMENT);
        p_iolets.setAlignmentY(TOP_ALIGNMENT);

        p_inlets.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_inlets.setLayout(new BoxLayout(p_inlets, BoxLayout.PAGE_AXIS));
        p_inlets.setAlignmentX(LEFT_ALIGNMENT);
        p_inlets.setAlignmentY(TOP_ALIGNMENT);

        p_outlets.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_outlets.setLayout(new BoxLayout(p_outlets, BoxLayout.PAGE_AXIS));
        p_outlets.setAlignmentX(RIGHT_ALIGNMENT);
        p_outlets.setAlignmentY(TOP_ALIGNMENT);

        p_params.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_params.setAlignmentX(LEFT_ALIGNMENT);

        p_displays.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        p_displays.setAlignmentX(LEFT_ALIGNMENT);
    }

    public AxoObjectInstance() {
        super();
        init1();
    }

    public AxoObjectInstance(AxoObject type, Patch patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
        init1();
    }

    public void OpenEditor() {
        getType().OpenEditor(editorBounds, editorActiveTabIndex);
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
    public void Lock() {
        super.Lock();
        for (AttributeInstance a : attributeInstances) {
            a.Lock();
        }
    }

    public void updateObj() {
        getPatch().ChangeObjectInstanceType(this, this.getType());
        getPatch().cleanUpIntermediateChangeStates(3);
    }

    @Override
    public void Unlock() {
        super.Unlock();
        for (AttributeInstance a : attributeInstances) {
            a.UnLock();
        }
        if (deferredObjTypeUpdate) {
            updateObj();
            deferredObjTypeUpdate = false;
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
                if (i.GetDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.GetCName(), i.GetCName() + "[buffer_index]");
                }
            }
            for (OutletInstance i : outletInstances) {
                if (i.GetDataType() instanceof Frac32buffer) {
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
            s += "const " + i.GetDataType().CType() + " " + i.GetCName();
            comma = true;
        }
        for (OutletInstance i : outletInstances) {
            if (comma) {
                s += ",\n";
            }
            s += i.GetDataType().CType() + " & " + i.GetCName();
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
    public void PromoteToOverloadedObj() {
        if (getType() instanceof AxoObjectFromPatch) {
            return;
        }
        if (getType() instanceof AxoObjectPatcher) {
            return;
        }
        if (getType() instanceof AxoObjectPatcherObject) {
            return;
        }
        String id = typeName;
        ArrayList<AxoObjectAbstract> candidates = MainFrame.axoObjects.GetAxoObjectFromName(id, patch.GetCurrentWorkingDirectory());
        if (candidates == null) {
            return;
        }
        if (candidates.isEmpty()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "could not resolve any candidates {0}", id);
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
            return;
        }
        if (selected != getType()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.FINE, "promoting " + this + " to " + selected);
            patch.ChangeObjectInstanceType(this, selected);
            patch.cleanUpIntermediateChangeStates(4);
        } else {
//            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO, "no promotion for {0}", typeName);
        }
    }

    @Override
    public ArrayList<DisplayInstance> GetDisplayInstances() {
        return displayInstances;
    }

    Rectangle editorBounds;
    Integer editorActiveTabIndex;

    @Override
    public void ObjectModified(Object src) {
        if (getPatch() != null) {
            if (!getPatch().IsLocked()) {
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
    public ArrayList<SDFileReference> GetDependendSDFiles() {
        ArrayList<SDFileReference> files = getType().filedepends;
        if (files == null){
            files = new ArrayList<SDFileReference>();
        } else {
            String p1 = getType().sPath;
            if (p1==null) {
                // embedded object, reference path is of the patch
                p1 = getPatch().getFileNamePath();
                if (p1 == null) {
                    p1 = "";
                }
            }
            File f1 = new File(p1);
            java.nio.file.Path p = f1.toPath().getParent();
            for (SDFileReference f: files){                
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

    void ConvertToPatchPatcher() {
        if (IsLocked()) {
            return;
        }
        ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/patcher", null);
        assert (!ol.isEmpty());
        AxoObjectAbstract o = ol.get(0);
        String iname = getInstanceName();
        AxoObjectInstancePatcher oi = (AxoObjectInstancePatcher) getPatch().ChangeObjectInstanceType1(this, o);
        AxoObjectFromPatch ao = (AxoObjectFromPatch) getType();
        PatchFrame pf = PatchGUI.OpenPatch(ao.f);
        oi.pf = pf;
        oi.pg = pf.getPatch();
        oi.setInstanceName(iname);
        oi.updateObj();
        getPatch().delete(this);
        getPatch().SetDirty();
    }

    void ConvertToEmbeddedObj() {
        if (IsLocked()) {
            return;
        }
        try {
            ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/object", null);
            assert (!ol.isEmpty());
            AxoObjectAbstract o = ol.get(0);
            String iname = getInstanceName();
            AxoObjectInstancePatcherObject oi = (AxoObjectInstancePatcherObject) getPatch().ChangeObjectInstanceType1(this, o);
            AxoObject ao = getType();
            oi.ao = new AxoObjectPatcherObject(ao.id, ao.sDescription);
            oi.ao.copy(ao);
            oi.ao.sPath = "";
            oi.ao.upgradeSha = null;
            oi.ao.CloseEditor();
            oi.setInstanceName(iname);
            oi.updateObj();
            getPatch().delete(this);
            getPatch().SetDirty();
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
    public void Close() {
        super.Close();
        for (AttributeInstance a : attributeInstances) {
            a.Close();
        }
    }
}
