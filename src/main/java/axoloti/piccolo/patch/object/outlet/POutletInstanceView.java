package axoloti.piccolo.patch.object.outlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.components.PJackOutputComponent;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.components.PSignalMetaDataIcon;
import axoloti.piccolo.patch.object.iolet.PIoletAbstract;
import axoloti.swingui.patch.object.iolet.IoletInstancePopupMenu;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class POutletInstanceView extends PIoletAbstract implements IOutletInstanceView {

    OutletInstance outletInstance;
    PLabelComponent label;

    public POutletInstanceView(OutletInstance outletInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.outletInstance = outletInstance;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        addToSwingProxy(Box.createHorizontalGlue());
        if (axoObjectInstanceView.getDModel().getDModel().getOutlets().size() > 1) {
            label = new PLabelComponent(getDModel().getDModel().getName());
        } else {
            label = new PLabelComponent("");
        }

        addChild(label);
        addToSwingProxy(Box.createHorizontalStrut(2));

        PSignalMetaDataIcon foo = new PSignalMetaDataIcon(getDModel().getDModel().getSignalMetaData(), axoObjectInstanceView);
        addChild(foo);

        jack = new PJackOutputComponent(this);
        ((PJackOutputComponent) jack).setForeground(getDModel().getDModel().getDataType().getColor());
        addChild(jack);

        addInputEventListener(getInputEventHandler());
        addInputEventListener(toolTipEventListener);
    }

    private final PBasicInputEventHandler toolTipEventListener = new PBasicInputEventHandler() {
        @Override
        public void mouseEntered(PInputEvent e) {
            if (e.getInputManager().getMouseFocus() == null) {
                axoObjectInstanceView.getCanvas().setToolTipText(getDModel().getDModel().getDescription());
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
    public OutletInstance getDModel() {
        return outletInstance;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if (axoObjectInstanceView != null
                && axoObjectInstanceView.getPatchView() != null) {
            INetView netView = axoObjectInstanceView.getPatchView().findNetView(this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }

    @Override
    public JPopupMenu getPopup() {
        return new IoletInstancePopupMenu(getDModel(),
                null /* TODO: piccolo implement  focusEdit */);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
	if (OutletInstance.NAME.is(evt)) {
	    label.setText((String) evt.getNewValue());
	} else if (OutletInstance.DESCRIPTION.is(evt)) {
	    axoObjectInstanceView.getCanvas().setToolTipText((String) evt.getNewValue());
	}
        else if (OutletInstance.CONNECTED.is(evt)) {
            getJack().setConnected((Boolean) evt.getNewValue());
            getJack().repaint();
        }
    }

    @Override
    public void dispose() {
    }

    private PJackOutputComponent getJack() {
        return (PJackOutputComponent) jack;
    }
}
