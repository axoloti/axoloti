/**
 * Copyright (C) 2016 Johannes Taelman
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
package axoloti.atom;

import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import axoloti.property.PropagatedProperty;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
/**
 * An Axoloti Object Instance is composed out of AtomInstances
 */
public abstract class AtomInstance<T extends AtomDefinition> extends AbstractModel implements IView, IModel {

    public abstract T getModel();

    public static final PropagatedProperty NAME = new PropagatedProperty(AtomDefinition.NAME, AtomInstance.class);
    public static final PropagatedProperty DESCRIPTION = new PropagatedProperty(AtomDefinition.DESCRIPTION, AtomInstance.class);

    /**
     *
     * @param evt
     */
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
//        super.modelPropertyChange(evt);
        // triggered by a model definition change, triggering instance view changes
        final PropagatedProperty propagateProperties[] = new PropagatedProperty[]{NAME, DESCRIPTION};
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
        List<Property> l = new ArrayList<>();
        l.add(NAME);
        l.add(DESCRIPTION);
        return l;
    }

    @Override
    public void dispose() {
    }
}
