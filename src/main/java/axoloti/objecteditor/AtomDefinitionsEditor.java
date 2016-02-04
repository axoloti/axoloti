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
package axoloti.objecteditor;

import axoloti.atom.AtomDefinition;
import axoloti.object.AxoObject;
import axoloti.object.ObjectModifiedListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jtaelman
 * @param <T>
 */
abstract class AtomDefinitionsEditor<T extends AtomDefinition> extends JPanel {

    final T[] AtomDefinitionsList;
    AxoObject obj;

    public AtomDefinitionsEditor(T[] AtomDefinitionsList) {
        this.AtomDefinitionsList = AtomDefinitionsList;
    }

    abstract ArrayList<T> GetAtomDefinitions();

    abstract String getDefaultName();

    final ObjectModifiedListener oml = new ObjectModifiedListener() {
        @Override
        public void ObjectModified(Object src
        ) {
            jTable1.revalidate();
            jTable1.repaint();
        }

        public void removeNotify() {
            obj.removeObjectModifiedListener(this);
        }
    };

    void initComponents(AxoObject obj) {
        this.obj = obj;
        obj.addObjectModifiedListener(oml);
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jTable1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setVisible(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        jScrollPane1.add(jTable1);
        add(jScrollPane1);
        jText = new JTextArea();
        jScrollPane2 = new JScrollPane();
        add(jScrollPane2);
        jScrollPane2.setViewportView(jText);

        jButtonAdd = new JButton("Add");
        jButtonAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    T o = (T) AtomDefinitionsList[0].getClass().newInstance();
                    int i = 0;
                    while (true) {
                        i++;
                        boolean free = true;
                        for (T a : GetAtomDefinitions()) {
                            if (a.getName().equals(getDefaultName() + i)) {
                                free = false;
                            }
                        }
                        if (free == true) {
                            break;
                        }
                    }
                    o.setName(getDefaultName() + i);
                    GetAtomDefinitions().add(o);
                    jTable1.setRowSelectionInterval(GetAtomDefinitions().size() - 1, GetAtomDefinitions().size() - 1);
                    AtomDefinitionsEditor.this.obj.FireObjectModified(this);
                } catch (InstantiationException ex) {
                    Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(jButtonAdd);
        jButtonRemove = new JButton("Remove");
        jButtonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                if (row < 0) {
                    return;
                }
                GetAtomDefinitions().remove(row);
                if (row > 0) {
                    jTable1.setRowSelectionInterval(row - 1, row - 1);
                }
                AtomDefinitionsEditor.this.obj.FireObjectModified(this);
            }
        });
        add(jButtonRemove);
        jButtonMoveUp = new JButton("Move up");
        jButtonMoveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                if (row < 1) {
                    return;
                }
                T o = GetAtomDefinitions().remove(row);
                GetAtomDefinitions().add(row - 1, o);
                jTable1.setRowSelectionInterval(row - 1, row - 1);
                AtomDefinitionsEditor.this.obj.FireObjectModified(this);
            }
        });
        add(jButtonMoveUp);
        jButtonMoveDown = new JButton("Move down");
        jButtonMoveDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                if (row < 0) {
                    return;
                }
                if (row > (GetAtomDefinitions().size() - 1)) {
                    return;
                }
                T o = GetAtomDefinitions().remove(row);
                GetAtomDefinitions().add(row + 1, o);
                AtomDefinitionsEditor.this.obj.FireObjectModified(this);
                jTable1.setRowSelectionInterval(row + 1, row + 1);
            }
        });
        add(jButtonMoveDown);
        jScrollPane1.setVisible(true);
        jTable1.setModel(new AbstractTableModel() {
            private String[] columnNames = {"Name", "Class", "Description"};

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return getValueAt(0, column).getClass();
                    case 2:
                        return String.class;
                }
                return null;
            }

            @Override
            public int getRowCount() {
                return GetAtomDefinitions().size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {

                switch (columnIndex) {
                    case 0:
                        assert (value instanceof String);
                        GetAtomDefinition(rowIndex).setName((String) value);
                        AtomDefinitionsEditor.this.obj.FireObjectModified(this);
                        break;
                    case 1:
                        try {
                            T j = (T) value.getClass().newInstance();
                            j.setName(GetAtomDefinition(rowIndex).getName());
                            GetAtomDefinitions().set(rowIndex, j);
                            AtomDefinitionsEditor.this.obj.FireObjectModified(this);
                        } catch (InstantiationException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 2:
                        assert (value instanceof String);
                        GetAtomDefinition(rowIndex).setDescription((String) value);
                        AtomDefinitionsEditor.this.obj.FireObjectModified(this);
                        break;
                }
            }

            T GetAtomDefinition(int rowIndex) {
                return GetAtomDefinitions().get(rowIndex);
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object returnValue = null;

                switch (columnIndex) {
                    case 0:
                        returnValue = GetAtomDefinitions().get(rowIndex).getName();
                        break;
                    case 1:
                        returnValue = GetAtomDefinitions().get(rowIndex);
                        break;
                    case 2:
                        returnValue = GetAtomDefinitions().get(rowIndex).getDescription();
                        break;
                }

                return returnValue;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

        });
        JComboBox jComboBoxAtomDefinitionsList = new JComboBox(AtomDefinitionsList);
        jTable1.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jComboBoxAtomDefinitionsList));
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = jTable1.getSelectedRow();
                if (row < 0) {
                    jButtonMoveUp.setEnabled(false);
                    jButtonMoveDown.setEnabled(false);
                } else {
                    jButtonMoveUp.setEnabled(row > 0);
                    jButtonMoveDown.setEnabled(row < GetAtomDefinitions().size() - 1);

                    T o = GetAtomDefinitions().get(row);
                    Class c = o.getClass();
                    Annotation annotations[] = c.getAnnotations();
                    String s = "";
                    for (Annotation a : annotations) {
                        s += a.getClass().getName();
                    }
                    jText.setText(s);
                }
            }
        });
        jScrollPane1.setViewportView(jTable1);
    }

    JScrollPane jScrollPane1;
    JTable jTable1;
    JScrollPane jScrollPane2;
    JTextArea jText;
    JButton jButtonMoveUp;
    JButton jButtonMoveDown;
    JButton jButtonRemove;
    JButton jButtonAdd;

}
