package axoloti.patch.object;

import axoloti.patch.Modulator;
import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.property.Property;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract extension of AxoObjectInstanceAbstract without any inlets,
 * outlets, attributes or parameters
 *
 * @author jtaelman
 */
abstract class AxoObjectInstance0 extends AxoObjectInstanceAbstract {

    protected AxoObjectInstance0() {
        super();
    }

    protected AxoObjectInstance0(IAxoObject obj, PatchModel patchModel, String InstanceName1, Point location) {
        super(obj, patchModel, InstanceName1, location);
    }

    @Override
    public final InletInstance findInletInstance(String n) {
        return null;
    }

    @Override
    public final OutletInstance findOutletInstance(String n) {
        return null;
    }

    @Override
    public final List<InletInstance> getInletInstances() {
        return Collections.emptyList();
    }

    @Override
    public final List<OutletInstance> getOutletInstances() {
        return Collections.emptyList();
    }

    @Override
    public final List<ParameterInstance> getParameterInstances() {
        return Collections.emptyList();
    }

    @Override
    public final List<AttributeInstance> getAttributeInstances() {
        return Collections.emptyList();
    }

    @Override
    public final List<DisplayInstance> getDisplayInstances() {
        return Collections.emptyList();
    }

    @Override
    public List<Modulator> getModulators() {
        return Collections.emptyList();
    }

    @Override
    public void setModulators(List<Modulator> modulators) {
        throw new Error("setModulators " + modulators);
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        return l;
    }

}
