package axoloti.codegen.patch.object;

import axoloti.codegen.CodeGeneration;
import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.display.DisplayInstanceViewFactory;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceViewFactory;
import axoloti.datatypes.Frac32buffer;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
class AxoObjectInstanceCodegenView implements IAxoObjectInstanceCodegenView {

    private final IAxoObjectInstance objectInstance;
    private final List<ParameterInstanceView> parameterInstances;
    private final List<DisplayInstanceView> displayInstances;

    AxoObjectInstanceCodegenView(IAxoObjectInstance objectInstance) {
        this.objectInstance = objectInstance;

        parameterInstances = new LinkedList<>();
        for (ParameterInstance p : objectInstance.getParameterInstances()) {
            ParameterInstanceView pv = ParameterInstanceViewFactory.createView(p);
            parameterInstances.add(pv);
        }

        displayInstances = new LinkedList<>();
        for (DisplayInstance d : objectInstance.getDisplayInstances()) {
            DisplayInstanceView dv = DisplayInstanceViewFactory.createView(d);
            displayInstances.add(dv);
        }

    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }


    @Override
    public IAxoObjectInstance getDModel() {
        return objectInstance;
    }

    @Override
    public List<ParameterInstanceView> getParameterInstanceViews() {
        return Collections.unmodifiableList(parameterInstances);
    }

    @Override
    public List<DisplayInstanceView> getDisplayInstanceViews() {
        return Collections.unmodifiableList(displayInstances);
    }

