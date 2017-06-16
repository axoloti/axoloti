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
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.ObjectInstanceController;
import axoloti.utils.CodeGeneration;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance<T extends Display> extends AbstractModel implements AtomInstance<T> {

    @Attribute
    String name;
    @Attribute(required = false)
    Boolean onParent;
    protected int index;
    public T display;
    AxoObjectInstance axoObjectInstance;
    protected int offset;

    public DisplayInstance() {
    }

    @Override
    public AxoObjectInstanceAbstract getObjectInstance() {
        return axoObjectInstance;
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public String GetCName() {
        return display.GetCName();
    }

    public int getLength() { // length in 32-bit words
        return display.getLength();
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

    public void ProcessByteBuffer(ByteBuffer bb) {
        //notifyModelChangedListeners();
    }

    public String GenerateDisplayMetaInitializer() {
        String c = "{ display_type: " + display.GetCMetaType() + ", name: "
                + CodeGeneration.CPPCharArrayStaticInitializer(display.getName(), CodeGeneration.param_name_length)
                + ", displaydata: &displayVector[" + offset + "]},\n";
        return c;
    }

    abstract public Object getValue();
//    abstract void setValue(Object o);

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        // triggered by a model definition change, triggering instance view changes
        if (evt.getPropertyName().equals(AtomDefinitionController.ATOM_NAME)
                || evt.getPropertyName().equals(AtomDefinitionController.ATOM_DESCRIPTION)) {
            firePropertyChange(
                    evt.getPropertyName(),
                    evt.getOldValue(),
                    evt.getNewValue());
        }
    }

    @Override
    public AtomDefinitionController getController() {
        // returning the singleton for now...
        return display.createController(null, null);
    }
}
