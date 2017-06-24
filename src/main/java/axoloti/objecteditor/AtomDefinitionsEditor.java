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

import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomDefinition;
import axoloti.datatypes.ValueFrac32;
import axoloti.datatypes.ValueInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.mvc.array.ArrayView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jtaelman
 * @param <T>
 */
abstract class AtomDefinitionsEditor<T extends AtomDefinition> extends ArrayView {

    final T[] AtomDefinitionsList;
//    ArrayController atomDefinitions;
    private AtomDefinitionController o;

    public AtomDefinitionsEditor(ArrayController atomDefinitions, T[] atomDefinitionsList) {
        super(atomDefinitions);
        this.AtomDefinitionsList = atomDefinitionsList;
    }

    abstract String getDefaultName();

    static String StringArrayToString(ArrayList<String> va) {
        // items quoted, separated by comma
        // quote characters escaped with backslash
        String s = "";
        boolean first = true;
        for (String s1 : va) {
            if (!first) {
                s += ", ";
            }
            String s2 = s1.replaceAll("\\\\", "\\\\\\");
            s2 = s2.replaceAll("\"", "\\\\\"");
            s += "\"" + s2 + "\"";
            first = false;
        }
        return s;
    }

    static ArrayList<String> StringToStringArrayList(String s) {
        // items separated by comma
        // items can be within quotes
        // backlash to escape quote character
        ArrayList<String> l = new ArrayList<String>();
        int si = 0;
        int se = 0;
        boolean quoted = false;
        boolean escaped = false;
        String e = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!escaped) {
                switch (c) {
                    case '\"':
                        if (!quoted) {
                            quoted = true;
                            e = e.trim();
                            si = i;
                        } else {
                            quoted = false;
                            se = i;
                        }
                        break;
                    case ',':
                        if (!quoted) {
                            if (i == 0) {
                                l.add("");
                                si = 1;
                            } else if (se > si) {
                                // quoted
                                l.add(e);
                                si = i + 1;
                                e = "";
                            } else {
                                l.add(e);
                                si = i + 1;
                                e = "";
                            }
                        } else {
                            e += c;
                        }
                        break;
                    case '\\':
                        escaped = true;
                        break;
                    default:
                        e += c;
                }
            } else {
                e += c;
                escaped = false;
            }
        }
        if (e.length() > 0) {
            l.add(e);
        }
        return l;
    }

    void initComponents(ArrayController obj, JPanel panel) {
//        this.obj = obj;
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jTable1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setVisible(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        jScrollPane1.add(jTable1);
        panel.add(jScrollPane1);

        jPanel1 = new JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));
        panel.add(jPanel1);

        jScrollPane2 = new JScrollPane();
        jTable2 = new JTable();
        jTable2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable2.setVisible(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        jScrollPane2.add(jTable2);
        panel.add(jScrollPane2);

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
                        for (Object a : getController().getModel().getArray()) {
                            if (((AtomDefinition) a).getName().equals(getDefaultName() + i)) {
                                free = false;
                            }
                        }
                        if (free == true) {
                            break;
                        }
                    }
                    o.setName(getDefaultName() + i);
                    getController().addMetaUndo("add " + getAtomTypeName());
                    getController().add(o);
                    UpdateTable2();
                } catch (InstantiationException ex) {
                    Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        jPanel1.add(jButtonAdd);
        jButtonRemove = new JButton("Remove");
        jButtonRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                if (row < 0) {
                    return;
                }
                if (jTable1.getRowCount() >= row) {
                getController().addMetaUndo("remove " + getAtomTypeName());
                    getController().remove(getController().get(row).getModel());
                }
                if (row > 0) {
                    jTable1.setRowSelectionInterval(row - 1, row - 1);
                }
                UpdateTable2();
                panel.revalidate();
            }
        });
        jPanel1.add(jButtonRemove);
        jButtonMoveUp = new JButton("Move up");
        jButtonMoveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                getController().addMetaUndo("move " + getAtomTypeName());
                getController().moveUp(row);
                jTable1.setRowSelectionInterval(row - 1, row - 1);
            }
        });
        jPanel1.add(jButtonMoveUp);
        jButtonMoveDown = new JButton("Move down");
        jButtonMoveDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable1.getSelectedRow();
                getController().addMetaUndo("move " + getAtomTypeName());
                getController().moveDown(row);
                jTable1.setRowSelectionInterval(row + 1, row + 1);
            }
        });
        jPanel1.add(jButtonMoveDown);
        jScrollPane1.setVisible(true);
        jTable1.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"Name", "Type", "Description"};

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
                return getController().getModel().size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                AtomDefinitionController atomDefinitionController = ((AtomDefinitionController) (getController().get(rowIndex)));
                if (atomDefinitionController == null) {
                    return;
                }
                switch (columnIndex) {
                    case 0: {
                        assert (value instanceof String);
                        AtomDefinitionController c = (AtomDefinitionController) getController().get(rowIndex);
                        getController().addMetaUndo("edit " + getAtomTypeName() + " name");
                        c.changeName((String) value);
                    }
                    break;
                    case 1:
                        try {
                            T j = (T) value.getClass().newInstance();
                            j.setName(GetAtomDefinition(rowIndex).getName());
                            j.setDescription(GetAtomDefinition(rowIndex).getDescription());
                            getController().addMetaUndo("change " + getAtomTypeName() + " type");
                            getController().add(j);
                            getController().getModel().remove(getController().getModel().get(rowIndex));
                            UpdateTable2();
                        } catch (InstantiationException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 2: {
                        assert (value instanceof String);
                        getController().addMetaUndo("edit " + getAtomTypeName() + " description");
                        atomDefinitionController.changeDescription((String) value);
                    }
                    break;
                }
            }

            T GetAtomDefinition(int rowIndex) {
                if (rowIndex < getController().getModel().size()) {
                    return (T) getController().getModel().get(rowIndex);
                } else {
                    return null;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object returnValue = null;

                switch (columnIndex) {
                    case 0:
                        returnValue = ((AtomDefinitionController) (getController().get(rowIndex))).getModel().getName();
                        break;
                    case 1:
                        returnValue = ((AtomDefinitionController) (getController().get(rowIndex))).getModel().getTypeName();
                        break;
                    case 2:
                        returnValue = ((AtomDefinitionController) (getController().get(rowIndex))).getModel().getDescription();
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
                    jButtonRemove.setEnabled(false);
                    jTable2.removeEditor();
                    UpdateTable2();
                } else {
                    jButtonMoveUp.setEnabled(row > 0);
                    jButtonMoveDown.setEnabled(row < getController().getModel().size() - 1);
                    jButtonRemove.setEnabled(true);
                    jTable2.removeEditor();
                    UpdateTable2();
                }
            }
        });

        jTable2.setModel(new AbstractTableModel() {
            private String[] columnNames = {"Property", "Value"};

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
                        return String.class;
                }
                return null;
            }

            @Override
            public int getRowCount() {
                if (fields == null) {
                    return 0;
                }
                return fields.size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                if (fields == null) {
                    return;
                }
                if (o == null) {
                    return;
                }
                String field = fields.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        assert (false);
                        break;
                    case 1:
                        //Field f = getController().get(rowIndex).fields.get(rowIndex);
                        Object v = o.getModelProperty(field);
                        Type t = v.getClass();
                        if (v instanceof Integer) {
                            try {
                                getController().addMetaUndo("edit " + field);
                                o.setModelUndoableProperty(field, Integer.parseInt((String) value));
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (v instanceof ArrayList) {
                            /*
                             try {
                             ArrayList<String> l = (ArrayList<String>) f.get(o);
                             l.clear();
                             String s = (String) value;
                             l.addAll(StringToStringArrayList(s));
                             } catch (IllegalArgumentException ex) {
                             Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                             } catch (IllegalAccessException ex) {
                             Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                             }
                             */
                        } else if (v instanceof ValueInt32) {
                            try {
                                ValueInt32 v2 = new ValueInt32(Integer.parseInt((String) value));
                                getController().addMetaUndo("edit " + field);
                                o.setModelUndoableProperty(field, v2);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (v instanceof ValueFrac32) {
                            try {
                                ValueFrac32 v2 = new ValueFrac32(Double.parseDouble((String) value));
                                getController().addMetaUndo("edit " + field);
                                o.setModelUndoableProperty(field, v2);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (v instanceof String) {
                            try {
                                getController().addMetaUndo("edit " + field);
                                o.setModelUndoableProperty(field, value);
                            } catch (IllegalArgumentException ex) {
                                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (fields == null) {
                    return null;
                }
                if (o == null) {
                    return null;
                }
                String field = fields.get(rowIndex);
                Object returnValue = null;
                switch (columnIndex) {
                    case 0:
                        returnValue = field;
                        break;
                    case 1: {
                        try {
                            Object v = o.getModelProperty(field);
                            if (v == null) {
                                return "";
                            } else if (v instanceof ArrayList) {
                                ArrayList<String> va = (ArrayList<String>) v;
                                returnValue = StringArrayToString(va);
                            } else if (v instanceof ValueInt32) {
                                ValueInt32 vi = (ValueInt32) v;
                                returnValue = String.valueOf(vi.getInt());
                            } else if (v instanceof ValueFrac32) {
                                ValueFrac32 vi = (ValueFrac32) v;
                                returnValue = String.valueOf(vi.getDouble());
                            } else {
                                returnValue = v.toString();
                            }
                        } catch (IllegalArgumentException ex) {
                            return "illegal argument";
                        }
                    }
                    break;
                }

                return returnValue;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return (columnIndex == 1);
            }

        });

        jScrollPane1.setViewportView(jTable1);
        jScrollPane2.setViewportView(jTable2);
    }

    List<String> fields;

    void UpdateTable2() {
        jButtonRemove.setEnabled(jTable1.getRowCount() > 0);
        fields = null;
        int row = jTable1.getSelectedRow();
        if (row != -1 && (row < getController().getModel().size())) {
            o = (AtomDefinitionController) getController().get(row);
            fields = o.getModel().getEditableFields();
        }
        ((AbstractTableModel) jTable2.getModel()).fireTableDataChanged();
    }

    void setEditable(boolean editable) {
        jTable1.setEnabled(editable);
        jTable2.setEnabled(editable);
        jButtonMoveUp.setEnabled(editable);
        jButtonMoveDown.setEnabled(editable);
        jButtonRemove.setEnabled(editable);
        jButtonAdd.setEnabled(editable);
    }

    JScrollPane jScrollPane1;
    JScrollPane jScrollPane2;
    JTable jTable1;
    JTable jTable2;
    JButton jButtonMoveUp;
    JButton jButtonMoveDown;
    JButton jButtonRemove;
    JButton jButtonAdd;
    JPanel jPanel1;

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (jTable1 != null) {
            jTable1.tableChanged(new TableModelEvent(jTable1.getModel()));
        }
    }

    @Override
    public void updateUI() {
    }

    @Override
    public AbstractView viewFactory(AbstractController ctrl) {
        return null;
    }

    @Override
    public void removeView(AbstractView view) {
    }

    abstract String getAtomTypeName();

}
