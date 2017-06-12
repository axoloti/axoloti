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
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceZombie;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.array.ArrayModel;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceZombie;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "zombie")
public class AxoObjectInstanceZombie extends AxoObjectInstanceAbstract {

    public ArrayModel<InletInstance> inletInstances = new ArrayModel<InletInstance>();
    public ArrayModel<OutletInstance> outletInstances = new ArrayModel<OutletInstance>();

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
    public ArrayModel<InletInstance> getInletInstances() {
        return inletInstances;
    }

    @Override
    public ArrayModel<OutletInstance> getOutletInstances() {
        return outletInstances;
    }

    @Override
    public ObjectInstanceController createController(AbstractDocumentRoot documentRoot) {
        return new ObjectInstanceController(this, documentRoot);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {        
    }

}
