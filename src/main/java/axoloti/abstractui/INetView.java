package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.net.NetController;
import java.util.List;

public interface INetView extends IView<NetController> {

    void updateBounds();

    void setSelected(boolean selected);

    boolean getSelected();

    List<IIoletInstanceView> getIoletViews();

    void setVisible(boolean isVisible); // TODO: review

    void repaint();
}
