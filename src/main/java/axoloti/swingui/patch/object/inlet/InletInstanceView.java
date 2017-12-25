package axoloti.swingui.patch.object.inlet;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.abstractui.INetView;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.preferences.Theme;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.components.JackInputComponent;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.SignalMetaDataIcon;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.iolet.IoletAbstract;
import axoloti.swingui.patch.object.iolet.IoletInstancePopupMenu;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class InletInstanceView extends IoletAbstract<IoletInstanceController> implements IIoletInstanceView {

    LabelComponent label;

    public InletInstanceView(IoletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        super(controller);
        this.axoObj = axoObj;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent(this);
        jack.setForeground(getModel().getModel().getDatatype().GetColor());
        jack.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(jack);
        add(new SignalMetaDataIcon(getModel().getModel().GetSignalMetaData()));
        add(Box.createHorizontalStrut(3));
        if (!((axoObj != null) && axoObj.getModel().getType().getInlets().size() <= 1)) {
            label = new LabelComponent(getModel().getModel().getName());
        } else {
            label = new LabelComponent("");
        }
        add(label);
        add(Box.createHorizontalGlue());

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return new IoletInstancePopupMenu(getController());
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
            INetView netView = axoObj.getPatchView().GetNetView(this);
            if (netView != null
                    && netView.getSelected() != highlighted) {
                netView.setSelected(highlighted);
            }
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (InletInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (InletInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }
}
