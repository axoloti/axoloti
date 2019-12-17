package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.parameter.ParameterInstance;
import javax.swing.JPopupMenu;

public interface IParameterInstanceView extends IView<ParameterInstance> {

    void populatePopup(JPopupMenu m);

    boolean handleAdjustment();

    void update();

}
