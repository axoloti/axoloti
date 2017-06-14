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
package axoloti.inlets;

import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "dest")
public class InletInstance<T extends Inlet> extends AbstractModel implements AtomInstance<T> {

    @Attribute(name = "inlet", required = false)
    public String inletname;
    @Deprecated
    @Attribute(required = false)
    public String name;
    @Attribute(name = "obj", required = false)
    public String objname;

    private final T inlet;

    protected AxoObjectInstanceAbstract axoObj;

    public String getInletname() {
        return inletname;
    }

    @Override
    public AxoObjectInstanceAbstract getObjectInstance() {
        return axoObj;
    }

    @Override
    public T getDefinition() {
        return inlet;
    }

    public InletInstance() {
        this.inlet = null;
        this.axoObj = null;
    }

    public InletInstance(T inlet, final AxoObjectInstance axoObj) {
        this.inlet = inlet;
        this.axoObj = axoObj;
        RefreshName();
    }

    public DataType getDataType() {
        return inlet.getDatatype();
    }

    public String GetCName() {
        return inlet.GetCName();
    }

    public String GetLabel() {
        return inlet.getName();
    }

    public Inlet getInlet() {
        return inlet;
    }

    public void RefreshName() {
        if (axoObj != null) {
            name = axoObj.getInstanceName() + " " + inlet.getName();
            objname = axoObj.getInstanceName();
            name = null;
        }
        inletname = inlet.getName();
    }

    public String getObjname() {
        return this.objname;
    }

    public boolean isConnected() {
        if (axoObj == null) {
            return false;
        }
        return false;
        //FIXME: return (axoObj.getPatchModel().GetNet(this) != null);
    }

    @Override
    public InletInstanceController createController(AbstractDocumentRoot documentRoot) {
        return new InletInstanceController(this, documentRoot);
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
        return inlet.createController(null);
    }
}
