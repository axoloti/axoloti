package axoloti.object;

import axoloti.PatchController;
import axoloti.PatchModel;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import java.awt.Point;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceFactory {

    static public AxoObjectInstanceAbstract createView(ObjectController objc, PatchController patchController, String instanceName, Point location) {
        PatchModel patchModel;
        if (patchController != null) {
            patchModel = patchController.getModel();
        } else {
            patchModel = null;
        }
        IAxoObject objm = objc.getModel();
        AxoObjectInstanceAbstract obji;
        if (objm instanceof AxoObjectUnloaded) {
            ((AxoObjectUnloaded) objm).Load();
            objm = ((AxoObjectUnloaded) objm).loadedObject;
            objc = objm.createController(null, null);
        }
        if (objm instanceof AxoObjectComment) {
            obji = new AxoObjectInstanceComment(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectHyperlink) {
            obji = new AxoObjectInstanceHyperlink(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcher) {
            // every AxoObjectPatcherInstance needs an independent AxoObjectPatcher object
            AxoObjectPatcher obj = new AxoObjectPatcher();
            objc = new ObjectController(obj, null);
            obji = new AxoObjectInstancePatcher(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcherObject) {
            AxoObjectPatcherObject objm1 = new AxoObjectPatcherObject();
            AbstractDocumentRoot dr;
            if (patchController != null) {
                dr = patchController.getDocumentRoot();
            } else {
                dr = null;
            }
            objc = objm1.createController(dr, patchController);
            obji = new AxoObjectInstancePatcherObject(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectZombie) {
            obji = new AxoObjectInstanceZombie(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObject) {
            obji = new AxoObjectInstance(objc, patchModel, instanceName, location);
        } else {
            obji = null;
            throw new Error("unknown object type");
        }
        objc.addView(obji);
        return obji;
    }
}
