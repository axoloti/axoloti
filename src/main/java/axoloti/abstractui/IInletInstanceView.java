package axoloti.abstractui;

import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.inlet.InletInstanceController;
import axoloti.mvc.IView;
import java.awt.Point;

public interface IInletInstanceView extends IView<InletInstanceController> {

    public void PostConstructor();

    public InletInstance getModel();

    public void setHighlighted(boolean highlighted);

    public void repaint();

    public Point getJackLocInCanvas();

    public IAxoObjectInstanceView getObjectInstanceView();

    public void setAlignmentX(float alignmentX);

    public void setAlignmentY(float alignmentY);
}
