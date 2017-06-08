package axoloti;

import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.outlets.OutletInstance;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class NetController extends AbstractController<Net, INetView> {

    public final static String NET_SOURCES = "Sources";
    public final static String NET_DESTINATIONS = "Destinations";

    public NetController(Net model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
    }
    
    public void connectInlet(InletInstance inlet) {
        if (inlet.getObjectInstance().patchModel != getModel().patchModel) {
            return;
        }
        ArrayList<InletInstance> n = (ArrayList<InletInstance>)getModel().dest.clone();
        n.add(inlet);
        setModelUndoableProperty(NET_DESTINATIONS, n);
    }

    public void connectOutlet(OutletInstance outlet) {
        if (outlet.getObjectInstance().patchModel != getModel().patchModel) {
            return;
        }
        ArrayList<OutletInstance> n = (ArrayList<OutletInstance>)getModel().source.clone();
        n.add(outlet);
        setModelUndoableProperty(NET_SOURCES, n);
    }    

    void disconnect(InletInstance inlet) {
        if (inlet.getObjectInstance().patchModel != getModel().patchModel) {
            return;
        }
        ArrayList<InletInstance> n = (ArrayList<InletInstance>)getModel().dest.clone();
        n.remove(inlet);
        setModelUndoableProperty(NET_DESTINATIONS, n);
    }

    void disconnect(OutletInstance outlet) {
        if (outlet.getObjectInstance().patchModel != getModel().patchModel) {
            return;
        }
        ArrayList<OutletInstance> n = (ArrayList<OutletInstance>)getModel().source.clone();
        n.remove(outlet);
        setModelUndoableProperty(NET_SOURCES, n);
    }
    
}
