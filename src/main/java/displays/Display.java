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
package displays;

import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstance;
import java.security.MessageDigest;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class Display {

    @Attribute
    public String name;
    @Attribute(required = false)
    public Boolean noLabel;

    public Display() {
    }

    public Display(String name) {
        this.name = name;
    }

    public int getLength() {
        return 1;
    }
    
    public String GetCName(){
        return "disp_" + name;
    }    
    
    public DisplayInstance CreateInstance(AxoObjectInstance o) {
        // resolve deserialized object, copy value and remove
        DisplayInstance pidn = null;
        for (DisplayInstance pi : o.displayInstances) {
//            System.out.println("compare " + this.name + "<>" + pi.name);
            if (pi.name.equals(this.name)) {
                pidn = (DisplayInstance1) pi;
                break;
            }
        }
        if (pidn == null) {
//            System.out.println("no match " + this.name);
            DisplayInstance pi = InstanceFactory();
            pi.axoObj = o;
            pi.name = this.name;
            pi.display = this;
//            pi.SetValue(DefaultValue);
            o.p_displays.add(pi);
            pi.PostConstructor();
            return pi;
        } else {
//            System.out.println("match" + pidn.getName());
            o.parameterInstances.remove(pidn);
            pidn.axoObj = o;
            pidn.display = this;
            pidn.PostConstructor();
            o.p_displays.add(pidn);
            return pidn;
        }
    }

    public abstract DisplayInstance InstanceFactory();

    public abstract DataType getDatatype();

    public void updateSHA(MessageDigest md) {
        md.update(name.getBytes());
//        md.update((byte)getDatatype().hashCode());
    }
}
