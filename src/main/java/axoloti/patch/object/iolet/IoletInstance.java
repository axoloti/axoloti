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
package axoloti.patch.object.iolet;

import java.beans.PropertyChangeEvent;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

import axoloti.datatypes.DataType;
import axoloti.object.atom.AtomDefinition;
import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.iolet.Iolet;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.atom.AtomInstance;
import axoloti.patch.object.outlet.OutletInstance;

/**
 *
 * @author Johannes Taelman
 */
public abstract class IoletInstance<T extends Iolet> extends AtomInstance<T> implements Comparable<IoletInstance> {

    @Deprecated
    @Attribute(required = false)
    protected String name;
    @Attribute(name = "obj", required = false)
    protected String objname;

    final private AtomDefinitionController controller;

    protected IAxoObjectInstance axoObj;

    @Persist
    public void Persist() {
        objname = axoObj.getInstanceName();
    }

    public IAxoObjectInstance getObjectInstance() {
        return this.axoObj;
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public IoletInstance() {
        this.controller = null;
        this.axoObj = null;
    }

    public IoletInstance(String objname) {
        this();
        this.objname = objname;
    }

    public IoletInstance(AtomDefinitionController outletController, IAxoObjectInstance axoObj) {
        this.controller = outletController;
        this.axoObj = axoObj;
    }

    public DataType getDataType() {
        return getModel().getDatatype();
    }

    public String GetCName() {
        return getModel().GetCName();
    }

    public String GetLabel() {
        return getModel().getName();
    }

    @Override
    public int compareTo(IoletInstance t) {
        return axoObj.compareTo(t.axoObj);
    }

    public abstract void setName(String outletname);

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

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

    public abstract String getName();
    public abstract boolean isSource();
}
