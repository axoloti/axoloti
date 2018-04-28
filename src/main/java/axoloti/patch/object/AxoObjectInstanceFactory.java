package axoloti.patch.object;

import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectComment;
import axoloti.object.AxoObjectHyperlink;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.AxoObjectUnloaded;
import axoloti.object.AxoObjectZombie;
import axoloti.object.IAxoObject;
import axoloti.object.ObjectController;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
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
            objm = ((AxoObjectUnloaded) objm).Load();
        }
        if (objm instanceof AxoObjectComment) {
            obji = new AxoObjectInstanceComment(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectHyperlink) {
            obji = new AxoObjectInstanceHyperlink(objc, patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcher) {
            // every AxoObjectPatcherInstance needs an independent AxoObjectPatcher object
            AxoObjectPatcher obj = new AxoObjectPatcher();
            if (patchController != null) {
                obj.setDocumentRoot(patchController.getDocumentRoot());
            }
            obji = new AxoObjectInstancePatcher(obj.getControllerFromModel(), patchModel, instanceName, location);
        } else if (objm instanceof AxoObjectPatcherObject) {
            AxoObjectPatcherObject objm1 = new AxoObjectPatcherObject();
            AbstractDocumentRoot dr;
            if (patchController != null) {
                dr = patchController.getDocumentRoot();
            } else {
                dr = null;
            }
            objm1.setDocumentRoot(dr);
            objc = objm1.getControllerFromModel();
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
