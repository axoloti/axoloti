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
package axoloti.object.parameter;

import axoloti.datatypes.ValueInt32;
import axoloti.property.Property;
import java.security.MessageDigest;
import java.util.List;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInt32VRadio extends ParameterInt32 {

    @Element
    public ValueInt32 MaxValue;

    public ParameterInt32VRadio() {
        this.MaxValue = new ValueInt32(1);
    }

    public ParameterInt32VRadio(String name, int MinValue, int MaxValue) {
        super(name);
        this.MaxValue = new ValueInt32(MaxValue);
    }

    @Override
    public void updateSHA(MessageDigest md) {
        super.updateSHA(md);
        md.update(("int32.vradio" + MaxValue).getBytes());
    }

    static public final String TYPE_NAME = "int32.vradio";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public List<Property> getEditableFields() {
        List l = super.getEditableFields();
        l.add(VALUE_MAX);
        return l;
    }

    @Override
    public String getCType() {
        return "param_type_int";
    }

    @Override
    public Integer getMinValue() {
        return 0;
    }

    @Override
    public Integer getMaxValue() {
        return MaxValue.getInt();
    }

    @Override
    public void setMaxValue(Integer max) {
        this.MaxValue = new ValueInt32(max);
        firePropertyChange(VALUE_MAX, null, max);
    }

    @Override
    public void setMinValue(Integer v) {
    }


}
