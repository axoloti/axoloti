package axoloti.parameterviews;

import axoloti.Preset;
import axoloti.Theme;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32;
import java.awt.Graphics;

public abstract class ParameterInstanceViewInt32 extends ParameterInstanceView {

    ParameterInstanceViewInt32(ParameterInstanceInt32 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);
    }

    @Override
    public void setValue(Value value) {
        parameterInstance.setValue(value);
        updateV();
    }

    @Override
    public void ShowPreset(int i) {
        presetEditActive = i;
        if (i > 0) {
            Preset p = parameterInstance.GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.value.getDouble());
            } else {
                setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(parameterInstance.getValue().getDouble());
            }
        } else {
            setBackground(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(parameterInstance.getValue().getDouble());
        }
    }

    @Override
    public boolean handleAdjustment() {
        Preset p = parameterInstance.GetPreset(presetEditActive);
        if (p != null) {
            p.value = new ValueInt32((int) getControlComponent().getValue());
        } else if (parameterInstance.getValue().getInt() != (int) getControlComponent().getValue()) {
            parameterInstance.getValue().setInt((int) getControlComponent().getValue());
            parameterInstance.setNeedsTransmit(true);
            UpdateUnit();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (parameterInstance.isOnParent()) {
            setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        } else {
            setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        }
        super.paintComponent(g);
    }
}
