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

import java.util.Calendar;

/**
 *
 * @author jtaelman
 */
public class SDFileInfo {

    String filename;
    Calendar timestamp;
    int size;

    public SDFileInfo(String filename, Calendar timestamp, int size) {
        this.filename = filename;
        this.timestamp = timestamp;
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public int getSize() {
        return size;
    }

    public boolean isDirectory() {
        return filename.endsWith("/");
    }

    public String getFilenameNoExtension() {
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            return filename.substring(0, i);
        } else {
            return filename;
        }
    }

    public String getExtension() {
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            return filename.substring(i + 1);
        } else {
            return "";
        }
    }
}
