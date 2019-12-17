/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.patch.object;

import axoloti.object.AxoObjectZombie;
import axoloti.object.IAxoObject;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletZombie;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletZombie;
import axoloti.patch.PatchModel;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import java.awt.Point;
import java.util.Collections;
import java.util.List;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "zombie")
public class AxoObjectInstanceZombie extends AxoObjectInstance {

    public AxoObjectInstanceZombie() {
    }

    public AxoObjectInstanceZombie(IAxoObject obj, PatchModel patch1, String InstanceName1, Point location) {
        super(obj, patch1, InstanceName1, location);
    }

    @Override
    public String getCInstanceName() {
        return "";
    }

    @Override
    public InletInstance findInletInstance(String name) {
        InletInstance inletInstance = super.findInletInstance(name);
        if (inletInstance != null) {
            return inletInstance;
        }
        // add zombie inlet
        AxoObjectZombie obj = (AxoObjectZombie) getDModel();
        Inlet inlet = new InletZombie(name);
        obj.getController().addInlet(inlet);
        // now try again...
        inletInstance = super.findInletInstance(name);
        if (inletInstance != null) {
            return inletInstance;
        }
        throw new Error("zombie inlet failed");
    }

    @Override
    public OutletInstance findOutletInstance(String name) {
        OutletInstance outletInstance = super.findOutletInstance(name);
        if (outletInstance != null) {
            return outletInstance;
        }
        // add zombie outlet
        AxoObjectZombie obj = (AxoObjectZombie) getDModel();
        Outlet outlet = new OutletZombie(name);
        obj.getController().addOutlet(outlet);
        // now try again...
        outletInstance = super.findOutletInstance(name);
        if (outletInstance != null) {
            return outletInstance;
        }
        throw new Error("zombie outlet failed");
    }

//    @Override
//    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
//        return "\n#error \"unresolved object: " + getInstanceName() + " in patch: " + getParent().getFileNamePath() + "\"\n";
//    }
    @Override
    public List<ParameterInstance> getParameterInstances() {
        return Collections.emptyList();
    }

    @Override
    public List<AttributeInstance> getAttributeInstances() {
        return Collections.emptyList();
    }

    @Override
    public List<DisplayInstance> getDisplayInstances() {
        return Collections.emptyList();
    }

    @Override
    public void dispose() {
    }

}
