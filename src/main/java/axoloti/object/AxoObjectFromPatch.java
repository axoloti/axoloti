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

import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.display.Display;
import axoloti.object.inlet.Inlet;
import axoloti.object.outlet.Outlet;
import axoloti.object.parameter.Parameter;
import axoloti.objectlibrary.AxoObjects;
import axoloti.patch.PatchModel;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectFromPatch extends AxoObject {

    private PatchModel patchModel;
    private final File f;

    public AxoObjectFromPatch(File f) throws FileNotFoundException {
        this.f = f;
        patchModel = PatchModel.open(f);
        shortId = f.getName().substring(0, f.getName().lastIndexOf('.'));
        setPath(f.getAbsolutePath());
        updateObject();
        init1();
    }

    private void init1() {
        // TODO: review, perhaps source of multiple entries of eg. fx/flanger in objectselector
        AxoObjects.getAxoObjects().objectList.add(this);
        // strip file extension
    }

    final public void updateObject() {
        PatchViewCodegen codegen = new PatchViewCodegen(patchModel);
        AxoObject o = codegen.generateAxoObj(new AxoObject());
        attributes = o.getAttributes();
        depends = o.depends;
        displays = o.getDisplays();
        id = f.getName().substring(0, f.getName().length() - 4);
        includes = o.includes;
        inlets = o.getInlets();
        outlets = o.getOutlets();
        params = o.getParameters();

        for (AxoAttribute a : attributes) {
            a.setParent(this);
        }
        for (Inlet i : inlets) {
            i.setParent(this);
        }
        for (Outlet i : outlets) {
            i.setParent(this);
        }
        for (Parameter i : params) {
            i.setParent(this);
        }
        for (Display i : displays) {
            i.setParent(this);
        }

        setAuthor(o.getAuthor());
        setDescription(o.getDescription());
        sDisposeCode = o.sDisposeCode;
        sInitCode = o.sInitCode;
        sKRateCode = o.sKRateCode;
        setLicense(o.getLicense());
        sLocalData = o.sLocalData;
        sMidiCode = o.sMidiCode;
        sSRateCode = o.sSRateCode;
        helpPatch = o.helpPatch;
    }

    public PatchModel getPatchModel() {
        return patchModel;
    }

}
