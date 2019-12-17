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
package axoloti.patch.object.display;

import axoloti.object.display.Display;
import axoloti.patch.object.atom.AtomInstance;
import axoloti.property.ObjectProperty;
import axoloti.property.PropagatedProperty;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance<T extends Display> extends AtomInstance<T, DisplayInstanceController> {

    @Attribute
    private String name;
    @Attribute(required = false)
    private Boolean onParent;

    T display;

    public static final Property DISP_VALUE = new ObjectProperty("Value", Object.class, DisplayInstance.class);
    public static final PropagatedProperty NOLABEL = new PropagatedProperty(Display.NOLABEL, DisplayInstance.class);

    public DisplayInstance(T display) {
        this.display = display;
    }

    @Override
    public T getDModel() {
        return display;
    }

    @Override
    public DisplayInstanceController getController() {
        return super.getController();
    }

    public abstract Object getValue();

    public abstract void setValue(Object o);

    private final PropagatedProperty propagateProperties[] = new PropagatedProperty[]{NOLABEL};

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        // triggered by a model definition change, triggering instance view changes
        for (PropagatedProperty p : propagateProperties) {
            if (p.is(evt)) {
                firePropertyChange(p,
                        evt.getOldValue(),
                        evt.getNewValue());
            }
        }
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(NOLABEL);
        return l;
    }

    @Override
    protected DisplayInstanceController createController() {
        return new DisplayInstanceController(this);
    }
}
