package axoloti.object;

import axoloti.PatchController;
import axoloti.PatchModel;
import axoloti.mvc.AbstractDocumentRoot;
import java.awt.Point;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceFactory {

    static public AxoObjectInstanceAbstract createView(ObjectController obj, PatchController patchController, String instanceName, Point location) {
        PatchModel patchModel;
        if (patchController != null) {
            patchModel = patchController.getModel();
        } else {
            patchModel = null;
        }
        IAxoObject objm = obj.getModel();
        AxoObjectInstanceAbstract obji;
        if (objm instanceof AxoObjectUnloaded) {
            ((AxoObjectUnloaded) objm).Load();
            objm = ((AxoObjectUnloaded) objm).loadedObject;
            obj = objm.createController(null, null);
        }
        if (objm instanceof AxoObjectComment) {
            obji = new AxoObjectInstanceComment(obj, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectHyperlink) {
            obji = new AxoObjectInstanceHyperlink(obj, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcher) {
            obji = new AxoObjectInstancePatcher(obj, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcherObject) {
            AxoObjectPatcherObject objm1 = new AxoObjectPatcherObject();
            AbstractDocumentRoot dr;
            if (patchController != null) {
                dr = patchController.getDocumentRoot();
            } else {
                dr = null;
            }
            ObjectController oc = objm1.createController(dr, patchController);
            obji = new AxoObjectInstancePatcherObject(oc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectZombie) {
            obji = new AxoObjectInstanceZombie(obj, patchModel, instanceName, location);
        } else if (objm instanceof AxoObject) {
            obji = new AxoObjectInstance(obj, patchModel, instanceName, location);
        } else {
            obji = null;
            throw new Error("unknown object type");
        }
        obj.addView(obji);
        return obji;
    }
}
