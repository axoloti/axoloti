package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import javax.swing.JPopupMenu;

public interface IParameterInstanceView extends IView<ParameterInstanceController> {

    public void populatePopup(JPopupMenu m);

    public boolean handleAdjustment();

    public void ShowPreset(int i);

    public void IncludeInPreset();

    public void ExcludeFromPreset();

    public ParameterInstance getModel();

}
