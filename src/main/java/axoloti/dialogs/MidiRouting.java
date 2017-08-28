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
import javax.swing.table.DefaultTableModel;
import midirouting.MidiInputRoutingTableController;
import midirouting.MidiOutputRoutingTable;
import midirouting.MidiOutputRoutingTableController;
import midirouting.MidiRoutingTables;
import midirouting.MidiRoutingTablesController;

/**
 *
 * @author jtaelman
 */
public class MidiRouting extends javax.swing.JFrame implements ConnectionStatusListener, IView<MidiRoutingTablesController> {

    final MidiRoutingTablesController controller;

    JButton buttonApply;
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
        table_midi_in_routing = new JTable(new DefaultTableModel() {
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

        });
        table_midi_in_routing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_midi_in_routing.setShowGrid(true);
        DefaultTableModel tm_in = (DefaultTableModel) table_midi_in_routing.getModel();
        tm_in.setRowCount(0);
        tm_in.setColumnIdentifiers(CreateColumnIds());
        table_midi_in_routing.setRowSelectionAllowed(false);
        table_midi_in_routing.setColumnSelectionAllowed(false);

        table_midi_out_routing = new JTable(new DefaultTableModel() {
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

        });
        table_midi_out_routing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_midi_out_routing.setShowGrid(true);
        DefaultTableModel tm_out = (DefaultTableModel) table_midi_out_routing.getModel();
        table_midi_out_routing.setRowSelectionAllowed(false);
        table_midi_out_routing.setColumnSelectionAllowed(false);
        tm_out.setRowCount(0);
        tm_out.setColumnIdentifiers(CreateColumnIds());
        table_midi_out_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_out_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_out_routing.doLayout();
        JScrollPane scrollpane_in = new JScrollPane(table_midi_in_routing);
        JScrollPane scrollpane_out = new JScrollPane(table_midi_out_routing);
        JPanel vPane = new JPanel();
        vPane.setLayout(new BoxLayout(vPane, BoxLayout.PAGE_AXIS));
        vPane.add(new JLabel("work in progress... todo: allow modification, apply changes..."));
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
        buttonApply = new JButton("Apply");
        buttonApply.setEnabled(false);
        buttonApply.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        bPane.add(buttonRefresh);
        bPane.add(buttonApply);
        vPane.add(bPane);
        this.add(vPane);
    }

    void showConnect1(boolean connected) {
        table_midi_in_routing.setEnabled(connected);
        table_midi_out_routing.setEnabled(connected);
        buttonRefresh.setEnabled(connected);
        buttonApply.setEnabled(connected);
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
    }

    private Object[] CreateEmptyTableRowData() {
        Object[] rowdata = new Object[17];
        rowdata[0] = "";
        for (int i = 1; i < 17; i++) {
            rowdata[i] = false;
        }
        return rowdata;
    }

    private Object[] CreateColumnIds() {
        return new Object[]{"port",
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
    }

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

    private void refreshInputs() {
        DefaultTableModel tm_in = (DefaultTableModel) table_midi_in_routing.getModel();
        tm_in.setRowCount(0);
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
    
    private void populateInputTable(){
        int row = 0;
        DefaultTableModel tm_in = (DefaultTableModel) table_midi_in_routing.getModel();
        tm_in.setRowCount(0);
        for(MidiInputRoutingTableView v: mirtvs) {
            if (v == null) continue;
            if (v.controller.getModel().getMapping() == null) continue;
            for(int i = 0; i<v.controller.getModel().getMapping().length; i++) {

                Object[] rowdata = new Object[17];
                tm_in.addRow(rowdata);

                for (int k = 1; k < 17; k++) {
                    tm_in.setValueAt(false, row, k);
                }
                if (v.controller.getModel().getMapping().length > 1) {
                    tm_in.setValueAt(v.controller.getModel().getPortName() + " #" + i + " ->", row, 0);
                } else {
                    tm_in.setValueAt(v.controller.getModel().getPortName() + " ->", row, 0);
                }
                for (int j = 0; j < 4; j++) {
                    int dest = v.controller.getModel().getMapping()[i][j];
                    if (dest != -1) {
                        tm_in.setValueAt(true, row, dest + 1);
                    }
                }
                row++;
            }        
        }        
    }

    final int ntargets = 4;

    class MidiOutputRoutingTableView implements IView<MidiOutputRoutingTableController> {

        final MidiOutputRoutingTableController controller;

        public MidiOutputRoutingTableView(MidiOutputRoutingTableController controller) {
            this.controller = controller;
        }

        @Override
        public void modelPropertyChange(PropertyChangeEvent evt) {
            DefaultTableModel tm_out = (DefaultTableModel) table_midi_out_routing.getModel();
            tm_out.setRowCount(0);
            Object[] rowdata = CreateEmptyTableRowData();
            rowdata[0] = "DIN <-";
            tm_out.addRow(rowdata);
            rowdata = CreateEmptyTableRowData();
            rowdata[0] = "USB Device <-";
            tm_out.addRow(rowdata);
            for (int i = 0; i < 16; i++) {
                rowdata = CreateEmptyTableRowData();
                rowdata[0] = "USB Host #" + i + " <-";
                tm_out.addRow(rowdata);
            }
            MidiOutputRoutingTable.MidiDestinations mort[] = (MidiOutputRoutingTable.MidiDestinations[]) evt.getNewValue();
            if (mort == null) {
                return;
            }
            for (int vport = 0; vport < mort.length; vport++) {
                MidiOutputRoutingTable.MidiDestinations vportd = mort[vport];
                for (MidiOutputRoutingTable.MidiDestination dest : vportd.destinations) {
                    switch (dest.device) {
                        case 0: // no destination
                            break;
                        case 1: // DIN
                            tm_out.setValueAt(true, 0, vport + 1);
                            break;
                        case 2: // USB Device port
                            tm_out.setValueAt(true, 1, vport + 1);
                            break;
                        case 3: // USB Host port
                            tm_out.setValueAt(true, 2 + dest.port, vport + 1);
                            break;
                    }
                }
            }
        }

        @Override
        public MidiOutputRoutingTableController getController() {
            return controller;
        }
    }

    MidiOutputRoutingTableView mortView;

    private void refreshOutputs() {
        MidiOutputRoutingTableController mortc = getController().getModel().outputRoutingTableController;
        mortView = new MidiOutputRoutingTableView(mortc);
        mortc.addView(mortView);
    }

    void refresh() {
        getController().getModel().readFromTarget();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case MidiRoutingTablesController.MRTS_INPUT:
                refreshInputs();
                break;
            case MidiRoutingTablesController.MRTS_OUTPUT:
                refreshOutputs();
                break;
        }
    }

    @Override
    public MidiRoutingTablesController getController() {
        return controller;
    }
}
