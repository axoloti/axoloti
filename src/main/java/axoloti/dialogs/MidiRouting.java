package axoloti.dialogs;

import axoloti.CConnection;
import axoloti.ConnectionStatusListener;
import axoloti.menus.StandardMenubar;
import axoloti.mvc.IView;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import midirouting.MidiInputRoutingTableController;
import midirouting.MidiOutputRoutingTableController;
import midirouting.MidiRoutingTables;
import midirouting.MidiRoutingTablesController;

/**
 *
 * @author jtaelman
 */
public class MidiRouting extends javax.swing.JFrame implements ConnectionStatusListener, IView<MidiRoutingTablesController> {

    final MidiRoutingTablesController controller;

    JButton buttonRefresh;

    public MidiRouting(MidiRoutingTablesController controller) {
        this.controller = controller;
        initComponents();
        CConnection.GetConnection().addConnectionStatusListener(this);
        setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());
    }

    JTable table_midi_in_routing;
    JTable table_midi_out_routing;

    void initComponents() {
        setJMenuBar(new StandardMenubar());
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
                if (mirtvs == null) {
                    return 0;
                }
                for (MidiInputRoutingTableView mirtv : mirtvs) {
                    int[] mapping = mirtv.getController().getModel().getMapping();
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
                for (MidiInputRoutingTableView mirtv : mirtvs) {
                    int[] mapping = mirtv.getController().getModel().getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            int map = mapping[i];
                            if (columnIndex == 0) {
                                if (mapping.length > 1) {
                                    return mirtv.getController().getModel().getPortName() + " #" + i + " ->";
                                } else {
                                    return mirtv.getController().getModel().getPortName() + " ->";
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
                for (MidiInputRoutingTableView mirtv : mirtvs) {
                    int[] mapping = mirtv.getController().getModel().getMapping();
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
                            mirtv.getController().getModel().setMapping(newmapping);
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
                if (mortvs == null) {
                    return 0;
                }
                int rows = 0;
                for (MidiOutputRoutingTableView v : mortvs) {
                    if (v == null) {
                        continue;
                    }
                    int[] mapping = v.controller.getModel().getMapping();
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
                if (mortvs == null) {
                    return 0;
                }
                int row = 0;
                for (MidiOutputRoutingTableView v : mortvs) {
                    if (v == null) {
                        continue;
                    }
                    int[] mapping = v.controller.getModel().getMapping();
                    if (mapping == null) {
                        continue;
                    }
                    for (int i = 0; i < mapping.length; i++) {
                        if (row == rowIndex) {
                            if (columnIndex == 0) {
                                if (mapping.length > 1) {
                                    return v.getController().getModel().getPortName() + " #" + i + " ->";
                                } else {
                                    return v.getController().getModel().getPortName() + " ->";
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
                for (MidiOutputRoutingTableView v : mortvs) {
                    int[] mapping = v.getController().getModel().getMapping();
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
                            v.getController().getModel().setMapping(newmapping);
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

    @Override
    public void ShowConnect() {
        showConnect1(true);
    }

    @Override
    public void ShowDisconnect() {
        showConnect1(false);
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

    class MidiInputRoutingTableView implements IView<MidiInputRoutingTableController> {

        final MidiInputRoutingTableController controller;

        public MidiInputRoutingTableView(MidiInputRoutingTableController controller) {
            this.controller = controller;
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            populateInputTable();
        }

        @Override
        public MidiInputRoutingTableController getController() {
            return controller;
        }
    }

    MidiInputRoutingTableView mirtvs[];
    MidiOutputRoutingTableView mortvs[];

    private void refreshInputs() {
        MidiRoutingTables mrts = getController().getModel();
        if (mrts.inputRoutingTablesController == null) {
            return;
        }
        mirtvs = new MidiInputRoutingTableView[mrts.getInputRoutingTables().length];
        int i = 0;
        for (MidiInputRoutingTableController mirtc : mrts.inputRoutingTablesController) {
            mirtvs[i] = new MidiInputRoutingTableView(mirtc);
            mirtc.addView(mirtvs[i]);
            i++;
        }
        table_midi_in_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (i = 1; i < 17; i++) {
            table_midi_in_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_in_routing.doLayout();
    }

    private void populateInputTable() {
        ((AbstractTableModel) table_midi_in_routing.getModel()).fireTableDataChanged();
    }

    final int ntargets = 4;

    class MidiOutputRoutingTableView implements IView<MidiOutputRoutingTableController> {

        final MidiOutputRoutingTableController controller;

        public MidiOutputRoutingTableView(MidiOutputRoutingTableController controller) {
            this.controller = controller;
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            populateOutputTable();
        }

        @Override
        public MidiOutputRoutingTableController getController() {
            return controller;
        }
    }

    MidiOutputRoutingTableView mortView;

    private void refreshOutputs() {
        MidiRoutingTables mrts = getController().getModel();
        if (mrts.outputRoutingTablesController == null) {
            return;
        }
        mortvs = new MidiOutputRoutingTableView[mrts.getOutputRoutingTable().length];
        int i = 0;
        for (MidiOutputRoutingTableController mortc : mrts.outputRoutingTablesController) {
            mortvs[i] = new MidiOutputRoutingTableView(mortc);
            mortc.addView(mortvs[i]);
            i++;
        }
        table_midi_out_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (i = 1; i < 17; i++) {
            table_midi_out_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_out_routing.doLayout();
    }

    private void populateOutputTable() {
        ((AbstractTableModel) table_midi_out_routing.getModel()).fireTableDataChanged();
    }

    void refresh() {
        getController().getModel().readFromTarget();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case MidiRoutingTables.MRTS_INPUT:
                refreshInputs();
                break;
            case MidiRoutingTables.MRTS_OUTPUT:
                refreshOutputs();
                break;
        }
    }

    @Override
    public MidiRoutingTablesController getController() {
        return controller;
    }
}
