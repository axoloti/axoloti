package axoloti.abstractui;

import java.awt.Point;

import axoloti.mvc.IView;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;

public interface IIoletInstanceView extends IView<IoletInstanceController> {

    public void PostConstructor();

    public IoletInstance getModel();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);
}
