package axoloti.dialogs;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jtaelman
 *
 * status: work in progress...
 *
 */
public class PropertyTable extends JTable implements IView {

    final List<Property> properties;
    final AbstractController controller;

    public PropertyTable(AbstractController controller, List<Property> properties) {
        super();
        this.properties = properties;
        this.controller = controller;
        setModel(new AbstractTableModel() {
            private String[] columnNames = {"Property", "Value"};

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
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public int getRowCount() {
                return properties.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return properties.get(rowIndex).getFriendlyName();
                    case 1:
                        return properties.get(rowIndex).get(controller.getModel());
                    default:
                        return null;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 1:
                        return !properties.get(rowIndex).isReadOnly();
                    default:
                        return false;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 1:
                        controller.setModelUndoableProperty(properties.get(rowIndex),
                                aValue);
                    default:
                }
            }

        });
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p.is(evt)) {
                ((AbstractTableModel) getModel()).fireTableCellUpdated(i, 1);
                break;
            }
        }
    }

    @Override
    public AbstractController getController() {
        return controller;
    }

    @Override
    public void dispose() {
    }

}
