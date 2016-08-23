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

import axoloti.MainFrame;
import axoloti.Net;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.inlets.PInletInstanceView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;
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
        return axoObj.getPatchModel().disconnect(this);
    }

    public Net deleteNet() {
        return axoObj.getPatchModel().delete(axoObj.getPatchModel().GetNet(this));
    }

    public IInletInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PInletInstanceView(this, (PAxoObjectInstanceView) o);
        } else {
            return new InletInstanceView(this, (AxoObjectInstanceViewAbstract) o);
        }
    }

    public IInletInstanceView createView(IAxoObjectInstanceView o) {
        IInletInstanceView inletInstanceView = getViewInstance(o);
        o.addInletInstanceView(inletInstanceView);
        inletInstanceView.PostConstructor();
        return inletInstanceView;
    }
}
