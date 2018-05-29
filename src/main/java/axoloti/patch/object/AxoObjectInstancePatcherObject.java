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
package axoloti.patch.object;

import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcherObject extends AxoObjectInstance {

    @Element(name = "object")
    private AxoObjectPatcherObject ao = null;

    public AxoObjectInstancePatcherObject() {
        if (ao == null) {
            ao = new AxoObjectPatcherObject();
        }
    }

    public AxoObjectInstancePatcherObject(AxoObjectPatcherObject obj, PatchModel patch1, String InstanceName1, Point location) {
        super(obj, patch1, InstanceName1, location);
        ao = obj;
        if (patch1 != null) {
            ao.setDocumentRoot(patch1.getDocumentRoot());
        } else {
            System.out.println("AxoObjectInstancePatcherObject: no patch");
        }
        ao.setId("patch/object");
    }

    @Override
    public AxoObject getDModel() {
        return ao;
    }

    @Override
    public IAxoObject resolveType(String directory) {
        return ao;
    }

    @Override
    public void applyValues(IAxoObjectInstance sourceObject) {
        if (sourceObject instanceof AxoObjectInstancePatcherObject) {
            ao = ((AxoObjectInstancePatcherObject) sourceObject).ao;
        }
        ao.getController().addView(this);
        super.applyValues(sourceObject);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean b = super.setInstanceName(s);
        ao.setId(getInstanceName());
        return b;
    }
    
    @Override
    public void setDocumentRoot(AbstractDocumentRoot documentRoot) {
        super.setDocumentRoot(documentRoot);
        ao.setDocumentRoot(documentRoot);
    }

}
