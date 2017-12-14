package axoloti.piccolo.parameterviews;

import axoloti.PresetInt;
import axoloti.Theme;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueInt32;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.parameters.ParameterInstanceInt32;
import java.awt.Graphics2D;
import org.piccolo2d.util.PPaintContext;

public abstract class PParameterInstanceViewInt32 extends PParameterInstanceView {

    PParameterInstanceViewInt32(ParameterInstanceInt32 parameterInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(parameterInstance, axoObjectInstanceView);

    }

    @Override
    public ParameterInstanceInt32 getModel() {
        return (ParameterInstanceInt32)controller.getModel();
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
            PresetInt p = getModel().getPreset(presetEditActive);
            if (p != null) {
                setPaint(Theme.getCurrentTheme().Parameter_Preset_Highlight);
                getControlComponent().setValue(p.getValue());
            } else {
                setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
                getControlComponent().setValue(getModel().getValue());
            }
        } else {
            setPaint(Theme.getCurrentTheme().Parameter_Default_Background);
            getControlComponent().setValue(getModel().getValue());
        }
    }

    @Override
    public boolean handleAdjustment() {
        PresetInt p = getModel().getPreset(presetEditActive);
        if (p != null) {
            p.setValue((int) getControlComponent().getValue());
        } else if (getModel().getValue() != (int) getControlComponent().getValue()) {
            parameterInstance.setValue(new ValueInt32((int) getControlComponent().getValue()));
            parameterInstance.setNeedsTransmit(true);
            UpdateUnit();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void CopyValueFrom(PParameterInstanceView p) {
        if (p instanceof PParameterInstanceViewInt32) {
            parameterInstance.CopyValueFrom(((PParameterInstanceViewInt32) p).parameterInstance);
        }
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        if (parameterInstance.getOnParent()) {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
        } else {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
        }
    }
}
