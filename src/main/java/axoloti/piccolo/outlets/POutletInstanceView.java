package axoloti.piccolo.outlets;

import axoloti.INetView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceController;
import axoloti.outlets.OutletInstancePopupMenu;
import axoloti.piccolo.iolet.PIoletAbstract;
import components.piccolo.PJackOutputComponent;
import components.piccolo.PLabelComponent;
import components.piccolo.PSignalMetaDataIcon;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class POutletInstanceView extends PIoletAbstract implements IOutletInstanceView {

    OutletInstance outletInstance;
    OutletInstanceController controller;

    public POutletInstanceView(OutletInstance outletInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.outletInstance = outletInstance;

    }

    private final PBasicInputEventHandler toolTipEventListener = new PBasicInputEventHandler() {
        @Override
        public void mouseEntered(PInputEvent e) {
            if (e.getInputManager().getMouseFocus() == null) {
                axoObjectInstanceView.getCanvas().setToolTipText(outletInstance.getModel().getDescription());
            }
        }

        @Override
        public void mouseExited(PInputEvent e) {
            if (e.getInputManager().getMouseFocus() == null) {
                axoObjectInstanceView.getCanvas().setToolTipText(null);
            }
        }
    };

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        addToSwingProxy(Box.createHorizontalGlue());

        if (axoObjectInstanceView.getModel().getType().getOutlets().size() > 1) {
            addChild(new PLabelComponent(outletInstance.getModel().getName()));
            addToSwingProxy(Box.createHorizontalStrut(2));
        }
        PSignalMetaDataIcon foo = new PSignalMetaDataIcon(outletInstance.getModel().GetSignalMetaData(), axoObjectInstanceView);
        addChild(foo);

        jack = new PJackOutputComponent(this);
        ((PJackOutputComponent) jack).setForeground(outletInstance.getModel().getDatatype().GetColor());
        addChild(jack);

        addInputEventListener(getInputEventHandler());
        addInputEventListener(toolTipEventListener);
    }

    @Override
    public OutletInstance getModel() {
        return outletInstance;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if (axoObjectInstanceView != null
                && axoObjectInstanceView.getPatchView() != null) {
            INetView netView = axoObjectInstanceView.getPatchView().GetNetView((IOutletInstanceView) this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }

    @Override
    public JPopupMenu getPopup() {
        return new OutletInstancePopupMenu(getController());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutletInstanceController getController() {
        return controller;
    }

    @Override
    public void dispose() {
    }
}
