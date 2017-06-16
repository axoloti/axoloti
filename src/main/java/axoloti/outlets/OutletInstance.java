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

import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "source")
public class OutletInstance<T extends Outlet> extends AbstractModel implements Comparable<OutletInstance>, AtomInstance<T> {

    @Attribute(name = "outlet", required = false)
    public String outletname;
    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    public String objname;

    private AtomDefinitionController controller;

    protected AxoObjectInstanceAbstract axoObj;

    @Override
    public AxoObjectInstanceAbstract getObjectInstance() {
        return this.axoObj;
    }

    public String getOutletname() {
        return outletname;

    }

    @Override
    public T getModel() {
        return (T)getController().getModel();
    }

    public OutletInstance() {
        this.controller = null;
        this.axoObj = null;
    }

    public OutletInstance(AtomDefinitionController outletController, AxoObjectInstance axoObj) {
        this.controller = outletController;
        this.axoObj = axoObj;
        RefreshName();
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

    public void RefreshName() {
        name = axoObj.getInstanceName() + " " + getModel().getName();
        objname = axoObj.getInstanceName();
        outletname = getModel().getName();
        name = null;
    }

    public String getObjname() {
        return this.objname;
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
        // triggered by a model definition change, triggering instance view changes
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)
                || evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            firePropertyChange(
                    evt.getPropertyName(),
                    evt.getOldValue(),
                    evt.getNewValue());
        }
    }

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

}
