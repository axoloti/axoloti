package axoloti.patch.object;

import axoloti.object.AxoObject;
import axoloti.object.AxoObjectComment;
import axoloti.object.AxoObjectHyperlink;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.AxoObjectUnloaded;
import axoloti.object.AxoObjectZombie;
import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class AxoObjectInstanceFactory {

    private AxoObjectInstanceFactory() {
    }

    static public AxoObjectInstanceAbstract createView(IAxoObject obj, PatchModel patchModel, String instanceName, Point location) {

        AxoObjectInstanceAbstract obji;
        if (obj instanceof AxoObjectUnloaded) {
            try {
                obj = ((AxoObjectUnloaded) obj).load();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AxoObjectInstanceFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (obj instanceof AxoObjectComment) {
            obji = new AxoObjectInstanceComment(obj, patchModel, instanceName, location);
        } else if (obj instanceof AxoObjectHyperlink) {
            obji = new AxoObjectInstanceHyperlink(obj, patchModel, instanceName, location);
        } else if (obj instanceof AxoObjectPatcher) {
            obj = new AxoObjectPatcher();
            obji = new AxoObjectInstancePatcher(obj, patchModel, instanceName, location);
        } else if (obj instanceof AxoObjectPatcherObject) {
            AxoObjectPatcherObject objp = new AxoObjectPatcherObject();
            obji = new AxoObjectInstancePatcherObject(objp, patchModel, instanceName, location);
        } else if (obj instanceof AxoObjectZombie) {
            obji = new AxoObjectInstanceZombie(obj, patchModel, instanceName, location);
        } else if (obj instanceof AxoObject) {
            obji = new AxoObjectInstance(obj, patchModel, instanceName, location);
        } else {
            obji = null;
            throw new Error("unknown object type");
        }
        obji.getDModel().getController().addView(obji);
        return obji;
    }
}
