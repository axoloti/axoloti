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
package axoloti.object;

import axoloti.PatchModel;
import axoloti.attribute.AttributeInstance;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceZombie;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceZombie;
import axoloti.parameters.ParameterInstance;
import axoloti.property.Property;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "zombie")
public class AxoObjectInstanceZombie extends AxoObjectInstanceAbstract {

    public List<InletInstance> inletInstances = new ArrayList<>();
    public List<OutletInstance> outletInstances = new ArrayList<>();

    public AxoObjectInstanceZombie() {
    }

    public AxoObjectInstanceZombie(ObjectController type, PatchModel patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
    }

    public String getCInstanceName() {
        return "";
    }

    @Override
    public InletInstance GetInletInstance(String n) {
        if (inletInstances != null) {
            for (InletInstance i : inletInstances) {
                if (i.GetLabel().equals(n)) {
                    return i;
                }
            }
        }
        InletInstance i = new InletInstanceZombie(this, n);
        inletInstances.add(i);
        return i;
    }

    @Override
    public OutletInstance GetOutletInstance(String n) {
        if (outletInstances != null) {
            for (OutletInstance i : outletInstances) {
                if (n.equals(i.GetLabel())) {
                    return i;
                }
            }
        }
        OutletInstance i = new OutletInstanceZombie(this, n);
        outletInstances.add(i);
        return i;
    }

//    @Override
//    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
//        return "\n#error \"unresolved object: " + getInstanceName() + " in patch: " + getPatchModel().getFileNamePath() + "\"\n";
//    }
    @Override
    public List<InletInstance> getInletInstances() {
        return inletInstances;
    }

    @Override
    public List<OutletInstance> getOutletInstances() {
        return outletInstances;
    }

    @Override
    public List<ParameterInstance> getParameterInstances() {
        return new ArrayList<>();
    }

    @Override
    public List<AttributeInstance> getAttributeInstances() {
        return new ArrayList<>();
    }

    @Override
    public List<DisplayInstance> getDisplayInstances() {
        return new ArrayList<>();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void Remove() {
    }    

    @Override
    public List<Property> getProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void dispose() {
    }

}
