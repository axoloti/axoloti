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

import components.displays.VValueLabelsComponent;
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public class DisplayInstanceVScale extends DisplayInstance<DisplayVScale> {

    private VValueLabelsComponent vlabels;

    public DisplayInstanceVScale() {
        super();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vlabels = new VValueLabelsComponent(-60, 10, 10);
        add(vlabels);
    }

    @Override
    public String valueName(String vprefix) {
        return "";
    }

    @Override
    public String GenerateCodeInit(String vprefix) {
        return "";
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
    }

    @Override
    public void updateV() {
    }

}
