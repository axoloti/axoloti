package axoloti.codegen.patch.object;

import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.display.DisplayInstanceViewFactory;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceViewFactory;
import axoloti.datatypes.Frac32buffer;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
class AxoObjectInstanceCodegenView implements IAxoObjectInstanceCodegenView {

    final ObjectInstanceController controller;
    List<ParameterInstanceView> parameterInstances;
    List<DisplayInstanceView> displayInstances;

    AxoObjectInstanceCodegenView(ObjectInstanceController controller) {
        this.controller = controller;
        IAxoObjectInstance model = controller.getModel();

        parameterInstances = new LinkedList<>();
        for (ParameterInstance p : model.getParameterInstances()) {
            ParameterInstanceView pv = ParameterInstanceViewFactory.createView(p.getControllerFromModel());
            parameterInstances.add(pv);
        }

        displayInstances = new LinkedList<>();
        for (DisplayInstance d : model.getDisplayInstances()) {
            DisplayInstanceView dv = DisplayInstanceViewFactory.createView(d.getControllerFromModel());
            displayInstances.add(dv);
        }

    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public ObjectInstanceController getController() {
        return controller;
    }

    @Override
    public AxoObjectInstance getModel() {
        return (AxoObjectInstance)controller.getModel();
    }

    @Override
    public List<ParameterInstanceView> getParameterInstanceViews() {
        return parameterInstances;
    }

    @Override
    public List<DisplayInstanceView> getDisplayInstanceViews() {
        return displayInstances;
    }

    @Override
    public String GenerateUICode(int count[]) {
        /*
        // generate static initializer for ui_object_t

typedef struct ui_object {
	char name[MAX_PARAMETER_NAME_LENGTH];
	uint32_t nparams;
	Parameter_t **params;
	uint32_t ndisplays;
	Display_t **displays;
	uint32_t nobjects;
	struct ui_object *objects;
} ui_object_t;
         */
        int nparams = getModel().getParameterInstances().size();
        int ndisplays = displayInstances.size();
        if (nparams + ndisplays == 0) {
            return "";
        }
        count[0]++;
        StringBuilder s = new StringBuilder(
            "{ name : " + CodeGeneration.CPPCharArrayStaticInitializer(getModel().getInstanceName(), CodeGeneration.param_name_length)
            + ", nparams : " + nparams);
        if (nparams > 0) {
            s.append(", params : &params[" + parameterInstances.get(0).getIndex() + "]");
            s.append(", param_names : &param_names[" + parameterInstances.get(0).getIndex() + "]");
        } else {
            s.append(", params : 0");
            s.append(", param_names : 0");
        }

        if (ndisplays > 0) {
            s.append(", ndisplays : " + ndisplays
                     + ", displays : &display_metas[" + displayInstances.get(0).getIndex() + "]");
        } else {
            s.append(", ndisplays : 0"
                     + ", displays : 0");
        }
        s.append(", nobjects : 0" // TBC
                 + ", objects : 0");
        s.append("},\n");
        return s.toString();
    }

    @Override
    public String GenerateInitCodePlusPlus(String classname, boolean enableOnParent) {
        StringBuilder c = new StringBuilder();
//        if (hasStruct())
//            c = new StringBuilder("  void " + GenerateInitFunctionName() + "(" + GenerateStructName() + " * x ) {\n");
//        else
//        if (!classname.equals("one"))
//        c.append("parent = _parent;\n");
        for (ParameterInstanceView p : parameterInstances) {
            if (p.getModel().getModel().PropagateToChild != null) {
                c.append("// on Parent: propagate " + p.getModel().getName() + " " + enableOnParent + " " + getModel().getLegalName() + "" + p.getModel().getModel().PropagateToChild + "\n");
                c.append(p.PExName("parent->") + ".pfunction = PropagateToSub;\n");
                c.append(p.PExName("parent->") + ".d.frac.finalvalue = (int32_t)(&(parent->instance"
                         + getModel().getLegalName() + "_i.params[instance" + getModel().getLegalName() + "::PARAM_INDEX_"
                         + p.getModel().getModel().PropagateToChild + "]));\n");
            }
            c.append(p.getModel().GenerateCodeInitModulator("parent->", ""));
            //           if ((p.getOnParent() && !enableOnParent)) {
            //c += "// on Parent: propagate " + p.name + "\n";
            //String parentparametername = classname.substring(8);
            //c += "// classname : " + classname + " : " + parentparametername + "\n";
            //c += "parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].pfunction = PropagateToSub;\n";
            //c += "parent->parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].finalvalue = (int32_t)(&(" + p.PExName("parent->") + "));\n";
            //         }
        }
        for (DisplayInstanceView p : displayInstances) {
            c.append(p.GenerateCodeInit(""));
        }
        if (getModel().getType().getInitCode() != null) {
            String s = getModel().getType().getInitCode();
            for (AttributeInstance p : getModel().getAttributeInstances()) {
                s = s.replace(p.GetCName(), p.CValue());
            }
            c.append(s + "\n");
        }
        StringBuilder d = new StringBuilder("  public: void Init(" + classname + " * parent");
        if (!displayInstances.isEmpty()) {
            for (DisplayInstanceView p : displayInstances) {
                if (p.getModel().getModel().getLength() > 0) {
                    d.append(",\n");
                    if (p.getModel().getModel().getDatatype().isPointer()) {
                        d.append(p.getModel().getModel().getDatatype().CType() + " " + p.GetCName());
                    } else {
                        d.append(p.getModel().getModel().getDatatype().CType() + " & " + p.GetCName());
                    }
                }
            }
        }
        d.append(") {\n" + c.toString() + "}\n");
        return d.toString();
    }

    @Override
    public String GenerateDisposeCodePlusPlus(String classname) {
        String c = "";
        if (getModel().getType().getDisposeCode() != null) {
            String s = getModel().getType().getDisposeCode();
            for (AttributeInstance p : getModel().getAttributeInstances()) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            c += s + "\n";
        }
        c = "  public: void Dispose() {\n" + c + "}\n";
        return c;
    }

    public String GenerateKRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        String s = getModel().getType().getKRateCode();
        if (s != null) {
            for (AttributeInstance p : getModel().getAttributeInstances()) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            s = s.replace("attr_name", getModel().getCInstanceName());
            s = s.replace("attr_legal_name", getModel().getLegalName());
            for (ParameterInstance p : getModel().getParameterInstances()) {
                Boolean op = p.getOnParent();
                if (op!=null && op == true) {
//                    s = s.replace("%" + p.name + "%", OnParentAccess + p.variableName(vprefix, enableOnParent));
                } else {
//                    s = s.replace("%" + p.name + "%", p.variableName(vprefix, enableOnParent));
                }
            }
            for (DisplayInstance p : getModel().getDisplayInstances()) {
//                s = s.replace("%" + p.name + "%", p.valueName(vprefix));
            }
            return s + "\n";
        }
        return "";
    }

