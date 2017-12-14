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
package axoloti.object;

import axoloti.PatchModel;
import axoloti.SDFileReference;
import axoloti.Synonyms;
import axoloti.atom.AtomDefinitionController;
import axoloti.attribute.*;
import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceFactory;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceFactory;
import axoloti.inlets.InletInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.array.ArrayView;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceFactory;
import axoloti.outlets.OutletInt32;
import axoloti.parameters.*;
import axoloti.property.BooleanProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj")
public class AxoObjectInstance extends AxoObjectInstanceAbstract {

    List<InletInstance> inletInstances;
    List<OutletInstance> outletInstances;
    
    @Path("params")
    @ElementListUnion({
        @ElementList(entry = "frac32.u.map", type = ParameterInstanceFrac32UMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.map", type = ParameterInstanceFrac32SMap.class, inline = true, required = false),
        @ElementList(entry = "frac32.u.mapvsl", type = ParameterInstanceFrac32UMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "frac32.s.mapvsl", type = ParameterInstanceFrac32SMapVSlider.class, inline = true, required = false),
        @ElementList(entry = "int32", type = ParameterInstanceInt32Box.class, inline = true, required = false),
        @ElementList(entry = "int32.small", type = ParameterInstanceInt32BoxSmall.class, inline = true, required = false),
        @ElementList(entry = "int32.hradio", type = ParameterInstanceInt32HRadio.class, inline = true, required = false),
        @ElementList(entry = "int32.vradio", type = ParameterInstanceInt32VRadio.class, inline = true, required = false),
        @ElementList(entry = "int2x16", type = ParameterInstance4LevelX16.class, inline = true, required = false),
        @ElementList(entry = "bin12", type = ParameterInstanceBin12.class, inline = true, required = false),
        @ElementList(entry = "bin16", type = ParameterInstanceBin16.class, inline = true, required = false),
        @ElementList(entry = "bin32", type = ParameterInstanceBin32.class, inline = true, required = false),
        @ElementList(entry = "bool32.tgl", type = ParameterInstanceBin1.class, inline = true, required = false),
        @ElementList(entry = "bool32.mom", type = ParameterInstanceBin1Momentary.class, inline = true, required = false)
    })
    List<ParameterInstance> parameterInstances = new ArrayList<>();
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = "objref", type = AttributeInstanceObjRef.class, inline = true, required = false),
        @ElementList(entry = "table", type = AttributeInstanceTablename.class, inline = true, required = false),
        @ElementList(entry = "combo", type = AttributeInstanceComboBox.class, inline = true, required = false),
        @ElementList(entry = "int", type = AttributeInstanceInt32.class, inline = true, required = false),
        @ElementList(entry = "spinner", type = AttributeInstanceSpinner.class, inline = true, required = false),
        @ElementList(entry = "file", type = AttributeInstanceSDFile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AttributeInstanceTextEditor.class, inline = true, required = false)})
    List<AttributeInstance> attributeInstances = new ArrayList<>();
    List<DisplayInstance> displayInstances = new ArrayList<>();


    public AxoObjectInstance() {
        super();
    }

    public AxoObjectInstance(ObjectController controller, PatchModel patchModel, String InstanceName1, Point location) {
        super(controller, patchModel, InstanceName1, location);
        switch (typeName) {
            case "patch/outlet a":
                parentOutlet = new OutletFrac32Buffer(getInstanceName(), "");
                break;
            case "patch/outlet b":
                parentOutlet = new OutletBool32(getInstanceName(), "");
                break;
            case "patch/outlet f":
                parentOutlet = new OutletFrac32(getInstanceName(), "");
                break;
            case "patch/outlet i":
                parentOutlet = new OutletInt32(getInstanceName(), "");
                break;
            default:
                parentOutlet = null;
                break;
        }
        switch (typeName) {
            case "patch/inlet a":
                parentInlet = new InletFrac32Buffer(getInstanceName(), "");
                break;
            case "patch/inlet b":
                parentInlet = new InletBool32(getInstanceName(), "");
                break;
            case "patch/inlet f":
                parentInlet = new InletFrac32(getInstanceName(), "");
                break;
            case "patch/inlet i":
                parentInlet = new InletInt32(getInstanceName(), "");
                break;
            default:
                parentInlet = null;
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();
            if (parentInlet != null) {
                ArrayList<Inlet> inlets = new ArrayList<>(aop.getInlets());
                inlets.add(parentInlet);
                aop.setInlets(inlets);
            }
            if (parentOutlet != null) {
                ArrayList<Outlet> outlets = new ArrayList<>(aop.getOutlets());
                outlets.add(parentOutlet);
                aop.setOutlets(outlets);
            }
        }
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean result = super.setInstanceName(s);
        if (parentInlet != null) {
            parentInlet.setName(s);
        }
        if (parentOutlet != null) {
            parentOutlet.setName(s);
        }
        return result;
    }

    @Override
    public InletInstance GetInletInstance(String n) {
        if (inletInstances == null) return null;
        for (InletInstance o : inletInstances) {
            if (n.equals(o.GetLabel())) {
                return o;
            }
        }
        for (InletInstance o : inletInstances) {
            String s = Synonyms.instance().inlet(n);
            if (o.GetLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public OutletInstance GetOutletInstance(String n) {
        if (outletInstances == null) return null;
        for (OutletInstance o : outletInstances) {
            if (n.equals(o.GetLabel())) {
                return o;
            }
        }
        for (OutletInstance o : outletInstances) {
            String s = Synonyms.instance().outlet(n);
            if (o.GetLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    public ParameterInstance GetParameterInstance(String n) {
        if (parameterInstances == null) return null;
        for (ParameterInstance o : parameterInstances) {
            if (n.equals(o.parameter.getName())) {
                return o;
            }
        }
        return null;
    }
/* these functions seem unused and are obsolete:
    
    @Override
    public boolean hasStruct() {
        if (getParameterInstances() != null && !(getParameterInstances().isEmpty())) {
            return true;
        }
        if (getType().sLocalData == null) {
            return false;
        }
        return getType().sLocalData.length() != 0;
    }

    @Override
    public boolean hasInit() {
        if (getType().sInitCode == null) {
            return false;
        }
        return getType().sInitCode.length() != 0;
    }
*/

    Outlet parentOutlet;
    Inlet parentInlet;

    @Override
    public boolean providesModulationSource() {
        IAxoObject atype = getType();
        if (atype == null) {
            return false;
        } else {
            //return atype.providesModulationSource();
            return false; // FIXME
        }
    }

    @Override
    public IAxoObject getType() {
        return (IAxoObject) super.getType();
    }

    @Override
    public ArrayList<SDFileReference> getFileDepends() {
        ArrayList<SDFileReference> files = getType().getFileDepends();
        if (files == null) {
            files = new ArrayList<SDFileReference>();
        } else {
            String p1 = getType().getPath();
            if (p1 == null) {
                // embedded object, reference path is of the patch
                p1 = getPatchModel().getFileNamePath();
                if (p1 == null) {
                    p1 = "";
                }
            }
            File f1 = new File(p1);
            java.nio.file.Path p = f1.toPath().getParent();
            for (SDFileReference f : files) {
                f.Resolve(p);
            }
        }
        if (attributeInstances != null) {
            for (AttributeInstance a : attributeInstances) {
                ArrayList<SDFileReference> f2 = a.GetDependendSDFiles();
                if (f2 != null) {
                    files.addAll(f2);
                }
            }
        }
        return files;
    }

    @Persist
    public void Persist() {
        IAxoObject o = getType();
        if (o != null) {
            if (o.getUUID() != null && !o.getUUID().isEmpty()) {
                typeUUID = o.getUUID();
                typeSHA = null;
            }
        }
        if ((parameterInstances!= null) && parameterInstances.isEmpty()) {
            parameterInstances = null;
        }
        if ((attributeInstances!=null) && attributeInstances.isEmpty()) {
            attributeInstances = null;
        }
    }

    public Rectangle editorBounds;
    public Integer editorActiveTabIndex;

    @Override
    public void dispose() {
    }

    @Override
    public void applyValues(IAxoObjectInstance sourceObject) {
        if (sourceObject instanceof AxoObjectInstance) {
            AxoObjectInstance sourceObject2 = (AxoObjectInstance) sourceObject;
            for (ParameterInstance p : parameterInstances) {
                // find matching parameter in source
                for (ParameterInstance p2 : sourceObject2.parameterInstances) {
                    if (p.getName().equals(p2.getName())) {
                        p.CopyValueFrom(p2);
                        break;
                    }
                }
            }
            for (AttributeInstance a : attributeInstances) {
                // find matching parameter in source
                for (AttributeInstance a2 : sourceObject2.attributeInstances) {
                    if (a.getName().equals(a2.getName())) {
                        a.CopyValueFrom(a2);
                        break;
                    }
                }
            }
            typeWasAmbiguous = sourceObject.isTypeWasAmbiguous();
        }
    }    

    @Override
    public void Remove() {
        for (ParameterInstance p : getParameterInstances()) {
            p.dispose();
        }
        for (AttributeInstance p : getAttributeInstances()) {
            p.dispose();
        }
        for (DisplayInstance p : getDisplayInstances()) {
            p.dispose();
        }
        for (InletInstance p : getInletInstances()) {
            p.dispose();
        }
        for (OutletInstance p : getOutletInstances()) {
            p.dispose();
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getController().getModel();        
            if (parentInlet != null) {
                ArrayList<Inlet> inlets = new ArrayList<>(aop.getInlets());
                inlets.remove(parentInlet);
                aop.setInlets(inlets);
            }
            if (parentOutlet != null) {
                ArrayList<Outlet> outlets = new ArrayList<>(aop.getOutlets());
                outlets.remove(parentOutlet);
                aop.setOutlets(outlets);
            }
        }
    }    
    
        /* MVC clean code below here */

    public static final Property OBJ_LOCATION = new ObjectProperty("Location", Point.class, AxoObjectInstance.class);
    public static final Property OBJ_SELECTED = new BooleanProperty("Selected", AxoObjectInstance.class);
    public static final Property OBJ_INSTANCENAME = new StringProperty("InstanceName", AxoObjectInstance.class);
//    public static final Property OBJ_PARAMETER_INSTANCES = new ObjectProperty("ParameterInstances", new ArrayList<ParameterInstance>().getClass(), AxoObjectInstance.class);
//    public static final Property OBJ_COMMENT = new StringPropertyNull("CommentText", AxoObjectInstance.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(OBJ_LOCATION);
        l.add(OBJ_SELECTED);
        l.add(OBJ_INSTANCENAME);
        l.add(OBJ_PARAMETER_INSTANCES);
        l.add(OBJ_ATTRIBUTE_INSTANCES);
        l.add(OBJ_INLET_INSTANCES);
        l.add(OBJ_OUTLET_INSTANCES);
        l.add(OBJ_DISPLAY_INSTANCES);
        return l;
    }
    
    @Override
    public List<InletInstance> getInletInstances() {
        if (inletInstances == null) {
            return new ArrayList<>();
        }
        return inletInstances;
    }

    public void setInletInstances(List<InletInstance> inletInstances) {
        List<InletInstance> oldval = this.inletInstances;
        this.inletInstances = inletInstances;
        firePropertyChange(OBJ_INLET_INSTANCES, oldval, inletInstances);
    }

    @Override
    public List<OutletInstance> getOutletInstances() {
        if (outletInstances == null) {
            return new ArrayList<>();
        }
        return outletInstances;
    }

    public void setOutletInstances(List<OutletInstance> outletInstances) {
        List<OutletInstance> oldval = this.outletInstances;
        this.outletInstances = outletInstances;
        firePropertyChange(OBJ_OUTLET_INSTANCES, oldval, outletInstances);
    }

    @Override
    public List<ParameterInstance> getParameterInstances() {
        if (parameterInstances == null) {
            return new ArrayList<>();
        }
        return parameterInstances;
    }

    public void setParameterInstances(List parameterInstances) {
        List<ParameterInstance> oldval = this.parameterInstances;
        this.parameterInstances = parameterInstances;
        firePropertyChange(OBJ_PARAMETER_INSTANCES, oldval, parameterInstances);
    }

    @Override
    public List<AttributeInstance> getAttributeInstances() {
        if (attributeInstances == null) {
            return new ArrayList<>();
        }
        return attributeInstances;
    }

    public void setAttributeInstances(List<AttributeInstance> attributeInstances) {
        List<AttributeInstance> oldval = this.attributeInstances;
        this.attributeInstances = attributeInstances;
        firePropertyChange(OBJ_ATTRIBUTE_INSTANCES, oldval, attributeInstances);
    }

    @Override
    public List<DisplayInstance> getDisplayInstances() {
        if (displayInstances == null) {
            return new ArrayList<>();
        }
        return displayInstances;
    }

    public void setDisplayInstances(List<DisplayInstance> displayInstances) {
        List<DisplayInstance> oldval = this.displayInstances;
        this.displayInstances = displayInstances;
        firePropertyChange(OBJ_DISPLAY_INSTANCES, oldval, displayInstances);
    }

    ArrayView<OutletInstance> outletInstanceSync = new ArrayView<OutletInstance>() {
        @Override
        public void updateUI(List<OutletInstance> views) {
            setOutletInstances(new ArrayList<OutletInstance>(views));
        }

        @Override
        public OutletInstance viewFactory(AbstractController ctrl) {
            AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
            return OutletInstanceFactory.createView(ctrl1, AxoObjectInstance.this);
        }

        @Override
        public void removeView(OutletInstance view) {
        }
    };

    ArrayView<InletInstance> inletInstanceSync = new ArrayView<InletInstance>() {
        @Override
        public void updateUI(List<InletInstance> views) {
            setInletInstances(new ArrayList<InletInstance>(views));
        }

        @Override
        public InletInstance viewFactory(AbstractController ctrl) {
            AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
            return InletInstanceFactory.createView(ctrl1, AxoObjectInstance.this);
        }

        @Override
        public void removeView(InletInstance view) {
        }
    };

    ArrayView<DisplayInstance> displayInstanceSync = new ArrayView<DisplayInstance>() {
        @Override
        public void updateUI(List<DisplayInstance> views) {
            setDisplayInstances(new ArrayList<DisplayInstance>(views));
        }

        @Override
        public DisplayInstance viewFactory(AbstractController ctrl) {
            AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
            return DisplayInstanceFactory.createView(ctrl1);
        }

        @Override
        public void removeView(DisplayInstance view) {
        }
    };

    ArrayView<ParameterInstance> parameterInstanceSync = new ArrayView<ParameterInstance>() {
        @Override
        public void updateUI(List<ParameterInstance> views) {
            setParameterInstances(new ArrayList<ParameterInstance>(views));
        }

        @Override
        public ParameterInstance viewFactory(AbstractController ctrl) {
            AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
            return ParameterInstanceFactory.createView(ctrl1, AxoObjectInstance.this);
        }

        @Override
        public void removeView(ParameterInstance view) {
        }
    };

    ArrayView<AttributeInstance> attributeInstanceSync = new ArrayView<AttributeInstance>() {
        @Override
        public void updateUI(List<AttributeInstance> views) {
            setAttributeInstances(new ArrayList<>(views));
        }

        @Override
        public AttributeInstance viewFactory(AbstractController ctrl) {
            AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
            return AttributeInstanceFactory.createView(ctrl1, AxoObjectInstance.this);
        }

        @Override
        public void removeView(AttributeInstance view) {
        }
    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (AxoObject.OBJ_ATTRIBUTES.is(evt)) {
            attributeInstances = attributeInstanceSync.Sync(attributeInstances, getController().attrs);
        } else if (AxoObject.OBJ_PARAMETERS.is(evt)) {
            parameterInstances = parameterInstanceSync.Sync(parameterInstances, getController().params);
        } else if (AxoObject.OBJ_DISPLAYS.is(evt)) {
            displayInstances = displayInstanceSync.Sync(displayInstances, getController().disps);
        } else if (AxoObject.OBJ_INLETS.is(evt)) {
            inletInstances = inletInstanceSync.Sync(inletInstances, getController().inlets);
        } else if (AxoObject.OBJ_OUTLETS.is(evt)) {
            outletInstances = outletInstanceSync.Sync(outletInstances, getController().outlets);
        }
    }

}
