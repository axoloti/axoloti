package axoloti.swingui.patch.object.outlet;

import axoloti.abstractui.INetView;
import axoloti.abstractui.IOutletInstanceView;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.TransparentCursor;
import axoloti.swingui.components.SignalMetaDataIcon;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.iolet.IoletInstancePopupMenu;
import axoloti.swingui.patch.object.iolet.IoletInstanceView;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class OutletInstanceView extends IoletInstanceView<OutletInstance> implements IOutletInstanceView {

    public OutletInstanceView(OutletInstance outletInstance, AxoObjectInstanceViewAbstract axoObj) {
        super(outletInstance, axoObj);
        initComponents();
    }

    private void initComponents() {
        setBackground(Theme.getCurrentTheme().Object_Default_Background);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(Box.createHorizontalGlue());
        if (axoObj.getDModel().getDModel().getOutlets().size() > 1) {
            add(label);
        }
        add(Box.createHorizontalStrut(2));
        add(new SignalMetaDataIcon(getDModel().getDModel().getSignalMetaData()));
        jack = new JackOutputComponent();
        jack.setForeground(getDModel().getDModel().getDataType().getColor());
        add(jack);
    }

    @Override
    protected JPopupMenu getPopup() {
        return new IoletInstancePopupMenu(model, focusEdit);
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
        if (OutletInstance.CONNECTED.is(evt)) {
            getJack().setConnected((Boolean) evt.getNewValue());
        }
    }

    @Override
    public void dispose() {
    }

    private JackOutputComponent getJack() {
        return (JackOutputComponent) jack;
    }

}
