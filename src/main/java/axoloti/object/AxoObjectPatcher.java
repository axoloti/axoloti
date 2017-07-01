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
import java.awt.Point;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectPatcher extends AxoObject {

    public AxoObjectPatcher() {
        super("patch/patcher", "");
    }

    public AxoObjectPatcher(String id, String sDescription) {
        super(id, sDescription);
    }

    public AxoObjectInstancePatcher CreateInstance(PatchController patchController, String InstanceName1, Point location, PatchModel subPatchModel) {
//        AxoObjectPatcher newObj = new AxoObjectPatcher();
        ObjectController ctrl1 = createController(patchController.getDocumentRoot(), null);
        AxoObjectInstancePatcher o = new AxoObjectInstancePatcher(ctrl1, patchController.getModel(), InstanceName1, location, subPatchModel);
        subPatchModel.setContainer(o);
        ctrl1.addView(o);
        return o;
    }

}
