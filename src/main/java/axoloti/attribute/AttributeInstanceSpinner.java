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
package axoloti.attribute;

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.attributeviews.AttributeInstanceViewSpinner;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.attributeviews.PAttributeInstanceViewSpinner;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceSpinner extends AttributeInstanceInt<AxoAttributeSpinner> {

    private AxoObjectInstance axoObj;

    public AttributeInstanceSpinner() {
    }

    public AttributeInstanceSpinner(AxoAttributeSpinner param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        this.axoObj = axoObj1;
        value = attr.getDefaultValue();
    }

    @Override
    public String CValue() {
        return "" + value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public IAttributeInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PAttributeInstanceViewSpinner(this, (PAxoObjectInstanceView) o);
        } else {
            return new AttributeInstanceViewSpinner(this, (AxoObjectInstanceView) o);
        }
    }
}
