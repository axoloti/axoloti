/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package axoloti.outlets;

import axoloti.atom.AtomDefinition;
import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.object.IAxoObjectInstance;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "source")
public class OutletInstance<T extends Outlet> extends AtomInstance<T> implements Comparable<OutletInstance> {

    @Attribute(name = "outlet", required = false)
    String outletname;
    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    String objname;

    final private AtomDefinitionController controller;

    protected IAxoObjectInstance axoObj;

    @Persist
    public void Persist() {
        objname = axoObj.getInstanceName();
    }

    public IAxoObjectInstance getObjectInstance() {
        return this.axoObj;
    }

    public String getOutletname() {
        return outletname;
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public OutletInstance() {
        this.controller = null;
        this.axoObj = null;
    }

    public OutletInstance(String objname, String outletname) {
        this.controller = null;
        this.axoObj = null;
        this.objname = objname;
        this.outletname = outletname;
    }

    public OutletInstance(AtomDefinitionController outletController, IAxoObjectInstance axoObj) {
        this.controller = outletController;
        this.axoObj = axoObj;
        //RefreshName();
    }

    public DataType getDataType() {
        return getModel().getDatatype();
    }

    public String GetLabel() {
        return getModel().getName();
    }

    public String GetCName() {
        return getModel().GetCName();
    }

    @Override
    public int compareTo(OutletInstance t) {
        return axoObj.compareTo(t.axoObj);
    }

    @Deprecated
    public void RefreshName() {
        /*
         name = axoObj.getInstanceName() + " " + getModel().getName();
         objname = axoObj.getInstanceName();
         outletname = getModel().getName();
         name = null;
         */
    }

    public String getObjname() {
        if (axoObj != null) {
            return axoObj.getInstanceName();
        } else {
            return this.objname;
        }
    }

    public boolean isConnected() {
        if (axoObj == null) {
            return false;
        }

        // FIXME: return (axoObj.getPatchModel().GetNet(this) != null);
        return false;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        // triggered by a model definition change, triggering instance view changes
        if (AtomDefinition.NAME.is(evt)) {
            setName((String) evt.getNewValue());
        }
    }
    
    public String getName() {
        return outletname;
    }

    public void setName(String outletname) {
        String preVal = this.outletname;
        this.outletname = outletname;
        firePropertyChange(AtomDefinition.NAME, preVal, outletname);
    }

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

}
