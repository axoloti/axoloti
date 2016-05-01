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

import axoloti.datatypes.Int8Ptr;
import java.security.MessageDigest;

/**
 *
 * @author Johannes Taelman
 */
public class DisplayFrac8U128VBar extends Display {

    public DisplayFrac8U128VBar() {
    }

    public DisplayFrac8U128VBar(String name) {
        super(name);
    }

    @Override
    public DisplayInstanceFrac8U128VBar InstanceFactory() {
        return new DisplayInstanceFrac8U128VBar();
    }

    @Override
    public void updateSHA(MessageDigest md) {
        super.updateSHA(md);
        md.update("frac8.u.128.vbar".getBytes());
    }

    @Override
    public Int8Ptr getDatatype() {
        return Int8Ptr.d;
    }

    static public final String TypeName = "uint8array128.vbar";

    @Override
    public String getTypeName() {
        return TypeName;
    }

}
