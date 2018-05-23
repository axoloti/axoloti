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

import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcher extends AxoObjectInstance {

    @Element(name = "subpatch")
    PatchModel subPatchModel = null;

    public AxoObjectInstancePatcher() {
    }

    public AxoObjectInstancePatcher(IAxoObject obj, PatchModel patch1, String InstanceName1, Point location) {
        super(obj, patch1, InstanceName1, location);
        //subPatchModel.setFileNamePath(InstanceName1); // TODO: review
    }

    public void setSubPatchModel(PatchModel subPatchModel) {
        this.subPatchModel = subPatchModel;
        subPatchModel.setParent(this);
        subPatchModel.getController();
        PatchModel parentPatch = getParent();
        if (parentPatch != null) {
            // parentPatch is null in objectselector...
            this.subPatchModel.setDocumentRoot(parentPatch.getDocumentRoot());
        }
    }

    public PatchModel getSubPatchModel() {
        if (subPatchModel == null) {
            subPatchModel = new PatchModel();
            subPatchModel.setParent(this);
        }
        return subPatchModel;
    }

    @Override
    public void applyValues(IAxoObjectInstance sourceObject) {
        if (sourceObject instanceof AxoObjectInstancePatcher) {
            setSubPatchModel(((AxoObjectInstancePatcher) sourceObject).getSubPatchModel());
        }
        getDModel().getController().addView(this);
        super.applyValues(sourceObject);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean b = super.setInstanceName(s);
        subPatchModel.setFileNamePath(s);
        return b;
    }

    @Override
    protected ObjectInstancePatcherController createController() {
        return new ObjectInstancePatcherController(this);
    }

    @Override
    public ObjectInstancePatcherController getController() {
        return (ObjectInstancePatcherController) super.getController();
    }

}
