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
import axoloti.datatypes.ValueFrac32;
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstanceFrac32<T extends Display> extends DisplayInstance1<T> {

    Double value = 0.0;

    DisplayInstanceFrac32(AtomDefinitionController controller) {
        super(controller);
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        ValueFrac32 f = new ValueFrac32();
        f.setRaw(bb.getInt());
        setValue(f.getDouble());
    }

    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double newValue) {
        value = newValue;
        firePropertyChange(DisplayInstanceController.DISP_VALUE, null, value);
    }
}
