package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.net.Net;
import java.util.List;

public interface INetView extends IView<Net> {

    void updateBounds();

    void setSelected(boolean selected);

    boolean getSelected();

    List<IInletInstanceView> getInletViews();

    List<IOutletInstanceView> getOutletViews();

    void setVisible(boolean isVisible);

    void repaint();
}
