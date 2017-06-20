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

import axoloti.atom.AtomDefinitionController;
import axoloti.attribute.AttributeInstanceSpinner;
import axoloti.object.AxoObjectInstance;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class AxoAttributeSpinner extends AxoAttribute {

    @Attribute
    Integer MinValue;
    @Attribute
    Integer MaxValue;
    @Attribute
    Integer DefaultValue;

    public AxoAttributeSpinner() {
        MinValue = 0;
        MaxValue = 0;
        DefaultValue = 0;
    }

    public AxoAttributeSpinner(String name, int MinValue, int MaxValue, int DefaultValue) {
        super(name);
        this.MinValue = MinValue;
        this.MaxValue = MaxValue;
        this.DefaultValue = DefaultValue;
    }

    public static final String TypeName = "spinner";

    @Override
    public String getTypeName() {
        return TypeName;
    }

    @Override
    public List<String> getEditableFields() {
        List l = super.getEditableFields();
        l.add(AtomDefinitionController.ATOM_MINVALUE);
        l.add(AtomDefinitionController.ATOM_MAXVALUE);
        l.add(AtomDefinitionController.ATOM_DEFAULTVALUE);
        return l;
    }

    public Integer getMinValue() {
        return MinValue;
    }

    public Integer getMaxValue() {
        return MaxValue;
    }

    public Integer getDefaultValue() {
        return DefaultValue;
    }

    public void setMinValue(Integer MinValue) {
        Integer oldValue = this.MinValue;
        this.MinValue = MinValue;
        firePropertyChange(AtomDefinitionController.ATOM_MINVALUE, oldValue, MinValue);
    }

    public void setMaxValue(Integer MaxValue) {
        Integer oldValue = this.MaxValue;
        this.MaxValue = MaxValue;
        firePropertyChange(AtomDefinitionController.ATOM_MAXVALUE, oldValue, MaxValue);
    }

    public void setDefaultValue(Integer DefaultValue) {
        Integer oldValue = this.DefaultValue;
        this.DefaultValue = DefaultValue;
        firePropertyChange(AtomDefinitionController.ATOM_DEFAULTVALUE, oldValue, DefaultValue);
    }

}
