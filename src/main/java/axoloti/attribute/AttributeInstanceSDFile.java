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
import axoloti.object.AxoObjectInstance;
import axoloti.utils.Constants;
import components.ButtonComponent;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceSDFile extends AttributeInstanceString<AxoAttributeSDFile> {

    @Attribute(name = "file")
    String fileName = "";
    JTextField TFFileName;
    JLabel vlabel;
    ButtonComponent ButtonChooseFile;

    public AttributeInstanceSDFile() {
    }

    public AttributeInstanceSDFile(AxoAttributeSDFile param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        this.axoObj = axoObj1;
    }

    String valueBeforeAdjustment = "";

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFFileName = new JTextField(fileName);
        Dimension d = TFFileName.getSize();
        d.width = 128;
        d.height = 22;
        TFFileName.setFont(Constants.FONT);
        TFFileName.setMaximumSize(d);
        TFFileName.setMinimumSize(d);
        TFFileName.setPreferredSize(d);
        TFFileName.setSize(d);
        add(TFFileName);
        TFFileName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                fileName = TFFileName.getText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        TFFileName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                valueBeforeAdjustment = TFFileName.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFFileName.getText().equals(valueBeforeAdjustment)) {
                    SetDirty();
                }
            }
        });
        ButtonChooseFile = new ButtonComponent("choose");
        ButtonChooseFile.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(GetObjectInstance().getPatch().GetCurrentWorkingDirectory());
                int returnVal = fc.showOpenDialog(GetObjectInstance().getPatch().getPatchframe());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = toRelative(fc.getSelectedFile());
                    TFFileName.setText(f);
                    if (!f.equals(fileName)) {
                        fileName = f;
                        SetDirty();
                    }
                }
            }
        });
        add(ButtonChooseFile);
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
    public void Lock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(false);
        }
        if (ButtonChooseFile != null) {
            ButtonChooseFile.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(true);
        }
        if (ButtonChooseFile != null) {
            ButtonChooseFile.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return fileName;
    }

    @Override
    public void setString(String tableName) {
        this.fileName = tableName;
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
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
        Path basePath = FileSystems.getDefault().getPath(GetObjectInstance().getPatch().getFileNamePath());
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

    String toRelative(File f) {
        if (f == null) {
            return "";
        }
        String FilenamePath = GetObjectInstance().getPatch().getFileNamePath();
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
}
