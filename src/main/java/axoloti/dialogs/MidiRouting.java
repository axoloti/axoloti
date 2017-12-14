package axoloti.dialogs;

import axoloti.TargetController;
import axoloti.TargetModel;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import midirouting.MidiInputRoutingTable;
import midirouting.MidiOutputRoutingTable;

/**
 *
 * @author jtaelman
 */
public class MidiRouting extends TJFrame {

    MidiInputRoutingTable[] inputRoutingTables;
    MidiOutputRoutingTable[] outputRoutingTables;

    JButton buttonRefresh;

    public MidiRouting(TargetController controller) {
        super(controller);
        initComponents();
    }

    JTable table_midi_in_routing;
    JTable table_midi_out_routing;

    void initComponents() {
        setMinimumSize(new java.awt.Dimension(200, 160));
        table_midi_in_routing = new JTable(new AbstractTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column > 0);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex > 0) {
                    return Boolean.class;
                } else {
                    return String.class;
                }
            }

            @Override
            public int getRowCount() {
                int rows = 0;
                if (inputRoutingTables == null) {
                    return 0;
                }
                for (MidiInputRoutingTable mirt : inputRoutingTables) {
                    int[] mapping = mirt.getMapping();
                    if (mapping != null) {
                        rows += mapping.length;
                    }
                }
                return rows;
            }

            @Override
            public int getColumnCount() {
                return 17;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                int row = 0;
                for (MidiInputRoutingTable mirt : inputRoutingTables) {
                    int[] mapping = mirt.getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            int map = mapping[i];
                            if (columnIndex == 0) {
                                if (mapping.length > 1) {
                                    return mirt.getPortName() + " #" + i + " ->";
                                } else {
                                    return mirt.getPortName() + " ->";
                                }
                            } else {
                                return ((mapping[i] & (1 << (columnIndex - 1))) != 0);
                            }
                        }
                        row++;
                    }
                }
                if (columnIndex == 0) {
                    return "???";
                } else {
                    return false;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                int row = 0;
                for (MidiInputRoutingTable mirt : inputRoutingTables) {
                    int[] mapping = mirt.getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    int[] newmapping = mapping.clone();
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            if (aValue.equals(false)) {
                                newmapping[i] &= ~(1 << columnIndex - 1);
                            } else {
                                newmapping[i] |= (1 << columnIndex - 1);
                            }
                            mirt.setMapping(newmapping);
                        }
                        row++;
                    }
                }
            }

            @Override
            public String getColumnName(int col) {
                return ColumnIds[col];
            }
        });

        table_midi_in_routing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_midi_in_routing.setShowGrid(true);
        table_midi_in_routing.setRowSelectionAllowed(true);
        table_midi_in_routing.setColumnSelectionAllowed(false);
        table_midi_in_routing.setCellSelectionEnabled(false);

        table_midi_out_routing = new JTable(new AbstractTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return (column > 0);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex > 0) {
                    return Boolean.class;
                } else {
                    return String.class;
                }
            }

            @Override
            public int getRowCount() {
                if (outputRoutingTables == null) {
                    return 0;
                }
                int rows = 0;
                for (MidiOutputRoutingTable v : outputRoutingTables) {
                    if (v == null) {
                        continue;
                    }
                    int[] mapping = v.getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    rows += mapping.length;
                }
                return rows;
            }

            @Override
            public int getColumnCount() {
                return 17;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (outputRoutingTables == null) {
                    return 0;
                }
                int row = 0;
                for (MidiOutputRoutingTable v : outputRoutingTables) {
                    if (v == null) {
                        continue;
                    }
                    int[] mapping = v.getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            if (columnIndex == 0) {
                                if (mapping.length > 1) {
                                    return v.getPortName() + " #" + i + " ->";
                                } else {
                                    return v.getPortName() + " ->";
                                }
                            } else {
                                return ((mapping[i] & (1 << (columnIndex - 1))) != 0);
                            }
                        }
                        row++;
                    }
                }
                if (columnIndex == 0) {
                    return "???";
                } else {
                    return false;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                int row = 0;
                for (MidiOutputRoutingTable v : outputRoutingTables) {
                    int[] mapping = v.getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    int[] newmapping = mapping.clone();
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            if (aValue.equals(false)) {
                                newmapping[i] &= ~(1 << columnIndex - 1);
                            } else {
                                newmapping[i] |= (1 << columnIndex - 1);
                            }
                            v.setMapping(newmapping);
                        }
                        row++;
                    }
                }
            }

            @Override
            public String getColumnName(int col) {
                return ColumnIds[col];
            }
        });
        table_midi_out_routing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_midi_out_routing.setShowGrid(true);
        table_midi_out_routing.setRowSelectionAllowed(false);
        table_midi_out_routing.setColumnSelectionAllowed(false);
        table_midi_out_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_out_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_out_routing.doLayout();
        JScrollPane scrollpane_in = new JScrollPane(table_midi_in_routing);
        JScrollPane scrollpane_out = new JScrollPane(table_midi_out_routing);
        JPanel vPane = new JPanel();
        vPane.setLayout(new BoxLayout(vPane, BoxLayout.PAGE_AXIS));
        vPane.add(new JLabel("todo: allow creating/storing devicename based rules..."));
        vPane.add(scrollpane_in);
        vPane.add(scrollpane_out);
        JPanel bPane = new JPanel();
        bPane.setLayout(new BoxLayout(bPane, BoxLayout.LINE_AXIS));
        buttonRefresh = new JButton("Refresh");
        buttonRefresh.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        bPane.add(buttonRefresh);
        vPane.add(bPane);
        this.add(vPane);
    }

    void showConnect1(boolean connected) {
        table_midi_in_routing.setEnabled(connected);
        table_midi_out_routing.setEnabled(connected);
        buttonRefresh.setEnabled(connected);
    }

    void apply() {
        getController().getModel().applyToTarget();
    }

    final String[] ColumnIds = {
        "port",
        "#1",
        "#2",
        "#3",
        "#4",
        "#5",
        "#6",
        "#7",
        "#8",
        "#9",
        "#10",
        "#11",
        "#12",
        "#13",
        "#14",
        "#15",
        "#16"
    };

    private void refreshInputs() {
        table_midi_in_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_in_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_in_routing.doLayout();
        ((AbstractTableModel) table_midi_in_routing.getModel()).fireTableDataChanged();
    }

    private void populateInputTable() {
        ((AbstractTableModel) table_midi_in_routing.getModel()).fireTableDataChanged();
    }

    final int ntargets = 4;

    private void refreshOutputs() {
        table_midi_out_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_out_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_out_routing.doLayout();
        ((AbstractTableModel) table_midi_out_routing.getModel()).fireTableDataChanged();
    }

    private void populateOutputTable() {
        ((AbstractTableModel) table_midi_out_routing.getModel()).fireTableDataChanged();
    }

    void refresh() {
        getController().getModel().readFromTarget();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (TargetModel.CONNECTION.is(evt)) {
            showConnect1(evt.getNewValue() != null);
        } else if (TargetModel.MRTS_INPUT.is(evt)) {
            this.inputRoutingTables = (MidiInputRoutingTable[]) evt.getNewValue();
            refreshInputs();
        } else if (TargetModel.MRTS_OUTPUT.is(evt)) {
            this.outputRoutingTables = (MidiOutputRoutingTable[]) evt.getNewValue();
            refreshOutputs();
        }
    }

}
