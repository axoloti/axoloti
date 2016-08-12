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
package axoloti.attribute;

import axoloti.SDFileReference;
import axoloti.attributedefinition.AxoAttributeSDFile;
import axoloti.attributeviews.AttributeInstanceViewSDFile;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceSDFile extends AttributeInstanceString<AxoAttributeSDFile> {

    @Attribute(name = "file")
    String fileName = "";

    public AttributeInstanceSDFile() {
    }

    public AttributeInstanceSDFile(AxoAttributeSDFile param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        this.axoObj = axoObj1;
    }

    @Override
    public String CValue() {
        File f = getFile();
        if ((f != null) && f.exists()) {
            return f.getName().replaceAll("\\\\", "\\/");
        } else {
            return fileName.replaceAll("\\\\", "\\/");
        }
    }

    @Override
    public String getString() {
        return fileName;
    }

    @Override
    public void setString(String tableName) {
        this.fileName = tableName;
    }

    @Override
    public ArrayList<SDFileReference> GetDependendSDFiles() {
        ArrayList<SDFileReference> files = new ArrayList<SDFileReference>();
        File f = getFile();
        if (f != null && f.exists()) {
            files.add(new SDFileReference(f, f.getName()));
        }
        return files;
    }

    File getFile() {
        Path basePath = FileSystems.getDefault().getPath(getObjectInstance().getPatchModel().getFileNamePath());
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
        String FilenamePath = getObjectInstance().getPatchModel().getFileNamePath();
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
    public void Persist() {
        if (fileName == null) {
            fileName = "";
        }
    }

    @Override
    public AttributeInstanceViewSDFile ViewFactory(AxoObjectInstanceView o) {
        return new AttributeInstanceViewSDFile(this, o);
    }
}
