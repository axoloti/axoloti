package axoloti.patch.object;

import axoloti.object.ObjectController;
import axoloti.patch.PatchModel;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.property.Property;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract extension of AxoObjectInstanceAbstract without any inlets,
 * outlets, attributes or parameters
 *
 * @author jtaelman
 */
public abstract class AxoObjectInstance0 extends AxoObjectInstanceAbstract {

    public AxoObjectInstance0() {
        super();
    }

    public AxoObjectInstance0(ObjectController typeController, PatchModel patchModel, String InstanceName1, Point location) {
        super(typeController, patchModel, InstanceName1, location);
    }

    @Override
    public final InletInstance GetInletInstance(String n) {
        return null;
    }

    @Override
    public final OutletInstance GetOutletInstance(String n) {
        return null;
    }

    @Override
    public final List<InletInstance> getInletInstances() {
        return new ArrayList<>();
    }

    @Override
    public final List<OutletInstance> getOutletInstances() {
        return new ArrayList<>();
    }

    @Override
    public final List<ParameterInstance> getParameterInstances() {
        return new ArrayList<>();
    }

    @Override
    public final List<AttributeInstance> getAttributeInstances() {
        return new ArrayList<>();
    }

    @Override
    public final List<DisplayInstance> getDisplayInstances() {
        return new ArrayList<>();
    }

    @Override
    public void Remove() {
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        return l;
    }

}
