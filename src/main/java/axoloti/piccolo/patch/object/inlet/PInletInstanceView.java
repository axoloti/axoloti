package axoloti.piccolo.patch.object.inlet;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IInletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.piccolo.components.PJackInputComponent;
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

public class PInletInstanceView extends PIoletAbstract implements IInletInstanceView {

    InletInstance inletInstance;
    PLabelComponent label;

    public PInletInstanceView(InletInstance inletInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.inletInstance = inletInstance;
        initComponent();
    }

    private void initComponent() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        jack = new PJackInputComponent(this);
        jack.setForeground(getDModel().getDModel().getDataType().getColor());

        addChild(jack);
        addChild(new PSignalMetaDataIcon(getDModel().getDModel().getSignalMetaData(), axoObjectInstanceView));
        addToSwingProxy(Box.createHorizontalStrut(3));

        if (!((axoObjectInstanceView != null) && axoObjectInstanceView.getDModel().getDModel().getInlets().size() <= 1)) {
            label = new PLabelComponent(getDModel().getDModel().getName());
        } else {
            label = new PLabelComponent("");
        }

        addChild(label);
        addToSwingProxy(Box.createHorizontalGlue());

        addInputEventListener(getInputEventHandler());
        addInputEventListener(toolTipEventListener);
    }

    @Override
    public InletInstance getDModel() {
        return inletInstance;
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
                null /* TODO: piccolo implement focusEdit */);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (InletInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            getProxyComponent().doLayout();
        } else if (InletInstance.DESCRIPTION.is(evt)) {
            axoObjectInstanceView.getCanvas().setToolTipText((String) evt.getNewValue());
        }
        else if (InletInstance.CONNECTED.is(evt)) {
            getJack().setConnected((Boolean) evt.getNewValue());
            getJack().repaint();
        }
    }

    @Override
    public void dispose() {
    }

    private PJackInputComponent getJack() {
        return (PJackInputComponent) jack;
    }
}
