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

import axoloti.MainFrame;
import axoloti.PatchModel;
import axoloti.SDFileReference;
import axoloti.attribute.AttributeInstance;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IView;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.utils.CharEscape;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj_abstr")
public abstract class AxoObjectInstanceAbstract extends AbstractModel implements IAxoObjectInstance, IView<ObjectController> {

    @Attribute(name = "type")
    String typeName;
    @Deprecated
    @Attribute(name = "sha", required = false)
    String typeSHA;
    @Attribute(name = "uuid", required = false)
    String typeUUID;
    @Attribute(name = "name", required = false)
    String InstanceName;
    @Attribute
    int x;
    @Attribute
    int y;
    
    Boolean selected = false;

    private final PatchModel patchModel;

    IAxoObject type;
    boolean typeWasAmbiguous = false;
    
    ObjectController controller;

    public AxoObjectInstanceAbstract() {
        patchModel = null;
        type = null;
    }

    public AxoObjectInstanceAbstract(ObjectController typeController, PatchModel patchModel, String InstanceName1, Point location) {
        super();
        this.type = typeController.getModel();
        this.controller = typeController;
        typeName = type.getId();
        if (type.isCreatedFromRelativePath() && (patchModel != null)) {
            String pPath = patchModel.getFileNamePath();
            String oPath = type.getPath();

            if (oPath.endsWith(".axp") || oPath.endsWith(".axo") || oPath.endsWith(".axs")) {
                oPath = oPath.substring(0, oPath.length() - 4);
            }
            pPath = pPath.replaceAll("\\\\", "/");
            oPath = oPath.replaceAll("\\\\", "/");
            String[] pPathA = pPath.split("/");
            String[] oPathA = oPath.split("/");
            int i = 0;
            while ((i < pPathA.length) && (i < oPathA.length) && (oPathA[i].equals(pPathA[i]))) {
                i++;
            }
            String rPath = "";
            for (int j = i; j < pPathA.length - 1; j++) {
                rPath += "../";
            }
            if (rPath.isEmpty()) {
                rPath = ".";
            } else {
                rPath = rPath.substring(0, rPath.length() - 1);
            }
            for (int j = i; j < oPathA.length; j++) {
                rPath += "/" + oPathA[j];
            }

            System.out.println(rPath);
            typeName = rPath;
        }

        typeUUID = type.getUUID();
        this.InstanceName = InstanceName1;
        this.x = location.x;
        this.y = location.y;
        this.patchModel = patchModel;
    }

    @Override
    public IAxoObject getType() {
        return type;
    }

    @Override
    public IAxoObject resolveType(String directory) {
        if (type != null) {
            return type;
        }
        if (typeUUID != null) {
            type = MainFrame.axoObjects.GetAxoObjectFromUUID(typeUUID);
            if (type != null) {
                System.out.println("restored from UUID:" + type.getId());
                typeName = type.getId();
            }
        }
        if (type == null) {
            List<IAxoObject> types = MainFrame.axoObjects.GetAxoObjectFromName(typeName, directory);
            if (types == null) {
                Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, "Object name {0} not found", typeName);
            } else { // pick first
                if (types.size() > 1) {
                    typeWasAmbiguous = true;
                }
                type = types.get(0);
                if (type instanceof AxoObjectUnloaded) {
                    AxoObjectUnloaded aou = (AxoObjectUnloaded) type;
                    type = aou.Load();
                    return (AxoObject) type;
                }
            }
        }
        return type;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    /*
     public class AxoObjectInstanceNameVerifier extends InputVerifier {

     @Override
     public boolean verify(JComponent input) {
     String text = ((TextFieldComponent) input).getText();
     Pattern p = Pattern.compile("[^a-z0-9_]", Pattern.CASE_INSENSITIVE);
     Matcher m = p.matcher(text);
     boolean b = m.find();
     if (b) {
     System.out.println("reject instancename : special character found");
     return false;
     }
     if (patch != null) {
     for (AxoObjectInstanceAbstract o : patch.objectinstances) {
     if (o.InstanceName.equalsIgnoreCase(text) && (AxoObjectInstanceAbstract.this != o)) {
     System.out.println("reject instancename : exists");
     return false;
     }
     }
     }
     return true;
     }
     }
     */
    public String GenerateInstanceDataDeclaration2() {
        return "";
    }

