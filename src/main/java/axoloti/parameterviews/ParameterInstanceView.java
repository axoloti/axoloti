package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.atom.AtomDefinitionController;
import axoloti.datatypes.Value;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceController;
import axoloti.parameters.ParameterInstance;
import components.AssignMidiCCComponent;
import components.AssignPresetMenuItems;
import components.LabelComponent;
import components.control.ACtrlComponent;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
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

    public ParameterInstance getModel() {
        return controller.getModel();
    }

    public void PostConstructor() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel lbls = null;
        if ((((getModel().getModel().noLabel == null) || (getModel().getModel().noLabel == false))) && (getModel().getConvs() != null)) {
            lbls = new JPanel();
            lbls.setLayout(new BoxLayout(lbls, BoxLayout.Y_AXIS));
            this.add(lbls);
        }

        if ((getModel().getModel().noLabel == null) || (getModel().getModel().noLabel == false)) {
            label.setText(getModel().getModel().getName());
        }
        if (lbls != null) {
            lbls.add(label);
        } else {
            add(label);
        }

        if (getModel().getConvs() != null) {
            if (lbls != null) {
                lbls.add(valuelbl);
            } else {
                add(valuelbl);
            }
            Dimension d = new Dimension(50, 10);
            valuelbl.setMinimumSize(d);
            valuelbl.setPreferredSize(d);
            valuelbl.setSize(d);
            valuelbl.setMaximumSize(d);
            valuelbl.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getModel().setSelectedConv(getModel().getSelectedConv() + 1);
                    if (getModel().getSelectedConv() >= getModel().getConvs().length) {
                        getModel().setSelectedConv(0);
                    }
                    UpdateUnit();
                }
            });
            UpdateUnit();
        }

        ctrl = CreateControl();

        add(getControlComponent());
        getControlComponent().addMouseListener(popupMouseListener);
        getControlComponent().addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                boolean changed = handleAdjustment();
                getController().getModel().setNeedsTransmit(true);
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                getController().addMetaUndo("change parameter " + getModel().getName());
            }
            
            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
            }
        });
    }

    void showOnParent(boolean onParent){
        if (getModel().getOnParent()) {
            setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        } else {
            setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);               
        }
    }

    public void doPopup(MouseEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        m.show(this, 0, getHeight());
    }

    public void populatePopup(JPopupMenu m) {
        final JCheckBoxMenuItem m_onParent = new JCheckBoxMenuItem("parameter on parent");
        boolean op = getModel().getOnParent();
        m_onParent.setSelected(op);
        m.add(m_onParent);
        m_onParent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (op) {
                    getController().addMetaUndo("set parameter on parameter of " + getModel().getName());
                } else {
                    getController().addMetaUndo("clear parameter on parameter of " + getModel().getName());
                }
                getController().setModelUndoableProperty(ParameterInstanceController.ELEMENT_PARAM_ON_PARENT, !op);
            }
        });

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
        if (getModel().getConvs() != null) {
            valuelbl.setText(getModel().getConvs()[getModel().getSelectedConv()].ToReal(getModel().getValue()));
        }
    }

    public abstract void ShowPreset(int i);

    public int presetEditActive = 0;

    public void IncludeInPreset() {
        if (presetEditActive > 0) {
            Preset p = getModel().GetPreset(presetEditActive);
            if (p != null) {
                return;
            }
            if (getModel().getPresets() == null) {
                getModel().setPresets(new ArrayList<Preset>());
            }
            p = new Preset(presetEditActive, getModel().getValue());
            getModel().getPresets().add(p);
        }
        ShowPreset(presetEditActive);
    }

    public void ExcludeFromPreset() {
        if (presetEditActive > 0) {
            Preset p = getModel().GetPreset(presetEditActive);
            if (p != null) {
                getModel().getPresets().remove(p);
                if (getModel().getPresets().isEmpty()) {
                    getModel().setPresets(null);
                }
            }
        }
        ShowPreset(presetEditActive);
    }

    final public Preset AddPreset(int index, Value value) {
        return getController().AddPreset(index, value);
    }

    final public void RemovePreset(int index) {
        getController().RemovePreset(index);
    }

    public IAxoObjectInstanceView getAxoObjectInstanceView() {
        return axoObjectInstanceView;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            setToolTipText((String) evt.getNewValue());
        } else if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_ON_PARENT)) {
            showOnParent((Boolean)evt.getNewValue());
        } else if (evt.getPropertyName().equals(ParameterInstanceController.ELEMENT_PARAM_MIDI_CC)) {
            Integer v = (Integer) evt.getNewValue();
            if (midiAssign != null) {
                if (v != null) {
                    midiAssign.setCC(v);
                } else {
                    midiAssign.setCC(-1);
                }
            }
        }
    }
}
