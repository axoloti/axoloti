/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti;

import java.io.File;
import java.nio.file.Path;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author jtaelman
 */
public class SDFileReference {

    @Attribute
    public String localFilename;

    @Attribute
    public String targetPath;

    public File localfile;

    public SDFileReference() {
    }

    public SDFileReference(File localfile, String targetPath) {
        this.localfile = localfile;
        this.targetPath = targetPath;
    }

    @Persist
    public void Persist() {
        if (localFilename == null) {
            if (localfile != null) {
                localFilename = localfile.getName();
            } else {
                localFilename = "";
            }
        }
    }

    public void Resolve(Path p) {
        if (localfile != null) {
            return;
        }
        if (p != null) {
            localfile = p.resolve(localFilename).toFile();
        }
    }
}
