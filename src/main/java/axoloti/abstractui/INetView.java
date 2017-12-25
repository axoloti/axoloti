package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.net.NetController;
import java.util.List;

public interface INetView extends IView<NetController> {

    public void updateBounds();

    public void PostConstructor();

    public void setSelected(boolean selected);

    public boolean getSelected();

    public List<IIoletInstanceView> getIoletViews();

    public void setVisible(boolean isVisible);

    public void repaint();
}