    public String GenerateCodeMidiHandler(String vprefix) {
        return "";
    }

    private Point p = new Point();
    
    public boolean providesModulationSource() {
        return false;
    }

    @Override
    public int compareTo(IAxoObjectInstance o) {
        if (o.getY() == this.y) {
            if (o.getX() < x) {
                return 1;
            } else if (o.getX() == x) {
                return 0;
            } else {
                return -1;
            }
        }
        if (o.getY() < y) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String getLegalName() {
        return CharEscape.CharEscape(InstanceName);
    }

    @Override
    public String getCInstanceName() {
        String s = "instance" + getLegalName();
        return s;
    }

    public boolean hasStruct() {
        return false;
    }

    public boolean hasInit() {
        return false;
    }

    @Override
    public ArrayList<SDFileReference> getFileDepends() {
        return null;
    }

    @Override
    public boolean isTypeWasAmbiguous() {
        return typeWasAmbiguous;
    }

    @Override
    public PatchModel getPatchModel() {
        return patchModel;
    }

    public void Close() {
    }

    @Override
    public ObjectController getController() {
        return controller;
    }

    public void applyValues(IAxoObjectInstance unlinked_object_instance) {
    }

    public String getTypeName() {
        if (type != null) {
            return type.getId();
        } else {
            return typeName;
        }
    }

    final AxoObjectInstancePatcher getContainer() {
        PatchModel pm = getPatchModel();
        if (pm == null) {
            return null;
        }
        return pm.getContainer();
    }

    /* MVC clean methods*/
    public static final Property OBJ_PARAMETER_INSTANCES = new ObjectProperty("ParameterInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_ATTRIBUTE_INSTANCES = new ObjectProperty("AttributeInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_INLET_INSTANCES = new ObjectProperty("InletInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_OUTLET_INSTANCES = new ObjectProperty("OutletInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_DISPLAY_INSTANCES = new ObjectProperty("DisplayInstances", List.class, AxoObjectInstanceAbstract.class);

    @Override
    public String getInstanceName() {
        return InstanceName;
    }

    @Override
    public boolean setInstanceName(String InstanceName) {
        String oldvalue = this.InstanceName;
        if (this.InstanceName.equals(InstanceName)) {
            return false;
        }
        if (getPatchModel() != null) {
            IAxoObjectInstance o1 = getPatchModel().GetObjectInstance(InstanceName);
            if ((o1 != null) && (o1 != this)) {
                Logger.getLogger(IAxoObjectInstance.class.getName()).log(Level.SEVERE, "Object name {0} already exists!", InstanceName);
                return false;
            }
        }
        this.InstanceName = InstanceName;

        firePropertyChange(
                AxoObjectInstance.OBJ_INSTANCENAME,
                oldvalue, this.InstanceName);
        for (ParameterInstance p : getParameterInstances()) {
            p.updateParamOnParent();
        }
        return true;
    }

    @Override
    public Boolean getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Boolean selected) {
        Boolean prev_value = this.selected;
        this.selected = selected;
        firePropertyChange(AxoObjectInstance.OBJ_SELECTED, prev_value, selected);
    }

    @Override
    public void setLocation(Point p) {
        Point oldvalue = this.p;
        this.p = p;
        x = p.x;
        y = p.y;
        firePropertyChange(
                AxoObjectInstance.OBJ_LOCATION,
                oldvalue, p);
    }

    @Override
    public Point getLocation() {
        p.x = x;
        p.y = y;
        return p;
    }

    @Override
    abstract public List<InletInstance> getInletInstances();

    public void setInletInstances(List<InletInstance> x) {
    }

    @Override
    abstract public List<OutletInstance> getOutletInstances();

    public void setOutletInstances(List<OutletInstance> x) {
    }

    @Override
    abstract public List<ParameterInstance> getParameterInstances();

    public void setParameterInstances(List<ParameterInstance> x) {
    }

    @Override
    abstract public List<AttributeInstance> getAttributeInstances();

    public void setAttributeInstances(List<AttributeInstance> x) {
    }

    @Override
    abstract public List<DisplayInstance> getDisplayInstances();

    public void setDisplayInstances(List<DisplayInstance> x) {
    }

    @Override
    abstract public InletInstance GetInletInstance(String n);

    @Override
    abstract public OutletInstance GetOutletInstance(String n);
}
