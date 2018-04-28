package axoloti.swingui.patch.object.inlet;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.preferences.Theme;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.components.JackInputComponent;
import axoloti.swingui.components.SignalMetaDataIcon;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.iolet.IoletInstancePopupMenu;
import axoloti.swingui.patch.object.iolet.IoletInstanceView;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class InletInstanceView extends IoletInstanceView<IoletInstanceController> implements IIoletInstanceView {

    public InletInstanceView(IoletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        super(controller);
        this.axoObj = axoObj;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent();
        jack.setForeground(getModel().getModel().getDatatype().GetColor());
        jack.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(jack);
        add(new SignalMetaDataIcon(getModel().getModel().GetSignalMetaData()));
        add(Box.createHorizontalStrut(3));
        if (!((axoObj != null) && axoObj.getModel().getType().getInlets().size() <= 1)) {
            add(label);
        }
        add(Box.createHorizontalGlue());

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
    }

    @Override
    public JPopupMenu getPopup() {
        return new IoletInstancePopupMenu(getController(), focusEdit);
    }

    public String getInletname() {
        int sepIndex = name.lastIndexOf(' ');
        return name.substring(sepIndex + 1);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != TransparentCursor.get())
                && axoObj != null
                && axoObj.getPatchView() != null) {
            INetView netView = axoObj.getPatchView().findNetView(this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (InletInstance.CONNECTED.is(evt)) {
            getJack().setConnected((Boolean) evt.getNewValue());
            getJack().repaint();
        }
    }

    @Override
    public void dispose() {
    }

    private JackInputComponent getJack() {
        return (JackInputComponent) jack;
    }

}
