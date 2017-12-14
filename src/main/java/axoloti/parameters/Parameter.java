/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
package axoloti.parameters;

import axoloti.atom.AtomDefinition;
import axoloti.atom.AtomDefinitionController;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import axoloti.property.BooleanProperty;
import axoloti.property.Property;
import axoloti.utils.CharEscape;
import generatedobjects.GeneratedObjects;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
public abstract class Parameter<T extends ParameterInstance> extends AtomDefinition implements Cloneable {

    @Attribute(required = false)
    public Boolean noLabel;

    public final static Property NOLABEL = new BooleanProperty("NoLabel", Parameter.class, "Hide label");

    public String PropagateToChild;

    public String CType() {
        return "int";
    }

    public Parameter() {
    }

    public Parameter(String name) {
        super(name, null);
    }

    public String GetCName() {
        return "param_" + CharEscape.CharEscape(getName());
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    public ParameterInstance CreateInstance(AxoObjectInstance o) {
        ParameterInstance pi = InstanceFactory();
        AtomDefinitionController c = createController(null, null);
        pi.setController(c);
        c.addView(pi);
        pi.axoObjectInstance = o;
        pi.name = getName();
        pi.parameter = this;
//        pi.applyDefaultValue();
        return pi;
    }

    abstract public Object getDefaultValue();

    public abstract T InstanceFactory();

    public Parameter getClone() {
        Serializer serializer = new Persister();
        ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
        Parameter p = null;
        try {
            serializer.write(this, os);
            p = serializer.read(getClass(), new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception ex) {
            Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public DataType getDatatype() {
        return null;
    }

    public void updateSHA(MessageDigest md) {
        md.update(getName().getBytes());
//        md.update((byte) getDatatype().hashCode());
    }

    @Override
    public Parameter clone() throws CloneNotSupportedException {
        return (Parameter) super.clone();
    }

    @Override
    public List<Property> getEditableFields() {
        List<Property> l = new ArrayList<>();
        l.add(NOLABEL);
        return l;
    }

    public String GetCType() {
        return "param_type_undefined";
    }

    public String GetCUnit() {
        return "param_unit_abstract";
    }

    public Boolean getNoLabel() {
        return noLabel;
    }

    public void setNoLabel(Boolean noLabel) {
        this.noLabel = noLabel;
        firePropertyChange(NOLABEL, null, this.noLabel);
    }

}
