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

import axoloti.Patch;
import axoloti.objecteditor.AxoObjectEditor;
import components.ButtonComponent;
import components.ButtonComponent.ActListener;
import java.awt.Component;
import java.awt.Point;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectInstancePatcherObject extends AxoObjectInstance {

    AxoObjectEditor aoe;
    @Element(name = "object")
    AxoObject ao;

    public AxoObjectInstancePatcherObject() {
    }

    public AxoObjectInstancePatcherObject(AxoObject type, Patch patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
    }

    public void updateObj1() {
        if (ao == null) {
            ao = new AxoObject();
            ao.id = "patch/object";
            ao.sDescription = "description";
        }
        setType(ao);
        /*
         if (pg != null) {
         AxoObject ao = pg.GenerateAxoObj();
         setType(ao);
         pg.container(patch);
         }
         */
    }

    public void updateObj() {
        if (ao != null) {
            ao.id = "patch/object";
            setType(ao);
            PostConstructor();
        }
        for (Component cmp : getComponents()) {
            cmp.doLayout();
        }
        doLayout();
        invalidate();
        validate();
    }

    public void edit() {
        if (ao == null) {
            ao = new AxoObject();
//            ao.id = "id";
            ao.sDescription = "description";
        }
        if (aoe == null) {
            aoe = new AxoObjectEditor(ao);
        }
        aoe.setState(java.awt.Frame.NORMAL);
        aoe.setVisible(true);
    }

   @Override
    public void ObjectModified(Object src) {
        updateObj();
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
        for (Component cmp : getComponents()) {
            cmp.doLayout();
        }
        resizeToGrid();
    }

}
