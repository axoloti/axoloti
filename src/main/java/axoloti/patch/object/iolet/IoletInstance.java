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

import axoloti.datatypes.DataType;
import axoloti.object.atom.AtomDefinition;
import axoloti.object.iolet.Iolet;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.atom.AtomInstance;
import axoloti.property.BooleanProperty;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public abstract class IoletInstance<T extends Iolet> extends AtomInstance<T, IoletInstanceController> implements Comparable<IoletInstance> {

    @Deprecated
    @Attribute(required = false)
    protected String name;
    @Attribute(name = "obj", required = false)
    protected String objname;

    final private T iolet;

    protected IAxoObjectInstance axoObj;

    private boolean connected = false;

    public final static Property CONNECTED = new BooleanProperty("Connected", IoletInstance.class);

    @Persist
    public void persist() {
        objname = axoObj.getInstanceName();
    }

    @Override
    public IAxoObjectInstance getParent() {
        return axoObj;
    }

    @Override
    public T getDModel() {
        return iolet;
    }

    public IoletInstance() {
        this.iolet = null;
        this.axoObj = null;
    }

    public IoletInstance(String objname) {
        this();
        this.objname = objname;
    }

    public IoletInstance(T iolet, IAxoObjectInstance axoObj) {
        this.iolet = iolet;
        this.axoObj = axoObj;
    }

    public DataType getDataType() {
        return getDModel().getDataType();
    }

    public String getLabel() {
        return getDModel().getName();
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

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        // triggered by a model definition change, triggering instance view changes
        if (AtomDefinition.NAME.is(evt)) {
            setName((String) evt.getNewValue());
        }
    }

    public abstract String getName();
    public abstract boolean isSource();

    public boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        boolean oldValue = this.connected;
        this.connected = connected;
        firePropertyChange(
            CONNECTED,
            oldValue, connected);
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(CONNECTED);
        return l;
    }

    @Override
    public IoletInstanceController createController() {
        return new IoletInstanceController(this, null);
    }

    @Override
    public IoletInstanceController getController() {
        return super.getController();
    }

}
