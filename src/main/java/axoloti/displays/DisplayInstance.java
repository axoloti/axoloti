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

import axoloti.ModelChangedListener;
import axoloti.atom.AtomInstance;
import axoloti.displayviews.IDisplayInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance<T extends Display> implements AtomInstance<T> {

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
    public T getDefinition() {
        return display;
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

    public abstract String valueName(String vprefix);

    public abstract String GenerateCodeInit(String vprefix);

    public void ProcessByteBuffer(ByteBuffer bb) {
        notifyModelChangedListeners();
    }

    public abstract IDisplayInstanceView getViewInstance(IAxoObjectInstanceView o);

    public IDisplayInstanceView createView(IAxoObjectInstanceView o) {
        IDisplayInstanceView displayInstanceView = getViewInstance(o);
        addModelChangedListener((ModelChangedListener) displayInstanceView);
        displayInstanceView.PostConstructor();
        o.addDisplayInstanceView(displayInstanceView);
        return displayInstanceView;
    }

    ArrayList<ModelChangedListener> modelChangedListeners = new ArrayList<ModelChangedListener>();

    public void addModelChangedListener(ModelChangedListener l) {
        modelChangedListeners.add(l);
    }

    protected void notifyModelChangedListeners() {
        for (ModelChangedListener l : modelChangedListeners) {
            l.modelChanged();
        }
    }
}
