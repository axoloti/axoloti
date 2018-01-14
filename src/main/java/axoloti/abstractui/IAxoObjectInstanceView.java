package axoloti.abstractui;

import axoloti.patch.object.inlet.InletInstance;
import axoloti.mvc.IView;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.PatchModel;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import javax.swing.JComponent;

public interface IAxoObjectInstanceView extends IView<ObjectInstanceController> {

    public IAxoObjectInstance getModel();

    public void Lock();

    public void Unlock();

    public boolean isLocked();

    public void PostConstructor();

    public PatchView getPatchView();

    public PatchModel getPatchModel();

    public IIoletInstanceView getInletInstanceView(InletInstance inletInstance);

    public IIoletInstanceView getOutletInstanceView(OutletInstance ouletInstance);

    public List<IIoletInstanceView> getInletInstanceViews();

    public List<IIoletInstanceView> getOutletInstanceViews();

    public List<IParameterInstanceView> getParameterInstanceViews();

    public void setLocation(int x, int y);

    public void addInstanceNameEditor();

    public void showInstanceName(String InstanceName);

    public void moveToFront();

    public void resizeToGrid();

    public Point getLocation();

    public void repaint();

    public Dimension getPreferredSize();

    public Dimension getSize();

    public void addParameterInstanceView(IParameterInstanceView view);

    public void addAttributeInstanceView(IAttributeInstanceView view);

    public void addDisplayInstanceView(IDisplayInstanceView view);

    public void addOutletInstanceView(IIoletInstanceView view);

    public void addInletInstanceView(IIoletInstanceView view);

    public JComponent getCanvas();

    public boolean isZombie();


}
