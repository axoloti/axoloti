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

import axoloti.MainFrame;
import axoloti.PatchController;
import axoloti.PatchFrame;
import axoloti.PatchModel;
import axoloti.PatchView;
import axoloti.PatchViewCodegen;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcher extends AxoObjectInstance {

    public PatchFrame pf;

    @Element(name = "subpatch")
    PatchModel subPatchModel;

    public AxoObjectInstancePatcher() {
    }

    public AxoObjectInstancePatcher(AxoObject type, PatchModel patch1, String InstanceName1, Point location) {
        super(type.createController(null), patch1, InstanceName1, location);
    }

    public PatchModel getSubPatchModel() {
        return subPatchModel;
    }

    public void setSubPatchModel(PatchModel subPatchModel) {
        this.subPatchModel = subPatchModel;
    }

    @Override
    public void updateObj1() {
        init();
        if (getSubPatchModel() != null) {
            // cheating here by creating a new controller...
            PatchController controller = new PatchController(getSubPatchModel(), null);
            PatchViewCodegen codegen = new PatchViewCodegen(getSubPatchModel(), controller);
            AxoObject ao = codegen.GenerateAxoObj(new AxoObjectPatcher());

            setType(ao);
            ao.id = "patch/patcher";
            ao.sDescription = getSubPatchModel().getNotes();
            ao.sLicense = getSubPatchModel().getSettings().getLicense();
            ao.sAuthor = getSubPatchModel().getSettings().getAuthor();
            getSubPatchModel().setContainer(getPatchModel());
        }
    }

    public void initSubpatchFrame() {
        PatchController patchController = getSubPatchModel().createController(null); /* FIXME: null */
        PatchView patchView = MainFrame.prefs.getPatchView(patchController);
        pf = new PatchFrame(patchController, patchView, MainFrame.mainframe.getQcmdprocessor());
        patchController.addView(patchView);
        patchView.setPatchFrame(pf);
        patchView.setFileNamePath(getInstanceName());
        patchView.PostConstructor();
    }

    public void init() {
        if (getSubPatchModel() == null) {
            setSubPatchModel(new PatchModel());
        }
        if (pf == null) {
            initSubpatchFrame();
        }
    }

    @Override
    public void updateObj() {
        if (getSubPatchModel() != null) {
            // cheating here by creating a new controller...
            PatchController controller = new PatchController(getSubPatchModel(), null);
            PatchViewCodegen codegen = new PatchViewCodegen(getSubPatchModel(), controller);
            AxoObject ao = codegen.GenerateAxoObj(new AxoObjectPatcher());

            setType(ao);
        }
    }

    @Override
    public void Close() {
        super.Close();
        if (pf != null) {
            pf.Close();
        }
    }

    public PatchFrame getPatchFrame() {
        return pf;
    }
}
