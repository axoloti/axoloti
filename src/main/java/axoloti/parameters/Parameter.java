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

import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import axoloti.utils.CharEscape;
import generatedobjects.GeneratedObjects;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 * @param <dt> data type
 */
public abstract class Parameter<dt extends DataType> {

    @Attribute
    public String name;
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

    public ParameterInstance<dt> CreateInstance(AxoObjectInstance o) {
        // resolve deserialized object, copy value and remove
        ParameterInstance<dt> pidn = null;
        for (ParameterInstance pi : o.parameterInstances) {
//            System.out.println("compare " + this.name + "<>" + pi.name);
            if (pi.name.equals(this.name)) {
                pidn = (ParameterInstance<dt>) pi;
                break;
            }
        }
        if (pidn == null) {
//            System.out.println("no match " + this.name);
            ParameterInstance<dt> pi = InstanceFactory();
            pi.axoObj = o;
            pi.name = this.name;
            pi.parameter = this;
//            pi.SetValue(DefaultValue);
            pi.applyDefaultValue();
            o.p_params.add(pi);
            pi.PostConstructor();
            return pi;
        } else {
//            System.out.println("match" + pidn.getName());
            ParameterInstance<dt> pi = InstanceFactory();
//            pidn.convs = pi.convs;
            o.parameterInstances.remove(pidn);
            pi.axoObj = o;
            pi.name = this.name;
            pi.parameter = this;
            pi.CopyValueFrom(pidn);
            pi.PostConstructor();
            o.p_params.add(pi);
            return pi;
        }
    }

    public abstract ParameterInstance InstanceFactory();

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

    public dt getDatatype() {
        return null;
    }

    public void updateSHA(MessageDigest md) {
        md.update(name.getBytes());
//        md.update((byte) getDatatype().hashCode());
    }
}
