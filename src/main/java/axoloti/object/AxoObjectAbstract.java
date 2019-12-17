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

import axoloti.abstractui.IAbstractEditor;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.display.Display;
import axoloti.object.inlet.Inlet;
import axoloti.object.outlet.Outlet;
import axoloti.object.parameter.Parameter;
import axoloti.property.ListProperty;
import axoloti.utils.StringUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "objdef")
public abstract class AxoObjectAbstract extends AbstractModel<ObjectController> implements Comparable, IAxoObject {

    @Attribute
    public String id;

    @Attribute(required = false)
    private String uuid;

    @Deprecated
    @Attribute(required = false)
    private String sha;

    @Deprecated
    @ElementListUnion({
        @ElementList(entry = "upgradeSha", type = String.class, inline = true, required = false),})
    HashSet<String> upgradeSha;

    @Element(name = "sDescription", required = false)
    private String sDescription;

    public String shortId;

    public boolean createdFromRelativePath = false;

    @Element(name = "author", required = false)
    private String sAuthor;
    @Element(name = "license", required = false)
    private String sLicense;
    private String sPath;

    @Commit
    void commit1() {
        // called after deserialializtion
        this.sha = null;
        this.upgradeSha = null;
    }

    @Persist
    public void persist() {
        // called prior to serialization
        this.sha = null;
        this.upgradeSha = null;
    }

    public AxoObjectAbstract() {
        this.sha = null;
        this.upgradeSha = null;
    }

    public AxoObjectAbstract(String id, String sDescription) {
        this.sDescription = sDescription;
        this.id = id;
        this.sha = null;
        this.upgradeSha = null;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(Object t) {
        String tn = ((AxoObjectAbstract) t).id;
        if (id.startsWith(tn)) {
            return 1;
        }
        if (tn.startsWith(id)) {
            return -1;
        }
        return id.compareTo(tn);
    }

    public String getCName() {
        return "noname";
    }

    @Override
    public String getUUID() {
        if (uuid == null) {
            uuid = generateUUID();
        }
        return uuid;
    }

    @Override
    public List<String> getProcessedIncludes() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getIncludes() {
        return Collections.emptyList();
    }

    @Override
    public void setIncludes(List<String> includes) {
    }

    @Override
    public List<String> getDepends() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getModules() {
        return Collections.emptyList();
    }

    @Override
    public String getDefaultInstanceName() {
        if (shortId == null) {
            return "obj";
        }
        int i = shortId.indexOf(' ');
        if (i > 0) {
            return shortId.substring(0, i);
        }
        return shortId;
    }

    @Override
    public List<String> getModulators() {
        return Collections.emptyList();
    }

    public abstract String generateUUID();

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    private IAbstractEditor editor;

    @Override
    public IAbstractEditor getEditor() {
        return editor;
    }

    @Override
    public void setEditor(IAbstractEditor editor) {
        this.editor = editor;
    }


/* MVC clean methods below... */
    public static final ListProperty OBJ_INLETS = new ListProperty("Inlets", AxoObjectAbstract.class);
    public static final ListProperty OBJ_OUTLETS = new ListProperty("Outlets", AxoObjectAbstract.class);
    public static final ListProperty OBJ_ATTRIBUTES = new ListProperty("Attributes", AxoObjectAbstract.class);
    public static final ListProperty OBJ_PARAMETERS = new ListProperty("Parameters", AxoObjectAbstract.class);
    public static final ListProperty OBJ_DISPLAYS = new ListProperty("Displays", AxoObjectAbstract.class);

    @Override
    public abstract List<Inlet> getInlets();

    @Override
    public abstract List<Outlet> getOutlets();

    @Override
    public abstract List<AxoAttribute> getAttributes();

    @Override
    public abstract List<Parameter> getParameters();

    @Override
    public abstract List<Display> getDisplays();

    public void setInlets(List<Inlet> inlets) {
    }

    public void setOutlets(List<Outlet> outlets) {
    }

    public void setAttributes(List<AxoAttribute> attributes) {
    }

    public void setParameters(List<Parameter> parameters) {
    }

    public void setDisplays(List<Display> displays) {
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        String oldvalue = this.id;
        this.id = id;
        firePropertyChange(
                AxoObject.OBJ_ID,
                oldvalue, id);
    }

    @Override
    public String getSHA() {
        return sha;
    }

    @Override
    public String getDescription() {
        return StringUtils.denullString(sDescription);
    }

    public void setDescription(String sDescription) {
        String oldvalue = this.sDescription;
        this.sDescription = sDescription;
        firePropertyChange(
                AxoObject.OBJ_DESCRIPTION,
                oldvalue, sDescription);
    }

    @Override
    public String getLicense() {
        return StringUtils.denullString(sLicense);
    }

    public void setLicense(String sLicense) {
        String oldvalue = this.sLicense;
        this.sLicense = sLicense;
        firePropertyChange(
                AxoObject.OBJ_LICENSE,
                oldvalue, sLicense);
    }

    @Override
    public String getPath() {
        return StringUtils.denullString(sPath);
    }

    public final void setPath(String sPath) {
        String oldvalue = this.sPath;
        this.sPath = sPath;
        firePropertyChange(
                AxoObject.OBJ_PATH,
                oldvalue, sPath);
    }

    @Override
    public String getAuthor() {
        return StringUtils.denullString(sAuthor);
    }

    public void setAuthor(String sAuthor) {
        String oldvalue = this.sAuthor;
        this.sAuthor = sAuthor;
        firePropertyChange(
                AxoObject.OBJ_AUTHOR,
                oldvalue, sAuthor);
    }

    @Override
    protected ObjectController createController() {
        ObjectController controller = new ObjectController(this);
        return controller;
    }

    @Override
    public boolean isCreatedFromRelativePath() {
        return createdFromRelativePath;
    }

    @Override
    public IModel getParent() {
        return null;
    }

}
