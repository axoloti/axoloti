package axoloti.codegen.patch;

import axoloti.codegen.CodeGeneration;
import axoloti.codegen.patch.object.AxoObjectInstanceCodegenViewFactory;
import axoloti.codegen.patch.object.IAxoObjectInstanceCodegenView;
import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.mvc.View;
import axoloti.object.AxoObject;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletCharPtr32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.Parameter;
import axoloti.patch.Modulation;
import axoloti.patch.Modulator;
import axoloti.patch.PatchModel;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.preset.Preset;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class PatchViewCodegen extends View<PatchModel> {

    //TODO: (enhancement) use execution order, rather than UI ordering
    public static final boolean USE_EXECUTION_ORDER = false;

    private final List<IAxoObjectInstanceCodegenView> objectInstanceViews;
    private final List<ParameterInstanceView> parameterInstances;
    private final List<DisplayInstanceView> displayInstances;
    private final int displayDataLength;

    public PatchViewCodegen(PatchModel patchModel) {
        super(patchModel);

        patchModel.createIID();
        if (USE_EXECUTION_ORDER) {
            patchModel.sortByExecution();
        } else {
            patchModel.sortByPosition();
        }
        // needed...
        patchModel.getController();

        // TODO: report/fail zombies
        objectInstanceViews = new ArrayList<>(
                patchModel.getObjectInstances().size()
        );
        for (IAxoObjectInstance ox : patchModel.getObjectInstances()) {
            IAxoObjectInstanceCodegenView o = AxoObjectInstanceCodegenViewFactory.createView(ox);
            objectInstanceViews.add(o);
        }

        int i = 0;
        parameterInstances = new LinkedList<>();
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            for (ParameterInstanceView p : o.getParameterInstanceViews()) {
                p.setIndex(i);
                i++;
                parameterInstances.add(p);
            }
        }
        int offset = 0;
        i = 0;
        displayInstances = new ArrayList<>();
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            for (DisplayInstanceView p : o.getDisplayInstanceViews()) {
                p.setOffset(offset);
                p.setIndex(i);
                int l = p.getDModel().getDModel().getLength();
                offset += l;
                i++;
                displayInstances.add(p);
            }
        }
        displayDataLength = offset;
    }

    public String generateIncludes() {
        StringBuilder inc = new StringBuilder();
        List<String> includes = getDModel().getIncludes();
        for (String s : includes) {
            if (s.startsWith("\"")) {
                inc.append("#include " + s + "\n");
            } else {
                inc.append("#include \"" + s + "\"\n");
            }
        }
        return inc.toString();
    }

    /* the c++ code generator */
    String generatePexchAndDisplayCode() {
        StringBuilder c = new StringBuilder(generatePexchAndDisplayCodeV());
        c.append("    int32_t PExModulationPrevVal[attr_poly][NMODULATIONSOURCES];\n");
        return c.toString();
    }

    String generatePexchAndDisplayCodeV() {
        StringBuilder c = new StringBuilder();
        c.append("    static const uint32_t nparams = " + parameterInstances.size() + ";\n");
        c.append("    Parameter_t PExch[nparams] = {\n");
        for (ParameterInstanceView param : parameterInstances) {
            c.append(param.generateParameterInitializer());
        }
        c.append("};\n");
        c.append("    Parameter_name_t param_names[nparams] = {\n");
        for (ParameterInstanceView param : parameterInstances) {
            c.append("{ name : " + CodeGeneration.CPPCharArrayStaticInitializer(param.getDModel().getUserParameterName(), CodeGeneration.PARAM_NAME_LENGTH) + "},\n");
        }
        c.append("};\n");
        c.append("    int32_t displayVector[" + displayDataLength + "];\n");

        c.append("    static const uint32_t ndisplay_metas = " + displayInstances.size() + ";\n");
        c.append("    Display_meta_t display_metas[ndisplay_metas] = {\n");
        for (DisplayInstanceView disp : displayInstances) {
            c.append(disp.generateDisplayMetaInitializer());
        }
        c.append("};\n");
        c.append("    static const uint32_t NPRESETS = " + getDModel().getNPresets() + ";\n");
        c.append("    static const uint32_t NPRESET_ENTRIES = " + getDModel().getNPresetEntries() + ";\n");
        c.append("    static const uint32_t NMODULATIONSOURCES = " + getDModel().getNModulationSources() + ";\n");
        c.append("    static const uint32_t NMODULATIONTARGETS = " + getDModel().getNModulationTargetsPerSource() + ";\n");
        return c.toString();
    }

    String generateObjectCode(String classname, boolean enableOnParent, String OnParentAccess) {
        StringBuilder c = new StringBuilder();
        {
            c.append("/* modsource defines */\n");
            int k = 0;
            for (Modulator m : getDModel().getModulators()) {
                c.append("static const int " + m.getCName() + " = " + k + ";\n");
                k++;
            }
        }
        {
            c.append("/* parameter instance indices */\n");
            int k = 0;
            for (ParameterInstanceView p : parameterInstances) {
                c.append("static const int PARAM_INDEX_" + p.getDModel().getObjectInstance().getLegalName() + "_" + p.getDModel().getLegalName() + " = " + k + ";\n");
                k++;
            }
        }

        c.append("/* object classes */\n");
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c.append(o.generateClass(classname, OnParentAccess, enableOnParent));
        }

        c.append("/* object instances */\n");
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            String s = o.getDModel().getCInstanceName();
            if (!s.isEmpty()) {
                c.append("     " + s + " " + s + "_i;\n");
            }
        }
        c.append("/* net latches */\n");
        for (Net n : model.getNets()) {
            NetController nc = n.getController();
            // check if net has multiple sources
            if ((n.CType() != null) && n.needsLatch()) {
                c.append("    " + n.CType() + " " + n.getCName() + "Latch" + ";\n");
            }
        }
        return c.toString();
    }

    public String generateStructCodePlusPlusSub(String classname, boolean enableOnParent) {
        StringBuilder c = new StringBuilder();
        c.append(generatePexchAndDisplayCode());
        c.append(generateObjectCode(classname, enableOnParent, "parent->"));
        return c.toString();
    }

    String generateStructCodePlusPlus(String classname, boolean enableOnParent, String parentclassname) {
        StringBuilder c = new StringBuilder();
        c.append("class rootc : public PatchInstance {\n"
                + "public:\n"
                + "  void tick( int32_t * inbuf, int32_t * outbuf) {\n"
                + "    int32_t *p = outbuf;\n"
                + "    int i;\n"
                + "    for(i=0;i<BUFSIZE;i++){*p++=0;*p++=0;}\n"
                + "    AudioInputLeft = &inbuf[0];\n"
                + "    AudioInputRight = &inbuf[BUFSIZE];\n"
                + "    AudioOutputLeft = &outbuf[0];\n"
                + "    AudioOutputRight = &outbuf[BUFSIZE];\n"
                + "    dsp();\n"
                + "  }\n"
                + "\n"
                + "  void midiInHandler(int32_t m) {\n"
                + "    midi_message_t m1;\n"
                + "    m1.word = m;\n"
                + "    midiInHandler(0, m1);\n"
                + "  }\n"
                + "\n"
                + "  void* getProperty(ax_property_id_t id, int index) {\n"
                + "    switch(id) {\n"
                + "    case ax_prop_displayvector :\n"
                + "      return displayVector;\n"
                + "    case ax_prop_displayvector_size:\n"
                + "      return (void*)(sizeof(displayVector)/sizeof(int32_t));\n"
                + "    case ax_prop_nparams:\n"
                + "      return (void*)nparams;\n"
                + "    case ax_prop_param:\n"
                + "      return &PExch[index];\n"
                + "    case ax_prop_paramName:\n"
                + "      return &param_names[index].name[0];\n"
                + "    case ax_prop_presetData:\n"
                + "      return presets;\n"
                + "    default:\n"
                + "      return 0;\n"
                + "    }\n"
                + "  }\n"
                + "\n"
                + "  int setProperty(ax_property_id_t id, int index, void * value) {\n"
                + "    switch(id) {\n"
                + "    case ax_prop_applyPreset: {\n"
                + "      ApplyPreset(index);\n"
                + "      return 0;\n"
                + "    }\n"
                + "    default:\n"
                + "      return -1;\n"
                + "    }\n"
                + "  }\n"
                + "\n"
                + "  int reserved1(){return 0;}\n"
                + "  int reserved2(){return 0;}\n"
                + "\n");
        c.append(generateStructCodePlusPlusSub(parentclassname, enableOnParent));
        return c.toString();
    }

    String generateUICode() {
        int count[] = new int[]{0};
        StringBuilder c = new StringBuilder();
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c.append(o.generateUICode(count));
        }
        c.append("};\n");
        String r = "static const int n_ui_objects = " + count[0] + ";\n"
                + "ui_object_t ui_objects[n_ui_objects] = {\n" + c.toString();
        return r;
    }


    public String generateParamInitCode3(String ClassName) {
        int s = parameterInstances.size();
        StringBuilder c = new StringBuilder(
            "   static int32_t * GetInitParams(void){\n"
                + "      static const int32_t p[" + s + "]= {\n");
        ParameterInstanceView lastParam = null;
        if (s > 0) {
            lastParam = parameterInstances.get(s - 1);
        }
        for (ParameterInstanceView param : parameterInstances) {
            c.append("      ");
            c.append(param.getDModel().valToInt32(param.getDModel().getValue()));
            if (param == lastParam) {
                c.append("\n");
            } else {
                c.append(",\n");
            }
        }
        c.append("      };\n");
        c.append("      return (int32_t *)&p[0];\n");
        c.append("   }\n");
        return c.toString();
    }

    public int[] distillPreset(int i) {
        int[] pdata;
        pdata = new int[getDModel().getNPresetEntries() * 2];
        for (int j = 0; j < getDModel().getNPresetEntries(); j++) {
            pdata[j * 2] = -1;
        }
        int index = 0;
        for (ParameterInstanceView param : parameterInstances) {
            Preset p = param.getDModel().getPreset(i);
            if (p != null) {
                pdata[index * 2] = param.getIndex();
                pdata[index * 2 + 1] = param.getDModel().valToInt32(p.getValue());
                index++;
                if (index == getDModel().getNPresetEntries()) {
                    Logger.getLogger(PatchViewCodegen.class.getName()).log(Level.SEVERE, "more than {0}entries in preset, skipping...", getDModel().getNPresetEntries());
                    return pdata;
                }
            }
        }
        boolean debug = false;
        if (debug) {
            System.out.format("preset #%d data : (index, value)%n", i);
            for (int j = 0; j < pdata.length / 2; j++) {
                System.out.format("  %d : 0x%08X%n", pdata[j * 2], pdata[j * 2 + 1]);
            }
        }
        return pdata;
    }

    public String generatePresetCode3(String ClassName) {
        StringBuilder c = new StringBuilder();
        c.append("      int32_t presets[NPRESETS][NPRESET_ENTRIES][2] = {\n");
        for (int i = 0; i < getDModel().getNPresets(); i++) {
//            c.append("// preset " + i + "\n");
//            c.append("pp = (int*)(&Presets[" + i + "]);\n");
            int[] dp = distillPreset(i + 1);
            c.append("         {\n");
            for (int j = 0; j < getDModel().getNPresetEntries(); j++) {
                c.append("           {" + dp[j * 2] + "," + dp[j * 2 + 1] + "}");
                if (j != getDModel().getNPresetEntries() - 1) {
                    c.append(",\n");
                } else {
                    c.append("\n");
                }
            }
            if (i != getDModel().getNPresets() - 1) {
                c.append("         },\n");
            } else {
                c.append("         }\n");
            }
        }
        c.append("      };\n");

        c.append("const int32_t * GetPresets(void){\n");
        c.append("  return (int32_t *)&presets[0][0][0];\n");
        c.append("}\n\n");

        c.append("void ApplyPreset(int index){\n"
                + "   if (!index) {\n"
                 + "     int i;\n"
                 + "     int32_t *p = GetInitParams();\n"
                 + "     for(i=0;i<nparams;i++){\n"
                + "        parameter_setVal(&PExch[i], p[i], 0xFFEF);\n"
                + "     }\n"
                 + "   }\n"
                 + "   index--;\n"
                 + "   if (index < NPRESETS) {\n"
                 + "     PresetParamChange_t *pa = (PresetParamChange_t *)(GetPresets());\n"
                 + "     PresetParamChange_t *p = &pa[index*NPRESET_ENTRIES];\n"
                 + "       int i;\n"
                 + "       for(i=0;i<NPRESET_ENTRIES;i++){\n"
                 + "         PresetParamChange_t *pp = &p[i];\n"
                 + "         if ((pp->pexIndex>=0)&&(pp->pexIndex<nparams)) {\n"
                + "            parameter_setVal(&PExch[pp->pexIndex],pp->value,0xFFEF);" + "         }\n"                 + "         else break;\n"
                 + "       }\n"
                 + "   }\n"
                 + "}\n");
        return c.toString();
    }

    private ParameterInstanceView findCorrespondingParameterInstanceView(ParameterInstance param) {
        for (ParameterInstanceView p : parameterInstances) {
            if (p.getDModel() == param) {
                return p;
            }
        }
        return null;
    }

    public String generateModulationCode3() {
        StringBuilder s = new StringBuilder("   static PExModulationTarget_t * GetModulationTable(void){\n");
        s.append("    static const PExModulationTarget_t PExModulationSources[NMODULATIONSOURCES][NMODULATIONTARGETS] = \n");
        s.append("{");
        for (int i = 0; i < getDModel().getNModulationSources(); i++) {
            s.append("{");
            if (i < getDModel().getModulators().size()) {
                Modulator m = getDModel().getModulators().get(i);
                for (int j = 0; j < getDModel().getNModulationTargetsPerSource(); j++) {
                    if (j < m.getModulations().size()) {
                        Modulation n = m.getModulations().get(j);
                        ParameterInstance destination = n.getParameter();
                        ParameterInstanceView piv = findCorrespondingParameterInstanceView(destination);
                        s.append("{");
                        s.append(piv.indexName());
                        s.append(", ");
                        s.append(destination.valToInt32(n.getValue()));
                        s.append("}");
                    } else {
                        s.append("{-1,0}");
                    }
                    if (j != getDModel().getNModulationTargetsPerSource() - 1) {
                        s.append(",");
                    } else {
                        s.append("}");
                    }
                }
            } else {
                for (int j = 0; j < getDModel().getNModulationTargetsPerSource() - 1; j++) {
                    s.append("{-1,0},");
                }
                s.append("{-1,0}}");
            }
            if (i != getDModel().getNModulationSources() - 1) {
                s.append(",\n");
            }
        }
        s.append("};\n");
        s.append("   return (PExModulationTarget_t *)&PExModulationSources[0][0];\n");
        s.append("   };\n");

        return s.toString();
    }


    public String generateObjInitCodePlusPlusSub(String className, String parentReference) {
        StringBuilder c = new StringBuilder();
        c.append("  int r = 0;\n");
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            String s = o.getDModel().getCInstanceName();
            if (!s.isEmpty()) {
                c.append("  r =  " + o.getDModel().getCInstanceName() + "_i.init(" + parentReference);
                for (DisplayInstanceView i : o.getDisplayInstanceViews()) {
                    if (i.getDModel().getDModel().getLength() > 0) {
                        c.append(", ");
                        c.append(i.valueName(""));
                    }
                }
                c.append(" );\n");
                c.append("  if (r) return r;\n");
            }
        }
        /* // no need for this?
           c.append("      int k;\n"
           + "      for (k = 0; k < nparams; k++) {"
           + "        Parameter_t *param = &params[k];\n"
           + "        switch(param->type) {\n"
           + "          case param_type_frac: "
           + "             if (param->pfunction)"
           + "                 (param->pfunction)(param);\n"
           + "             else param->finalvalue = param->value;"
           + "             break;"
           + "          default: \n"
           + "            param[k].finalvalue = param[k].value;\n"
           + "        }\n"
           + "      }\n");
         */
        return c.toString();
    }

    public String generateParamInitCodePlusPlusSub(String className, String parentReference) {
        StringBuilder c = new StringBuilder("// GenerateParamInitCodePlusPlusSub\n");
        c.append("   int i;\n");
        c.append("   int j;\n");
        c.append("   const int32_t *p;\n");
        c.append("   p = GetInitParams();\n");
        c.append("   for(j=0;j<" + parameterInstances.size() + ";j++){\n");
        c.append("      parameter_setVal(&PExch[j], p[j], 0xFFEE);\n");
        c.append("   }\n");
        c.append("   int32_t *pp = &PExModulationPrevVal[0][0];\n");
        c.append("   for(j=0;j<attr_poly*NMODULATIONSOURCES;j++){\n");
        c.append("      *pp = 0; pp++;\n");
        c.append("   }\n");
        return c.toString();
    }

    String generateInitCodePlusPlus(String className) {
        StringBuilder c = new StringBuilder();
        c.append("/* init */\n");
        c.append("int init() {\n");
        c.append(generateObjInitCodePlusPlusSub("", "this"));
        c.append(generateParamInitCodePlusPlusSub("", "this"));
        c.append("  return 0;\n");
        c.append("}\n\n");
        return c.toString();
    }


    public String generateDisposeCodePlusPlusSub(String className) {
        // reverse order
        StringBuilder c = new StringBuilder();
        int l = getDModel().getObjectInstances().size();
        for (int i = l - 1; i >= 0; i--) {
            IAxoObjectInstance o = getDModel().getObjectInstances().get(i);
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c.append("   " + o.getCInstanceName() + "_i.dispose();\n");
            }
        }

        return c.toString();
    }

    String generateDisposeCodePlusPlus(String className) {
        StringBuilder c = new StringBuilder();
        c.append("/* dispose */\n");
        c.append("void dispose() {\n");
        c.append(generateDisposeCodePlusPlusSub(className));
        c.append("  this->~rootc();\n");
        c.append("}\n\n");
        return c.toString();
    }

    public String generateDSPCodePlusPlusSub(String ClassName, boolean enableOnParent) {
        StringBuilder c = new StringBuilder();
        c.append("//--------- <nets> -----------//\n");
        for (Net n : model.getNets()) {
            if (n.CType() != null) {
                c.append("    " + n.CType() + " " + n.getCName() + ";\n");
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Net has no data type!");
            }
        }
        c.append("//--------- </nets> ----------//\n");
        c.append("//--------- <zero> ----------//\n");
        c.append("  int32_t UNCONNECTED_OUTPUT;\n");
        c.append("  static const int32_t UNCONNECTED_INPUT=0;\n");
        c.append("  static const int32buffer zerobuffer = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};\n");
        c.append("  int32buffer UNCONNECTED_OUTPUT_BUFFER;\n");
        c.append("//--------- </zero> ----------//\n");

        c.append("//--------- <object calls> ----------//\n");
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c.append(generateDSPCodePlusPlusSubObj(o, ClassName, enableOnParent));
        }
        c.append("//--------- </object calls> ----------//\n");

        c.append("//--------- <net latch copy> ----------//\n");
        for (Net n : model.getNets()) {
            // check if net has multiple sources
            if (n.needsLatch()) {
                if (n.getDataType() != null) {
                    c.append(n.getDataType().generateCopyCode(n.getCName() + "Latch", n.getCName()));
                } else {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Only inlets connected on net!");
                }
            }
        }
        c.append("//--------- </net latch copy> ----------//\n");
        return c.toString();
    }

    String generateDSPCodePlusPlusSubObj(IAxoObjectInstanceCodegenView o, String ClassName, boolean enableOnParent) {
        StringBuilder c = new StringBuilder();
        String s = o.getDModel().getCInstanceName();
        if (s.isEmpty()) {
            return c.toString();
        }
        c.append("  " + o.getDModel().getCInstanceName() + "_i.dsp( this ");
//            c.append("  " + o.GenerateDoFunctionName() + "(this");
        boolean needsComma = true;
        for (InletInstance i : o.getDModel().getInletInstances()) {
            if (needsComma) {
                c.append(", ");
            }
            Net n = getDModel().getController().getNetFromIolet(i);
            if ((n != null) && (n.isValidNet())) {
                OutletInstance firstSource = java.util.Collections.min(n.getSources());
                if (i.getDataType().equals(n.getDataType())) {
                    if (n.needsLatch()
                            && (getDModel().getObjectInstances().indexOf(firstSource.getParent()) >= getDModel().getObjectInstances().indexOf(o.getDModel()))) {
                        c.append(n.getCName()).append("Latch");
                    } else {
                        c.append(n.getCName());
                    }
                } else if (n.needsLatch()
                        && (getDModel().getObjectInstances().indexOf(firstSource.getParent()) >= getDModel().getObjectInstances().indexOf(o.getDModel()))) {
                    c.append(n.getDataType().generateConversionToType(i.getDataType(), n.getCName() + "Latch"));
                } else {
                    c.append(n.getDataType().generateConversionToType(i.getDataType(), n.getCName()));
                }
            } else if (n == null) { // unconnected input
                c.append(i.getDataType().generateSetDefaultValueCode());
            } else if (!n.isValidNet()) {
                c.append(i.getDataType().generateSetDefaultValueCode());
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Patch contains invalid net! {0}", i.getParent().getInstanceName() + ":" + i.getName());
            }
            needsComma = true;
        }
        for (OutletInstance i : o.getDModel().getOutletInstances()) {
            if (needsComma) {
                c.append(", ");
            }
            Net net = model.getController().getNetFromIolet(i);
            if ((net != null) && net.isValidNet()) {
                if (net.isFirstOutlet(i)) {
                    c.append(net.getCName());
                } else {
                    c.append(net.getCName() + "+");
                }
            } else {
                c.append(i.getDataType().unconnectedSink());
            }
            needsComma = true;
        }
        for (ParameterInstanceView i : o.getParameterInstanceViews()) {
            if (i.getDModel().getDModel().PropagateToChild == null) {
                if (needsComma) {
                    c.append(", ");
                }
                c.append(i.variableName("", false));
                needsComma = true;
            }
        }
        for (DisplayInstanceView i : o.getDisplayInstanceViews()) {
            if (i.getDModel().getDModel().getLength() > 0) {
                if (needsComma) {
                    c.append(", ");
                }
                c.append(i.valueName(""));
                needsComma = true;
            }
        }
        c.append(");\n");
        return c.toString();
    }

    String generateDSPCodePlusPlus(String ClassName, boolean enableOnParent) {
        StringBuilder c = new StringBuilder("/* krate */\n");
        c.append("void dsp (void) {\n");
        c.append("  int i;\n");
        c.append(generateDSPCodePlusPlusSub(ClassName, enableOnParent));
        c.append("}\n\n");
        return c.toString();
    }

    public String generateMidiInCodePlusPlus() {
        StringBuilder c = new StringBuilder();
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c.append(o.generateCallMidiHandler());
        }
        return c.toString();
    }


    String generateMidiCodePlusPlus(String ClassName) {
        StringBuilder c = new StringBuilder();
        c.append("void midiInHandler(" + ClassName + " *parent, midi_message_t midi_message){\n");
        c.append(generateMidiInCodePlusPlus());
        c.append("}\n\n");
        return c.toString();
    }

    String generatePatchCodePlusPlus(String ClassName) {
        StringBuilder c = new StringBuilder();
        c.append("};\n\n");
        c.append("extern \"C\" {\n"
                + "  int getInstanceSize() {\n"
                + "    return sizeof(rootc);\n"
                + "  }\n"
                + "\n"
                + "  int initInstance(PatchInstance *instance /*,... args */) {\n"
                + "    // placement new\n"
                + "    rootc * _inst = new((void *)instance) rootc();\n"
                + "    return _inst->init();\n"
                + "  }\n"
                + "}\n");

        return c.toString();
    }


    public String generateCode4() {
        StringBuilder c = new StringBuilder();

        List<String> moduleSet = getDModel().getModules();
        if (moduleSet != null) {
            StringBuilder modules = new StringBuilder();
            StringBuilder moduleDirs = new StringBuilder();
            for (String m : moduleSet) {
                modules.append(m + " ");
                moduleDirs.append(
                    getDModel().getModuleDir(m) + " ");
            }
            c.append("//$MODULES=" + modules.toString() + "\n");
            c.append("//$MODULE_DIRS=" + moduleDirs.toString() + "\n");
        }
        c.append("#include <new>\n");
        c.append(generateIncludes());
        c.append("\n");
        // modules are included through makefile
        c.append("\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-variable\"\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-parameter\"\n");
        if (true == false) {
            c.append("#define MIDICHANNEL 0 // DEPRECATED!\n");
        } else {
            c.append("#define MIDICHANNEL " + (getDModel().getMidiChannel() - 1) + " // DEPRECATED!\n");
        }

        c.append("\n"
                + "static int32_t * AudioInputLeft;\n"
                + "static int32_t * AudioInputRight;\n"
                + "static int32_t * AudioOutputLeft;\n"
                + "static int32_t * AudioOutputRight;\n\n");

        c.append("static void ModulationSourceChange(PExModulationTarget_t *modulation,\n"
                + "                               int32_t nTargets,\n"
                + "                               Parameter_t *parameters,\n"
                + "                               int32_t *oldvalue,\n"
                + "                               int32_t value) {\n"
                + "  PExModulationTarget_t *s = modulation;\n"
                + "  int t;\n"
                + "  for (t = 0; t < nTargets; t++) {\n"
                + "    PExModulationTarget_t *target = &s[t];\n"
                + "    if (target->parameterIndex == -1)\n"
                + "      continue;\n"
                + "    Parameter_t *PEx = &parameters[target->parameterIndex];\n"
                + "    int32_t v = PEx->d.frac.modvalue;\n"
                + "    v -= ___SMMUL(*oldvalue, target->amount) << 5;\n"
                + "    v += ___SMMUL(value, target->amount) << 5;\n"
                + "    PEx->d.frac.modvalue = v;\n"
                + "    if (PEx->pfunction) {\n"
                + "      (PEx->pfunction)(PEx);\n"
                + "      // TBC: modulation on root of polyphonic-subpatch-parameters\n"
                + "    }\n"
                + "    else {\n"
                + "      PEx->d.frac.finalvalue = v;\n"
                + "    }\n"
                + "  }\n"
                + "  *oldvalue = value;\n"
                + "}\n\n");

        c.append("     typedef enum { A_STEREO, A_MONO, A_BALANCED } AudioModeType;\n");
        c.append("     static AudioModeType AudioOutputMode = A_STEREO;\n");
        c.append("     static AudioModeType AudioInputMode = A_STEREO;\n");
        c.append("static void PropagateToSub(Parameter_t *origin) {\n"
                 + "      Parameter_t *p = (Parameter_t *)origin->d.frac.finalvalue;\n"
                 //                + "      LogTextMessage(\"tosub %8x\",origin->modvalue);\n"
                 + "      parameter_setVal(p,origin->d.frac.modvalue,0xFFFFFFEE);\n"                 + "}\n");

        c.append(generateStructCodePlusPlus("rootc", false, "rootc")
                + "static const int polyIndex = 0;\n"
                + generateUICode()
                + generateParamInitCode3("rootc")
                + generatePresetCode3("rootc")
                + generateModulationCode3()
                + generateInitCodePlusPlus("rootc")
                + generateDisposeCodePlusPlus("rootc")
                + generateDSPCodePlusPlus("rootc", false)
                + generateMidiCodePlusPlus("rootc")
                + generatePatchCodePlusPlus("rootc"));

        String cs = c.toString();
        cs = cs.replace("attr_poly", "1")
                .replace("attr_midichannel", Integer.toString(getDModel().getMidiChannel() - 1))
                .replace("attr_midiport", Integer.toString(getDModel().getMidiPort() - 1));


//        if (!getDModel().getMidiSelector()) {
//            cs = cs.replace("attr_mididevice", "0")
//                .replace("attr_midiport", "0");
//        }
        return cs;
    }


    public AxoObject generateAxoObjNormal(AxoObject template) {
        AxoObject ao = template;
        List<Inlet> inlets = new LinkedList<>(ao.getInlets());
        List<Outlet> outlets = new LinkedList<>(ao.getOutlets());
        List<Parameter> parentParams = new LinkedList<>(ao.getParameters());
        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("patch/inlet f")) {
                Inlet i = new InletFrac32(o.getInstanceName(), o.getInstanceName());
                i.setParent(ao);
                inlets.add(i);
            } else if (typeName.equals("patch/inlet i")) {
                Inlet i = new InletInt32(o.getInstanceName(), o.getInstanceName());
                i.setParent(ao);
                inlets.add(i);
            } else if (typeName.equals("patch/inlet b")) {
                Inlet i = new InletBool32(o.getInstanceName(), o.getInstanceName());
                i.setParent(ao);
                inlets.add(i);
            } else if (typeName.equals("patch/inlet a")) {
                Inlet i = new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName());
                i.setParent(ao);
                inlets.add(i);
            } else if (typeName.equals("patch/inlet string")) {
                Inlet i = new InletCharPtr32(o.getInstanceName(), o.getInstanceName());
                i.setParent(ao);
                inlets.add(i);
            } else if (typeName.equals("patch/outlet f")) {
                Outlet outlet = new OutletFrac32(o.getInstanceName(), o.getInstanceName());
                outlet.setParent(ao);
                outlets.add(outlet);
            } else if (typeName.equals("patch/outlet i")) {
                Outlet outlet = new OutletInt32(o.getInstanceName(), o.getInstanceName());
                outlet.setParent(ao);
                outlets.add(outlet);
            } else if (typeName.equals("patch/outlet b")) {
                Outlet outlet = new OutletBool32(o.getInstanceName(), o.getInstanceName());
                outlet.setParent(ao);
                outlets.add(outlet);
            } else if (typeName.equals("patch/outlet a")) {
                Outlet outlet = new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName());
                outlet.setParent(ao);
                outlets.add(outlet);
            } else if (typeName.equals("patch/outlet string")) {
                Outlet outlet = new OutletCharPtr32(o.getInstanceName(), o.getInstanceName());
                outlet.setParent(ao);
                outlets.add(outlet);
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                Boolean op = p.getOnParent();
                if (op != null && op == true) {
                    parentParams.add(p.createParameterForParent());
                }
            }
            ao.setInlets(inlets);
            ao.setOutlets(outlets);
            ao.setParameters(parentParams);
        }
        /* object structures */
