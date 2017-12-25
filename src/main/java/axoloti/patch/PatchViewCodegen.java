package axoloti.patch;

import axoloti.Modulation;
import axoloti.Modulator;
import axoloti.abstractui.INetView;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.object.inlet.InletInt32;
import axoloti.mvc.View;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.object.AxoObject;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.codegen.patch.object.AxoObjectInstanceCodegenViewFactory;
import axoloti.codegen.patch.object.IAxoObjectInstanceCodegenView;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletCharPtr32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.object.outlet.OutletInt32;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.target.TargetController;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class PatchViewCodegen extends View<PatchController> {
   
    List<IAxoObjectInstanceCodegenView> objectInstanceViews;
    List<INetView> netViews;

    final ArrayList<ParameterInstance> ParameterInstances;    
    final ArrayList<DisplayInstance> DisplayInstances;
    final int displayDataLength;
    
    public PatchViewCodegen(PatchController controller) {
        super(controller);
        objectInstanceViews = new ArrayList<>();
        for(ObjectInstanceController c: controller.objectInstanceControllers) {
            IAxoObjectInstanceCodegenView o = AxoObjectInstanceCodegenViewFactory.createView(c);
            objectInstanceViews.add(o);
        }

        int i = 0;        
        ParameterInstances = new ArrayList<ParameterInstance>();
        for (IAxoObjectInstance o : controller.getModel().objectinstances) {
            for (ParameterInstance p : o.getParameterInstances()) {
                p.setIndex(i);
                i++;
                ParameterInstances.add(p);
            }
        }
        int offset = 0;
        i = 0;
        DisplayInstances = new ArrayList<DisplayInstance>();
        for (IAxoObjectInstance o : controller.getModel().objectinstances) {
            for (DisplayInstance p : o.getDisplayInstances()) {
                p.setOffset(offset);
                p.setIndex(i);
                int l = p.getLength();
                offset += l;
                i++;
                DisplayInstances.add(p);
            }
        }
        displayDataLength = offset;        
    }

    private PatchModel getModel() {
        return getController().getModel();
    }

    public String generateIncludes() {
        String inc = "";
        Set<String> includes = getModel().getIncludes();
        for (String s : includes) {
            if (s.startsWith("\"")) {
                inc += "#include " + s + "\n";
            } else {
                inc += "#include \"" + s + "\"\n";
            }
        }
        return inc;
    }

    public String generateModules() {
        String inc = "";
        Set<String> modules = getModel().getModules();
        for (String s : modules) {
            inc += "#include \"" + s + "_wrapper.h\"\n";
        }
        return inc;
    }


    /* the c++ code generator */
    String GeneratePexchAndDisplayCode() {
        String c = GeneratePexchAndDisplayCodeV();
        c += "    int32_t PExModulationPrevVal[attr_poly][NMODULATIONSOURCES];\n";
        return c;
    }

    String GeneratePexchAndDisplayCodeV() {
        String c = "";
        c += "    static const uint32_t nparams = " + ParameterInstances.size() + ";\n";
        c += "    Parameter_t params[nparams] = {\n";
        for (ParameterInstance param : ParameterInstances) {
            c += param.GenerateParameterInitializer();
        }
        c += "};\n";
        c += "    Parameter_name_t param_names[nparams] = {\n";
        for (ParameterInstance param : ParameterInstances) {
            c += "{ name : " + CodeGeneration.CPPCharArrayStaticInitializer(param.GetUserParameterName(), CodeGeneration.param_name_length) + "},\n";
        }
        c += "};\n";
        c += "    int32_t displayVector[" + displayDataLength + "];\n";

        c += "    static const uint32_t ndisplay_metas = " + DisplayInstances.size() + ";\n";
        c += "    Display_meta_t display_metas[ndisplay_metas] = {\n";
        for (DisplayInstance disp : DisplayInstances) {
            c += disp.GenerateDisplayMetaInitializer();
        }
        c += "};\n";
        c += "    static const uint32_t NPRESETS = " + getModel().getNPresets() + ";\n";
        c += "    static const uint32_t NPRESET_ENTRIES = " + getModel().getNPresetEntries() + ";\n";
        c += "    static const uint32_t NMODULATIONSOURCES = " + getModel().getNModulationSources() + ";\n";
        c += "    static const uint32_t NMODULATIONTARGETS = " + getModel().getNModulationTargetsPerSource() + ";\n";
        return c;
    }    

    String GenerateObjectCode(String classname, boolean enableOnParent, String OnParentAccess) {
        String c = "";
        {
            c += "/* modsource defines */\n";
            int k = 0;
            for (Modulator m : getModel().Modulators) {
                c += "static const int " + m.getCName() + " = " + k + ";\n";
                k++;
            }
        }
        {
            c += "/* parameter instance indices */\n";
            int k = 0;
            for (ParameterInstance p : ParameterInstances) {
                c += "static const int PARAM_INDEX_" + p.getObjectInstance().getLegalName() + "_" + p.getLegalName() + " = " + k + ";\n";
                k++;
            }
        }

// FIXME: enable "controller object" code generation
//        c += "/* controller classes */\n";
//        if (getModel().controllerObjectInstance != null) {
//            c += getModel().controllerObjectInstance.GenerateClass(classname, OnParentAccess, enableOnParent);
//        }

        c += "/* object classes */\n";
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c += o.GenerateClass(classname, OnParentAccess, enableOnParent);
        }
// FIXME (2): enable "controller object" code generation
//        c += "/* controller instances */\n";
//        if (getModel().controllerObjectInstance != null) {
//            String s = getModel().controllerObjectInstance.getCInstanceName();
//            if (!s.isEmpty()) {
//                c += "     " + s + " " + s + "_i;\n";
//            }
//        }

        c += "/* object instances */\n";
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            String s = o.getModel().getCInstanceName();
            if (!s.isEmpty()) {
                c += "     " + s + " " + s + "_i;\n";
            }
        }
        c += "/* net latches */\n";
        for (NetController n : getController().netControllers) {
            // check if net has multiple sources
            if ((n.getModel().CType() != null) && n.NeedsLatch()) {
                c += "    " + n.getModel().CType() + " " + n.CName() + "Latch" + ";\n";
            }
        }
        return c;
    }
    
    public String GenerateStructCodePlusPlusSub(String classname, boolean enableOnParent) {
        String c = "";
        c += GeneratePexchAndDisplayCode();
        c += GenerateObjectCode(classname, enableOnParent, "parent->");
        return c;
    }    
    
    String GenerateStructCodePlusPlus(String classname, boolean enableOnParent, String parentclassname) {
        String c = "";
        c += "class " + classname + "{\n";
        c += "   public:\n";
        c += GenerateStructCodePlusPlusSub(parentclassname, enableOnParent);
        return c;
    }
    
    String GenerateUICode() {
        int count[] = new int[]{0};
        String c = "";
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c += o.GenerateUICode(count);
        }
        c = "static const int n_ui_objects = " + count[0] + ";\n"
                + "ui_object_t ui_objects[n_ui_objects] = {\n" + c + "};\n";
        return c;
    }


    public String GenerateParamInitCode3(String ClassName) {
        int s = ParameterInstances.size();
        String c = "   static int32_t * GetInitParams(void){\n"
                + "      static const int32_t p[" + s + "]= {\n";
        for (int i = 0; i < s; i++) {
            c += "      " + 0; //FIXME ParameterInstances.get(i).GetValueRaw();
            if (i != s - 1) {
                c += ",\n";
            } else {
                c += "\n";
            }
        }
        c += "      };\n"
                + "      return (int32_t *)&p[0];\n"
                + "   }";
        return c;
    }
    

    public String GeneratePresetCode3(String ClassName) {
        String c = "   static const int32_t * GetPresets(void){\n";
        c += "      static const int32_t p[NPRESETS][NPRESET_ENTRIES][2] = {\n";
        for (int i = 0; i < getModel().getNPresets(); i++) {
//            c += "// preset " + i + "\n";
//            c += "pp = (int*)(&Presets[" + i + "]);\n";
            int[] dp = getModel().DistillPreset(i + 1);
            c += "         {\n";
            for (int j = 0; j < getModel().getNPresetEntries(); j++) {
                c += "           {" + dp[j * 2] + "," + dp[j * 2 + 1] + "}";
                if (j != getModel().getNPresetEntries() - 1) {
                    c += ",\n";
                } else {
                    c += "\n";
                }
            }
            if (i != getModel().getNPresets() - 1) {
                c += "         },\n";
            } else {
                c += "         }\n";
            }
        }
        c += "      };\n";
        c += "   return &p[0][0][0];\n";
        c += "   };\n";

        c += "void ApplyPreset(int index){\n"
                + "   if (!index) {\n"
                + "     int i;\n"
                + "     int32_t *p = GetInitParams();\n"
                + "     for(i=0;i<nparams;i++){\n"
                + "        ParameterChange(&params[i], p[i], 0xFFEF);\n"
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
                + "            ParameterChange(&params[pp->pexIndex],pp->value,0xFFEF);"
                + "         }\n"
                + "         else break;\n"
                + "       }\n"
                + "   }\n"
                + "}\n";
        return c;
    }
    
    public String GenerateModulationCode3() {
        String s = "   static PExModulationTarget_t * GetModulationTable(void){\n";
        s += "    static const PExModulationTarget_t PExModulationSources[NMODULATIONSOURCES][NMODULATIONTARGETS] = \n";
        s += "{";
        for (int i = 0; i < getModel().getNModulationSources(); i++) {
            s += "{";
            if (i < getModel().Modulators.size()) {
                Modulator m = getModel().Modulators.get(i);
                for (int j = 0; j < getModel().getNModulationTargetsPerSource(); j++) {
                    if (j < m.Modulations.size()) {
                        Modulation n = m.Modulations.get(j);
                        ParameterInstance destination = n.destination;
                        s += "{" + destination.indexName() + ", " + destination.valToInt32(n.getValue()) + "}";
                    } else {
                        s += "{-1,0}";
                    }
                    if (j != getModel().getNModulationTargetsPerSource() - 1) {
                        s += ",";
                    } else {
                        s += "}";
                    }
                }
            } else {
                for (int j = 0; j < getModel().getNModulationTargetsPerSource() - 1; j++) {
                    s += "{-1,0},";
                }
                s += "{-1,0}}";
            }
            if (i != getModel().getNModulationSources() - 1) {
                s += ",\n";
            }
        }
        s += "};\n";
        s += "   return (PExModulationTarget_t *)&PExModulationSources[0][0];\n";
        s += "   };\n";

        return s;
    }


    public String GenerateObjInitCodePlusPlusSub(String className, String parentReference) {
        String c = "";
        if (getModel().controllerObjectInstance != null) {
            String s = getModel().controllerObjectInstance.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + s + "_i.Init(" + parentReference;
                for (DisplayInstance i : getModel().controllerObjectInstance.getDisplayInstances()) {
                    if (i.getModel().getLength() > 0) {
                        c += ", ";
                        c += i.valueName("");
                    }
                }
                c += " );\n";
            }
        }

        for (IAxoObjectInstance o : getModel().objectinstances) {
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + o.getCInstanceName() + "_i.Init(" + parentReference;
                for (DisplayInstance i : o.getDisplayInstances()) {
                    if (i.getModel().getLength() > 0) {
                        c += ", ";
                        c += i.valueName("");
                    }
                }
                c += " );\n";
            }
        }
        /* // no need for this?
        c += "      int k;\n"
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
                + "      }\n";
         */
        return c;
    }

    public String GenerateParamInitCodePlusPlusSub(String className, String parentReference) {
        String c = "// GenerateParamInitCodePlusPlusSub\n";
        c += "   int i;\n";
        c += "   int j;\n";
        c += "   const int32_t *p;\n";
        c += "   p = GetInitParams();\n";
        c += "   for(j=0;j<" + ParameterInstances.size() + ";j++){\n";
        c += "      Parameter_t *param = &params[j];\n";
        c += "      if (param->pfunction)\n";
        c += "         (param->pfunction)(param);\n";
        c += "      else\n";
        c += "         param->d.frac.finalvalue = param->d.frac.modvalue;\n";
        c += "   }\n";
        c += "   int32_t *pp = &PExModulationPrevVal[0][0];\n";
        c += "   for(j=0;j<attr_poly*NMODULATIONSOURCES;j++){\n";
        c += "      *pp = 0; pp++;\n";
        c += "   }\n";
        return c;
    }

    String GenerateInitCodePlusPlus(String className) {
        String c = "";
        c += "/* init */\n";
        c += "void Init() {\n";
        c += GenerateObjInitCodePlusPlusSub("", "this");
        c += GenerateParamInitCodePlusPlusSub("", "this");
        c += "}\n\n";
        return c;
    }

    
    public String GenerateDisposeCodePlusPlusSub(String className) {
        // reverse order
        String c = "";
        int l = getModel().objectinstances.size();
        for (int i = l - 1; i >= 0; i--) {
            IAxoObjectInstance o = getModel().objectinstances.get(i);
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + o.getCInstanceName() + "_i.Dispose();\n";
            }
        }
        if (getModel().controllerObjectInstance != null) {
            String s = getModel().controllerObjectInstance.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + getModel().controllerObjectInstance.getCInstanceName() + "_i.Dispose();\n";
            }
        }

        return c;
    }

    String GenerateDisposeCodePlusPlus(String className) {
        String c = "";
        c += "/* dispose */\n";
        c += "void Dispose() {\n";
        c += GenerateDisposeCodePlusPlusSub(className);
        c += "}\n\n";
        return c;
    }
    
    public String GenerateDSPCodePlusPlusSub(String ClassName, boolean enableOnParent) {
        String c = "";
        c += "//--------- <nets> -----------//\n";
        for (NetController n : getController().netControllers) {
            if (n.getModel().CType() != null) {
                c += "    " + n.getModel().CType() + " " + n.CName() + ";\n";
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Net has no data type!");
            }
        }
        c += "//--------- </nets> ----------//\n";
        c += "//--------- <zero> ----------//\n";
        c += "  int32_t UNCONNECTED_OUTPUT;\n";
        c += "  static const int32_t UNCONNECTED_INPUT=0;\n";
        c += "  static const int32buffer zerobuffer = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};\n";
        c += "  int32buffer UNCONNECTED_OUTPUT_BUFFER;\n";
        c += "//--------- </zero> ----------//\n";

        c += "//--------- <controller calls> ----------//\n";
        if (getModel().controllerObjectInstance != null) {
            c += GenerateDSPCodePlusPlusSubObj(getModel().controllerObjectInstance, ClassName, enableOnParent);
        }
        c += "//--------- <object calls> ----------//\n";
        for (IAxoObjectInstance o : getModel().objectinstances) {
            c += GenerateDSPCodePlusPlusSubObj(o, ClassName, enableOnParent);
        }
        c += "//--------- </object calls> ----------//\n";

        c += "//--------- <net latch copy> ----------//\n";
        for (NetController nc : getController().netControllers) {
            // check if net has multiple sources
            if (nc.NeedsLatch()) {
                if (nc.getModel().getDataType() != null) {
                    c += nc.getModel().getDataType().GenerateCopyCode(nc.CName() + "Latch", nc.CName());
                } else {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Only inlets connected on net!");
                }
            }
        }
        c += "//--------- </net latch copy> ----------//\n";
        return c;
    }

    String GenerateDSPCodePlusPlusSubObj(IAxoObjectInstance o, String ClassName, boolean enableOnParent) {
        String c = "";
        String s = o.getCInstanceName();
        if (s.isEmpty()) {
            return c;
        }
        c += "  " + o.getCInstanceName() + "_i.dsp( this ";
//            c += "  " + o.GenerateDoFunctionName() + "(this";
        boolean needsComma = true;
        for (InletInstance i : o.getInletInstances()) {
            if (needsComma) {
                c += ", ";
            }
            NetController nc = getController().getNetFromIolet(i);
            if ((nc != null) && (nc.getModel().isValidNet())) {
                Net n = nc.getModel();
                OutletInstance firstSource = java.util.Collections.min(Arrays.asList(n.getSources()));
                if (i.getDataType().equals(n.getDataType())) {
                    if (nc.NeedsLatch()
                            && (getModel().objectinstances.indexOf(firstSource.getObjectInstance()) >= getModel().objectinstances.indexOf(o))) {
                        c += nc.CName() + "Latch";
                    } else {
                        c += nc.CName();
                    }
                } else if (nc.NeedsLatch()
                        && (getModel().objectinstances.indexOf(firstSource) >= getModel().objectinstances.indexOf(o))) {
                    c += n.getDataType().GenerateConversionToType(i.getDataType(), nc.CName() + "Latch");
                } else {
                    c += n.getDataType().GenerateConversionToType(i.getDataType(), nc.CName());
                }
            } else if (nc == null) { // unconnected input
                c += i.getDataType().GenerateSetDefaultValueCode();
            } else if (!nc.getModel().isValidNet()) {
                c += i.getDataType().GenerateSetDefaultValueCode();
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Patch contains invalid net! {0}", i.getObjectInstance().getInstanceName() + ":" + i.getName());
            }
            needsComma = true;
        }
        for (OutletInstance i : o.getOutletInstances()) {
            if (needsComma) {
                c += ", ";
            }
            NetController nc = getController().getNetFromIolet(i);
            if ((nc != null) && nc.getModel().isValidNet()) {
                if (nc.IsFirstOutlet(i)) {
                    c += nc.CName();
                } else {
                    c += nc.CName() + "+";
                }
            } else {
                c += i.getDataType().UnconnectedSink();
            }
            needsComma = true;
        }
        for (ParameterInstance i : o.getParameterInstances()) {
            if (i.parameter.PropagateToChild == null) {
                if (needsComma) {
                    c += ", ";
                }
                c += i.variableName("", false);
                needsComma = true;
            }
        }
        for (DisplayInstance i : o.getDisplayInstances()) {
            if (i.getModel().getLength() > 0) {
                if (needsComma) {
                    c += ", ";
                }
                c += i.valueName("");
                needsComma = true;
            }
        }
        c += ");\n";
        return c;
    }
    
    String GenerateDSPCodePlusPlus(String ClassName, boolean enableOnParent) {
        String c;
        c = "/* krate */\n";
        c += "void dsp (void) {\n";
        c += "  int i;\n";
        c += "  for(i=0;i<BUFSIZE;i++) AudioOutputLeft[i]=0;\n";
        c += "  for(i=0;i<BUFSIZE;i++) AudioOutputRight[i]=0;\n";
        c += GenerateDSPCodePlusPlusSub(ClassName, enableOnParent);
        c += "}\n\n";
        return c;
    }    

    public String GenerateMidiInCodePlusPlus() {
        String c = "";
        // fixme: enable controller object
//        if (getModel().controllerObjectInstance != null) {
//            c += getModel().controllerObjectInstance.GenerateCallMidiHandler();
//        }
        for (IAxoObjectInstanceCodegenView o : objectInstanceViews) {
            c += o.GenerateCallMidiHandler();
        }
        return c;
    }


    String GenerateMidiCodePlusPlus(String ClassName) {
        String c = "";
        c += "void MidiInHandler(" + ClassName + " *parent, midi_device_t dev, uint8_t port,uint8_t status, uint8_t data1, uint8_t data2){\n";
        c += GenerateMidiInCodePlusPlus();
        c += "}\n\n";
        return c;
    }


    String GeneratePatchCodePlusPlus(String ClassName) {
        String c = "";
        c += "};\n\n";
        c += "static rootc root;\n";

        c += "void PatchProcess( int32_t * inbuf, int32_t * outbuf) {\n"
                + "  int i;\n"
                + "  for(i=0;i<BUFSIZE;i++){\n"
                + "    AudioInputLeft[i] = inbuf[i*2]>>4;\n"
                + "    switch(AudioInputMode) {\n"
                + "       case A_MONO:\n"
                + "             AudioInputRight[i] = AudioInputLeft[i];break;\n"
                + "       case A_BALANCED:\n"
                + "             AudioInputLeft[i] = (AudioInputLeft[i] - (inbuf[i*2+1]>>4) ) >> 1;\n"
                + "             AudioInputRight[i] = AudioInputLeft[i];"
                + "             break;\n"
                + "       case A_STEREO:\n"
                + "       default:\n"
                + "             AudioInputRight[i] = inbuf[i*2+1]>>4;\n"
                + "     }\n"
                + "  }\n"
                + "  root.dsp();\n";
        if (true /*TBC: review: getModel().getSaturate()*/) {
            c += "  for(i=0;i<BUFSIZE;i++){\n"
                    + "    outbuf[i*2] = __SSAT(AudioOutputLeft[i],28)<<4;\n"
                    + "    switch(AudioOutputMode) {\n"
                    + "       case A_MONO:\n"
                    + "             outbuf[i*2+1] = 0;break;\n"
                    + "       case A_BALANCED:\n"
                    + "             outbuf[i*2+1] = ~ outbuf[i*2];break;\n"
                    + "       case A_STEREO:\n"
                    + "       default:\n"
                    + "             outbuf[i*2+1] = __SSAT(AudioOutputRight[i],28)<<4;\n"
                    + "     }\n"
                    + "  }\n";
        } else {
            c += "  for(i=0;i<BUFSIZE;i++){\n"
                    + "    outbuf[i*2] = AudioOutputLeft[i];\n"
                    + "    switch(AudioOutputMode) {\n"
                    + "       case A_MONO:\n"
                    + "             outbuf[i*2+1] = 0;break;\n"
                    + "       case A_BALANCED:\n"
                    + "             outbuf[i*2+1] = ~ outbuf[i*2];break;\n"
                    + "       case A_STEREO:\n"
                    + "       default:\n"
                    + "             outbuf[i*2+1] = AudioOutputRight[i];\n"
                    + "     }\n"
                    + "  }\n";
        }
        c += "}\n\n";

        c += "void ApplyPreset(int32_t i) {\n"
                + "   root.ApplyPreset(i);\n"
                + "}\n\n";

        c += "void PatchMidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2){\n"
                + "  root.MidiInHandler(&root, dev, port, status, data1, data2);\n"
                + "}\n\n";

        c += "typedef void (*funcp_t)(void);\n"
                + "typedef funcp_t * funcpp_t;\n"
                + "extern funcp_t __ctor_array_start;\n"
                + "extern funcp_t __ctor_array_end;"
                + "extern funcp_t __dtor_array_start;\n"
                + "extern funcp_t __dtor_array_end;";

        c += "void PatchDispose( ) {\n"
                + "  root.Dispose();\n"
                + "  {\n"
                + "    funcpp_t fpp = &__dtor_array_start;\n"
                + "    while (fpp < &__dtor_array_end) {\n"
                + "      (*fpp)();\n"
                + "      fpp++;\n"
                + "    }\n"
                + "  }\n"
                + "}\n\n";

        c += "void xpatch_init2(int32_t fwid)\n"
                + "{\n"
                + "  if (fwid != 0x" + TargetController.getTargetController().getModel().getFirmwareLinkID() + ") {\n"
                + "    return;"
                + "  }\n"
                + "  extern uint32_t _pbss_start;\n"
                + "  extern uint32_t _pbss_end;\n"
                + "  volatile uint32_t *p;\n"
                + "  for(p=&_pbss_start;p<&_pbss_end;p++) *p=0;\n"
                + "  {\n"
                + "    funcpp_t fpp = &__ctor_array_start;\n"
                + "    while (fpp < &__ctor_array_end) {\n"
                + "      (*fpp)();\n"
                + "      fpp++;\n"
                + "    }\n"
                + "  }\n"
                + "  extern char _sdram_dyn_start;\n"
                + "  extern char _sdram_dyn_end;\n"
                + "  sdram_init(&_sdram_dyn_start,&_sdram_dyn_end);\n"
                + "  root.Init();\n"
                + "}\n\n";
        
        c += "#define fourcc_patch_root FOURCC('A','X','P','T')\n"
                + "typedef struct {\n"
                + "	chunk_header_t header;\n"
                + "	chunk_patch_meta_t patch_meta;\n"
                + "	chunk_patch_preset_t patch_preset;\n"
                + "	chunk_patch_parameter_t patch_parameter;\n"
                + "	chunk_patch_ui_objects_t patch_ui_objects;\n"
                + "	chunk_patch_initpreset_t patch_initpreset;\n"
                + "	chunk_patch_display_t patch_display;\n"
                + "	chunk_patch_display_meta_t patch_display_meta;\n"
                + "	chunk_patch_functions_t patch_functions;\n"
                + "} chunk_patch_root_t;\n"
                + "\n"
                + "chunk_patch_root_t patch_root_chunk = {\n"
                + "		header : CHUNK_HEADER(patch_root),\n"
                + "		patch_meta : {\n"
                + "			header : CHUNK_HEADER(patch_meta),\n"
                + "			patchID : " + getModel().GetIID() + ",\n"
                + "			patchname : {'p','a','t','c','h'}\n"
                + "		},\n"
                + "		patch_preset : {\n"
                + "			header : CHUNK_HEADER(patch_preset),\n"
                + "			npresets : " + getModel().getNPresets() + ",\n"
                + "			npreset_entries : " + getModel().getNPresetEntries() + ",\n"
                + "			pPresets : 0\n"
                + "		},\n"
                + "		patch_parameter : {\n"
                + "			header : CHUNK_HEADER(patch_parameter),\n"
                + "			nparams : " + ParameterInstances.size() + ",\n"
                + "			pParams : &root.params[0],\n"
                + "			pParam_names : root.param_names\n"
                + "		},\n"
                + "		patch_ui_objects : {\n"
                + "			header : CHUNK_HEADER(patch_ui_objects),\n"
                + "                     nobjects : root.n_ui_objects,\n"
                + "			pObjects : root.ui_objects,\n"
                + "		},\n"
                + "		patch_initpreset : {\n"
                + "			header : CHUNK_HEADER(patch_initpreset),\n"
                + "		},\n"
                + "		patch_display : {\n"
                + "			header : CHUNK_HEADER(patch_display),\n"
                + "                     ndisplayVector : " + displayDataLength + ",\n"
                + "                     pDisplayVector : root.displayVector,\n"
                + "		},\n"
                + "		patch_display_meta : {\n"
                + "			header : CHUNK_HEADER(patch_display_meta),\n"
                + "			ndisplay_metas : root.ndisplay_metas,\n"
                + "			pDisplay_metas : &root.display_metas[0]\n"
                + "		},\n"
                + "		patch_functions : {\n"
                + "			header : CHUNK_HEADER(patch_functions),\n"
                + "					fptr_patch_init: xpatch_init2,\n"
                + "					fptr_patch_dispose: PatchDispose,\n"
                + "					fptr_dsp_process: PatchProcess,\n"
                + "					fptr_MidiInHandler: PatchMidiInHandler,\n"
                + "					fptr_applyPreset: ApplyPreset,\n"
                + "		},\n"
                + "};\n";
        
        return c;
    }
    
    
    String GenerateCode4() {
        String c = "";

        Set<String> moduleSet = getModel().getModules();
        if (moduleSet != null) {
            String modules = "";
            String moduleDirs = "";
            for (String m : moduleSet) {
                modules += m + " ";
                moduleDirs
                        += getModel().getModuleDir(m)
                        + " ";
            }
            c += "//$MODULES=" + modules + "\n";
            c += "//$MODULE_DIRS=" + moduleDirs + "\n";
        }

        c += generateIncludes();
        c += "\n";
        c += generateModules();
        c += "\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-variable\"\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-parameter\"\n";
        if (true == false) {
            c += "#define MIDICHANNEL 0 // DEPRECATED!\n";
        } else {
            c += "#define MIDICHANNEL " + (getModel().getMidiChannel() - 1) + " // DEPRECATED!\n";
        }
        c += "void xpatch_init2(int32_t fwid);\n"
                + "extern \"C\" __attribute__ ((section(\".boot\"))) void xpatch_init(int32_t fwid){\n"
                + "  xpatch_init2(fwid);\n"
                + "}\n\n";

        c += "void PatchMidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2);\n\n";

        c += "     int32buffer AudioInputLeft;\n";
        c += "     int32buffer AudioInputRight;\n";
        c += "     int32buffer AudioOutputLeft;\n";
        c += "     int32buffer AudioOutputRight;\n";
        c += "     typedef enum { A_STEREO, A_MONO, A_BALANCED } AudioModeType;\n";
        c += "     AudioModeType AudioOutputMode = A_STEREO;\n";
        c += "     AudioModeType AudioInputMode = A_STEREO;\n";
        c += "static void PropagateToSub(Parameter_t *origin) {\n"
                + "      Parameter_t *p = (Parameter_t *)origin->d.frac.finalvalue;\n"
                //                + "      LogTextMessage(\"tosub %8x\",origin->modvalue);\n"
                + "      ParameterChange(p,origin->d.frac.modvalue,0xFFFFFFEE);\n"
                + "}\n";

        c += GenerateStructCodePlusPlus("rootc", false, "rootc")
                + "static const int polyIndex = 0;\n"
                + GenerateUICode()
                + GenerateParamInitCode3("rootc")
                + GeneratePresetCode3("rootc")
                + GenerateModulationCode3()
                + GenerateInitCodePlusPlus("rootc")
                + GenerateDisposeCodePlusPlus("rootc")
                + GenerateDSPCodePlusPlus("rootc", false)
                + GenerateMidiCodePlusPlus("rootc")
                + GeneratePatchCodePlusPlus("rootc");

        c = c.replace("attr_poly", "1");

        c = c.replace("attr_midichannel", Integer.toString(getModel().getMidiChannel() - 1));

        if (!getModel().getMidiSelector()) {
            c = c.replace("attr_mididevice", "0");
            c = c.replace("attr_midiport", "0");
        }
        return c;
    }


    public AxoObject GenerateAxoObjNormal(AxoObject template) {
        AxoObject ao = template;
        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("patch/inlet f")) {
                ao.inlets.add(new InletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet i")) {
                ao.inlets.add(new InletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet b")) {
                ao.inlets.add(new InletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet a")) {
                ao.inlets.add(new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet string")) {
                ao.inlets.add(new InletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet f")) {
                ao.outlets.add(new OutletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet i")) {
                ao.outlets.add(new OutletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet b")) {
                ao.outlets.add(new OutletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet a")) {
                ao.outlets.add(new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet string")) {
                ao.outlets.add(new OutletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                Boolean op = p.getOnParent();
                if (op!=null && op == true) {
                    ao.params.add(p.createParameterForParent());
                }
            }
        }
        /* object structures */
//         ao.sCName = fnNoExtension;
        ao.includes = getModel().getIncludes();
        ao.depends = getModel().getDepends();
        ao.modules = getModel().getModules();
        if ((getModel().notes != null) && (!getModel().notes.isEmpty())) {
            ao.setDescription(getModel().notes);
        } else {
            ao.setDescription("no description");
        }

        if (getModel().getMidiSelector()) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midichannel", uch, cch));
            // use a cut down list of those currently supported
            String cdev[] = {"0", "1", "2", "3", "15"};
            String udev[] = {"omni", "din", "usb device", "usb host", "internal"};
            ao.attributes.add(new AxoAttributeComboBox("mididevice", udev, cdev));
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            String uport[] = {"omni", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midiport", uport, cport));
        }
        GenerateNormalCode(ao);
        return ao;
    }
    
    public void GenerateNormalCode(AxoObject ao){
        ao.sLocalData = GenerateStructCodePlusPlusSub("attr_parent", true)
                + "static const int polyIndex = 0;\n";
        ao.sLocalData += GenerateParamInitCode3("");
        ao.sLocalData += GeneratePresetCode3("");
        ao.sLocalData += GenerateModulationCode3();
        ao.sLocalData = ao.sLocalData.replaceAll("attr_poly", "1");
        ao.sInitCode = GenerateParamInitCodePlusPlusSub("attr_parent", "this");
        ao.sInitCode += GenerateObjInitCodePlusPlusSub("attr_parent", "this");
        ao.sDisposeCode = GenerateDisposeCodePlusPlusSub("attr_parent");
        ao.sKRateCode = "int i; /*...*/\n";
        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("patch/inlet f") || typeName.equals("patch/inlet i") || typeName.equals("patch/inlet b")) {
                ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n";
            } else if (typeName.equals("patch/inlet string")) {
                ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n";
            } else if (typeName.equals("patch/inlet a")) {
                ao.sKRateCode += "   for(i=0;i<BUFSIZE;i++) " + o.getCInstanceName() + "_i._inlet[i] = inlet_" + o.getLegalName() + "[i];\n";
            }

        }
        ao.sKRateCode += GenerateDSPCodePlusPlusSub("attr_parent", true);
        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("patch/outlet f") || typeName.equals("patch/outlet i") || typeName.equals("patch/outlet b")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = " + o.getCInstanceName() + "_i._outlet;\n";
            } else if (typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = (char *)" + o.getCInstanceName() + "_i._outlet;\n";
            } else if (typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "      for(i=0;i<BUFSIZE;i++) outlet_" + o.getLegalName() + "[i] = " + o.getCInstanceName() + "_i._outlet[i];\n";
            }
        }

        ao.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
                + GenerateMidiInCodePlusPlus();
    
    }
    
    public void GeneratePolyCode(AxoObject ao) {
        ao.sLocalData = GenerateParamInitCode3("");
        ao.sLocalData += GeneratePexchAndDisplayCode();
        ao.sLocalData += "/* parameter instance indices */\n";
        int k = 0;
        for (ParameterInstance p : ParameterInstances) {
            ao.sLocalData += "static const int PARAM_INDEX_" + p.getObjectInstance().getLegalName() + "_" + p.getLegalName() + " = " + k + ";\n";
            k++;
        }

        ao.sLocalData += GeneratePresetCode3("");
        ao.sLocalData += GenerateModulationCode3();
        ao.sLocalData += "class voice {\n";
        ao.sLocalData += "   public:\n";
        ao.sLocalData += "   int polyIndex;\n";
        ao.sLocalData += GeneratePexchAndDisplayCodeV();
        ao.sLocalData += GenerateObjectCode("voice", true, "parent->common->");
        ao.sLocalData += "attr_parent *common;\n";
        ao.sLocalData += "void Init(voice *parent) {\n";
        ao.sLocalData += GenerateObjInitCodePlusPlusSub("voice", "parent");
        ao.sLocalData += "}\n\n";
        ao.sLocalData += "void dsp(void) {\n int i;\n";
        ao.sLocalData += GenerateDSPCodePlusPlusSub("", true);
        ao.sLocalData += "}\n";
        ao.sLocalData += "void dispose(void) {\n int i;\n";
        ao.sLocalData += GenerateDisposeCodePlusPlusSub("");
        ao.sLocalData += "}\n";
        ao.sLocalData += GenerateMidiCodePlusPlus("attr_parent");
        ao.sLocalData += "};\n";
        ao.sLocalData += "static voice * getVoices(void){\n"
                + "     static voice v[attr_poly];\n"
                + "    return v;\n"
                + "}\n";

        // FIXME
        ao.sLocalData += "static void PropagateToVoices(Parameter_t *origin) {\n"
                + "      Parameter_t *p = (Parameter_t *)origin->d.frac.finalvalue;\n"
                //                + "      LogTextMessage(\"tovcs %8x\",origin->modvalue);\n"
                + "      int vi;\n"
                + "      for (vi = 0; vi < attr_poly; vi++) {\n"
                + "        ParameterChange(p,origin->d.frac.modvalue,0xFFFFFFEE);\n"
                + "          p = (Parameter_t *)((int)p + sizeof(voice)); // dirty trick...\n"
                + "      }"
                + "}\n";

        ao.sLocalData += "int8_t notePlaying[attr_poly];\n";
        ao.sLocalData += "int32_t voicePriority[attr_poly];\n";
        ao.sLocalData += "int32_t priority;\n";
        ao.sLocalData += "int32_t sustain;\n";
        ao.sLocalData += "int8_t pressed[attr_poly];\n";

        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationSources", "parent->common->PExModulationSources");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationPrevVal", "parent->common->PExModulationPrevVal");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->GetModulationTable", "parent->common->GetModulationTable");

        ao.sInitCode = GenerateParamInitCodePlusPlusSub("", "parent");
        ao.sInitCode += "int k;\n"
                + "   for(k=0;k<nparams;k++){\n"
                + "      params[k].pfunction = PropagateToVoices;\n"
                + "      params[k].d.frac.finalvalue = (int32_t) (&(getVoices()[0].params[k]));\n"
                + "   }\n";
        ao.sInitCode += "int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "   voice *v = &getVoices()[vi];\n"
                + "   v->polyIndex = vi;\n"
                + "   v->common = this;\n"
                + "   v->Init(&getVoices()[vi]);\n"
                + "   notePlaying[vi]=0;\n"
                + "   voicePriority[vi]=0;\n"
                + "   for (j = 0; j < v->nparams; j++) {\n"
                + "      v->params[j].d.frac.value = 0;\n"
                + "      v->params[j].d.frac.modvalue = 0;\n"
                + "   }\n"
                + "}\n"
                + "      for (k = 0; k < nparams; k++) {\n"
                + "        if (params[k].pfunction){\n"
                + "          (params[k].pfunction)(&params[k]);\n"
                + "        } else {\n"
                + "          params[k].d.frac.finalvalue = params[k].d.frac.value;\n"
                + "        }\n"
                + "      }\n"
                + "priority=0;\n"
                + "sustain=0;\n";
        ao.sDisposeCode = "int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "  voice *v = &getVoices()[vi];\n"
                + "  v->dispose();\n"
                + "}\n";
        ao.sKRateCode = "";
        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("patch/outlet f") || typeName.equals("patch/outlet i")
                    || typeName.equals("patch/outlet b") || typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = 0;\n";
            } else if (typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "{\n"
                        + "      int j;\n"
                        + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] = 0;\n"
                        + "}\n";
            }
        }
        ao.sKRateCode += "int vi; for(vi=0;vi<attr_poly;vi++) {";

        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("inlet") || typeName.equals("inlet_i") || typeName.equals("inlet_b") || typeName.equals("inlet_")
                    || typeName.equals("patch/inlet f") || typeName.equals("patch/inlet i") || typeName.equals("patch/inlet b")) {
                ao.sKRateCode += "   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n";
            } else if (typeName.equals("inlet_string") || typeName.equals("patch/inlet string")) {
                ao.sKRateCode += "   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n";
            } else if (typeName.equals("inlet~") || typeName.equals("patch/inlet a")) {
                ao.sKRateCode += "{int j; for(j=0;j<BUFSIZE;j++) getVoices()[vi]." + o.getCInstanceName() + "_i._inlet[j] = inlet_" + o.getLegalName() + "[j];}\n";
            }
        }
        ao.sKRateCode += "getVoices()[vi].dsp();\n";
        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("outlet") || typeName.equals("patch/outlet f")
                    || typeName.equals("patch/outlet i")
                    || typeName.equals("patch/outlet b")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n";
            } else if (typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = (char *)getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n";
            } else if (typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "{\n"
                        + "      int j;\n"
                        + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet[j];\n"
                        + "}\n";
            }
        }
        ao.sKRateCode += "}\n";
        ao.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
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
                + "  getVoices()[mini].MidiInHandler(this, dev, port, status, data1, data2);\n"
                + "} else if (((status == MIDI_NOTE_ON + attr_midichannel) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + attr_midichannel)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if ((notePlaying[i] == data1) && pressed[i]){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "        getVoices()[i].MidiInHandler(this, dev, port, status, data1, data2);\n"
                + "      }\n"
                + "  }\n"
                + "} else if (status == attr_midichannel + MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) getVoices()[i].MidiInHandler(this, dev, port, status, data1, data2);\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].MidiInHandler(this, dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;   for(i=0;i<attr_poly;i++) getVoices()[i].MidiInHandler(this, dev, port, status, data1, data2);\n"
                + "}\n";
    }

    public void GeneratePolyChannelCode(AxoObject o) {
        GeneratePolyCode(o);
        o.sLocalData
                += "int8_t voiceChannel[attr_poly];\n";
        o.sInitCode
                += "int vc;\n"
                + "for (vc=0;vc<attr_poly;vc++) {\n"
                + "   voiceChannel[vc]=0xFF;\n"
                + "}\n";
        o.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
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
                + "  getVoices()[mini].MidiInHandler(this, dev, port, status & 0xF0, data1, data2);\n"
                + "} else if (((msg == MIDI_NOTE_ON) && (!data2))||\n"
                + "            (msg == MIDI_NOTE_OFF)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (notePlaying[i] == data1){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      voiceChannel[i] = 0xFF;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "         getVoices()[i].MidiInHandler(this, dev, port, msg + attr_midichannel, data1, data2);\n"
                + "      }\n"
                + "  }\n"
                + "} else if (msg == MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].MidiInHandler(this, dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].MidiInHandler(this, dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else if (msg == MIDI_PITCH_BEND) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].MidiInHandler(this, dev, port, MIDI_PITCH_BEND + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "         getVoices()[i].MidiInHandler(this, dev, port,msg + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "}\n";
    }

    
    public void GeneratePolyExpressionCode(AxoObject o) {
        GeneratePolyCode(o);
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
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
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
                + "  getVoices()[mini].MidiInHandler(this, dev, port, status & 0xF0, data1, data2);\n"
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
                + "         getVoices()[i].MidiInHandler(dev, port, msg + attr_midichannel, data1, data2);\n"
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
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB);\n"
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB);\n"
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange);\n"
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
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2);\n"
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
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB);\n"
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB);\n"
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange);\n"
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
                + "          getVoices()[i].MidiInHandler(dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
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
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_PITCH_BEND + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "} else {" // end pb, other midi
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "         getVoices()[i].MidiInHandler(dev, port, msg + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "}\n"; // other midi        
    }
    
