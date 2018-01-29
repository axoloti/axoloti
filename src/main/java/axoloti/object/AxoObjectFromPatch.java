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

import axoloti.abstractui.PatchView;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.patch.PatchViewCodegen;
import axoloti.swingui.patch.PatchFrame;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import qcmds.QCmdProcessor;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectFromPatch extends AxoObject {

    PatchModel patchModel;
    PatchView patchView;
    PatchController patchController;
    PatchFrame pf;
    File f;

    public AxoObjectFromPatch(File f) {
        this.f = f;
        Serializer serializer = new Persister();
        try {
            patchModel = serializer.read(PatchModel.class, f);
            patchModel.setFileNamePath(f.getPath());
            AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
            patchController = new PatchController(patchModel, documentRoot, null);
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        shortId = f.getName().substring(0, f.getName().lastIndexOf("."));
        setPath(f.getAbsolutePath());
        UpdateObject();
        AxoObjects.getAxoObjects().ObjectList.add(this);
        // strip file extension
    }

    final public void UpdateObject() {
        // cheating here by creating a new controller...
        PatchController controller = new PatchController(patchModel, null, null);
        PatchViewCodegen codegen = new PatchViewCodegen(controller);
        AxoObject o = codegen.GenerateAxoObj(new AxoObject());
        attributes = o.getAttributes();
        depends = o.depends;
        displays = o.getDisplays();
        id = f.getName().substring(0, f.getName().length() - 4);
        includes = o.includes;
        inlets = o.getInlets();
        outlets = o.getOutlets();
        params = o.getParameters();
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

    @Override
    public void OpenEditor() {
        if (pf == null) {
            pf = new PatchFrame(patchController, QCmdProcessor.getQCmdProcessor());
        }
        pf.toFront();
    }

    public PatchModel getPatchModel() {
        return patchModel;
    }

}
