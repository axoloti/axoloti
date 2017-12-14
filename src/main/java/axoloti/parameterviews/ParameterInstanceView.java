package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import axoloti.property.Property;
import axoloti.propertyViewSwingMenu.ViewFactory;
import components.AssignMidiCCComponent;
import components.AssignPresetMenuItems;
import components.LabelComponent;
import components.control.ACtrlComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

public abstract class ParameterInstanceView extends JPanel implements ActionListener, IParameterInstanceView {

    LabelComponent valuelbl = new LabelComponent("123456789");
    ACtrlComponent ctrl;
    LabelComponent label = new LabelComponent("");

    final ParameterInstanceController controller;

    @Override
    public ParameterInstanceController getController() {
        return controller;
    }

    AssignMidiCCComponent midiAssign;

    IAxoObjectInstanceView axoObjectInstanceView;

    ParameterInstanceView(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super();
        this.controller = controller;
        this.axoObjectInstanceView = axoObjectInstanceView;
    }

    @Override
    public ParameterInstance getModel() {
        return controller.getModel();
    }

    @Override
    public void PostConstructor() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel lbls = new JPanel();
        lbls.setLayout(new BoxLayout(lbls, BoxLayout.Y_AXIS));
        this.add(lbls);
        label.setText(getModel().getModel().getName());
        lbls.add(label);

        if (getModel().getConvs() != null) {
            lbls.add(valuelbl);
            Dimension d = new Dimension(50, 10);
            valuelbl.setMinimumSize(d);
            valuelbl.setPreferredSize(d);
            valuelbl.setSize(d);
            valuelbl.setMaximumSize(d);
            valuelbl.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getModel().cycleConversions();
                }
            });
            UpdateUnit();
        }

        ctrl = CreateControl();

        add(getControlComponent());
        getControlComponent().addMouseListener(popupMouseListener);

        getControlComponent().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE_ADJ_BEGIN)) {
                    getController().addMetaUndo("change parameter " + getModel().getName());
                } else if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    boolean changed = handleAdjustment();
                    getController().getModel().setNeedsTransmit(true);
                }
            }
        });
    }

    void showOnParent(Boolean onParent) {
        if (onParent == null || onParent == false) {
            setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        } else {
            setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        }
    }

    public void doPopup(MouseEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        m.show(this, 0, getHeight());
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        List<Property> ps = getModel().getEditableFields();
        for (Property p : ps) {
            Component mi = ViewFactory.createMenuItemView(controller, p);
            if (mi != null) {
                m.add(mi);
            }
        }
        JMenu m_preset = new JMenu("Preset");
        // AssignPresetMenuItems, does stuff in ctor
        AssignPresetMenuItems assignPresetMenuItems = new AssignPresetMenuItems(this, m_preset);
        m.add(m_preset);
    }

    /**
     *
     * @return control component
     */
    abstract public ACtrlComponent getControlComponent();

    @Override
    abstract public boolean handleAdjustment();

    public abstract ACtrlComponent CreateControl();

    MouseListener popupMouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
                e.consume();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    void UpdateUnit() {
//        if (getModel().getConvs() != null) {
//            valuelbl.setText(getModel().getConversion().ToReal(
//                    getModel().getValue().));
//        }
    }

    @Override
    public abstract void ShowPreset(int i);

    public int presetEditActive = 0;

    @Override
    public void IncludeInPreset() {
        if (presetEditActive > 0) {
            Preset p = getModel().getPreset(presetEditActive);
            if (p != null) {
                return;
            }
            if (getModel().getPresets() == null) {
                getModel().setPresets(new ArrayList<Preset>());
            }
            p = getModel().presetFactory(presetEditActive, getModel().getValue());
            getModel().getPresets().add(p);
        }
        ShowPreset(presetEditActive);
    }

    @Override
    public void ExcludeFromPreset() {
        if (presetEditActive > 0) {
            Preset p = getModel().getPreset(presetEditActive);
            if (p != null) {
                getModel().getPresets().remove(p);
                if (getModel().getPresets().isEmpty()) {
                    getModel().setPresets(null);
                }
            }
        }
        ShowPreset(presetEditActive);
    }

    public IAxoObjectInstanceView getAxoObjectInstanceView() {
        return axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (ParameterInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (ParameterInstance.DESCRIPTION.is(evt)) {
            setToolTipText((String) evt.getNewValue());
        } else if (ParameterInstance.ON_PARENT.is(evt)) {
            showOnParent((Boolean) evt.getNewValue());
        } else if (ParameterInstance.MIDI_CC.is(evt)) {
            Integer v = (Integer) evt.getNewValue();
            if (midiAssign != null) {
                if (v != null) {
                    midiAssign.setCC(v);
                } else {
                    midiAssign.setCC(-1);
                }
            }
        } else if (ParameterInstance.NOLABEL.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b == null) {
                b = false;
            }
            label.setVisible(!b);
        }
    }

    @Override
    public void dispose() {
    }

}