//         ao.sCName = fnNoExtension;
        ao.setIncludes(getDModel().getIncludes());
        ao.setDepends(getDModel().getDepends());
        ao.setModules(getDModel().getModules());

        if (getDModel().getMidiSelector()) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            AxoAttribute attr_midichannel = new AxoAttributeComboBox("midichannel", uch, cch);
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7"};
            String uport[] = {"1", "2", "3", "4", "5", "6", "7", "8"};
            AxoAttribute attr_midiport = new AxoAttributeComboBox("midiport", uport, cport);
            AxoAttribute attrs[] = new AxoAttribute[]{attr_midichannel, attr_midiport};
            ao.setAttributes(Arrays.asList(attrs));
        }
        generateNormalCode(ao);
        return ao;
    }

    public void generateNormalCode(AxoObject ao) {
        StringBuilder sLocalData = new StringBuilder(
                generateStructCodePlusPlusSub("attr_parent", true)
                + "static const int polyIndex = 0;\n");
        sLocalData.append(generateParamInitCode3(""));
        sLocalData.append(generatePresetCode3(""));
        sLocalData.append(generateModulationCode3());
        ao.sLocalData = sLocalData.toString().replaceAll("attr_poly", "1");

        ao.sInitCode = generateObjInitCodePlusPlusSub("attr_parent", "this") + generateParamInitCodePlusPlusSub("attr_parent", "this") + "\nreturn 0; //(2)\n";

        ao.sDisposeCode = generateDisposeCodePlusPlusSub("attr_parent");

        StringBuilder sKRateCode = new StringBuilder("int i; /*...*/\n");
        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("patch/inlet f") || typeName.equals("patch/inlet i") || typeName.equals("patch/inlet b")) {
                sKRateCode.append("   " + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n");
            } else if (typeName.equals("patch/inlet string")) {
                sKRateCode.append("   " + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n");
            } else if (typeName.equals("patch/inlet a")) {
                sKRateCode.append("   for(i=0;i<BUFSIZE;i++) " + o.getCInstanceName() + "_i._inlet[i] = inlet_" + o.getLegalName() + "[i];\n");
            }
        }
        sKRateCode.append(generateDSPCodePlusPlusSub("attr_parent", true));
        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("patch/outlet f") || typeName.equals("patch/outlet i") || typeName.equals("patch/outlet b")) {
                sKRateCode.append("   outlet_" + o.getLegalName() + " = " + o.getCInstanceName() + "_i._outlet;\n");
            } else if (typeName.equals("patch/outlet string")) {
                sKRateCode.append("   outlet_" + o.getLegalName() + " = (char *)" + o.getCInstanceName() + "_i._outlet;\n");
            } else if (typeName.equals("patch/outlet a")) {
                sKRateCode.append("      for(i=0;i<BUFSIZE;i++) outlet_" + o.getLegalName() + "[i] = " + o.getCInstanceName() + "_i._outlet[i];\n");
            }
        }

        ao.sKRateCode = sKRateCode.toString();

        ao.sMidiCode = ""
                + "if (attr_midiport != port) return;\n"
                + generateMidiInCodePlusPlus();
    }

    public void generatePolyCode(AxoObject ao) {
        StringBuilder sLocalData = new StringBuilder(generateParamInitCode3(""));
        sLocalData.append(generatePexchAndDisplayCode());
        sLocalData.append("/* parameter instance indices */\n");
        int k = 0;
        for (ParameterInstanceView p : parameterInstances) {
            sLocalData.append("static const int PARAM_INDEX_"
                              + p.getDModel().getObjectInstance().getLegalName() + "_"
                              + p.getDModel().getLegalName() + " = " + k + ";\n");
            k++;
        }

        sLocalData.append(generatePresetCode3(""));
        sLocalData.append(generateModulationCode3());
        sLocalData.append("class voice {\n");
        sLocalData.append("   public:\n");
        sLocalData.append("   int polyIndex;\n");
        sLocalData.append(generatePexchAndDisplayCodeV());
        sLocalData.append(generateObjectCode("voice", true, "parent->common->"));
        sLocalData.append("attr_parent *common;\n");
        sLocalData.append("int init(voice *parent) {\n");
        sLocalData.append(generateObjInitCodePlusPlusSub("voice", "parent"));
        sLocalData.append("   int j;\n");
        sLocalData.append("   const int32_t *p = GetInitParams();\n");
        sLocalData.append("   for(j=0;j<" + parameterInstances.size() + ";j++){\n");
        sLocalData.append("      parameter_setVal(&PExch[j], p[j], 0xFFEE);\n");
        sLocalData.append("   }\n");
        sLocalData.append("return 0;\n");
        sLocalData.append("}\n\n");
        sLocalData.append("void dsp(void) {\n int i;\n");
        sLocalData.append(generateDSPCodePlusPlusSub("", true));
        sLocalData.append("}\n");
        sLocalData.append("void dispose(void) {\n int i;\n");
        sLocalData.append(generateDisposeCodePlusPlusSub(""));
        sLocalData.append("}\n");
        sLocalData.append(generateMidiCodePlusPlus("attr_parent"));
        sLocalData.append("};\n");
        sLocalData.append("static voice * getVoices(void){\n"
                          + "     static voice v[attr_poly];\n"
                          + "    return v;\n"
                          + "}\n");

        // FIXME
        sLocalData.append("static void PropagateToVoices(Parameter_t *origin) {\n"
                + "      Parameter_t *p = (Parameter_t *)origin->d.frac.finalvalue;\n"
                //                + "      LogTextMessage(\"tovcs %8x\",origin->modvalue);\n"
                + "      int vi;\n"
                + "      for (vi = 0; vi < attr_poly; vi++) {\n"
                + "        parameter_setVal(p,origin->d.frac.modvalue,0xFFFFFFEE);\n"
                + "          p = (Parameter_t *)((int)p + sizeof(voice)); // dirty trick...\n"
                + "      }"
                + "}\n");

        sLocalData.append("int8_t notePlaying[attr_poly];\n");
        sLocalData.append("int32_t voicePriority[attr_poly];\n");
        sLocalData.append("int32_t priority;\n");
        sLocalData.append("int32_t sustain;\n");
        sLocalData.append("int8_t pressed[attr_poly];\n");

        ao.sLocalData = sLocalData.toString();

        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationSources", "parent->common->PExModulationSources");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationPrevVal", "parent->common->PExModulationPrevVal");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->GetModulationTable", "parent->common->GetModulationTable");

        StringBuilder sInitCode = new StringBuilder(generateParamInitCodePlusPlusSub("", "parent"));

        sInitCode.append("int k;\n"
                + "   for(k=0;k<nparams;k++){\n"
                + "      PExch[k].pfunction = PropagateToVoices;\n"
                + "      PExch[k].d.frac.finalvalue = (int32_t) (&(getVoices()[0].PExch[k]));\n"
                + "   }\n");
        sInitCode.append("int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "   voice *v = &getVoices()[vi];\n"
                + "   v->polyIndex = vi;\n"
                + "   v->common = this;\n"
                + "   int r = v->init(v); if (r) return r;\n"
                + "   notePlaying[vi]=0;\n"
                + "   voicePriority[vi]=0;\n"
                + "   for (j = 0; j < v->nparams; j++) {\n"
                + "      v->PExch[j].d.frac.value = 0;\n"
                + "      v->PExch[j].d.frac.modvalue = 0;\n"
                + "   }\n"
                + "}\n"
                + "      for (k = 0; k < nparams; k++) {\n"
                + "        if (PExch[k].pfunction){\n"
                + "          (PExch[k].pfunction)(&PExch[k]);\n"
                + "        } else {\n"
                + "          PExch[k].d.frac.finalvalue = PExch[k].d.frac.value;\n"
                + "        }\n"
                + "      }\n"
                + "priority=0;\n"
                + "sustain=0;\n");

        ao.sInitCode = sInitCode.toString();

        ao.sDisposeCode = "int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "  voice *v = &getVoices()[vi];\n"
                + "  v->dispose();\n"
                + "}\n";
        StringBuilder sKRateCode = new StringBuilder();

        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("patch/outlet f") || typeName.equals("patch/outlet i")
                    || typeName.equals("patch/outlet b") || typeName.equals("patch/outlet string")) {
                sKRateCode.append("   outlet_" + o.getLegalName() + " = 0;\n");
            } else if (typeName.equals("patch/outlet a")) {
                sKRateCode.append(
                    "{\n"
                    + "      int j;\n"
                    + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] = 0;\n"
                    + "}\n");
            }
        }
        sKRateCode.append("int vi; for(vi=0;vi<attr_poly;vi++) {");

        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("inlet") || typeName.equals("inlet_i") || typeName.equals("inlet_b") || typeName.equals("inlet_")
                    || typeName.equals("patch/inlet f") || typeName.equals("patch/inlet i") || typeName.equals("patch/inlet b")) {
                sKRateCode.append("   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n");
            } else if (typeName.equals("inlet_string") || typeName.equals("patch/inlet string")) {
                sKRateCode.append("   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n");
            } else if (typeName.equals("inlet~") || typeName.equals("patch/inlet a")) {
                sKRateCode.append("{int j; for(j=0;j<BUFSIZE;j++) getVoices()[vi]." + o.getCInstanceName() + "_i._inlet[j] = inlet_" + o.getLegalName() + "[j];}\n");
            }
        }
        sKRateCode.append("getVoices()[vi].dsp();\n");
        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("outlet") || typeName.equals("patch/outlet f")
                    || typeName.equals("patch/outlet i")
                    || typeName.equals("patch/outlet b")) {
                sKRateCode.append("   outlet_" + o.getLegalName() + " += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n");
            } else if (typeName.equals("patch/outlet string")) {
                sKRateCode.append("   outlet_" + o.getLegalName() + " = (char *)getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n");
            } else if (typeName.equals("patch/outlet a")) {
                sKRateCode.append(
                    "{\n"
                    + "      int j;\n"
                    + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet[j];\n"
                    + "}\n");
            }
        }
        sKRateCode.append("}\n");
        ao.sKRateCode = sKRateCode.toString();

        ao.sMidiCode = ""
                + "if (attr_midiport != port) return;\n"
                + "if ((status == MIDI_NOTE_ON + attr_midichannel) && (data2)) {\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000+priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  getVoices()[mini].midiInHandler(this, midiMessage(port, status, data1, data2));\n"
                + "} else if (((status == MIDI_NOTE_ON + attr_midichannel) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + attr_midichannel)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if ((notePlaying[i] == data1) && pressed[i]){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "        getVoices()[i].midiInHandler(this, midiMessage(port, status, data1, data2));\n"
                + "      }\n"
                + "  }\n"
                + "} else if (status == attr_midichannel + MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) getVoices()[i].midiInHandler(this, midi_message);\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0));\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;   for(i=0;i<attr_poly;i++) getVoices()[i].midiInHandler(this, midi_message);\n"
                + "}\n";
    }

    public void generatePolyChannelCode(AxoObject o) {
        generatePolyCode(o);
        o.sLocalData
                += "int8_t voiceChannel[attr_poly];\n";
        o.sInitCode
                += "int vc;\n"
                + "for (vc=0;vc<attr_poly;vc++) {\n"
                + "   voiceChannel[vc]=0xFF;\n"
                + "   voice *v = &getVoices()[vc];\n"
                + "   int r = v->init(v); if (r) return r;\n"
                + "}\n";
        o.sMidiCode = ""
                + "if (attr_midiport != port) return;\n"
                + "int msg = (status & 0xF0);\n"
                + "int channel = (status & 0x0F);\n"
                + "if ((msg == MIDI_NOTE_ON) && (data2)) {\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000 + priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  voiceChannel[mini] = status & 0x0F;\n"
                + "  getVoices()[mini].midiInHandler(this, midiMessage(port, status & 0xF0, data1, data2));\n"
                + "} else if (((msg == MIDI_NOTE_ON) && (!data2))||\n"
                + "            (msg == MIDI_NOTE_OFF)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (notePlaying[i] == data1){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      voiceChannel[i] = 0xFF;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "         getVoices()[i].midiInHandler(this, midiMessage(port, msg + attr_midichannel, data1, data2));\n"
                + "      }\n"
                + "  }\n"
                + "} else if (msg == MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0));\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else if (msg == MIDI_PITCH_BEND) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_PITCH_BEND + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "         getVoices()[i].midiInHandler(this, midiMessage(port, msg + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "}\n";
    }


    public void generatePolyExpressionCode(AxoObject o) {
        generatePolyCode(o);
        o.sLocalData
                += "int8_t voiceChannel[attr_poly];\n"
                + "int8_t pitchbendRange;\n"
                + "int8_t lowChannel;\n"
                + "int8_t highChannel;\n"
                + "int8_t lastRPN_LSB;\n"
                + "int8_t lastRPN_MSB;\n";
        o.sInitCode
                += "int vc;\n"
                + "for (vc=0;vc<attr_poly;vc++) {\n"
                + "   voiceChannel[vc]=0xFF;\n"
                + "}\n"
                + "lowChannel = attr_midichannel + 1;\n"
                + "highChannel = attr_midichannel + ( 15 - attr_midichannel);\n"
                + "pitchbendRange = 48;\n"
                + "lastRPN_LSB=0xFF;\n"
                + "lastRPN_MSB=0xFF;\n";
        o.sMidiCode = ""
                + "if (attr_midiport != port) return;\n"
                + "int msg = (status & 0xF0);\n"
                + "int channel = (status & 0x0F);\n"
                + "if ((msg == MIDI_NOTE_ON) && (data2)) {\n"
                + "  if (channel == attr_midichannel \n"
                + "   || channel < lowChannel || channel > highChannel)\n"
                + "    return;\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000 + priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  voiceChannel[mini] = status & 0x0F;\n"
                + "  getVoices()[mini].midiInHandler(this, midiMessage(port, status & 0xF0, data1, data2));\n"
                + "} else if (((msg == MIDI_NOTE_ON) && (!data2))||\n"
                + "            (msg == MIDI_NOTE_OFF)) {\n"
                + "  if (channel == attr_midichannel\n "
                + "   || channel < lowChannel || channel > highChannel)\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (notePlaying[i] == data1 && voiceChannel[i] == channel){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      voiceChannel[i] = 0xFF;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "         getVoices()[i].midiInHandler(this, midiMessage(port, msg + attr_midichannel, data1, data2));\n"
                + "      }\n"
                + "  }\n"
                + "} else if (msg == MIDI_CONTROL_CHANGE) {\n"
                + "  if (data1 == MIDI_C_POLY) {\n" // MPE enable mode
                + "     if (data2 > 0) {\n "
                + "       if (channel == attr_midichannel) {\n"
                + "         if (channel != 15) {\n" // e.g ch 1 (g), we use 2-N notes
                + "           lowChannel = channel + 1;\n"
                + "           highChannel = lowChannel + data2 - 1;\n"
                + "         } else {\n" // ch 16, we use 16(g) 15-N notes
                + "           highChannel = channel - 1;\n"
                + "           lowChannel = highChannel + 1 - data2;\n"
                + "         }\n"
                + "         for(int i=0;i<attr_poly;i++) {\n"
                + "           getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB));\n"
                + "           getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB));\n"
                + "           getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange));\n"
                + "         }\n" //for
                + "      }\n" //if mainchannel
                + "    } else {\n" // enable/disable
                + "      lowChannel = 0;\n" //disable, we may in the future want to turn this in to normal poly mode
                + "      highChannel = 0;\n"
                + "    }\n"
                + "  }\n"// cc127
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "      getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "  if (data1 == MIDI_C_RPN_MSB || data1 == MIDI_C_RPN_LSB || data1 == MIDI_C_DATA_ENTRY) {\n"
                + "     switch(data1) {\n"
                + "         case MIDI_C_RPN_LSB: lastRPN_LSB = data2; break;\n"
                + "         case MIDI_C_RPN_MSB: lastRPN_MSB = data2; break;\n"
                + "         case MIDI_C_DATA_ENTRY: {\n"
                + "             if (lastRPN_LSB == 0 && lastRPN_MSB == 0) {\n"
                + "               for(i=0;i<attr_poly;i++) {\n"
                + "                 if (voiceChannel[i] != channel) {\n" // because already sent above
                + "                   pitchbendRange = data2;\n"
                + "                   getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB));\n"
                + "                   getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB));\n"
                + "                   getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange));\n"
                + "                 }\n" // if
                + "               }\n" //for
                + "             }\n" // if lsb/msb=0
                + "           }\n" // case 6
                + "           break;\n"
                + "         default: break;\n"
                + "     }\n" //switch
                + "  } else if (data1 == 64) {\n" //end //cc 101,100,6, cc64
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0));\n"
                + "        }\n"
                + "      }\n"
                + "    }\n" //sus=1
                + "  }\n" //cc64
                + "} else if (msg == MIDI_PITCH_BEND) {\n"
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "      getVoices()[i].midiInHandler(this, midiMessage(port, MIDI_PITCH_BEND + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "} else {" // end pb, other midi
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "         getVoices()[i].midiInHandler(this, midiMessage(port, msg + attr_midichannel, data1, data2));\n"
                + "    }\n"
                + "  }\n"
                + "}\n"; // other midi
    }

//    void ExportAxoObjPoly2(File f1) {
//        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
//    }
    // Poly voices from one (or omni) midi channel
    private AxoObject generateAxoObjPoly(AxoObject template) {
        AxoObject ao = template;
        ao.id = "unnamedobject";
        ao.setIncludes(getDModel().getIncludes());
        ao.setDepends(getDModel().getDepends());
        ao.setModules(getDModel().getModules());
        List<AxoAttribute> attrs = new LinkedList<>();
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        attrs.add(new AxoAttributeComboBox("poly", centries, centries));
        if (getDModel().getMidiSelector()) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            attrs.add(new AxoAttributeComboBox("midichannel", uch, cch));
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7"};
            String uport[] = {"1", "2", "3", "4", "5", "6", "7", "8"};
            attrs.add(new AxoAttributeComboBox("midiport", uport, cport));
        }
        ao.setAttributes(attrs);

        List<Inlet> inlets = new LinkedList<>(ao.getInlets());
        List<Outlet> outlets = new LinkedList<>(ao.getOutlets());
        List<Parameter> params = new LinkedList<>(ao.getParameters());
        for (IAxoObjectInstance o : getDModel().getObjectInstances()) {
            String typeName = o.getDModel().getId();
            if (typeName.equals("patch/inlet f")) {
                inlets.add(new InletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet i")) {
                inlets.add(new InletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet b")) {
                inlets.add(new InletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet a")) {
                inlets.add(new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet string")) {
                inlets.add(new InletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet f")) {
                outlets.add(new OutletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet i")) {
                outlets.add(new OutletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet b")) {
                outlets.add(new OutletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet a")) {
                outlets.add(new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet string")) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "string outlet impossible in poly subpatches!");
                // ao.outlets.add(new OutletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                if (p.getOnParent()) {
                    params.add(p.createParameterForParent());
                }
            }
            ao.setOutlets(outlets);
            ao.setInlets(inlets);
            ao.setParameters(params);
        }
        generatePolyCode(ao);
        return ao;
    }

    // Poly (Multi) Channel supports per Channel CC/Touch
    // all channels are independent
    private AxoObject generateAxoObjPolyChannel(AxoObject template) {
        AxoObject o = generateAxoObjPoly(template);
        generatePolyChannelCode(o);
        return o;
    }

    // Poly Expression supports the Midi Polyphonic Expression (MPE) Spec
    // Can be used with (or without) the MPE objects
    // the midi channel of the patch is the 'main/global channel'
    private AxoObject generateAxoObjPolyExpression(AxoObject template) {
        AxoObject o = generateAxoObjPoly(template);
        generatePolyExpressionCode(o);
        return o;
    }

    public AxoObject generateAxoObj(AxoObject template) {
        AxoObject ao;
        switch (getDModel().getSubPatchMode()) {
            case no:
            case normal:
                ao = generateAxoObjNormal(template);
                break;
            case polyphonic:
                ao = generateAxoObjPoly(template);
                break;
            case polychannel:
                ao = generateAxoObjPolyChannel(template);
                break;
            case polyexpression:
                ao = generateAxoObjPolyExpression(template);
                break;
            default:
                return null;
        }
        ao.setAuthor(getDModel().getAuthor());
        ao.setLicense(getDModel().getLicense());
        ao.setDescription(getDModel().getNotes());
        ao.helpPatch = getDModel().getHelpPatch();
        return ao;
    }

    public List<ParameterInstanceView> getParameterInstances() {
        return Collections.unmodifiableList(parameterInstances);
    }

    public List<DisplayInstanceView> getDisplayInstances() {
        return Collections.unmodifiableList(displayInstances);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

}
