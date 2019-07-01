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
package axoloti.object;

import java.io.File;
import java.io.FileNotFoundException;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class AxoObjectUnloaded extends AxoObjectAbstract0 {

    private File f;

    public AxoObjectUnloaded() {
        super();
    }

    public AxoObjectUnloaded(String id, File f) {
        super(id, "");
        this.f = f;
    }

    private AxoObjectFromPatch loadedObject;

    public AxoObjectFromPatch load() throws FileNotFoundException {
        if (loadedObject == null) {
            loadedObject = new AxoObjectFromPatch(f);
            loadedObject.id = id;
        }
        return loadedObject;
    }

    @Override
    public String generateUUID() {
        return "unloaded";
    }

    @Override
    public String getSHA() {
        return null;
    }
}
