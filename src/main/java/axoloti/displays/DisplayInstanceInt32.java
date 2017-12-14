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
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstanceInt32<T extends Display> extends DisplayInstance1<T> {

    Integer value = 0;

    DisplayInstanceInt32(AtomDefinitionController controller) {
        super(controller);
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        setValue(bb.getInt());
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        value = (Integer) newValue;
        firePropertyChange(DISP_VALUE, null, value);
    }
}
