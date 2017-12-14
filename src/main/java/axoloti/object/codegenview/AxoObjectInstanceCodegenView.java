package axoloti.object.codegenview;

import axoloti.attribute.AttributeInstance;
import axoloti.datatypes.Frac32buffer;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectInstance;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceCodegenView implements IAxoObjectInstanceCodegenView {

    final ObjectInstanceController controller;
    
    public AxoObjectInstanceCodegenView(AxoObjectInstance model, ObjectInstanceController controller) {
        this.controller = controller;
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
        int ndisplays = getModel().getDisplayInstances().size();
        if (nparams + ndisplays == 0) {
            return "";
        }
        count[0]++;
        String s = "{ name : " + CodeGeneration.CPPCharArrayStaticInitializer(getModel().getInstanceName(), CodeGeneration.param_name_length)
                + ", nparams : " + nparams;
        if (nparams > 0) {
            s += ", params : &params[" + getModel().getParameterInstances().get(0).getIndex() + "]";
            s += ", param_names : &param_names[" + getModel().getParameterInstances().get(0).getIndex() + "]";
        } else {
            s += ", params : 0";
            s += ", param_names : 0";
        }

        if (ndisplays > 0) {
            s += ", ndisplays : " + ndisplays
                    + ", displays : &display_metas[" + getModel().getDisplayInstances().get(0).getIndex() + "]";
        } else {
            s += ", ndisplays : 0"
                    + ", displays : 0";
        }
        s += ", nobjects : 0" // TBC
                + ", objects : 0";
        s += "},\n";
        return s;
    }

    @Override
    public String GenerateInitCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
//        if (hasStruct())
//            c = "  void " + GenerateInitFunctionName() + "(" + GenerateStructName() + " * x ) {\n";
//        else
//        if (!classname.equals("one"))
//        c += "parent = _parent;\n";
        for (ParameterInstance p : getModel().getParameterInstances()) {
            if (p.parameter.PropagateToChild != null) {
                c += "// on Parent: propagate " + p.getName() + " " + enableOnParent + " " + getModel().getLegalName() + "" + p.parameter.PropagateToChild + "\n";
                c += p.PExName("parent->") + ".pfunction = PropagateToSub;\n";
                c += p.PExName("parent->") + ".d.frac.finalvalue = (int32_t)(&(parent->instance"
                        + getModel().getLegalName() + "_i.params[instance" + getModel().getLegalName() + "::PARAM_INDEX_"
                        + p.parameter.PropagateToChild + "]));\n";
            }
            c += p.GenerateCodeInitModulator("parent->", "");
            //           if ((p.getOnParent() && !enableOnParent)) {
            //c += "// on Parent: propagate " + p.name + "\n";
            //String parentparametername = classname.substring(8);
            //c += "// classname : " + classname + " : " + parentparametername + "\n";
            //c += "parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].pfunction = PropagateToSub;\n";
            //c += "parent->parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].finalvalue = (int32_t)(&(" + p.PExName("parent->") + "));\n";
            //         }
        }
        for (DisplayInstance p : getModel().getDisplayInstances()) {
            c += p.GenerateCodeInit("");
        }
        if (getModel().getType().getInitCode() != null) {
            String s = getModel().getType().getInitCode();
            for (AttributeInstance p : getModel().getAttributeInstances()) {
                s = s.replace(p.GetCName(), p.CValue());
            }
            c += s + "\n";
        }
        String d = "  public: void Init(" + classname + " * parent";
        if (!getModel().getDisplayInstances().isEmpty()) {
            for (DisplayInstance p : getModel().getDisplayInstances()) {
                if (p.getModel().getLength() > 0) {
                    d += ",\n";
                    if (p.getModel().getDatatype().isPointer()) {
                        d += p.getModel().getDatatype().CType() + " " + p.GetCName();
                    } else {
                        d += p.getModel().getDatatype().CType() + " & " + p.GetCName();
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
                    s = s.replaceAll(i.GetCName(), i.GetCName() + "[buffer_index]");
                }
            }
            for (OutletInstance i : getModel().getOutletInstances()) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.GetCName(), i.GetCName() + "[buffer_index]");
                }
            }

            s = s.replace("attr_name", getModel().getCInstanceName());
            s = s.replace("attr_legal_name", getModel().getLegalName());

            return s;
        }
        return "";
    }

    public String GenerateDoFunctionPlusPlus(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        String s;
        boolean comma = true;
        s = "  public: void dsp (" + ClassName + " * parent";
        for (InletInstance i : getModel().getInletInstances()) {
            if (comma) {
                s += ",\n";
            }
            s += "const " + i.getDataType().CType() + " " + i.GetCName();
            comma = true;
        }
        for (OutletInstance i : getModel().getOutletInstances()) {
            if (comma) {
                s += ",\n";
            }
            s += i.getDataType().CType() + " & " + i.GetCName();
            comma = true;
        }
        for (ParameterInstance i : getModel().getParameterInstances()) {
            if (i.parameter.PropagateToChild == null) {
                if (comma) {
                    s += ",\n";
                }
                s += i.parameter.CType() + " " + i.GetCName();
                comma = true;
            }
        }
        for (DisplayInstance i : getModel().getDisplayInstances()) {
            if (i.getModel().getLength() > 0) {
                if (comma) {
                    s += ",\n";
                }
                if (i.getModel().getDatatype().isPointer()) {
                    s += i.getModel().getDatatype().CType() + " " + i.GetCName();
                } else {
                    s += i.getModel().getDatatype().CType() + " & " + i.GetCName();
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

    public String GenerateInstanceDataDeclaration2() {
        String c = "";
        if (getModel().getType().getLocalData() != null) {
            String s = getModel().getType().getLocalData();
            s = s.replaceAll("attr_parent", getModel().getCInstanceName());
            c += s + "\n";
        }
        return c;
    }

    public String GenerateInstanceCodePlusPlus(String classname, boolean enableOnParent) {
        String c = "";
        c += GenerateInstanceDataDeclaration2();
        for (AttributeInstance p : getModel().getAttributeInstances()) {
            if (p.CValue() != null) {
                c = c.replaceAll(p.GetCName(), p.CValue());
            }
        }
        return c;
    }
    
    @Override
    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        String s = "";
        s += "class " + getModel().getCInstanceName() + "{\n";
        s += "  public: // v1\n";
        s += GenerateInstanceCodePlusPlus(ClassName, enableOnParent);
        s += GenerateInitCodePlusPlus(ClassName, enableOnParent);
        s += GenerateDisposeCodePlusPlus(ClassName);
        s += GenerateDoFunctionPlusPlus(ClassName, OnParentAccess, enableOnParent);
        {
            String d3 = GenerateCodeMidiHandler("");
            if (!d3.isEmpty()) {
                s += "void MidiInHandler(" + ClassName + "*parent, midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n";
                s += d3;
                s += "}\n";
            }
        }
        s += "}\n;";
        return s;
    }

    public String GenerateCodeMidiHandler(String vprefix) {
        String s = "";
        if (getModel().getType().getMidiCode() != null) {
            s += getModel().getType().getMidiCode();
        }
        for (ParameterInstance i : getModel().getParameterInstances()) {
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

    public String GenerateCallMidiHandler() {
        if ((getModel().getType().getMidiCode() != null) && (!getModel().getType().getMidiCode().isEmpty())) {
            return getModel().getCInstanceName() + "_i.MidiInHandler(this, dev, port, status, data1, data2);\n";
        }
        for (ParameterInstance pi : getModel().getParameterInstances()) {
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
