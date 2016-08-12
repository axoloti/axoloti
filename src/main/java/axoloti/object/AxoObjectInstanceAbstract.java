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
import axoloti.PatchView;
import axoloti.SDFileReference;
import axoloti.attribute.AttributeInstance;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletInstance;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.CharEscape;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "obj_abstr")
public abstract class AxoObjectInstanceAbstract implements Comparable<AxoObjectInstanceAbstract>, ObjectModifiedListener {

    @Attribute(name = "type")
    public String typeName;
    @Deprecated
    @Attribute(name = "sha", required = false)
    public String typeSHA;
    @Attribute(name = "uuid", required = false)
    public String typeUUID;
    @Attribute(name = "name", required = false)
    String InstanceName;
    @Attribute
    int x;
    @Attribute
    int y;
    public PatchModel patchModel;
    AxoObjectAbstract type;
    private boolean typeWasAmbiguous = false;

    public AxoObjectInstanceAbstract() {
    }

    public AxoObjectInstanceAbstract(AxoObjectAbstract type, PatchModel patchModel, String InstanceName1, Point location) {
        super();
        this.type = type;
        typeName = type.id;
        if (type.createdFromRelativePath && (patchModel != null)) {
            String pPath = patchModel.getFileNamePath();
            String oPath = type.sPath;

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

    public String getInstanceName() {
        return InstanceName;
    }

    public void setType(AxoObjectAbstract type) {
        this.type = type;
        typeUUID = type.getUUID();
    }

    public void setInstanceName(String InstanceName) {
        if (this.InstanceName.equals(InstanceName)) {
            return;
        }
        this.InstanceName = InstanceName;
    }

    public AxoObjectAbstract getType() {
        return type;
    }

    public AxoObjectAbstract resolveType() {
        if (type != null) {
            return type;
        }
        if (typeUUID != null) {
            type = MainFrame.axoObjects.GetAxoObjectFromUUID(typeUUID);
            if (type != null) {
                System.out.println("restored from UUID:" + type.id);
                typeName = type.id;
            }
        }
        if (type == null) {
            ArrayList<AxoObjectAbstract> types = MainFrame.axoObjects.GetAxoObjectFromName(typeName, patchModel.GetCurrentWorkingDirectory());
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

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
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
        return null;
    }

    public String GenerateCodeMidiHandler(String vprefix) {
        return "";
    }

    public String GenerateCallMidiHandler() {
        return "";
    }

    public ArrayList<InletInstance> GetInletInstances() {
        return new ArrayList<InletInstance>();
    }

    public ArrayList<OutletInstance> GetOutletInstances() {
        return new ArrayList<OutletInstance>();
    }

    public ArrayList<ParameterInstance> getParameterInstances() {
        return new ArrayList<ParameterInstance>();
    }

    public ArrayList<AttributeInstance> getAttributeInstances() {
        return new ArrayList<AttributeInstance>();
    }

    public ArrayList<DisplayInstance> GetDisplayInstances() {
        return new ArrayList<DisplayInstance>();
    }

    public InletInstance GetInletInstance(String n) {
        return null;
    }

    public OutletInstance GetOutletInstance(String n) {
        return null;
    }

    public void refreshIndex() {
    }

    public void SetLocation(int x1, int y1) {
        x = x1;
        y = y1;
    }

    public boolean providesModulationSource() {
        return false;
    }

    @Override
    public int compareTo(AxoObjectInstanceAbstract o) {
        if (o.y == this.y) {
            if (o.x < x) {
                return 1;
            } else if (o.x == x) {
                return 0;
            } else {
                return -1;
            }
        }
        if (o.y < y) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getLegalName() {
        return CharEscape.CharEscape(InstanceName);
    }

    public String getCInstanceName() {
        String s = "instance" + getLegalName();
        return s;
    }

    public boolean PromoteToOverloadedObj() {
        return false;
    }

    /*
     public String GenerateStructName() {
     return "";
     }

     public String GenerateDoFunctionName(){
     return "";
     }

     public String GenerateInitFunctionName(){
     return "";
     }
     */
    public String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent) {
        return "";
    }

    public String GenerateDisposeCodePlusPlus(String vprefix) {
        return "";
    }

    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        return "";
    }

    public boolean hasStruct() {
        return false;
    }

    public boolean hasInit() {
        return false;
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        return null;
    }

    public boolean isTypeWasAmbiguous() {
        return typeWasAmbiguous;
    }

    public PatchModel getPatchModel() {
        return patchModel;
    }

    public void setPatchModel(PatchModel patchModel) {
        this.patchModel = patchModel;
    }

    public void updateObj1() {
    }

    public abstract AxoObjectInstanceViewAbstract ViewFactory(PatchView patchView);

    public AxoObjectInstanceViewAbstract CreateView(PatchView patchView) {
        AxoObjectInstanceViewAbstract pi = ViewFactory(patchView);
        pi.PostConstructor();
        return pi;
    }

    @Override
    public void ObjectModified(Object src) {
    }

    public void Close() {
        AxoObjectAbstract t = getType();
        if (t != null) {
            t.removeObjectModifiedListener(this);
        }
    }

    private boolean isDirty = false;

    public void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

}
