package axoloti.piccolo.patch.object.parameter;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.parameter.ParameterInstanceBin;
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.preferences.Theme;
import axoloti.preset.PresetInt;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import org.piccolo2d.util.PPaintContext;

public abstract class PParameterInstanceViewBin extends PParameterInstanceView {

    PParameterInstanceViewBin(ParameterInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);

    }

    @Override
    public ParameterInstanceBin getModel() {
        return (ParameterInstanceBin)controller.getModel();
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
            if (getController() != null) {
                Integer vi32 = (int) getControlComponent().getValue();
                getController().changeValue(vi32);
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

//        if (parameterInstance.getOnParent()) {
//            ctrl.setForeground(Theme.getCurrentTheme().Parameter_On_Parent_Highlight);
//        } else {
            ctrl.setForeground(Theme.getCurrentTheme().Parameter_Default_Foreground);
//        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (ParameterInstanceBin.VALUE.is(evt)) {
            Integer v = (Integer)evt.getNewValue();
            ctrl.setValue(v);
        }
    }
}
