package axoloti.patch.net;

import axoloti.abstractui.INetView;
import axoloti.mvc.AbstractController;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class NetController extends AbstractController<Net, INetView, PatchController> {

    protected NetController(Net model) {
        super(model);
        if (model.getParent() != null) {
            PatchModel patchModel = model.getParent();
            ArrayList<OutletInstance> source2 = new ArrayList<>();
            for (OutletInstance i : model.getSources()) {
                String objname = i.getObjname();
                String outletname = i.getName();

                IAxoObjectInstance o = patchModel.findObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net source obj : {0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                OutletInstance r = o.findOutletInstance(outletname);
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
                IAxoObjectInstance o = patchModel.findObjectInstance(objname);
                if (o == null) {
                    Logger.getLogger(NetController.class.getName()).log(Level.SEVERE, "could not resolve net dest obj :{0}::{1}", new Object[]{i.getObjname(), i.getName()});
                    patchModel.nets.remove(model);
                    return;
                }
                InletInstance r = o.findInletInstance(inletname);
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
// FIXME: sanity check
//        if (inlet.getParent().getParent() != getParent().getModel()) {
//            return;
//        }
//        inlet.getControllerFromModel().setModelUndoableProperty(InletInstance.CONNECTED, true);
        ArrayList<InletInstance> n = new ArrayList<>(Arrays.asList(getModel().getDestinations()));
        n.add(inlet);
        setModelUndoableProperty(Net.NET_DESTINATIONS, n.toArray(new InletInstance[]{}));
    }

    public void connectOutlet(OutletInstance outlet) {
// FIXME: sanity check
//        if (outlet.getParent().getParent() != getParent().getModel()) {
//            return;
//        }
//        outlet.getControllerFromModel().setModelUndoableProperty(OutletInstance.CONNECTED, true);
        ArrayList<OutletInstance> n = new ArrayList<>(Arrays.asList(getModel().getSources()));
        n.add(outlet);
        setModelUndoableProperty(Net.NET_SOURCES, n.toArray(new OutletInstance[]{}));
    }

    public void disconnect(OutletInstance outlet) {
// TODO: sanity check
//        if (iolet.getParent().getParent() != getParent().getModel()) {
//            return;
//        }

//        NetController nx = getModel().getParent().getControllerFromModel().getNetFromIolet(iolet);
//        if (nx == null) {
//            iolet.getControllerFromModel().setModelUndoableProperty(IoletInstance.CONNECTED, false);
//        }
        List<OutletInstance> n = new LinkedList<>(Arrays.asList(getModel().getSources()));
        n.remove(outlet);
        setModelUndoableProperty(Net.NET_SOURCES, n.toArray(new OutletInstance[]{}));
        // TODO: migrate NET_SOURCES to ListProperty, so this reduces to:
        // removeUndoableElementFromList(Net.NET_SOURCES, outlet);
    }

    public void disconnect(InletInstance inlet) {
// TODO: sanity check
//        if (iolet.getParent().getParent() != getParent().getModel()) {
//            return;
//        }

//        NetController nx = getModel().getParent().getControllerFromModel().getNetFromIolet(iolet);
//        if (nx == null) {
//            iolet.getControllerFromModel().setModelUndoableProperty(IoletInstance.CONNECTED, false);
//        }
        List<InletInstance> n = new LinkedList<>(Arrays.asList(getModel().getDestinations()));
        n.remove(inlet);
        setModelUndoableProperty(Net.NET_DESTINATIONS, n.toArray(new InletInstance[]{}));
        // TODO: migrate NET_DESTINATIONS to ListProperty, so this reduces to:
        // removeUndoableElementFromList(Net.NET_DESTINATIONS, outlet);
    }

    public boolean NeedsLatch() {
        // reads before last write on net
        int lastSource = 0;
        for (OutletInstance s : getModel().getSources()) {
            int i = getModel().getParent().getObjectInstances().indexOf(s.getParent());
            if (i > lastSource) {
                lastSource = i;
            }
        }
        int firstDest = java.lang.Integer.MAX_VALUE;
        for (InletInstance d : getModel().getDestinations()) {
            int i = getModel().getParent().getObjectInstances().indexOf(d.getParent());
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
        for (IAxoObjectInstance o : getModel().getParent().getObjectInstances()) {
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
        int i = getModel().getParent().getNets().indexOf(getModel());
        return "net" + i;
    }
}
