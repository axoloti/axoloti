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
import axoloti.Patch;
import axoloti.PatchFrame;
import axoloti.PatchGUI;
import components.ButtonComponent;
import components.ButtonComponent.ActListener;
import java.awt.Component;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcher extends AxoObjectInstance {

    PatchFrame pf;
    @Element(name = "subpatch")
    PatchGUI pg;

    private ButtonComponent BtnUpdate;

    public AxoObjectInstancePatcher() {
    }

    public AxoObjectInstancePatcher(AxoObject type, Patch patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
    }

    @Override
    public void updateObj1() {
        if (pg == null) {
            pg = new PatchGUI();
        }
        if (pf == null) {
            pf = new PatchFrame((PatchGUI) pg, MainFrame.mainframe.getQcmdprocessor());
            pg.setFileNamePath(getInstanceName());
            pg.PostContructor();
        }
        if (pg != null) {
            AxoObject ao = pg.GenerateAxoObj(new AxoObjectPatcher());
            setType(ao);
            ao.id = "patch/patcher";
            ao.sDescription = pg.getNotes();
            ao.sLicense = pg.getSettings().getLicense();
            ao.sAuthor = pg.getSettings().getAuthor();
            pg.container(patch);
        }
    }

    @Override
    public void updateObj() {
        if (pg != null) {
            AxoObject ao = pg.GenerateAxoObj(new AxoObjectPatcher());
            setType(ao);
            PostConstructor();
        }
        validate();
    }

    @Override
    public void Unlock() {
        super.Unlock();
        if (BtnUpdate != null) {
            BtnUpdate.setEnabled(true);
        }
    }

    @Override
    public void Lock() {
        super.Lock();
        if (BtnUpdate != null) {
            BtnUpdate.setEnabled(false);
        }
    }

    public void edit() {
        if (pg == null) {
            pg = new PatchGUI();
        }
        if (pf == null) {
            pf = new PatchFrame((PatchGUI) pg, MainFrame.mainframe.getQcmdprocessor());
            pg.setFileNamePath(getInstanceName());
            pg.PostContructor();
        }
        pf.setState(java.awt.Frame.NORMAL);
        pf.setVisible(true);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        //updateObj();
        ButtonComponent BtnEdit = new ButtonComponent("edit");
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new ActListener() {
            @Override
            public void OnPushed() {
                edit();
            }
        });
        add(BtnEdit);
        BtnUpdate = new ButtonComponent("update");
        BtnUpdate.setAlignmentX(LEFT_ALIGNMENT);
        BtnUpdate.setAlignmentY(TOP_ALIGNMENT);
        BtnUpdate.addActListener(new ActListener() {
            @Override
            public void OnPushed() {
                updateObj();
            }
        });
        add(BtnUpdate);
        resizeToGrid();
    }

    @Override
    public void Close() {
        super.Close();
        if (pf != null) {
            pf.Close();
        }
    }
}
