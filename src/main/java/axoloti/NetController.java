package axoloti;

import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.outlets.OutletInstance;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class NetController extends AbstractController<Net, INetView, PatchController> {

    public final static String NET_SOURCES = "Sources";
    public final static String NET_DESTINATIONS = "Destinations";

    public NetController(Net model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);
        if (parent != null) {
            PatchModel patchModel = parent.getModel();
            ArrayList<OutletInstance> source2 = new ArrayList<>();
            for (OutletInstance i : model.source) {
                String objname = i.getObjname();
                String outletname = i.getOutletname();

                AxoObjectInstanceAbstract o = patchModel.GetObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net source obj : {0}::{1}", new Object[]{i.getObjname(), i.getOutletname()});
                    patchModel.nets.remove(model);
                    return;
                }
                OutletInstance r = o.GetOutletInstance(outletname);
                if (r == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net source outlet : {0}::{1}", new Object[]{i.getObjname(), i.getOutletname()});
                    patchModel.nets.remove(model);
                    return;
                }
                source2.add(r);
            }
            ArrayList<InletInstance> dest2 = new ArrayList<>();
            for (InletInstance i : model.dest) {
                String objname = i.getObjname();
                String inletname = i.getInletname();
                AxoObjectInstanceAbstract o = patchModel.GetObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net dest obj :{0}::{1}", new Object[]{i.getObjname(), i.getInletname()});
                    patchModel.nets.remove(model);
                    return;
                }
                InletInstance r = o.GetInletInstance(inletname);
                if (r == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net dest inlet :{0}::{1}", new Object[]{i.getObjname(), i.getInletname()});
                    patchModel.nets.remove(model);
                    return;
                }
                dest2.add(r);
            }
            model.source = source2;
            model.dest = dest2;
        }
    }

    public void connectInlet(InletInstance inlet) {
        if (inlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<InletInstance> n = (ArrayList<InletInstance>) getModel().dest.clone();
        n.add(inlet);
        setModelUndoableProperty(NET_DESTINATIONS, n);
    }

    public void connectOutlet(OutletInstance outlet) {
        if (outlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<OutletInstance> n = (ArrayList<OutletInstance>) getModel().source.clone();
        n.add(outlet);
        setModelUndoableProperty(NET_SOURCES, n);
    }

    void disconnect(InletInstance inlet) {
        if (inlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<InletInstance> n = (ArrayList<InletInstance>) getModel().dest.clone();
        n.remove(inlet);
        setModelUndoableProperty(NET_DESTINATIONS, n);
    }

    void disconnect(OutletInstance outlet) {
        if (outlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<OutletInstance> n = (ArrayList<OutletInstance>) getModel().source.clone();
        n.remove(outlet);
        setModelUndoableProperty(NET_SOURCES, n);
    }

    public boolean NeedsLatch() {
        // reads before last write on net
        int lastSource = 0;
        for (OutletInstance s : getModel().source) {
            int i = getParent().getModel().objectinstances.indexOf(s.getObjectInstance());
            if (i > lastSource) {
                lastSource = i;
            }
        }
        int firstDest = java.lang.Integer.MAX_VALUE;
        for (InletInstance d : getModel().dest) {
            int i = getParent().getModel().objectinstances.indexOf(d.getObjectInstance());
            if (i < firstDest) {
                firstDest = i;
            }
        }
        return (firstDest <= lastSource);
    }

    public boolean IsFirstOutlet(OutletInstance oi) {
        if (getModel().source.size() == 1) {
            return true;
        }
        for (AxoObjectInstanceAbstract o : getParent().getModel().objectinstances) {
            for (OutletInstance i : o.getOutletInstances()) {
                if (getModel().source.contains(i)) {
                    // o is first objectinstance connected to this net
                    return oi == i;
                }
            }
        }
        Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "IsFirstOutlet: shouldn't get here");
        return false;
    }

    public String CName() {
        int i = getParent().getModel().nets.indexOf(this);
        return "net" + i;
    }
}