    @Override
    public String generateUICode(int count[]) {
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
        int nparams = getDModel().getParameterInstances().size();
        int ndisplays = displayInstances.size();
        if (nparams + ndisplays == 0) {
            return "";
        }
        count[0]++;
        StringBuilder s = new StringBuilder(
            "{ name : " + CodeGeneration.CPPCharArrayStaticInitializer(getDModel().getInstanceName(), CodeGeneration.PARAM_NAME_LENGTH)
            + ", nparams : " + nparams);
        if (nparams > 0) {
            s.append(", params : &PExch[" + parameterInstances.get(0).getIndex() + "]");
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
    public String generateInitCodePlusPlus(String classname, boolean enableOnParent) {
        StringBuilder c = new StringBuilder();
//        if (hasStruct())
//            c = new StringBuilder("  void " + GenerateInitFunctionName() + "(" + GenerateStructName() + " * x ) {\n");
//        else
//        if (!classname.equals("one"))
//        c.append("parent = _parent;\n");
        for (ParameterInstanceView p : parameterInstances) {
            if (p.getDModel().getDModel().PropagateToChild != null) {
                c.append("// on Parent: propagate " + p.getDModel().getName() + " " + enableOnParent + " " + getDModel().getLegalName() + "" + p.getDModel().getDModel().PropagateToChild + "\n");
                c.append(p.PExName("parent->") + ".pfunction = PropagateToSub;\n");
                c.append(p.PExName("parent->") + ".d.frac.finalvalue = (int32_t)(&(parent->instance"
                        + getDModel().getLegalName() + "_i.PExch[instance" + getDModel().getLegalName() + "::PARAM_INDEX_"
                        + p.getDModel().getDModel().PropagateToChild + "]));\n");
            }
            //           if ((p.getOnParent() && !enableOnParent)) {
            //c += "// on Parent: propagate " + p.name + "\n";
            //String parentparametername = classname.substring(8);
            //c += "// classname : " + classname + " : " + parentparametername + "\n";
            //c += "parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].pfunction = PropagateToSub;\n";
            //c += "parent->parent->PExch[PARAM_INDEX_" + parentparametername + "_" + getLegalName() + "].finalvalue = (int32_t)(&(" + p.PExName("parent->") + "));\n";
            //         }
        }
        for (DisplayInstanceView p : displayInstances) {
            c.append(p.generateCodeInit(""));
        }
        if (getDModel().getDModel().getInitCode() != null) {
            String s = getDModel().getDModel().getInitCode();
            for (AttributeInstance p : getDModel().getAttributeInstances()) {
                s = s.replace(p.getCName(), p.CValue());
            }
            c.append(s + "\n");
        }
        StringBuilder d = new StringBuilder("  public: void Init(" + classname + " * parent");
        if (!displayInstances.isEmpty()) {
            for (DisplayInstanceView p : displayInstances) {
                if (p.getDModel().getDModel().getLength() > 0) {
                    d.append(",\n");
                    if (p.getDModel().getDModel().getDataType().isPointer()) {
                        d.append(p.getDModel().getDModel().getDataType().CType() + " " + p.getCName());
                    } else {
                        d.append(p.getDModel().getDModel().getDataType().CType() + " & " + p.getCName());
                    }
                }
            }
        }
        d.append(") {\n" + c.toString() + "}\n");
        return d.toString();
    }

    @Override
    public String generateDisposeCodePlusPlus(String classname) {
        String c = "";
        if (getDModel().getDModel().getDisposeCode() != null) {
            String s = getDModel().getDModel().getDisposeCode();
            for (AttributeInstance p : getDModel().getAttributeInstances()) {
                s = s.replaceAll(p.getCName(), p.CValue());
            }
            c += s + "\n";
        }
        c = "  public: void Dispose() {\n" + c + "}\n";
        return c;
    }

    public String generateKRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        String s = getDModel().getDModel().getKRateCode();
        if (s != null) {
            for (AttributeInstance p : getDModel().getAttributeInstances()) {
                s = s.replaceAll(p.getCName(), p.CValue());
            }
            s = s.replace("attr_name", getDModel().getCInstanceName());
            s = s.replace("attr_legal_name", getDModel().getLegalName());
            for (ParameterInstance p : getDModel().getParameterInstances()) {
                Boolean op = p.getOnParent();
                if (op!=null && op == true) {
//                    s = s.replace("%" + p.name + "%", OnParentAccess + p.variableName(vprefix, enableOnParent));
                } else {
//                    s = s.replace("%" + p.name + "%", p.variableName(vprefix, enableOnParent));
                }
            }
            for (DisplayInstance p : getDModel().getDisplayInstances()) {
//                s = s.replace("%" + p.name + "%", p.valueName(vprefix));
            }
            return s + "\n";
        }
        return "";
    }

    public String generateSRateCodePlusPlus(String vprefix, boolean enableOnParent, String OnParentAccess) {
        if (getDModel().getDModel().getSRateCode() != null) {
            String s = "int buffer_index;\n"
                    + "for(buffer_index=0;buffer_index<BUFSIZE;buffer_index++) {\n"
                    + getDModel().getDModel().getSRateCode()
                    + "\n}\n";

            for (AttributeInstance p : getDModel().getAttributeInstances()) {
                s = s.replaceAll(p.getCName(), p.CValue());
            }
            for (InletInstance i : getDModel().getInletInstances()) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.getDModel().getCName(), i.getDModel().getCName() + "[buffer_index]");
                }
            }
            for (OutletInstance i : getDModel().getOutletInstances()) {
                if (i.getDataType() instanceof Frac32buffer) {
                    s = s.replaceAll(i.getDModel().getCName(), i.getDModel().getCName() + "[buffer_index]");
                }
            }

            s = s.replace("attr_name", getDModel().getCInstanceName());
            s = s.replace("attr_legal_name", getDModel().getLegalName());

