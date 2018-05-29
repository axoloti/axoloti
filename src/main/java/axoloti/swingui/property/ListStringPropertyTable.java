package axoloti.swingui.property;

import axoloti.mvc.FocusEdit;
import axoloti.mvc.IModel;
import axoloti.mvc.IView;
import axoloti.property.ListProperty;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Table with one row for each String element of the value of a ListProperty. It
 * attaches itself as a View.
 *
 * @author jtaelman
 */
public class ListStringPropertyTable extends JTable implements IView {

    private final IModel model;
    private final ListProperty property;
    private final TableModel tableModel;

    public ListStringPropertyTable(IModel model, ListProperty property) {
        super();
        this.model = model;
        this.property = property;
        tableModel = new TableModel();
        init();
    }

    private void init() {
        setModel(tableModel);
        setRowSelectionAllowed(false);
        setColumnSelectionAllowed(false);
        setCellSelectionEnabled(false);
        setTableHeader(null);
        model.getController().addView(this);
        update();
    }

    private class TableModel extends AbstractTableModel {

        private List<String> data;

        public void setData(List<String> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            if (data == null) {
                return 1;
            }
            return data.size() + 1;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if ((data == null) || (rowIndex >= data.size())) {
                return "";
            } else {
                return data.get(rowIndex);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public String getColumnName(int column) {
            return "header";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            List<String> new_list = new ArrayList<>(data);
            // handle inserting at end
            if (rowIndex >= new_list.size()) {
                new_list.add((String) aValue);
            } else {
                new_list.set(rowIndex, (String) aValue);
            }
            FocusEdit focusEdit = new FocusEdit() {
                @Override
                protected void focus() {
                    ListStringPropertyTable.this.changeSelection(rowIndex, 0, false, false);
                    ListStringPropertyTable.this.requestFocusInWindow();
                }
            };
            model.getController().addMetaUndo("change " + property.getFriendlyName(), focusEdit);
            model.getController().generic_setModelUndoableProperty(property, new_list);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }

    private List<String> processList(List<String> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        // strip empty items
        List<String> new_list = new ArrayList<>(list.size());
        for (String element : list) {
            if ((element != null) && (!element.isEmpty())) {
                new_list.add(element);
            }
        }
        return new_list;
    }

    private void update() {
        List<String> data = processList((List<String>) property.get(model));
        tableModel.setData(data);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (property.is(evt)) {
            update();
        }
    }

    @Override
    public IModel getDModel() {
        return model;
    }

    @Override
    public void dispose() {
        // TODO: unregister view from controller
    }

}
