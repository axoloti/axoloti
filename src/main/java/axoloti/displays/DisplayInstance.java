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

import axoloti.atom.AtomInstance;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import components.LabelComponent;
import java.nio.ByteBuffer;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance<T extends Display> extends JPanel implements AtomInstance<T> {

    @Attribute
    String name;
    @Attribute(required = false)
    Boolean onParent;
    protected int index;
    public T display;
    AxoObjectInstance axoObj;
    protected int offset;

    public DisplayInstance() {
    }

    @Override
    public AxoObjectInstanceAbstract GetObjectInstance() {
        return axoObj;
    }

    @Override
    public T GetDefinition() {
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

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        if ((display.noLabel == null) || (display.noLabel == false)) {
            add(new LabelComponent(display.name));
        }
        setSize(getPreferredSize());
        if (display.getDescription() != null) {
            setToolTipText(display.getDescription());
        }
    }

    public abstract void ProcessByteBuffer(ByteBuffer bb);

    public abstract void updateV();
}
