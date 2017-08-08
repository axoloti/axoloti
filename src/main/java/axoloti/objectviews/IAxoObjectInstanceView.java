package axoloti.objectviews;

import axoloti.PatchModel;
import axoloti.PatchView;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.parameterviews.IParameterInstanceView;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import axoloti.mvc.IView;
import axoloti.object.IAxoObjectInstance;
import java.util.List;

public interface IAxoObjectInstanceView extends IView<ObjectInstanceController> {

    public IAxoObjectInstance getModel();

    public void Lock();

    public void Unlock();

    public boolean isLocked();

    public void PostConstructor();

    public PatchView getPatchView();

    public PatchModel getPatchModel();

    public IInletInstanceView getInletInstanceView(InletInstance inletInstance);

    public IOutletInstanceView getOutletInstanceView(OutletInstance ouletInstance);

    public List<IInletInstanceView> getInletInstanceViews();

    public List<IOutletInstanceView> getOutletInstanceViews();

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

    public void addOutletInstanceView(IOutletInstanceView view);

    public void addInletInstanceView(IInletInstanceView view);

    public JComponent getCanvas();

    public boolean isZombie();

    public void dispose();

}
