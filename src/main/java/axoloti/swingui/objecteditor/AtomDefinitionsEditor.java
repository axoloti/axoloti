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
package axoloti.swingui.objecteditor;

import axoloti.mvc.FocusEdit;
import axoloti.mvc.IView;
import axoloti.object.AxoObject;
import axoloti.object.ObjectController;
import axoloti.object.atom.AtomDefinition;
import axoloti.property.ListProperty;
import axoloti.property.Property;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
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
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jtaelman
 * @param <T>
 */
abstract class AtomDefinitionsEditor<T extends AtomDefinition> implements IView<AxoObject> {

    private List<T> atomDefinitionsList;
    private AtomDefinition o;
    private final ListProperty prop;
    private final AxoObject obj;
    private final AxoObjectEditor editor;
    private JPanel parentPanel;

    AtomDefinitionsEditor(AxoObject obj, ListProperty atomfield, List<T> atomDefinitionsList, AxoObjectEditor editor) {
        this.obj = obj;
        this.prop = atomfield;
        this.atomDefinitionsList = atomDefinitionsList;
        this.editor = editor;
    }

    private ObjectController getObjectController() {
        return obj.getController();
    }

    @Override
    public AxoObject getDModel() {
        return obj;
    }

    abstract String getDefaultName();

    abstract String getAtomTypeName();

    private List<IView> atomViews = new ArrayList<>();

    private class FocusEditTableRow extends FocusEdit {

        private final int row;

        FocusEditTableRow(int row) {
            this.row = row;
        }

        @Override
        protected void focus() {
            editor.switchToTab(parentPanel);
            if ((row >= 0) && jTable1.getRowCount() > row) {
                jTable1.setRowSelectionInterval(row, row);
            }
        }

    };

    private ActionListener actionListenerMoveUp = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = jTable1.getSelectedRow();
            if (row < 1) {
                return;
            }
            FocusEditTableRow focusEdit = new FocusEditTableRow(row);
            getObjectController().addMetaUndo("move " + getAtomTypeName(), focusEdit);

            ArrayList<T> n = new ArrayList<>((List<T>) getObjectController().getModelProperty(prop));
            T elem = n.get(row);
            n.remove(row);
            n.add(row - 1, elem);
            getObjectController().generic_setModelUndoableProperty(prop, n);

