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
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
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
public abstract class Parameter<T extends ParameterInstance> implements AtomDefinition, Cloneable {

    @Attribute
    public String name;
    @Attribute(required = false)
    public String description;

//    @Attribute(required = false)
//    Value<dt> defaultVal;
    @Attribute(required = false)
    public Boolean noLabel;

    public String PropagateToChild;

    public String CType() {
        // fixme
        return "int";
    }

    public Parameter() {
    }

    public Parameter(String name) {
        this.name = name;
    }

    public String GetCName() {
        return "param_" + CharEscape.CharEscape(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public ParameterInstance CreateInstance(AxoObjectInstance o) {
        ParameterInstance pi = InstanceFactory();
        pi.axoObj = o;
        pi.name = this.name;
        pi.parameter = this;
        pi.applyDefaultValue();
        o.p_params.add(pi);
        return pi;
    }

    public abstract T InstanceFactory();

    public Parameter getClone() {
        Serializer serializer = new Persister();
        ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
        Parameter p = null;
        try {
            serializer.write(this, os);
            p = serializer.read(this.getClass(), new ByteArrayInputStream(os.toByteArray()));
        } catch (Exception ex) {
            Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }

    public DataType getDatatype() {
        return null;
    }

    public void updateSHA(MessageDigest md) {
        md.update(name.getBytes());
//        md.update((byte) getDatatype().hashCode());
    }

    @Override
    public Parameter clone() throws CloneNotSupportedException {
        return (Parameter) super.clone();
    }

    @Override
    public List<String> getEditableFields() {
        return new ArrayList<String>();
    }
}
