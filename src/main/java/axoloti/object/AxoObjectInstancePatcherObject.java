/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
import axoloti.PatchView;
import axoloti.objecteditor.AxoObjectEditor;
import axoloti.objectviews.AxoObjectInstanceViewPatcherObject;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcherObject extends AxoObjectInstance {

    public AxoObjectEditor aoe;

    @Element(name = "object")
    AxoObjectPatcherObject ao;

    public AxoObjectInstancePatcherObject() {
    }

    public AxoObjectInstancePatcherObject(AxoObject type, PatchModel patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
    }

    @Override
    public AxoObjectInstanceViewPatcherObject ViewFactory(PatchView patchView) {
        return new AxoObjectInstanceViewPatcherObject(this, patchView);
    }

    public AxoObject getAxoObject() {
        return ao;
    }

    public void setAxoObject(AxoObjectPatcherObject axoObject) {
        this.ao = axoObject;
    }

    @Override
    public void updateObj1() {
        if (getAxoObject() == null) {
            setAxoObject(new AxoObjectPatcherObject());
            getAxoObject().id = "patch/object";
            getAxoObject().sDescription = "";
        }
        setType(getAxoObject());
        /*
         if (pg != null) {
         AxoObject ao = pg.GenerateAxoObj();
         setType(ao);
         pg.container(patch);
         }
         */
    }

    @Override
    public void updateObj() {
        if (getAxoObject() != null) {
            getAxoObject().id = "patch/object";
            setType(getAxoObject());
            this.setDirty(true);
            getPatchModel().SetDirty();
        }
    }

    @Override
    public void Close() {
        super.Close();
        if (aoe != null) {
            aoe.Close();
        }
    }
}
