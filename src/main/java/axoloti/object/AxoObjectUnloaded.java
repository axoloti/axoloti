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

import axoloti.PatchController;
import axoloti.PatchModel;
import axoloti.inlets.Inlet;
import axoloti.mvc.array.ArrayModel;
import axoloti.outlets.Outlet;
import java.awt.Point;
import java.io.File;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class AxoObjectUnloaded extends AxoObjectAbstract {

    File f;

    public AxoObjectUnloaded() {
        super();
    }

    public AxoObjectUnloaded(String id, File f) {
        super(id, "");
        this.f = f;
    }

    AxoObjectFromPatch loadedObject;

    public AxoObjectFromPatch Load() {
        if (loadedObject == null) {
            loadedObject = new AxoObjectFromPatch(f);
            loadedObject.id = id;
        }
        return loadedObject;
    }

    /*
    @Override
    public AxoObjectInstance CreateInstance(PatchController patchController, String InstanceName1, Point location) {
        Load();
        AxoObjectInstance oi = new AxoObjectInstance(loadedObject.createController(null, null), patchController.getModel(), InstanceName1, location);
        if (patchController.getModel() != null) {
            patchController.getModel().objectinstances.add(oi);
        }
        return oi;
    }*/
    @Override
    public String GenerateUUID() {
        return "unloaded";
    }

    @Override
    public ArrayModel<Inlet> getInlets() {
        return null;
    }

    @Override
    public ArrayModel<Outlet> getOutlets() {
        return null;
    }
}
