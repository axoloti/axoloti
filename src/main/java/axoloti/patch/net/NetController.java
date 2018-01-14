package axoloti.patch.net;

import axoloti.abstractui.INetView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class NetController extends AbstractController<Net, INetView, PatchController> {


    public NetController(Net model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);
        if (parent != null) {
            PatchModel patchModel = parent.getModel();
            ArrayList<OutletInstance> source2 = new ArrayList<>();
            for (OutletInstance i : model.getSources()) {
                String objname = i.getObjname();
                String outletname = i.getName();

                IAxoObjectInstance o = patchModel.GetObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net source obj : {0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                OutletInstance r = o.GetOutletInstance(outletname);
                if (r == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net source outlet : {0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                source2.add(r);
            }
            ArrayList<InletInstance> dest2 = new ArrayList<>();
            for (InletInstance i : model.getDestinations()) {
                String objname = i.getObjname();
                String inletname = i.getName();
                IAxoObjectInstance o = patchModel.GetObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net dest obj :{0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                InletInstance r = o.GetInletInstance(inletname);
                if (r == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net dest inlet :{0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                dest2.add(r);
            }
            model.setSources(source2.toArray(new OutletInstance[]{}));
            model.setDestinations(dest2.toArray(new InletInstance[]{}));
        }
    }

    public void connectInlet(InletInstance inlet) {
        if (inlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<InletInstance> n = new ArrayList<>(Arrays.asList(getModel().getDestinations()));
        n.add(inlet);
        setModelUndoableProperty(Net.NET_DESTINATIONS, n.toArray(new InletInstance[]{}));
    }

    public void connectOutlet(OutletInstance outlet) {
        if (outlet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        ArrayList<OutletInstance> n = new ArrayList<>(Arrays.asList(getModel().getSources()));
        n.add(outlet);
        setModelUndoableProperty(Net.NET_SOURCES, n.toArray(new OutletInstance[]{}));
    }

    public void disconnect(IoletInstance iolet) {
        if (iolet.getObjectInstance().getPatchModel() != getParent().getModel()) {
            return;
        }
        if(iolet.isSource()) {
            List<IoletInstance> n = new ArrayList<>(Arrays.asList(getModel().getSources()));
            n.remove(iolet);
            setModelUndoableProperty(Net.NET_SOURCES, n.toArray(new OutletInstance[]{}));
        }
        else {
            List<IoletInstance> n = new ArrayList<>(Arrays.asList(getModel().getDestinations()));
            n.remove(iolet);
            setModelUndoableProperty(Net.NET_DESTINATIONS, n.toArray(new InletInstance[]{}));
        }
    }

    public boolean NeedsLatch() {
        // reads before last write on net
        int lastSource = 0;
        for (OutletInstance s : getModel().getSources()) {
            int i = getParent().getModel().objectinstances.indexOf(s.getObjectInstance());
            if (i > lastSource) {
                lastSource = i;
            }
        }
        int firstDest = java.lang.Integer.MAX_VALUE;
        for (InletInstance d : getModel().getDestinations()) {
            int i = getParent().getModel().objectinstances.indexOf(d.getObjectInstance());
            if (i < firstDest) {
                firstDest = i;
            }
        }
        return (firstDest <= lastSource);
    }

    public boolean IsFirstOutlet(OutletInstance oi) {
        if (getModel().getSources().length == 1) {
            return true;
        }
        for (IAxoObjectInstance o : getParent().getModel().objectinstances) {
            for (OutletInstance i : o.getOutletInstances()) {
                List<OutletInstance> outletlist = Arrays.asList(getModel().getSources());
                if (outletlist.contains(i)) {
                    // o is first objectinstance connected to this net
                    return oi == i;
                }
            }
        }
        Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "IsFirstOutlet: shouldn't get here");
        return false;
    }

    public String CName() {
        int i = getParent().netControllers.indexOf(this);
        return "net" + i;
    }
}