    public String GenerateSRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        if (getModel().getType().getSRateCode() != null) {
            String s = "int buffer_index;\n"
                    + "for(buffer_index=0;buffer_index<BUFSIZE;buffer_index++) {\n"
                    + getModel().getType().getSRateCode()
                    + "\n}\n";

            for (AttributeInstance p : getModel().getAttributeInstances()) {
                s = s.replaceAll(p.GetCName(), p.CValue());
            }
            for (InletInstance i : getModel().getInletInstances()) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.getModel().GetCName(), i.getModel().GetCName() + "[buffer_index]");
                }
            }
            for (OutletInstance i : getModel().getOutletInstances()) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.getModel().GetCName(), i.getModel().GetCName() + "[buffer_index]");
                }
            }

            s = s.replace("attr_name", getModel().getCInstanceName());
            s = s.replace("attr_legal_name", getModel().getLegalName());

            return s;
        }
        return "";
    }

    public String GenerateDoFunctionPlusPlus(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        StringBuilder s = new StringBuilder("  public: void dsp (" + ClassName + " * parent");
        boolean comma = true;
        for (InletInstance i : getModel().getInletInstances()) {
            if (comma) {
                s.append(",\n");
            }
            s.append("const " + i.getDataType().CType() + " " + i.getModel().GetCName());
            comma = true;
        }
        for (OutletInstance i : getModel().getOutletInstances()) {
            if (comma) {
                s.append(",\n");
            }
            s.append(i.getDataType().CType() + " & " + i.getModel().GetCName());
            comma = true;
        }
        for (ParameterInstance i : getModel().getParameterInstances()) {
            if (i.getModel().PropagateToChild == null) {
                if (comma) {
                    s.append(",\n");
                }
                s.append(i.getModel().CType() + " " + i.GetCName());
                comma = true;
            }
        }
        for (DisplayInstanceView i : displayInstances) {
            if (i.getModel().getModel().getLength() > 0) {
                if (comma) {
                    s.append(",\n");
                }
                if (i.getModel().getModel().getDatatype().isPointer()) {
                    s.append(i.getModel().getModel().getDatatype().CType() + " " + i.GetCName());
                } else {
                    s.append(i.getModel().getModel().getDatatype().CType() + " & " + i.GetCName());
                }
                comma = true;
            }
        }
        s.append("  ){\n");
        s.append(GenerateKRateCodePlusPlus("", enableOnParent, OnParentAccess));
        s.append(GenerateSRateCodePlusPlus("", enableOnParent, OnParentAccess));
        s.append("}\n");
        return s.toString();
    }

    public final static String MidiHandlerFunctionHeader = "void MidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n";

    public String GenerateInstanceDataDeclaration2() {
        String c = "";
        if (getModel().getType().getLocalData() != null) {
            c = getModel().getType().getLocalData()
                .replaceAll("attr_parent", getModel().getCInstanceName()) + "\n";
        }
        return c;
    }

    public String GenerateInstanceCodePlusPlus(String classname, boolean enableOnParent) {
        String c = GenerateInstanceDataDeclaration2();
        for (AttributeInstance p : getModel().getAttributeInstances()) {
            if (p.CValue() != null) {
                c = c.replaceAll(p.GetCName(), p.CValue());
            }
        }
        return c;
    }

    @Override
    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        StringBuilder s = new StringBuilder();
        s.append("class " + getModel().getCInstanceName() + "{\n");
        s.append("  public: // v1\n");
        s.append(GenerateInstanceCodePlusPlus(ClassName, enableOnParent));
        s.append(GenerateInitCodePlusPlus(ClassName, enableOnParent));
        s.append(GenerateDisposeCodePlusPlus(ClassName));
        s.append(GenerateDoFunctionPlusPlus(ClassName, OnParentAccess, enableOnParent));
        {
            String d3 = GenerateCodeMidiHandler("");
            if (!d3.isEmpty()) {
                s.append("void MidiInHandler(" + ClassName + "*parent, midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n");
                s.append(d3);
                s.append("}\n");
            }
        }
        s.append("}\n;");
        return s.toString();
    }

    public String GenerateCodeMidiHandler(String vprefix) {
        String s = "";
        if (getModel().getType().getMidiCode() != null) {
            s += getModel().getType().getMidiCode();
        }
        for (ParameterInstanceView i : parameterInstances) {
            s += i.GenerateCodeMidiHandler("");
        }
        for (AttributeInstance p : getModel().getAttributeInstances()) {
            s = s.replaceAll(p.GetCName(), p.CValue());
        }
        s = s.replace("attr_name", getModel().getCInstanceName());
        s = s.replace("attr_legal_name", getModel().getLegalName());

        if (s.length() > 0) {
            return "{\n" + s + "}\n";
        } else {
            return "";
        }
    }

    @Override
    public String GenerateCallMidiHandler() {
        if ((getModel().getType().getMidiCode() != null) && (!getModel().getType().getMidiCode().isEmpty())) {
            return getModel().getCInstanceName() + "_i.MidiInHandler(this, dev, port, status, data1, data2);\n";
        }
        for (ParameterInstanceView pi : parameterInstances) {
            if (!pi.GenerateCodeMidiHandler("").isEmpty()) {
                return getModel().getCInstanceName() + "_i.MidiInHandler(this, dev, port, status, data1, data2);\n";
            }
        }
        return "";
    }

    @Override
    public void dispose() {
    }

}
