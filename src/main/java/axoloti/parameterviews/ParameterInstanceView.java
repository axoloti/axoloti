/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.objectviews.AxoObjectInstanceView;
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
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

public abstract class ParameterInstanceView extends JPanel implements ActionListener {

    ParameterInstance parameterInstance;
    LabelComponent valuelbl = new LabelComponent("123456789");
    ACtrlComponent ctrl;

    AssignMidiCCComponent midiAssign;

    AxoObjectInstanceView axoObjInstanceView;

    ParameterInstanceView(ParameterInstance parameterInstance) {
        super();
        this.parameterInstance = parameterInstance;
    }

    public void PostConstructor() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        JPanel lbls = null;
        if ((((parameterInstance.getParameter().noLabel == null) || (parameterInstance.getParameter().noLabel == false))) && (parameterInstance.getConvs() != null)) {
            lbls = new JPanel();
            lbls.setLayout(new BoxLayout(lbls, BoxLayout.Y_AXIS));
            this.add(lbls);
        }

        if ((parameterInstance.getParameter().noLabel == null) || (parameterInstance.getParameter().noLabel == false)) {
            if (lbls != null) {
                lbls.add(new LabelComponent(parameterInstance.getParameter().name));
            } else {
                add(new LabelComponent(parameterInstance.getParameter().name));
            }
        }

        if (parameterInstance.getConvs() != null) {
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
                    parameterInstance.setSelectedConv(parameterInstance.getSelectedConv() + 1);
                    if (parameterInstance.getSelectedConv() >= parameterInstance.getConvs().length) {
                        parameterInstance.setSelectedConv(0);
                    }
                    UpdateUnit();
                }
            });
            UpdateUnit();
        }

        ctrl = CreateControl();
        if (parameterInstance.getParameter().description != null) {
            ctrl.setToolTipText(parameterInstance.getParameter().description);
        } else {
            ctrl.setToolTipText(parameterInstance.getParameter().name);
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
                if ((valueBeforeAdjustment != getControlComponent().getValue())
                        && (axoObjInstanceView != null)
                        && (axoObjInstanceView.getPatchModel() != null)) {
                    //System.out.println("finished" +getControlComponent().getValue());
                    axoObjInstanceView.getPatchModel().SetDirty();
                }
            }
        });
        updateV();
        parameterInstance.setMidiCC(parameterInstance.getMidiCC());
    }

    double valueBeforeAdjustment;

    public void doPopup(MouseEvent e) {
        JPopupMenu m = new JPopupMenu();
        populatePopup(m);
        m.show(this, 0, getHeight());
    }

    public void populatePopup(JPopupMenu m) {
        final JCheckBoxMenuItem m_onParent = new JCheckBoxMenuItem("parameter on parent");
        m_onParent.setSelected(parameterInstance.isOnParent());
        m.add(m_onParent);
        m_onParent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                parameterInstance.setOnParent(m_onParent.isSelected());
                repaint();
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
            if (i != parameterInstance.getMidiCC()) {
                SetMidiCC(i);
                axoObjInstanceView.getPatchModel().SetDirty();
            }
        } else if (s.equals("none")) {
            if (-1 != parameterInstance.getMidiCC()) {
                SetMidiCC(-1);
                axoObjInstanceView.getPatchModel().SetDirty();
            }
        }
    }

    @Override
    public String getName() {
        if(parameterInstance != null) {
            return parameterInstance.getName();
        }
        else {
            return super.getName();
        }
    }

    void UpdateUnit() {
        if (parameterInstance.getConvs() != null) {
            valuelbl.setText(parameterInstance.getConvs()[parameterInstance.getSelectedConv()].ToReal(parameterInstance.getValue()));
        }
    }

    public void updateV() {
        UpdateUnit();
    }

    void SetMidiCC(Integer cc) {
        parameterInstance.setMidiCC(cc);
        if ((cc != null) && (cc >= 0)) {
            if (midiAssign != null) {
                midiAssign.setCC(cc);
            }
        } else if (midiAssign != null) {
            midiAssign.setCC(-1);
        }
    }

    public void SetValueRaw(int v) {
        parameterInstance.SetValueRaw(v);
        updateV();
    }

    public abstract void ShowPreset(int i);

    public boolean isOnParent() {
        return parameterInstance.isOnParent();
    }

//    public void setOnParent(Boolean b) {
//        if (b == null) {
//            return;
//        }
//        if (isOnParent() == b) {
//            return;
//        }
//        if (b) {
//            parameterInstance.setOnParent(true);
//        } else {
//            parameterInstance.setOnParent(null);
//        }
//    }

    public int presetEditActive = 0;

    public void IncludeInPreset() {
        if (presetEditActive > 0) {
            Preset p = parameterInstance.GetPreset(presetEditActive);
            if (p != null) {
                return;
            }
            if (parameterInstance.getPresets() == null) {
                parameterInstance.setPresets(new ArrayList<Preset>());
            }
            p = new Preset(presetEditActive, parameterInstance.getValue());
            parameterInstance.getPresets().add(p);
        }
        ShowPreset(presetEditActive);
    }

    public void ExcludeFromPreset() {
        if (presetEditActive > 0) {
            Preset p = parameterInstance.GetPreset(presetEditActive);
            if (p != null) {
                parameterInstance.getPresets().remove(p);
                if (parameterInstance.getPresets().isEmpty()) {
                    parameterInstance.setPresets(null);
                }
            }
        }
        ShowPreset(presetEditActive);
    }

    public void CopyValueFrom(ParameterInstanceView p) {
        parameterInstance.CopyValueFrom(p.parameterInstance);
    }

    public void setValue(Value value) {
        parameterInstance.setValue(value);
        updateV();
    }

    public ParameterInstance getParameterInstance() {
        return parameterInstance;
    }

    public Preset AddPreset(int index, Value value) {
        return parameterInstance.AddPreset(index, value);
    }

    public void RemovePreset(int index) {
        parameterInstance.RemovePreset(index);
    }
}
