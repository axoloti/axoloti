package axoloti.parameterviews;

import axoloti.Preset;
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
        if (getModel().getModel().description != null) {
            ctrl.setToolTipText(getModel().getModel().description);
        } else {
            ctrl.setToolTipText(getModel().getModel().getName());
        }
        add(getControlComponent());
        getControlComponent().addMouseListener(popupMouseListener);
        getControlComponent().addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                boolean changed = handleAdjustment();
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
                valueBeforeAdjustment = getControlComponent().getValue();
                //System.out.println("begin "+value_before);
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
            }
        });
        updateV();
        getModel().setMidiCC(getModel().getMidiCC());
    }

    double valueBeforeAdjustment;

    public void doPopup(MouseEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        m.show(this, 0, getHeight());
    }

    public void populatePopup(JPopupMenu m) {
        final JCheckBoxMenuItem m_onParent = new JCheckBoxMenuItem("parameter on parent");
        m_onParent.setSelected(getModel().getOnParent());
        m.add(m_onParent);
        m_onParent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getModel().setOnParent(m_onParent.isSelected());
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
        String s = e.getActionCommand();
        if (s.startsWith("CC")) {
            int i = Integer.parseInt(s.substring(2));
            if (i != getModel().getMidiCC()) {
                SetMidiCC(i);
            }
        } else if (s.equals("none")) {
            if (-1 != getModel().getMidiCC()) {
                SetMidiCC(-1);
            }
        }
    }

    @Override
    public String getName() {
        if (getModel() != null) {
            return getModel().getName();
        } else {
            return super.getName();
        }
    }

    void UpdateUnit() {
        if (getModel().getConvs() != null) {
            valuelbl.setText(getModel().getConvs()[getModel().getSelectedConv()].ToReal(getModel().getValue()));
        }
    }

    public void updateV() {
        UpdateUnit();
    }

    public void SetMidiCC(Integer cc) {
        getModel().setMidiCC(cc);
        if ((cc != null) && (cc >= 0)) {
            if (midiAssign != null) {
                midiAssign.setCC(cc);
            }
        } else if (midiAssign != null) {
            midiAssign.setCC(-1);
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

    public Preset AddPreset(int index, Value value) {
        return getModel().AddPreset(index, value);
    }

    public void RemovePreset(int index) {
        getModel().RemovePreset(index);
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
            repaint();
        }
        else {
            updateV();
        }
    }
}
