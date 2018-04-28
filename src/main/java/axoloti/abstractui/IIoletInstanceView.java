package axoloti.abstractui;

import axoloti.mvc.IView;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import java.awt.Point;

public interface IIoletInstanceView extends IView<IoletInstanceController> {

    public IoletInstance getModel();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);
}
