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
package axoloti.patch.object.parameter;

import axoloti.object.parameter.ParameterFrac32;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.property.Property;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceFrac32UMap<T extends ParameterFrac32> extends ParameterInstanceFrac32U<T> {

    public ParameterInstanceFrac32UMap() {
        super();
    }

    public ParameterInstanceFrac32UMap(@Attribute(name = "value") double v) {
        super(v);
    }

    public ParameterInstanceFrac32UMap(T param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public List<Property> getEditableFields() {
        List<Property> l = super.getEditableFields();
        l.add(MIDI_CC);
        return l;
    }

}
