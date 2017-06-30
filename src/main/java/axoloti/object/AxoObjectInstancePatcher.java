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

    PatchController subPatchController;

    public AxoObjectInstancePatcher() {
        if (subPatchModel == null) {
            subPatchModel = new PatchModel();
        }
    }

    public AxoObjectInstancePatcher(ObjectController controller, PatchModel patch1, String InstanceName1, Point location) {
        this(controller, patch1, InstanceName1, location, new PatchController(new PatchModel(), controller.getDocumentRoot(), controller));
    }

    public AxoObjectInstancePatcher(ObjectController controller, PatchModel patch1, String InstanceName1, Point location, PatchController subPatchController) {
        super(controller, patch1, InstanceName1, location);
        this.subPatchModel = subPatchController.getModel();
        this.subPatchController = subPatchController;
        IView parenting = new IView<PatchController>() {
            @Override
            public void modelPropertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PatchController.PATCH_PARENT_INLETS)) {
                    setInletInstances((ArrayList<InletInstance>) evt.getNewValue());
                } else if (evt.getPropertyName().equals(PatchController.PATCH_PARENT_OUTLETS)) {
                    setOutletInstances((ArrayList<OutletInstance>) evt.getNewValue());
                } else if (evt.getPropertyName().equals(PatchController.PATCH_PARENT_PARAMETERS)) {
                    setParameterInstances((ArrayList<ParameterInstance>) evt.getNewValue());
                }
            }

            @Override
            public PatchController getController() {
                return subPatchController;
            }
        };
        subPatchController.addView(parenting);
    }

    public PatchController getSubPatchController() {
        return subPatchController;
    }

    public PatchModel getSubPatchModel() {
        return subPatchModel;
    }

    @Override
    public void Close() {
        super.Close();
    }

}
