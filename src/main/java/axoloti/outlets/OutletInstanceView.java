package axoloti.outlets;

import axoloti.INetView;
import axoloti.MainFrame;
import axoloti.Theme;
import axoloti.iolet.IoletAbstract;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class OutletInstanceView extends IoletAbstract implements IOutletInstanceView {

    final OutletInstanceController controller;

    LabelComponent label;

    public OutletInstanceView(OutletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        super();
        this.controller = controller;
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
        jack = new components.JackOutputComponent(this);
        jack.setForeground(getModel().getModel().getDatatype().GetColor());
        add(jack);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return new OutletInstancePopupMenu(getController());
    }

    @Override
    public OutletInstance getModel() {
        return getController().getModel();
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != MainFrame.transparentCursor)
                && axoObj != null
                && axoObj.getPatchView() != null) {
            INetView netView = axoObj.getPatchView().GetNetView((IOutletInstanceView) this);
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
    public OutletInstanceController getController() {
        return controller;
    }

    @Override
    public void dispose() {
    }
}
