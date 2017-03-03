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
import axoloti.PatchViewPiccolo;
import axoloti.PatchViewSwing;
import axoloti.objectviews.AxoObjectInstanceViewPatcher;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewPatcher;
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
        super(type, patch1, InstanceName1, location);
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
            AxoObject ao = getSubPatchModel().GenerateAxoObj(new AxoObjectPatcher());
            setType(ao);
            ao.id = "patch/patcher";
            ao.sDescription = getSubPatchModel().getNotes();
            ao.sLicense = getSubPatchModel().getSettings().getLicense();
            ao.sAuthor = getSubPatchModel().getSettings().getAuthor();
            getSubPatchModel().setContainer(getPatchModel());
        }
    }

    public void initSubpatchFrame() {
        PatchController patchController = new PatchController();
        PatchView patchView = MainFrame.prefs.getPatchView(patchController);
        patchController.setPatchView(patchView);
        patchController.setPatchModel(getSubPatchModel());
        getSubPatchModel().addModelChangedListener(patchView);
        pf = new PatchFrame(patchController, MainFrame.mainframe.getQcmdprocessor());
        patchController.patchView.setFileNamePath(getInstanceName());
        patchController.patchView.PostConstructor();
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
            AxoObject ao = getSubPatchModel().GenerateAxoObj(new AxoObjectPatcher());
            setType(ao);
            setDirty(true);
            getPatchModel().setDirty();
        }
    }

    @Override
    public void Close() {
        super.Close();
        if (pf != null) {
            pf.Close();
        }
    }

    @Override
    public IAxoObjectInstanceView getViewInstance(PatchView patchView) {
        if (patchView instanceof PatchViewPiccolo) {
            return new PAxoObjectInstanceViewPatcher(this, (PatchViewPiccolo) patchView);
        } else {
            return new AxoObjectInstanceViewPatcher(this, (PatchViewSwing) patchView);
        }
    }

    public PatchFrame getPatchFrame() {
        return pf;
    }
}
