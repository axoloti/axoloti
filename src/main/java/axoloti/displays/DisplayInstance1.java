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

import axoloti.ZoomUtils;
import axoloti.datatypes.Value;
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public abstract class DisplayInstance1<T extends Display> extends DisplayInstance<T> {

    public DisplayInstance1() {
        super();
    }

    @Override
    public String GenerateCodeInit(String vprefix) {
        String s = GetCName() + " = 0;\n";
        return s;
    }

    @Override
    public String valueName(String vprefix) {
        return "displayVector[" + offset + "]";
    }

    public abstract Value getValueRef();

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        boolean shouldPaint = false;
        int newValue = bb.getInt();
        if(getValueRef().getInt() != newValue) {
            shouldPaint = true;
        }
        getValueRef().setRaw(newValue);
        updateV();
        if(shouldPaint) {
            ZoomUtils.paintObjectLayer(this);
        }
    }
}