//    void ExportAxoObjPoly2(File f1) {
//        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
//    }
    // Poly voices from one (or omni) midi channel
    AxoObject GenerateAxoObjPoly(AxoObject template) {
        AxoObject ao = template;
        ao.id = "unnamedobject";
        ao.setDescription(getModel().FileNamePath);
        ao.includes = getModel().getIncludes();
        ao.depends = getModel().getDepends();
        ao.modules = getModel().getModules();
        if ((getModel().notes != null) && (!getModel().notes.isEmpty())) {
            ao.setDescription(getModel().notes);
        } else {
            ao.setDescription("no description");
        }
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        ao.attributes.add(new AxoAttributeComboBox("poly", centries, centries));
        if (getModel().getMidiSelector()) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midichannel", uch, cch));
            // use a cut down list of those currently supported
            String cdev[] = {"0", "1", "2", "3", "15"};
            String udev[] = {"omni", "din", "usb device", "usb host", "internal"};
            ao.attributes.add(new AxoAttributeComboBox("mididevice", udev, cdev));
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            String uport[] = {"omni", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midiport", uport, cport));
        }

        for (IAxoObjectInstance o : getModel().objectinstances) {
            String typeName = o.getType().getId();
            if (typeName.equals("patch/inlet f")) {
                ao.inlets.add(new InletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet i")) {
                ao.inlets.add(new InletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet b")) {
                ao.inlets.add(new InletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet a")) {
                ao.inlets.add(new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/inlet string")) {
                ao.inlets.add(new InletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet f")) {
                ao.outlets.add(new OutletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet i")) {
                ao.outlets.add(new OutletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet b")) {
                ao.outlets.add(new OutletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet a")) {
                ao.outlets.add(new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (typeName.equals("patch/outlet string")) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "string outlet impossible in poly subpatches!");
                // ao.outlets.add(new OutletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                if (p.getOnParent()) {
                    ao.params.add(p.createParameterForParent());
                }
            }
        }
        GeneratePolyCode(ao);
        return ao;
    }

    // Poly (Multi) Channel supports per Channel CC/Touch
    // all channels are independent
    AxoObject GenerateAxoObjPolyChannel(AxoObject template) {
        AxoObject o = GenerateAxoObjPoly(template);
        GeneratePolyChannelCode(o);
        return o;
    }

    // Poly Expression supports the Midi Polyphonic Expression (MPE) Spec
    // Can be used with (or without) the MPE objects
    // the midi channel of the patch is the 'main/global channel'
    AxoObject GenerateAxoObjPolyExpression(AxoObject template) {
        AxoObject o = GenerateAxoObjPoly(template);
        GeneratePolyExpressionCode(o);
        return o;
    }    
    
    public AxoObject GenerateAxoObj(AxoObject template) {
        AxoObject ao;
        switch (getModel().getSubPatchMode()) {
            case no:
            case normal:
                ao = GenerateAxoObjNormal(template);
                break;
            case polyphonic:
                ao = GenerateAxoObjPoly(template);
                break;
            case polychannel:
                ao = GenerateAxoObjPolyChannel(template);
                break;
            case polyexpression:
                ao = GenerateAxoObjPolyExpression(template);
                break;
            default:
                return null;
        }
        ao.setAuthor(getModel().getAuthor());
        ao.setLicense(getModel().getLicense());
        ao.setDescription(getModel().notes);
        ao.helpPatch = getModel().helpPatch;
        return ao;
    }

    public ArrayList<ParameterInstance> getParameterInstances() {
        return ParameterInstances;
    }

    public ArrayList<DisplayInstance> getDisplayInstances() {
        return DisplayInstances;
    }
    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

}
