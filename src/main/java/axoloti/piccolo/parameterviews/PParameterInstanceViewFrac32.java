package axoloti.piccolo.parameterviews;

import axoloti.Modulation;
import axoloti.PresetDouble;
import axoloti.datatypes.ValueFrac32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceFrac32;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public abstract class PParameterInstanceViewFrac32 extends PParameterInstanceView {

    PParameterInstanceViewFrac32(ParameterInstanceFrac32 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public ParameterInstanceFrac32 getModel() {
        return (ParameterInstanceFrac32) parameterInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        if (getModel().getModulators() != null) {
            List<Modulation> modulators = getModel().getModulators();
            for (Modulation m : modulators) {
                System.out.println("mod amount " + m.getValue());
                m.PostConstructor(getModel());
            }
        }
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenuItem m_default = new JMenuItem("Reset to default value");
        m_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //getModel().applyDefaultValue();
                updateV();
                getControlComponent().setValue(getModel().getValue());
                handleAdjustment();
            }
        });
        m.add(m_default);
    }

    @Override
    public boolean handleAdjustment() {
        PresetDouble p = getModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue(getControlComponent().getValue());
        } else if (getModel().getValue() != getControlComponent().getValue()) {
            getModel().setValue(new ValueFrac32(getControlComponent().getValue()));
            getModel().setNeedsTransmit(true);
            UpdateUnit();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void CopyValueFrom(PParameterInstanceView p) {
        if (p instanceof PParameterInstanceViewFrac32) {
            getModel().CopyValueFrom(((PParameterInstanceViewFrac32) p).parameterInstance);
        }
    }

    public void updateModulation(int index, double amount) {
        getModel().updateModulation(index, amount);
    }
}
