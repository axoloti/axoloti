package axoloti.inlets;

import axoloti.INetView;
import axoloti.MainFrame;
import axoloti.Theme;
import axoloti.atom.AtomDefinitionController;
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

    InletInstancePopupMenu popup = new InletInstancePopupMenu(this);

    InletInstance inletInstance;

    final InletInstanceController controller;

    LabelComponent label;

    public InletInstanceView(InletInstance inletInstance, InletInstanceController controller, AxoObjectInstanceViewAbstract axoObj) {
        this.inletInstance = inletInstance;
        this.axoObj = axoObj;
        this.controller = controller;
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        setMaximumSize(new Dimension(32767, 14));
        jack = new JackInputComponent(this);
        jack.setForeground(inletInstance.getInlet().getDatatype().GetColor());
        jack.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(jack);
        add(new SignalMetaDataIcon(inletInstance.getInlet().GetSignalMetaData()));
        add(Box.createHorizontalStrut(3));
        if (!((axoObj != null) && axoObj.getModel().getType().getInlets().size() <= 1)) {
            label = new LabelComponent(inletInstance.getInlet().getName());
        } else {
            label = new LabelComponent("");            
        }
        add(label);
        add(Box.createHorizontalGlue());
        setToolTipText(inletInstance.getInlet().getDescription());

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return popup;
    }

    public String getInletname() {
        int sepIndex = name.lastIndexOf(' ');
        return name.substring(sepIndex + 1);
    }

    @Override
    public InletInstance getInletInstance() {
        return inletInstance;
    }

    @Override
    public void disconnect() {
        getPatchView().getController().disconnect(getController().getModel());
    }

    @Override
    public void deleteNet() {
        getPatchView().getController().delete(getPatchView().getController().getNetFromInlet(this.getController().getModel()));
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
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)) {
            label.setText((String)evt.getNewValue());
            doLayout();
        } else if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            setToolTipText((String)evt.getNewValue());
        }
    }

    @Override
    public InletInstanceController getController() {
        return controller;
    }

}
