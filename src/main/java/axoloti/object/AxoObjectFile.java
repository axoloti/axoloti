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

import axoloti.Version;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Validate;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "objdefs")
public class AxoObjectFile {

    @Attribute(required = false)
    String appVersion;

    @ElementListUnion({
        @ElementList(entry = "obj.normal", type = AxoObject.class, inline = true, required = false),
        @ElementList(entry = "obj.comment", type = AxoObjectComment.class, inline = true, required = false),
        @ElementList(entry = "obj.hyperlink", type = AxoObjectHyperlink.class, inline = true, required = false)
    })
    public ArrayList<AxoObjectAbstract> objs;

    static public class ObjectVersionException
            extends RuntimeException {

        ObjectVersionException(String msg) {
            super(msg);
        }
    }

    private static final int AVX = getVersionX(Version.AXOLOTI_SHORT_VERSION),
            AVY = getVersionY(Version.AXOLOTI_SHORT_VERSION),
            AVZ = getVersionZ(Version.AXOLOTI_SHORT_VERSION);

    private static int getVersionX(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                String v = vS.substring(0, i);
                try {
                    return Integer.valueOf(v);
                } catch (NumberFormatException e) {
                }
            }
        }
        return -1;
    }

    private static int getVersionY(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                int j = vS.indexOf('.', i + 1);
                if (j > 0) {
                    String v = vS.substring(i + 1, j);
                    try {
                        return Integer.valueOf(v);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return -1;
    }

    private static int getVersionZ(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                int j = vS.indexOf('.', i + 1);
                if (j > 0) {
                    String v = vS.substring(j + 1);
                    try {
                        return Integer.valueOf(v);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return -1;
    }

    public AxoObjectFile() {
        objs = new ArrayList<AxoObjectAbstract>();
    }

    @Validate
    public void Validate() {
        // called after deserialializtion, stops validation
        if (appVersion != null
                && !appVersion.equals(Version.AXOLOTI_SHORT_VERSION)) {
            int vX = getVersionX(appVersion);
            int vY = getVersionY(appVersion);
            int vZ = getVersionZ(appVersion);

            if (AVX > vX) {
                return;
            }
            if (AVX == vX) {
                if (AVY > vY) {
                    return;
                }
                if (AVY == vY) {
                    if (AVZ > vZ) {
                        return;
                    }
                    if (AVZ == vZ) {
                        return;
                    }
                }
            }

            throw new ObjectVersionException(appVersion);
        }
    }


    @Persist
    public void Persist() {
        // called prior to serialization
        appVersion = Version.AXOLOTI_SHORT_VERSION;
    }
}
