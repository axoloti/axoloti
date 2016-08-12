package axoloti.parameterviews;

import axoloti.Modulation;
import axoloti.Preset;
import axoloti.datatypes.ValueFrac32;
import axoloti.parameters.ParameterInstanceFrac32;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public abstract class ParameterInstanceViewFrac32 extends ParameterInstanceView {
    ParameterInstanceViewFrac32(ParameterInstanceFrac32 parameterInstance) {
        super(parameterInstance);
    }
    
    @Override
    public ParameterInstanceFrac32 getParameterInstance() {
        return (ParameterInstanceFrac32) this.parameterInstance;
    }
    
    @Override
    public void PostConstructor() {
        super.PostConstructor();
        if (getParameterInstance().getModulators() != null) {
            List<Modulation> modulators = getParameterInstance().getModulators();
            for (Modulation m : modulators) {
                System.out.println("mod amount " + m.getValue().getDouble());
                m.PostConstructor(getParameterInstance());
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
                getParameterInstance().applyDefaultValue();
                getControlComponent().setValue(getParameterInstance().getValue().getDouble());
                handleAdjustment();
            }
        });
        m.add(m_default);
    }
    
    @Override
    public boolean handleAdjustment() {
        Preset p = getParameterInstance().GetPreset(presetEditActive);
        if (p != null) {
            p.value = new ValueFrac32(getControlComponent().getValue());
        } else {
            if (getParameterInstance().getValue().getDouble() != getControlComponent().getValue()) {
                getParameterInstance().getValue().setDouble(getControlComponent().getValue());
                getParameterInstance().setNeedsTransmit(true);
                UpdateUnit();
            } else {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void CopyValueFrom(ParameterInstanceView p) {
        if (p instanceof ParameterInstanceViewFrac32) {
            getParameterInstance().CopyValueFrom(((ParameterInstanceViewFrac32) p).parameterInstance);
        }
    }
    
    public void updateModulation(int index, double amount) {
        getParameterInstance().updateModulation(index, amount);
    }
}