            jTable1.setRowSelectionInterval(row - 1, row - 1);
        }
    };

    private ActionListener actionListenerMoveDown = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = jTable1.getSelectedRow();

            FocusEditTableRow focusEdit = new FocusEditTableRow(row);

            getObjectController().addMetaUndo("move " + getAtomTypeName(), focusEdit);

            if (row < 0) {
                return;
            }
            ArrayList<T> n = new ArrayList<>((List<T>) getObjectController().getModelProperty(prop));
            if (row > (n.size() - 1)) {
                return;
            }
            T o = n.remove(row);
            n.add(row + 1, o);
            getObjectController().generic_setModelUndoableProperty(prop, n);

            jTable1.setRowSelectionInterval(row + 1, row + 1);
        }
    };

    private final ActionListener actionListenerRemove = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = jTable1.getSelectedRow();
            if (row < 0) {
                return;
            }
            if (jTable1.getRowCount() >= row) {

                FocusEditTableRow focusEdit = new FocusEditTableRow(row);

                getObjectController().addMetaUndo("remove " + getAtomTypeName(), focusEdit);
                ArrayList<T> n = new ArrayList<>((List<T>) getObjectController().getModelProperty(prop));
                n.remove(row);
                getObjectController().generic_setModelUndoableProperty(prop, n);
            }
            if (row > 0) {
                jTable1.setRowSelectionInterval(row - 1, row - 1);
            }
            updateTable2();
            parentPanel.revalidate();
        }
    };

    private final ActionListener actionListenerAdd = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                T o2 = (T) atomDefinitionsList.get(0).getClass().newInstance();
                int i = 0;
                while (true) {
                    i++;
                    boolean free = true;
                    for (T a : (List<T>) getObjectController().getModelProperty(prop)) {
                        if (a.getName().equals(getDefaultName() + i)) {
                            free = false;
                        }
                    }
                    if (free == true) {
                        break;
                    }
                }
                o2.setName(getDefaultName() + i);
                o2.setParent(obj);
                FocusEditTableRow focusEdit = new FocusEditTableRow(jTable1.getRowCount() - 1);
                getObjectController().addMetaUndo("add " + getAtomTypeName(), focusEdit);
                getObjectController().generic_addUndoableElementToList(prop, o2);
                updateTable2();
            } catch (InstantiationException ex) {
                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AtomDefinitionsEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    void initComponents(JPanel parentPanel) {
        this.parentPanel = parentPanel;
//        this.obj = obj;
        jScrollPane1 = new JScrollPane();
        jTable1 = new JTable();
        jTable1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.setVisible(true);
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        jScrollPane1.add(jTable1);
        parentPanel.add(jScrollPane1);

        jPanel1 = new JPanel();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));
        parentPanel.add(jPanel1);

        jScrollPane2 = new JScrollPane();
        jTable2 = new JTable();
        jTable2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable2.setVisible(true);
        parentPanel.setLayout(new BoxLayout(parentPanel, BoxLayout.Y_AXIS));
        jScrollPane2.add(jTable2);
        parentPanel.add(jScrollPane2);

        jButtonAdd = new JButton("Add");
        jButtonAdd.addActionListener(actionListenerAdd);
        jPanel1.add(jButtonAdd);
        jButtonRemove = new JButton("Remove");
        jButtonRemove.addActionListener(actionListenerRemove);
        jButtonRemove.setEnabled(false);
        jPanel1.add(jButtonRemove);
        jButtonMoveUp = new JButton("Move up");
        jButtonMoveUp.addActionListener(actionListenerMoveUp);
        jPanel1.add(jButtonMoveUp);
        jButtonMoveDown = new JButton("Move down");
        jButtonMoveDown.addActionListener(actionListenerMoveDown);
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
                return ((List<T>) getObjectController().getModelProperty(prop)).size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                List<T> list = (List<T>) getObjectController().getModelProperty(prop);
                T atomDefinitionController = list.get(rowIndex);
                if (atomDefinitionController == null) {
                    return;
                }
                FocusEditTableRow focusEdit = new FocusEditTableRow(rowIndex);
                switch (columnIndex) {
                    case 0: {
                        assert (value instanceof String);
                        getObjectController().addMetaUndo("edit " + getAtomTypeName() + " name", focusEdit);
                        List atomDefinitions = (List) getObjectController().getModelProperty(prop);
                        AtomDefinition m = (AtomDefinition) atomDefinitions.get(rowIndex);
                        m.getController().changeName((String) value);
                    }
                    break;
                    case 1:
                        try {
                            T j = (T) value.getClass().newInstance();
                            j.setName(getAtomDefinition(rowIndex).getName());
                            j.setDescription(getAtomDefinition(rowIndex).getDescription());
                            j.setParent(obj);
                            getObjectController().addMetaUndo("change " + getAtomTypeName() + " type", focusEdit);
                            List<T> n = new ArrayList<>((List<T>) getObjectController().getModelProperty(prop));
                            n.set(rowIndex, j);
                            getObjectController().generic_setModelUndoableProperty(prop, n);
                            updateTable2();
                        } catch (InstantiationException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(AxoObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case 2: {
                        assert (value instanceof String);
                        getObjectController().addMetaUndo("edit " + getAtomTypeName() + " description", focusEdit);

                        List atomDefinitions = (List) getObjectController().getModelProperty(prop);
                        AtomDefinition m = (AtomDefinition) atomDefinitions.get(rowIndex);
                        m.getController().changeDescription((String) value);
                    }
                    break;
                    default:
                        break;
                }
            }

            T getAtomDefinition(int rowIndex) {
                List<T> list = (List<T>) getObjectController().getModelProperty(prop);
                if (rowIndex < list.size()) {
                    return list.get(rowIndex);
                } else {
                    return null;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object returnValue = null;
                List<T> list = (List<T>) getObjectController().getModelProperty(prop);

                switch (columnIndex) {
                    case 0:
                        returnValue = list.get(rowIndex).getName();
                        break;
                    case 1:
                        returnValue = list.get(rowIndex).getTypeName();
                        break;
                    case 2:
                        returnValue = list.get(rowIndex).getDescription();
                        break;
                    default:
                        break;
                }
                return returnValue;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

        });
        JComboBox jComboBoxAtomDefinitionsList = new JComboBox(atomDefinitionsList.toArray());
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
                    updateTable2();
                } else {
                    jButtonMoveUp.setEnabled(row > 0);
                    jButtonMoveDown.setEnabled(row < ((List<T>) getObjectController().getModelProperty(prop)).size() - 1);
                    jButtonRemove.setEnabled(true);
                    jTable2.removeEditor();
                    updateTable2();
                }
            }
        });

        jTable2.setModel(new AbstractTableModel() {
            private final String[] columnNames = {"Property", "Value"};

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
                if (properties == null) {
                    return 0;
                }
                return properties.size();
            }

            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                if (properties == null) {
                    return;
                }
                if (o == null) {
                    return;
                }
                Property property = properties.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        assert (false);
                        break;
                    case 1:
                        String svalue = (String) value;
                        Object newValue = property.convertStringToObj(svalue);
                        int table1row = jTable1.getSelectedRow();
                        o.getController().addMetaUndo("change " + property.getFriendlyName(), new FocusEdit() {

                            @Override
                            protected void focus() {
                                editor.switchToTab(parentPanel);
                                if ((table1row >= 0)
                                        && (table1row < jTable1.getRowCount())) {
                                    jTable1.setRowSelectionInterval(table1row, table1row);
                                }
                                if ((rowIndex >= 0)
                                        && (rowIndex < jTable2.getRowCount())) {
                                    jTable2.setRowSelectionInterval(rowIndex, rowIndex);
                                }
                            }
                        });
                        o.getController().generic_setModelUndoableProperty(property, newValue);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (properties == null) {
                    return null;
                }
                if (o == null) {
                    return null;
                }
                Property prop = properties.get(rowIndex);
                Object returnValue = null;
                switch (columnIndex) {
                    case 0:
                        returnValue = prop.getFriendlyName();
                        break;
                    case 1: {
                        try {
                            returnValue = prop.getAsString(o);
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

    private List<Property> properties;

    void updateTable2() {
        jButtonRemove.setEnabled(jTable1.getRowCount() > 0);
        List<T> list = (List<T>) getObjectController().getModelProperty(prop);
        int row = jTable1.getSelectedRow();
        if (row != -1 && (row < list.size())) {
            o = list.get(row);
            properties = o.getEditableFields();
        } else {
            properties = null;
        }
        ((AbstractTableModel) jTable2.getModel()).fireTableDataChanged();
    }

    public void setEditable(boolean editable) {
        jTable1.setEnabled(editable);
        jTable2.setEnabled(editable);
        jButtonMoveUp.setEnabled(editable);
        jButtonMoveDown.setEnabled(editable);
        jButtonRemove.setEnabled(editable);
        jButtonAdd.setEnabled(editable);
    }

    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTable jTable1;
    private JTable jTable2;
    private JButton jButtonMoveUp;
    private JButton jButtonMoveDown;
    private JButton jButtonRemove;
    private JButton jButtonAdd;
    private JPanel jPanel1;

    private class IView1 implements IView<AtomDefinition> {

        final AtomDefinition c;

        IView1(AtomDefinition c) {
            this.c = c;
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            // System.out.println("editor: changed " + evt.getPropertyName());
            if (jTable1 == null) {
                return;
            }
            AbstractTableModel tm = (AbstractTableModel) jTable1.getModel();
            if (tm == null) {
                return;
            }
            tm.fireTableDataChanged();
            updateTable2();
        }

        @Override
        public AtomDefinition getDModel() {
            return c;
        }

        @Override
        public void dispose() {
        }

    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (prop.is(evt)) {
            for (IView v : atomViews) {
                v.getDModel().getController().removeView(v);
            }
            atomViews.clear();
            List<T> list = (List<T>) evt.getNewValue();
            for (T t : list) {
                IView1 v = new IView1(t);
                t.getController().addView(v);
                atomViews.add(v);
            }
            if (jTable1 == null) {
                return;
            }
            ((AbstractTableModel) jTable1.getModel()).fireTableDataChanged();
            updateTable2();
        }
    }

    @Override
    public void dispose() {
    }

}
