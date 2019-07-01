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

import axoloti.mvc.AbstractModel;
import axoloti.mvc.IView;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectUnloaded;
import axoloti.object.AxoObjectZombie;
import axoloti.object.IAxoObject;
import axoloti.objectlibrary.AxoObjects;
import axoloti.patch.PatchModel;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.property.BooleanProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.PropagatedProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.CharEscape;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
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
public abstract class AxoObjectInstanceAbstract extends AbstractModel<ObjectInstanceController> implements IAxoObjectInstance, IView<IAxoObject> {

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
    private int x;
    @Attribute
    private int y;

    private Boolean selected = false;

    private PatchModel patchModel;

    IAxoObject type;
    boolean typeWasAmbiguous = false;

    public AxoObjectInstanceAbstract() {
        patchModel = null;
        type = null;
    }

    public AxoObjectInstanceAbstract(IAxoObject obj, PatchModel patchModel, String InstanceName1, Point location) {
        super();
        this.type = obj;
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
            StringBuilder rPath = new StringBuilder();
            for (int j = i; j < pPathA.length - 1; j++) {
                rPath.append("../");
            }
            if (rPath.length() == 0) {
                rPath = new StringBuilder(".");
            } else {
                rPath = new StringBuilder(rPath.substring(0, rPath.length() - 1));
            }
            for (int j = i; j < oPathA.length; j++) {
                rPath.append("/");
                rPath.append(oPathA[j]);
            }

            typeName = rPath.toString();
            System.out.println(typeName);
        }

        typeUUID = type.getUUID();
        this.InstanceName = InstanceName1;
        this.x = location.x;
        this.y = location.y;
        this.patchModel = patchModel;
    }

    @Override
    public IAxoObject getDModel() {
        return type;
    }

    @Override
    public IAxoObject resolveType(String directory) {
        if (type != null) {
            return type;
        }
        if (typeUUID != null) {
            type = AxoObjects.getAxoObjects().getAxoObjectFromUUID(typeUUID);
            if (type != null) {
                System.out.println("restored from UUID:" + type.getId());
                typeName = type.getId();
            }
        }
        if (type == null) {
            List<IAxoObject> types = AxoObjects.getAxoObjects().getAxoObjectFromName(typeName, directory);
            if (types == null) {
                // last resort, resolve from sha tag
                String tsha = typeSHA;
                List<IAxoObject> objs = AxoObjects.getAxoObjects().objectList;
                for (IAxoObject obj : objs) {
                    String sha = obj.getSHA();
                    if (sha != null && sha.equals(tsha)) {
                        type = obj;
                        Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, "Object name {0} found via sha tag, future releases may not support this anymore.", typeName);
                        return type;
                    }
                }
                // unresolved, return zombie
                Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, "Object name {0} not found", typeName);
                return new AxoObjectZombie(typeName, "");
            } else { // pick first
                if (types.size() > 1) {
                    typeWasAmbiguous = true;
                }
                type = types.get(0);
                if (type instanceof AxoObjectUnloaded) {
                    AxoObjectUnloaded aou = (AxoObjectUnloaded) type;
                    try {
                        type = aou.load();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(AxoObjectInstanceAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return type;
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
    public String generateInstanceDataDeclaration2() {
        return "";
    }

    public String generateCodeMidiHandler(String vprefix) {
        return "";
    }

    private Point p = new Point();

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
        return CharEscape.charEscape(InstanceName);
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
    public List<SDFileReference> getFileDepends() {
        return null;
    }

    @Override
    public boolean isTypeWasAmbiguous() {
        return typeWasAmbiguous;
    }

    @Override
    public PatchModel getParent() {
        return patchModel;
    }

    public void applyValues(IAxoObjectInstance unlinked_object_instance) {
    }

    @Override
    public String getTypeName() {
        if (type != null) {
            return type.getId();
        } else {
            return typeName;
        }
    }

    final AxoObjectInstancePatcher getContainer() {
        PatchModel pm = getParent();
        if (pm == null) {
            return null;
        }
        return pm.getParent();
    }

    /* MVC clean methods*/
    public static final Property OBJ_INSTANCENAME = new StringProperty("InstanceName", AxoObjectInstanceAbstract.class);
    public static final Property OBJ_LOCATION = new ObjectProperty("Location", Point.class, AxoObjectInstance.class);
    public static final Property OBJ_SELECTED = new BooleanProperty("Selected", AxoObjectInstance.class);
    public static final Property OBJ_PARAMETER_INSTANCES = new ObjectProperty("ParameterInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_ATTRIBUTE_INSTANCES = new ObjectProperty("AttributeInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_INLET_INSTANCES = new ObjectProperty("InletInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_OUTLET_INSTANCES = new ObjectProperty("OutletInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_DISPLAY_INSTANCES = new ObjectProperty("DisplayInstances", List.class, AxoObjectInstanceAbstract.class);
    public static final Property OBJ_INST_MODULATORS = new ObjectProperty("Modulators", List.class, AxoObjectInstanceAbstract.class);
    public static final PropagatedProperty OBJ_INST_AUTHOR = new PropagatedProperty(AxoObject.OBJ_AUTHOR, DisplayInstance.class);
    public static final PropagatedProperty OBJ_INST_LICENSE = new PropagatedProperty(AxoObject.OBJ_LICENSE, DisplayInstance.class);
    public static final PropagatedProperty OBJ_INST_DESCRIPTION = new PropagatedProperty(AxoObject.OBJ_DESCRIPTION, DisplayInstance.class);

    private static final Property[] PROPERTIES = {
        OBJ_INSTANCENAME,
        OBJ_LOCATION,
        OBJ_SELECTED,
        OBJ_PARAMETER_INSTANCES,
        OBJ_ATTRIBUTE_INSTANCES,
        OBJ_INLET_INSTANCES,
        OBJ_OUTLET_INSTANCES,
        OBJ_DISPLAY_INSTANCES
    };

    @Override
    public List<Property> getProperties() {
        List<Property> l = new LinkedList<>();
        l.addAll(Arrays.asList(PROPERTIES));
        return l;
    }

    @Override
    public String getInstanceName() {
        return InstanceName;
    }

    @Override
    public boolean setInstanceName(String InstanceName) {
        String oldvalue = this.InstanceName;
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
    abstract public InletInstance findInletInstance(String n);

    @Override
    abstract public OutletInstance findOutletInstance(String n);

    @Override
    protected ObjectInstanceController createController() {
        return new ObjectInstanceController(this);
    }

    @Override
    public void setParent(PatchModel patchModel) {
        this.patchModel = patchModel;
    }


}
