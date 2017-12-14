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
package axoloti.displays;

import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.property.ObjectProperty;
import axoloti.property.PropagatedProperty;
import axoloti.property.Property;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance<T extends Display> extends AtomInstance<T> {

    @Attribute
    String name;
    @Attribute(required = false)
    Boolean onParent;
    protected int index;

    protected int offset;

    AtomDefinitionController controller;

    public static final Property DISP_VALUE = new ObjectProperty("Value", Object.class, DisplayInstance.class);
    public static final PropagatedProperty NOLABEL = new PropagatedProperty(Display.NOLABEL, DisplayInstance.class);

    public DisplayInstance(AtomDefinitionController controller) {
        this.controller = controller;
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public String GetCName() {
        return getModel().GetCName();
    }

    public int getLength() { // length in 32-bit words
        return getModel().getLength();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    public abstract String valueName(String vprefix);

    public abstract String GenerateCodeInit(String vprefix);

    public abstract void ProcessByteBuffer(ByteBuffer bb);

    public String GenerateDisplayMetaInitializer() {
        String c = "{ display_type: " + getModel().GetCMetaType() + ", name: "
                + CodeGeneration.CPPCharArrayStaticInitializer(getModel().getName(), CodeGeneration.param_name_length)
                + ", displaydata: &displayVector[" + offset + "]},\n";
        return c;
    }

    public abstract Object getValue();
    public abstract void setValue(Object o);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        // triggered by a model definition change, triggering instance view changes
        final PropagatedProperty propagateProperties[] = new PropagatedProperty[]{NOLABEL};
        for (PropagatedProperty p : propagateProperties) {
            if (p.is(evt)) {
                firePropertyChange(p,
                        evt.getOldValue(),
                        evt.getNewValue());
            }
        }
    }

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(NOLABEL);
        return l;
    }

}
