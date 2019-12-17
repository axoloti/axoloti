package axoloti.codegen.patch.object;

import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.object.AxoObjectPatcher;
import axoloti.patch.PatchModel;
import axoloti.patch.SubPatchMode;
import axoloti.patch.object.AxoObjectInstancePatcher;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstancePatcherCodegenView extends AxoObjectInstanceCodegenView {

    AxoObjectInstancePatcherCodegenView(AxoObjectInstancePatcher objectInstance) {
        super(objectInstance);
        AxoObjectPatcher ao = (AxoObjectPatcher) objectInstance.getDModel();
        PatchModel pm = objectInstance.getSubPatchModel();
        PatchViewCodegen pvcg = new PatchViewCodegen(pm);

        // from GenerateAxoObjNormal
        if (pm.getSubPatchMode() == SubPatchMode.no ||
                pm.getSubPatchMode() == SubPatchMode.normal) {
/*
            ao.sLocalData = pvcg.GenerateStructCodePlusPlusSub("attr_parent", true)
                    + "static const int polyIndex = 0;\n";
            ao.sLocalData += pvcg.GenerateParamInitCode3("");
            ao.sLocalData += pvcg.GeneratePresetCode3("");
            ao.sLocalData += pvcg.GenerateModulationCode3();
            ao.sLocalData = ao.sLocalData.replaceAll("attr_poly", "1");
            ao.sInitCode = pvcg.GenerateParamInitCodePlusPlusSub("attr_parent", "this");
            ao.sInitCode += pvcg.GenerateObjInitCodePlusPlusSub("attr_parent", "this");
            ao.sDisposeCode = pvcg.GenerateDisposeCodePlusPlusSub("attr_parent");
            ao.includes = pm.getIncludes();
            ao.depends = pm.getDepends();
            ao.modules = pm.getModules();
            ao.sKRateCode = "int i; \n";
            for (IAxoObjectInstance o : pm.objectinstances) {
                String typeName = o.getType().getId();
                if (typeName.equals("patch/inlet f") || typeName.equals("patch/inlet i") || typeName.equals("patch/inlet b")) {
                    ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n";
                } else if (typeName.equals("patch/inlet string")) {
                    ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n";
                } else if (typeName.equals("patch/inlet a")) {
                    ao.sKRateCode += "   for(i=0;i<BUFSIZE;i++) " + o.getCInstanceName() + "_i._inlet[i] = inlet_" + o.getLegalName() + "[i];\n";
                }
            }
            ao.sKRateCode += pvcg.GenerateDSPCodePlusPlusSub("attr_parent", true);
            for (IAxoObjectInstance o : pm.objectinstances) {
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
                    + pvcg.GenerateMidiInCodePlusPlus();
*/
            pvcg.generateNormalCode(ao);
        } else if (pm.getSubPatchMode() == SubPatchMode.polyphonic) {
            pvcg.generatePolyCode(ao);
        } else if (pm.getSubPatchMode() == SubPatchMode.polychannel) {
            pvcg.generatePolyChannelCode(ao);
        } else if (pm.getSubPatchMode() == SubPatchMode.polyexpression) {
            pvcg.generatePolyExpressionCode(ao);
        }
    }

}
