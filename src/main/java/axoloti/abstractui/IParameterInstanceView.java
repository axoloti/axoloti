package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import javax.swing.JPopupMenu;

public interface IParameterInstanceView extends IView<ParameterInstanceController> {

    void populatePopup(JPopupMenu m);

    boolean handleAdjustment();

    void ShowPreset(int i);

    void IncludeInPreset();

    void ExcludeFromPreset();

    ParameterInstance getModel();

}
