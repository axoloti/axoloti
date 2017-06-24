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
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceFactory;
import axoloti.mvc.AbstractController;
import axoloti.mvc.array.ArrayModel;
import axoloti.mvc.array.ArrayView;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceFactory;
import axoloti.parameters.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj")
public class AxoObjectInstance extends AxoObjectInstanceAbstract {

    public ArrayModel<InletInstance> inletInstances = new ArrayModel<InletInstance>();
    public ArrayModel<OutletInstance> outletInstances = new ArrayModel<OutletInstance>();
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
        @ElementList(entry = "bool32.mom", type = ParameterInstanceBin1Momentary.class, inline = true, required = false)})
    public ArrayModel<ParameterInstance> parameterInstances = new ArrayModel<ParameterInstance>();
    @Path("attribs")
    @ElementListUnion({
        @ElementList(entry = "objref", type = AttributeInstanceObjRef.class, inline = true, required = false),
        @ElementList(entry = "table", type = AttributeInstanceTablename.class, inline = true, required = false),
        @ElementList(entry = "combo", type = AttributeInstanceComboBox.class, inline = true, required = false),
        @ElementList(entry = "int", type = AttributeInstanceInt32.class, inline = true, required = false),
        @ElementList(entry = "spinner", type = AttributeInstanceSpinner.class, inline = true, required = false),
        @ElementList(entry = "file", type = AttributeInstanceSDFile.class, inline = true, required = false),
        @ElementList(entry = "text", type = AttributeInstanceTextEditor.class, inline = true, required = false)})
    public ArrayModel<AttributeInstance> attributeInstances = new ArrayModel<>();
    public ArrayModel<DisplayInstance> displayInstances = new ArrayModel<DisplayInstance>();

    // link from object definition to object instance
    ArrayView<InletInstance> vInlets;
    ArrayView<OutletInstance> vOutlets;
    ArrayView<DisplayInstance> vDisps;
    ArrayView<ParameterInstance> vParams;
    ArrayView<AttributeInstance> vAttrs;

    public AxoObjectInstance() {
        super();
    }

    public AxoObjectInstance(ObjectController controller, PatchModel patchModel, String InstanceName1, Point location) {
        super(controller, patchModel, InstanceName1, location);

        vInlets = new ArrayView<InletInstance>(controller.inlets) {
            @Override
            public void updateUI() {
                inletInstances.setArray(new ArrayList<>(getSubViews()));
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
        controller.inlets.addView(vInlets);

        vOutlets = new ArrayView<OutletInstance>(controller.outlets) {
            @Override
            public void updateUI() {
                outletInstances.setArray(new ArrayList<>(getSubViews()));
            }

            @Override
            public OutletInstance viewFactory(AbstractController ctrl) {
                AtomDefinitionController ctrl1 = (AtomDefinitionController) ctrl;
                OutletInstance o = OutletInstanceFactory.createView(ctrl1, AxoObjectInstance.this);
                return o;
            }

            @Override
            public void removeView(OutletInstance view) {
            }
        };
        controller.outlets.addView(vOutlets);

        vDisps = new ArrayView<DisplayInstance>(controller.disps) {
            @Override
            public void updateUI() {
                displayInstances.setArray(new ArrayList<>(getSubViews()));
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
        controller.disps.addView(vDisps);

        vParams = new ArrayView<ParameterInstance>(controller.params) {
            @Override
            public void updateUI() {
                parameterInstances.setArray(new ArrayList<>(getSubViews()));
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
        controller.params.addView(vParams);

        vAttrs = new ArrayView<AttributeInstance>(controller.attrs) {
            @Override
            public void updateUI() {
                attributeInstances.setArray(new ArrayList<>(getSubViews()));
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
        controller.attrs.addView(vAttrs);
    }

    @Override
    public boolean setInstanceName(String s) {
        boolean result = super.setInstanceName(s);
        return result;
    }

    @Override
    public InletInstance GetInletInstance(String n) {
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
        for (ParameterInstance o : parameterInstances) {
            if (n.equals(o.parameter.getName())) {
                return o;
            }
        }
        return null;
    }

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

    @Override
    public boolean providesModulationSource() {
        AxoObject atype = getType();
        if (atype == null) {
            return false;
        } else {
            return atype.providesModulationSource();
        }
    }

    @Override
    public AxoObject getType() {
        return (AxoObject) super.getType();
    }

    @Override
    public boolean PromoteToOverloadedObj() {
        /* FIXME
         if (getType() instanceof AxoObjectFromPatch) {
         return false;
         }
         if (getType() instanceof AxoObjectPatcher) {
         return false;
         }
         if (getType() instanceof AxoObjectPatcherObject) {
         return false;
         }
         String id = typeName;
         ArrayList<AxoObjectAbstract> candidates = MainFrame.axoObjects.GetAxoObjectFromName(id, getPatchModel().GetCurrentWorkingDirectory());
         if (candidates == null) {
         return false;
         }
         if (candidates.isEmpty()) {
         Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "could not resolve any candidates {0}", id);
         }
         if (candidates.size() == 1) {
         return false;
         }

         int ranking[];
         ranking = new int[candidates.size()];
         // auto-choose depending on 1st connected inlet

         //      InletInstance i = null;// = GetInletInstances().get(0);
         for (InletInstance j : getInletInstances()) {
         Net n = getPatchModel().GetNet(j);
         if (n == null) {
         continue;
         }
         DataType d = n.getDataType();
         if (d == null) {
         continue;
         }
         String name = j.getModel().getName();
         for (int i = 0; i < candidates.size(); i++) {
         AxoObjectAbstract o = candidates.get(i);
         Inlet i2 = o.GetInlet(name);
         if (i2 == null) {
         continue;
         }
         if (i2.getDatatype().equals(d)) {
         ranking[i] += 10;
         } else if (d.IsConvertableToType(i2.getDatatype())) {
         ranking[i] += 2;
         }
         }
         }

         int max = -1;
         int maxi = 0;
         for (int i = 0; i < candidates.size(); i++) {
         if (ranking[i] > max) {
         max = ranking[i];
         maxi = i;
         }
         }
         AxoObjectAbstract selected = candidates.get(maxi);
         int rindex = candidates.indexOf(getType());
         if (rindex >= 0) {
         if (ranking[rindex] == max) {
         selected = getType();
         }
         }

         if (selected == null) {
         //Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO,"no promotion to null" + this + " to " + selected);
         return false;
         }
         if (selected != getType()) {
         Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.FINE, "promoting " + this + " to " + selected);
         getPatchModel().ChangeObjectInstanceType(this, selected);
         return true;
         }*/
        return false;
    }

    @Override
    public ArrayList<SDFileReference> GetDependendSDFiles() {
        ArrayList<SDFileReference> files = getType().filedepends;
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
        for (AttributeInstance a : attributeInstances) {
            ArrayList<SDFileReference> f2 = a.GetDependendSDFiles();
            if (f2 != null) {
                files.addAll(f2);
            }
        }
        return files;
    }

    public void ConvertToPatchPatcher() {/*
         try {
         ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/patcher", null);
         assert (!ol.isEmpty());
         AxoObjectAbstract o = ol.get(0);
         AxoObjectInstancePatcher oi = (AxoObjectInstancePatcher) getPatchModel().AddObjectInstance(o, new Point(x, y));
         AxoObjectFromPatch ao = (AxoObjectFromPatch) getType();
         Strategy strategy = new AnnotationStrategy();
         Serializer serializer = new Persister(strategy);
         oi.setSubPatchModel(serializer.read(PatchModel.class, new File(ao.patchModel.getFileNamePath())));
         oi.initSubpatchFrame();
         oi.updateObj();
         getPatchModel().transferState(this, oi);
         //getPatchModel().delete(this);
         getPatchModel().setDirty();
         oi.setInstanceName(getInstanceName());
         } catch (Exception ex) {
         Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "Failed to convert to patch/patcher", ex);
         }*/

    }

    public void ConvertToEmbeddedObj() {/*
         try {
         ArrayList<AxoObjectAbstract> ol = MainFrame.mainframe.axoObjects.GetAxoObjectFromName("patch/object", null);
         assert (!ol.isEmpty());
         AxoObjectAbstract o = ol.get(0);
         String iname = getInstanceName();
         AxoObjectInstancePatcherObject oi = (AxoObjectInstancePatcherObject) getPatchModel().ChangeObjectInstanceType1(this, o);
         AxoObject ao = getType();
         oi.ao = new AxoObjectPatcherObject(ao.id, ao.sDescription);
         oi.ao.copy(ao);
         oi.ao.sPath = "";
         oi.ao.upgradeSha = null;
         oi.ao.CloseEditor();
         oi.setInstanceName(iname);
         getPatchModel().setDirty();
         getPatchModel().transferState(this, oi);
         //getPatchModel().delete(this);
         getPatchModel().setDirty();
         } catch (CloneNotSupportedException ex) {
         Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, null, ex);
         }*/

    }

    @Persist
    public void Persist() {
        AxoObject o = getType();
        if (o != null) {
            if (o.uuid != null && !o.uuid.isEmpty()) {
                typeUUID = o.uuid;
                typeSHA = null;
            }
        }
    }

    public Rectangle editorBounds;
    public Integer editorActiveTabIndex;

    public void updateObj() {/*
         getPatchModel().ChangeObjectInstanceType(this, this.getType());
         */

    }

    public void ObjectModified(Object src) {
        if (getPatchModel() != null) {
            if (!getPatchModel().getLocked()) {
            } else {
            }
        }

        try {
            AxoObject o = (AxoObject) src;
            if (o.editor != null && o.editor.getBounds() != null) {
                editorBounds = o.editor.getBounds();
                editorActiveTabIndex = o.editor.getActiveTabIndex();
                this.getType().editorBounds = editorBounds;
                this.getType().editorActiveTabIndex = editorActiveTabIndex;
            }
        } catch (ClassCastException ex) {
        }
    }

    @Override
    public void Close() {
        super.Close();
        for (AttributeInstance a : attributeInstances) {
            a.Close();
        }
    }

    @Override
    public ArrayModel<InletInstance> getInletInstances() {
        return this.inletInstances;
    }

    @Override
    public ArrayModel<OutletInstance> getOutletInstances() {
        return this.outletInstances;
    }

    @Override
    public ArrayModel<ParameterInstance> getParameterInstances() {
        return this.parameterInstances;
    }

    @Override
    public ArrayModel<AttributeInstance> getAttributeInstances() {
        return this.attributeInstances;
    }

    @Override
    public ArrayModel<DisplayInstance> getDisplayInstances() {
        return this.displayInstances;
    }

    @Override
    public void setInletInstances(ArrayModel<InletInstance> inletInstances) {
        this.inletInstances = inletInstances;
    }

    @Override
    public void setOutletInstances(ArrayModel<OutletInstance> outletInstances) {
        this.outletInstances = outletInstances;
    }

    @Override
    public void setParameterInstances(ArrayModel<ParameterInstance> parameterInstances) {
        this.parameterInstances = parameterInstances;
    }

    @Override
    public void setAttributeInstances(ArrayModel<AttributeInstance> attributeInstances) {
        this.attributeInstances = attributeInstances;
    }

    @Override
    public void setDisplayInstances(ArrayModel<DisplayInstance> displayInstances) {
        this.displayInstances = displayInstances;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void applyValues(AxoObjectInstanceAbstract sourceObject) {
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
        }
    }

}