            return s;
        }
        return "";
    }

    public String generateDoFunctionPlusPlus(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        StringBuilder s = new StringBuilder("  public: void dsp (" + ClassName + " * parent");
        boolean comma = true;
        for (InletInstance i : getDModel().getInletInstances()) {
            if (comma) {
                s.append(",\n");
            }
            s.append("const " + i.getDataType().CType() + " " + i.getDModel().getCName());
            comma = true;
        }
        for (OutletInstance i : getDModel().getOutletInstances()) {
            if (comma) {
                s.append(",\n");
            }
            s.append(i.getDataType().CType() + " & " + i.getDModel().getCName());
            comma = true;
        }
        for (ParameterInstance i : getDModel().getParameterInstances()) {
            if (i.getDModel().PropagateToChild == null) {
                if (comma) {
                    s.append(",\n");
                }
                s.append(i.getDModel().CType() + " " + i.getCName());
                comma = true;
            }
        }
        for (DisplayInstanceView i : displayInstances) {
            if (i.getDModel().getDModel().getLength() > 0) {
                if (comma) {
                    s.append(",\n");
                }
                if (i.getDModel().getDModel().getDataType().isPointer()) {
                    s.append(i.getDModel().getDModel().getDataType().CType() + " " + i.getCName());
                } else {
                    s.append(i.getDModel().getDModel().getDataType().CType() + " & " + i.getCName());
                }
                comma = true;
            }
        }
        s.append("  ){\n");
        s.append(generateKRateCodePlusPlus("", enableOnParent, OnParentAccess));
        s.append(generateSRateCodePlusPlus("", enableOnParent, OnParentAccess));
        s.append("}\n");
        return s.toString();
    }

    public final static String MidiHandlerFunctionHeader = "void MidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n";

    public String generateInstanceDataDeclaration2() {
        String c = "";
        if (getDModel().getDModel().getLocalData() != null) {
            c = getDModel().getDModel().getLocalData()
                .replaceAll("attr_parent", getDModel().getCInstanceName()) + "\n";
        }
        return c;
    }

    public String generateInstanceCodePlusPlus(String classname, boolean enableOnParent) {
        String c = generateInstanceDataDeclaration2();
        for (AttributeInstance p : getDModel().getAttributeInstances()) {
            if (p.CValue() != null) {
                c = c.replaceAll(p.getCName(), p.CValue());
            }
        }
        return c;
    }

    @Override
    public String generateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        StringBuilder s = new StringBuilder();
        s.append("class " + getDModel().getCInstanceName() + "{\n");
        s.append("  public: // v1\n");
        s.append(generateInstanceCodePlusPlus(ClassName, enableOnParent));
        s.append(generateInitCodePlusPlus(ClassName, enableOnParent));
        s.append(generateDisposeCodePlusPlus(ClassName));
        s.append(generateDoFunctionPlusPlus(ClassName, OnParentAccess, enableOnParent));
        {
            String d3 = generateCodeMidiHandler("");
            if (!d3.isEmpty()) {
                s.append("void MidiInHandler(" + ClassName + "*parent, midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2) {\n");
                s.append(d3);
                s.append("}\n");
            }
        }
        s.append("}\n;");
        return s.toString();
    }

    public String generateCodeMidiHandler(String vprefix) {
        String s = "";
        if (getDModel().getDModel().getMidiCode() != null) {
            s += getDModel().getDModel().getMidiCode();
        }
        for (ParameterInstanceView i : parameterInstances) {
            s += i.generateCodeMidiHandler("");
        }
        for (AttributeInstance p : getDModel().getAttributeInstances()) {
            s = s.replaceAll(p.getCName(), p.CValue());
        }
        s = s.replace("attr_name", getDModel().getCInstanceName());
        s = s.replace("attr_legal_name", getDModel().getLegalName());

        if (s.length() > 0) {
            return "{\n" + s + "}\n";
        } else {
            return "";
        }
    }

    @Override
    public String generateCallMidiHandler() {
        if ((getDModel().getDModel().getMidiCode() != null) && (!getDModel().getDModel().getMidiCode().isEmpty())) {
            return getDModel().getCInstanceName() + "_i.MidiInHandler(this, dev, port, status, data1, data2);\n";
        }
        for (ParameterInstanceView pi : parameterInstances) {
            if (!pi.generateCodeMidiHandler("").isEmpty()) {
                return getDModel().getCInstanceName() + "_i.MidiInHandler(this, dev, port, status, data1, data2);\n";
            }
        }
        return "";
    }

    @Override
    public void dispose() {
    }

}
