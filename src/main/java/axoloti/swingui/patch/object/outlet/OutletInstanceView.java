package axoloti.swingui.patch.object.outlet;

import axoloti.preferences.Theme;
import axoloti.abstractui.INetView;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.SignalMetaDataIcon;
import axoloti.swingui.patch.object.iolet.IoletAbstract;
import axoloti.swingui.patch.object.iolet.IoletInstancePopupMenu;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;



public class OutletInstanceView extends IoletAbstract<IoletInstanceController> implements IIoletInstanceView {

    LabelComponent label;

    public OutletInstanceView(IoletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        super(controller);
        this.axoObj = axoObj;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(Box.createHorizontalGlue());
        if (axoObj.getModel().getType().getOutlets().size() > 1) {
            label = new LabelComponent(getModel().getModel().getName());
        } else {
            label = new LabelComponent("");
        }
        add(label);
        add(Box.createHorizontalStrut(2));
        add(new SignalMetaDataIcon(getModel().getModel().GetSignalMetaData()));
        jack = new axoloti.swingui.components.JackOutputComponent(this);
        jack.setForeground(getModel().getModel().getDatatype().GetColor());
        add(jack);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return new IoletInstancePopupMenu(getController());
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
        if (OutletInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
        } else if (OutletInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }
}
