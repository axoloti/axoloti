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

import axoloti.Net;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "dest")
public class InletInstance<T extends Inlet> implements AtomInstance<T> {

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
        return this.axoObj;
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

    public DataType GetDataType() {
        return inlet.getDatatype();
    }

    public String GetCName() {
        return inlet.GetCName();
    }

    public String GetLabel() {
        return inlet.name;
    }

    public Inlet getInlet() {
        return inlet;
    }

    public void RefreshName() {
        name = axoObj.getInstanceName() + " " + inlet.name;
        objname = axoObj.getInstanceName();
        inletname = inlet.name;
        name = null;
    }

    public String getObjname() {
        return this.objname;
    }

    public boolean isConnected() {
        if (axoObj == null) {
            return false;
        }
        if (axoObj.getPatchModel() == null) {
            return false;
        }

        return (axoObj.getPatchModel().GetNet(this) != null);
    }

    public Net disconnect() {
        Net n = axoObj.getPatchModel().disconnect(this);
        if (n != null) {
            axoObj.getPatchModel().SetDirty();
        }
        return n;
    }

    public void deleteNet() {
        Net n = axoObj.getPatchModel().GetNet(this);
        axoObj.getPatchModel().delete(n);
        if (n != null) {
            axoObj.getPatchModel().SetDirty();
        }
    }

    public InletInstanceView ViewFactory(AxoObjectInstanceViewAbstract o) {
        return new InletInstanceView(this, o);
    }

    public InletInstanceView CreateView(AxoObjectInstanceViewAbstract o) {
        InletInstanceView inletInstanceView = ViewFactory(o);
        AxoObjectInstanceView ov = (AxoObjectInstanceView) o;
        ov.p_inletViews.add(inletInstanceView);
        inletInstanceView.PostConstructor();
        return inletInstanceView;
    }
}
