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
import java.awt.Point;
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
        super(controller, patch1, InstanceName1, location);        
    }
    
    
    public AxoObjectInstancePatcher(ObjectController controller, PatchModel patch1, String InstanceName1, Point location, PatchModel subPatchModel) {
        super(controller, patch1, InstanceName1, location);
        if (subPatchModel == null) {
            subPatchModel = new PatchModel();
        }
        this.subPatchModel = subPatchModel;
    }

    public PatchModel getSubPatchModel() {
        return subPatchModel;
    }

    @Override
    @Deprecated
    public void updateObj1() {
        /*
         if (getSubPatchModel() != null) {
         // cheating here by creating a new controller...
         PatchViewCodegen codegen = new PatchViewCodegen(subPatchController);
         AxoObject ao = codegen.GenerateAxoObj(new AxoObjectPatcher());
         setType(ao);
         ao.id = "patch/patcher";
         ao.sDescription = getSubPatchModel().getNotes();
         ao.sLicense = getSubPatchModel().getSettings().getLicense();
         ao.sAuthor = getSubPatchModel().getSettings().getAuthor();
         getSubPatchModel().setContainer(getPatchModel());
         }*/
    }

    @Override
    @Deprecated
    public void updateObj() {
//        if (getSubPatchModel() != null) {
//            // cheating here by creating a new controller...
//            PatchController controller = new PatchController(getSubPatchModel(), null);
//            PatchViewCodegen codegen = new PatchViewCodegen(controller);
//            AxoObject ao = codegen.GenerateAxoObj(new AxoObjectPatcher());
//
//            setType(ao);
//        }
    }

    @Override
    public void Close() {
        super.Close();
    }

}
