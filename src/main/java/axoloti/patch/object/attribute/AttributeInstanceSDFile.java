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
package axoloti.patch.object.attribute;

import axoloti.object.attribute.AxoAttributeSDFile;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.target.fs.SDFileReference;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceSDFile extends AttributeInstanceString<AxoAttributeSDFile> {

    @Attribute(name = "file")
    private String fileName = "";

    AttributeInstanceSDFile() {
        super();
    }

    AttributeInstanceSDFile(AxoAttributeSDFile attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public String CValue() {
        File f = getFile();
        if ((f != null) && f.exists()) {
            return f.getName().replaceAll("\\\\", "\\/");
        } else {
            if (fileName == null) {
                return "";
            }
            return fileName.replaceAll("\\\\", "\\/");
        }
    }

    @Override
    public String getValue() {
        return fileName;
    }

    @Override
    public void setValueString(String value) {
        String oldvalue = this.fileName;
        this.fileName = value;
        firePropertyChange(
                ATTR_VALUE,
                oldvalue, this.fileName);
    }

    @Override
    public List<SDFileReference> getDependendSDFiles() {
        ArrayList<SDFileReference> files = new ArrayList<>();
        File f = getFile();
        if (f != null && f.exists()) {
            files.add(new SDFileReference(f, f.getName()));
        }
        return files;
    }

    File getFile() {
        Path basePath = FileSystems.getDefault().getPath(getParent().getParent().getFileNamePath());
        Path parent = basePath.getParent();
        if (parent == null) {
            return new File(fileName);
        } else if (fileName == null || fileName.length() == 0) {
            return null;
        }
        Path resolvedPath = parent.resolve(fileName);
        if (resolvedPath == null) {
            return null;
        }
        return resolvedPath.toFile();
    }

    public String toRelative(File f) {
        if (f == null) {
            return "";
        }
        String FilenamePath = getParent().getParent().getFileNamePath();
        if (FilenamePath != null && !FilenamePath.isEmpty()) {
            Path pathAbsolute = Paths.get(f.getPath());
            String parent = new File(FilenamePath).getParent();
            if (parent == null) {
                return f.getPath();
            }
            Path pathBase = Paths.get(parent);
            Path pathRelative = pathBase.relativize(pathAbsolute);
            return pathRelative.toString();
        } else {
            return f.getAbsolutePath();
        }
    }

    @Persist
    public void persist() {
        if (fileName == null) {
            fileName = "";
        }
    }

}
