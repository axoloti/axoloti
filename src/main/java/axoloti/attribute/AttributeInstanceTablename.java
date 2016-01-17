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

import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.object.AxoObjectInstance;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTablename extends AttributeInstanceString<AxoAttributeTablename> {

    @Attribute(name = "table")
    String tableName = "";
    JTextField TFtableName;
    JLabel vlabel;

    public AttributeInstanceTablename() {
    }

    public AttributeInstanceTablename(AxoAttributeTablename param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFtableName = new JTextField(tableName);
        Dimension d = TFtableName.getSize();
        d.width = 128;
        d.height = 22;
        TFtableName.setFont(Constants.font);
        TFtableName.setMaximumSize(d);
        TFtableName.setMinimumSize(d);
        TFtableName.setPreferredSize(d);
        TFtableName.setSize(d);
        add(TFtableName);
        TFtableName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tableName = TFtableName.getText();
                System.out.println("tablename change " + tableName);
            }
        });
        TFtableName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                tableName = TFtableName.getText();
                System.out.println("tablename change " + tableName);
            }
        });
    }

    @Override
    public String CValue() {
        return tableName;
    }

    @Override
    public void Lock() {
        if (TFtableName != null) {
            TFtableName.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFtableName != null) {
            TFtableName.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return tableName;
    }

    @Override
    public void setString(String tableName) {
        this.tableName = tableName;
        if (TFtableName != null) {
            TFtableName.setText(tableName);
        }
    }

}
