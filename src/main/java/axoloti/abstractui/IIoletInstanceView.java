package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import java.awt.Point;

public interface IIoletInstanceView extends IView<IoletInstanceController> {

    IoletInstance getModel();

    void setHighlighted(boolean highlighted);

    void repaint();

    Point getJackLocInCanvas();

    IAxoObjectInstanceView getObjectInstanceView();

}
