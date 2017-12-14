package axoloti.inlets;

import axoloti.INetView;
import axoloti.MainFrame;
import axoloti.Theme;
import axoloti.iolet.IoletAbstract;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.JackInputComponent;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;

public class InletInstanceView extends IoletAbstract implements IInletInstanceView {

    final InletInstanceController controller;

    LabelComponent label;

    public InletInstanceView(InletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        super();
        this.axoObj = axoObj;
        this.controller = controller;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    @Override
    public InletInstance getModel() {
        return controller.getModel();
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
        return new InletInstancePopupMenu(getController());
    }

    public String getInletname() {
        int sepIndex = name.lastIndexOf(' ');
        return name.substring(sepIndex + 1);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        if ((getRootPane() == null
                || getRootPane().getCursor() != MainFrame.transparentCursor)
                && axoObj != null
                && axoObj.getPatchView() != null) {
            INetView netView = axoObj.getPatchView().GetNetView((IInletInstanceView) this);
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
    public InletInstanceController getController() {
        return controller;
    }

    @Override
    public void dispose() {
    }
}
