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
package axoloti.patch.object;

import axoloti.Synonyms;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.IAxoObject;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.display.Display;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.Parameter;
import axoloti.patch.Modulation;
import axoloti.patch.Modulator;
import axoloti.patch.PatchModel;
import static axoloti.patch.object.AxoObjectInstanceAbstract.OBJ_PARAMETER_INSTANCES;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.patch.object.attribute.AttributeInstanceFactory;
import axoloti.patch.object.attribute.AttributeInstanceInt32;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.patch.object.attribute.AttributeInstanceSDFile;
import axoloti.patch.object.attribute.AttributeInstanceSpinner;
import axoloti.patch.object.attribute.AttributeInstanceTablename;
import axoloti.patch.object.attribute.AttributeInstanceTextEditor;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceFactory;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.inlet.InletInstanceFactory;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.outlet.OutletInstanceFactory;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstance4LevelX16;
import axoloti.patch.object.parameter.ParameterInstanceBin1;
import axoloti.patch.object.parameter.ParameterInstanceBin12;
import axoloti.patch.object.parameter.ParameterInstanceBin16;
import axoloti.patch.object.parameter.ParameterInstanceBin1Momentary;
import axoloti.patch.object.parameter.ParameterInstanceBin32;
import axoloti.patch.object.parameter.ParameterInstanceFactory;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMap;
import axoloti.patch.object.parameter.ParameterInstanceFrac32SMapVSlider;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMap;
import axoloti.patch.object.parameter.ParameterInstanceFrac32UMapVSlider;
import axoloti.patch.object.parameter.ParameterInstanceInt32Box;
import axoloti.patch.object.parameter.ParameterInstanceInt32BoxSmall;
import axoloti.patch.object.parameter.ParameterInstanceInt32HRadio;
import axoloti.patch.object.parameter.ParameterInstanceInt32VRadio;
import axoloti.property.PropagatedProperty;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.ListUtils;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
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

    private List<Modulator> modulators;

    public AxoObjectInstance() {
        super();
        parentInlet = null;
        parentOutlet = null;
    }

    @Commit
    void commit() {
        for (InletInstance o : getInletInstances()) {
            o.setParent(this);
        }
        for (OutletInstance o : getOutletInstances()) {
            o.setParent(this);
        }
        for (ParameterInstance o : getParameterInstances()) {
            o.setParent(this);
        }
        for (DisplayInstance o : getDisplayInstances()) {
            o.setParent(this);
        }
        for (AttributeInstance o : getAttributeInstances()) {
            o.setParent(this);
        }
    }

    public AxoObjectInstance(IAxoObject obj, PatchModel patchModel, String InstanceName1, Point location) {
        super(obj, patchModel, InstanceName1, location);

        switch (typeName) {
            case "patch/outlet a":
                parentOutlet = new OutletFrac32Buffer();
                break;
            case "patch/outlet b":
                parentOutlet = new OutletBool32();
                break;
            case "patch/outlet f":
                parentOutlet = new OutletFrac32();
                break;
            case "patch/outlet i":
                parentOutlet = new OutletInt32();
                break;
            default:
                parentOutlet = null;
                break;
        }
        switch (typeName) {
            case "patch/inlet a":
                parentInlet = new InletFrac32Buffer();
                break;
            case "patch/inlet b":
                parentInlet = new InletBool32();
                break;
            case "patch/inlet f":
                parentInlet = new InletFrac32();
                break;
            case "patch/inlet i":
                parentInlet = new InletInt32();
                break;
            default:
                parentInlet = null;
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getDModel();
            if (parentInlet != null) {
                parentInlet.setParent(aop);
                parentInlet.setName(InstanceName1);
                aop.getController().addInlet(parentInlet);
            }
            if (parentOutlet != null) {
                parentOutlet.setParent(aop);
                parentOutlet.setName(InstanceName1);
                aop.getController().addOutlet(parentOutlet);
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
    public InletInstance findInletInstance(String n) {
        if (inletInstances == null) {
            return null;
        }
        for (InletInstance o : inletInstances) {
            if (n.equals(o.getLabel())) {
                return o;
            }
        }
        for (InletInstance o : inletInstances) {
            String s = Synonyms.instance().inlet(n);
            if (o.getLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public OutletInstance findOutletInstance(String n) {
        if (outletInstances == null) {
            return null;
        }
        for (OutletInstance o : outletInstances) {
            if (n.equals(o.getLabel())) {
                return o;
            }
        }
        for (OutletInstance o : outletInstances) {
            String s = Synonyms.instance().outlet(n);
            if (o.getLabel().equals(s)) {
                return o;
            }
        }
        return null;
    }

    public ParameterInstance findParameterInstance(String n) {
        if (parameterInstances == null) {
            return null;
        }
        for (ParameterInstance o : parameterInstances) {
            if (n.equals(o.getDModel().getName())) {
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

    final Outlet parentOutlet;
    final Inlet parentInlet;

    @Override
    public List<Modulator> getModulators() {
        if (modulators == null) {
            return null;
        } else {
            return Collections.unmodifiableList(modulators);
        }
    }

    @Override
    public void setModulators(List<Modulator> modulators) {
        this.modulators = modulators;
        firePropertyChange(OBJ_INST_MODULATORS, null, modulators);
    }

    @Override
    public List<SDFileReference> getFileDepends() {
        LinkedList<SDFileReference> files = new LinkedList<>(getDModel().getFileDepends());
        String p1 = getDModel().getPath();
        if (p1 == null) {
            // embedded object, reference path is of the patch
            p1 = getParent().getFileNamePath();
            if (p1 == null) {
                p1 = "";
            }
        }
        File f1 = new File(p1);
        java.nio.file.Path p = f1.toPath().getParent();
        for (SDFileReference f : files) {
            f.resolve(p);
        }
        if (attributeInstances != null) {
            for (AttributeInstance a : attributeInstances) {
                List<SDFileReference> f2 = a.getDependendSDFiles();
                if (f2 != null) {
                    files.addAll(f2);
                }
            }
        }
        return files;
    }

    @Persist
    public void persist() {
        IAxoObject o = getDModel();
        if (o != null) {
            if (o.getUUID() != null && !o.getUUID().isEmpty()) {
                typeUUID = o.getUUID();
                typeSHA = null;
            }
        }
        if ((parameterInstances != null) && parameterInstances.isEmpty()) {
            parameterInstances = null;
        }
        if ((attributeInstances != null) && attributeInstances.isEmpty()) {
            attributeInstances = null;
        }
    }

    @Override
    public void dispose() {
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
        for (Modulator m : getModulators()) {
            List<Modulation> modulations = new ArrayList<>(m.getModulations());
            for (Modulation m1 : modulations) {
                m1.getParameter().getController().changeModulation(m, 0);
            }
        }
        AxoObjectInstancePatcher aoip = getContainer();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getDModel();
            if (parentInlet != null) {
                aop.getController().removeInlet(parentInlet);
            }
            if (parentOutlet != null) {
                aop.getController().removeOutlet(parentOutlet);
            }
        }
    }

    @Override
    public void applyValues(IAxoObjectInstance sourceObject) {
        if (sourceObject instanceof AxoObjectInstance) {
            AxoObjectInstance sourceObject2 = (AxoObjectInstance) sourceObject;
            for (ParameterInstance p : parameterInstances) {
                // find matching parameter in source
                for (ParameterInstance p2 : sourceObject2.parameterInstances) {
                    if (p.getName().equals(p2.getName())) {
                        p.copyValueFrom(p2);
                        break;
                    }
                }
            }
            for (AttributeInstance a : attributeInstances) {
                // find matching parameter in source
                for (AttributeInstance a2 : sourceObject2.attributeInstances) {
                    if (a.getName().equals(a2.getName())) {
                        a.copyValueFrom(a2);
                        break;
                    }
                }
            }
            typeWasAmbiguous = sourceObject.isTypeWasAmbiguous();
        }
    }

    /* MVC clean code below here */
    @Override
    public List<InletInstance> getInletInstances() {
        return ListUtils.export(inletInstances);
    }

    @Override
    public void setInletInstances(List<InletInstance> inletInstances) {
        List<InletInstance> oldval = this.inletInstances;
        this.inletInstances = inletInstances;
        firePropertyChange(OBJ_INLET_INSTANCES, oldval, inletInstances);
    }

    @Override
    public List<OutletInstance> getOutletInstances() {
        return ListUtils.export(outletInstances);
    }

    @Override
    public void setOutletInstances(List<OutletInstance> outletInstances) {
        List<OutletInstance> oldval = this.outletInstances;
        this.outletInstances = outletInstances;
        firePropertyChange(OBJ_OUTLET_INSTANCES, oldval, outletInstances);
    }

    @Override
    public List<ParameterInstance> getParameterInstances() {
        return ListUtils.export(parameterInstances);
    }

    @Override
    public void setParameterInstances(List parameterInstances) {
        List<ParameterInstance> oldval = this.parameterInstances;
        this.parameterInstances = parameterInstances;
        firePropertyChange(OBJ_PARAMETER_INSTANCES, oldval, parameterInstances);
    }

    @Override
    public List<AttributeInstance> getAttributeInstances() {
        return ListUtils.export(attributeInstances);
    }

    @Override
    public void setAttributeInstances(List<AttributeInstance> attributeInstances) {
        List<AttributeInstance> oldval = this.attributeInstances;
        this.attributeInstances = attributeInstances;
        firePropertyChange(OBJ_ATTRIBUTE_INSTANCES, oldval, attributeInstances);
    }

    @Override
    public List<DisplayInstance> getDisplayInstances() {
        return ListUtils.export(displayInstances);
    }

    @Override
    public void setDisplayInstances(List<DisplayInstance> displayInstances) {
        List<DisplayInstance> oldval = this.displayInstances;
        this.displayInstances = displayInstances;
        firePropertyChange(OBJ_DISPLAY_INSTANCES, oldval, displayInstances);
    }

    ArrayView<OutletInstance, Outlet> outletInstanceSync = new ArrayView<OutletInstance, Outlet>() {
        @Override
        protected void updateUI(List<OutletInstance> views) {
            getController().changeOutletInstances(views);
        }

        @Override
        protected OutletInstance viewFactory(Outlet outlet) {
            return OutletInstanceFactory.createView(outlet, AxoObjectInstance.this);
        }

        @Override
        protected void removeView(OutletInstance view) {
            view.dispose();
        }
    };

    ArrayView<InletInstance, Inlet> inletInstanceSync = new ArrayView<InletInstance, Inlet>() {
        @Override
        protected void updateUI(List<InletInstance> views) {
            getController().changeInletInstances(views);
        }

        @Override
        protected InletInstance viewFactory(Inlet inlet) {
            return InletInstanceFactory.createView(inlet, AxoObjectInstance.this);
        }

        @Override
        protected void removeView(InletInstance view) {
            view.dispose();
        }
    };

    ArrayView<DisplayInstance, Display> displayInstanceSync = new ArrayView<DisplayInstance, Display>() {
        @Override
        protected void updateUI(List<DisplayInstance> views) {
            getController().changeDisplayInstances(views);
        }

        @Override
        protected DisplayInstance viewFactory(Display display) {
            return DisplayInstanceFactory.createView(display);
        }

        @Override
        protected void removeView(DisplayInstance view) {
            view.dispose();
        }
    };

    ArrayView<ParameterInstance, Parameter> parameterInstanceSync = new ArrayView<ParameterInstance, Parameter>() {
        @Override
        protected void updateUI(List<ParameterInstance> views) {
            getController().changeParameterInstances(views);
        }

        @Override
        protected ParameterInstance viewFactory(Parameter parameter) {
            return ParameterInstanceFactory.createView(parameter, AxoObjectInstance.this);
        }

        @Override
        protected void removeView(ParameterInstance view) {
            view.dispose();
        }
    };

    ArrayView<AttributeInstance, AxoAttribute> attributeInstanceSync = new ArrayView<AttributeInstance, AxoAttribute>() {
        @Override
        protected void updateUI(List<AttributeInstance> views) {
            getController().changeAttributeInstances(views);
        }

        @Override
        protected AttributeInstance viewFactory(AxoAttribute attribute) {
            return AttributeInstanceFactory.createView(attribute, AxoObjectInstance.this);
        }

        @Override
        protected void removeView(AttributeInstance view) {
            view.dispose();
        }
    };

    private void syncModulators() {
        List<Modulator> prev_modulators;
        if (modulators == null) {
            prev_modulators = Collections.emptyList();
        } else {
            prev_modulators = new LinkedList<>(modulators);
        }
        List<Modulator> new_modulators = new ArrayList<>();
        List<String> obj_modulators = getDModel().getModulators();
        for (String obj_modulator_name : obj_modulators) {
            Modulator modulator = null;
            for (Modulator m : prev_modulators) {
                if (((m.getName() == null) && (obj_modulator_name == null))
                        || ((m.getName() == null) && (obj_modulator_name == null))) {
                    modulator = m;
                    prev_modulators.remove(m);
                    break;
                }
            }
            if (modulator == null) {
                modulator = new Modulator(this, obj_modulator_name);
            }
            new_modulators.add(modulator);
        }
        getController().generic_setModelUndoableProperty(OBJ_INST_MODULATORS, new_modulators);
    }

    private final PropagatedProperty propagateProperties[] = new PropagatedProperty[]{
        OBJ_INST_AUTHOR,
        OBJ_INST_DESCRIPTION,
        OBJ_INST_LICENSE
    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (AxoObject.OBJ_ATTRIBUTES.is(evt)) {
            attributeInstanceSync.sync(attributeInstances, (List) evt.getNewValue());
        } else if (AxoObject.OBJ_PARAMETERS.is(evt)) {
            parameterInstanceSync.sync(parameterInstances, (List) evt.getNewValue());
        } else if (AxoObject.OBJ_DISPLAYS.is(evt)) {
            displayInstanceSync.sync(displayInstances, (List) evt.getNewValue());
        } else if (AxoObject.OBJ_INLETS.is(evt)) {
            inletInstanceSync.sync(inletInstances, (List) evt.getNewValue());
        } else if (AxoObject.OBJ_OUTLETS.is(evt)) {
            outletInstanceSync.sync(outletInstances, (List) evt.getNewValue());
        } else if (AxoObject.OBJ_MODULATORS.is(evt)) {
            syncModulators();
        } else {
            for (PropagatedProperty p : propagateProperties) {
                if (p.is(evt)) {
                    firePropertyChange(p,
                            evt.getOldValue(),
                            evt.getNewValue());
                    break;
                }
            }
        }

    }
}
