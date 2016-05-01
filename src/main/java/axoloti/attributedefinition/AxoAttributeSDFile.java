/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.attributedefinition;

import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.object.AxoObjectInstance;

/**
 *
 * @author Johannes Taelman
 */
public class AxoAttributeSDFile extends AxoAttribute {

    public AxoAttributeSDFile() {
    }

    public AxoAttributeSDFile(String name) {
        super(name);
    }

    @Override
    public AttributeInstanceSDFile InstanceFactory(AxoObjectInstance o) {
        return new AttributeInstanceSDFile(this, o);
    }

    static public final String TypeName = "file";

    @Override
    public String getTypeName() {
        return TypeName;
    }
}
