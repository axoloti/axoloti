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
import axoloti.inlets.InletInstance;
import axoloti.mvc.IView;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcher extends AxoObjectInstance {

    @Element(name = "subpatch")
    PatchModel subPatchModel;

    public AxoObjectInstancePatcher() {
        if (subPatchModel == null) {
            subPatchModel = new PatchModel();
        }
    }

    public AxoObjectInstancePatcher(ObjectController controller, PatchModel patch1, String InstanceName1, Point location) {
        this(controller, patch1, InstanceName1, location, new PatchModel());
    }

    public AxoObjectInstancePatcher(ObjectController controller, PatchModel patch1, String InstanceName1, Point location, PatchModel subPatchModel) {
        super(controller, patch1, InstanceName1, location);
        this.subPatchModel = subPatchModel;
    }

    public PatchModel getSubPatchModel() {
        return subPatchModel;
    }

    @Override
    public void Close() {
        super.Close();
    }

}
