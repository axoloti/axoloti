package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.iolet.IoletInstance;
import java.awt.Point;

public interface IIoletInstanceView<T extends IoletInstance> extends IView<T> {

    void setHighlighted(boolean highlighted);

    void repaint();

    Point getJackLocInCanvas();

    IAxoObjectInstanceView getObjectInstanceView();

}
