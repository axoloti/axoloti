package axoloti.dialogs;

import axoloti.CConnection;
import axoloti.ConnectionStatusListener;
import axoloti.IConnection;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import qcmds.QCmdMemRead;

/**
 *
 * @author jtaelman
 */
public class MidiRouting extends javax.swing.JFrame implements ConnectionStatusListener {

    public MidiRouting() {
        initComponents();
        CConnection.GetConnection().addConnectionStatusListener(this);
    }

    JTable table_midi_in_routing;
    JTable table_midi_out_routing;

    void initComponents() {
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
        JScrollPane scrollpane_in = new JScrollPane(table_midi_in_routing);
        JScrollPane scrollpane_out = new JScrollPane(table_midi_out_routing);
        JPanel vPane = new JPanel();
        vPane.setLayout(new BoxLayout(vPane, BoxLayout.PAGE_AXIS));
        vPane.add(new JLabel("work in progress... todo: allow modification, apply changes..."));
        vPane.add(scrollpane_in);
        vPane.add(scrollpane_out);
        JPanel bPane = new JPanel();
        bPane.setLayout(new BoxLayout(bPane, BoxLayout.LINE_AXIS));
        JButton buttonRefresh = new JButton("Refresh");
        buttonRefresh.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        JButton buttonApply = new JButton("Apply");
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

    @Override
    public void ShowConnect() {
    }

    @Override
    public void ShowDisconnect() {
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

    class CInputRoutingData {

        final int destinations_per_port = 4;
        final String portname;
        final int nports;
        final int table_addr;
        final int vports[][];

        CInputRoutingData(IConnection conn, ChunkData cd) {
            cd.data.rewind();
            int name_addr = cd.data.getInt();
            nports = cd.data.getInt();
            table_addr = cd.data.getInt();
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(name_addr, 60));
            conn.WaitReadSync();
            String c = "";
            byte b = conn.getMemReadBuffer().get();
            while (b != 0) {
                c += (char) b;
                b = conn.getMemReadBuffer().get();
            }
            portname = c;
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(table_addr, nports * 4));
            conn.WaitReadSync();
            ByteBuffer routing_table = conn.getMemReadBuffer();
            vports = new int[nports][destinations_per_port];
            for (int i = 0; i < nports; i++) {
                for (int j = 0; j < destinations_per_port; j++) {
                    vports[i][j] = routing_table.get();
                }
            }
            System.out.println("input " + portname + " table addr " + String.format("0x%08X", table_addr));
        }
    }

    private CInputRoutingData[] readInputMapping() {
        IConnection conn = CConnection.GetConnection();
        ChunkData[] chunk_inputs = conn.GetFWChunks().GetAll(FourCCs.FW_MIDI_INPUT_ROUTING);
        CInputRoutingData[] cirs = new CInputRoutingData[chunk_inputs.length];
        for (int i = 0; i < chunk_inputs.length; i++) {
            cirs[i] = new CInputRoutingData(conn, chunk_inputs[i]);
        }
        return cirs;
    }

    private void refreshInputs(CInputRoutingData[] inputroutingdata) {
        DefaultTableModel tm_in = (DefaultTableModel) table_midi_in_routing.getModel();
        tm_in.setRowCount(0);
        tm_in.setColumnIdentifiers(CreateColumnIds());
        table_midi_in_routing.setRowSelectionAllowed(false);
        table_midi_in_routing.setColumnSelectionAllowed(false);
        for (CInputRoutingData ird : inputroutingdata) {
            for (int i = 0; i < ird.nports; i++) {
                Object[] rowdata = new Object[17];
                for (int k = 1; k < 17; k++) {
                    rowdata[k] = false;
                }
                if (ird.nports > 1) {
                    rowdata[0] = ird.portname + " #" + i + " ->";
                } else {
                    rowdata[0] = ird.portname + " ->";
                }
                for (int j = 0; j < 4; j++) {
                    int dest = ird.vports[i][j];
                    if (dest != -1) {
                        rowdata[dest + 1] = true;
                    }
                }
                tm_in.addRow(rowdata);
            }
        }
        table_midi_in_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_in_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_in_routing.doLayout();
    }

    final int ntargets = 4;

    private ByteBuffer readOutputMapping() {
        IConnection conn = CConnection.GetConnection();
        if (conn.isConnected()) {
            ChunkData chunk_output = conn.GetFWChunks().GetOne(FourCCs.FW_MIDI_OUTPUT_ROUTING);
            chunk_output.data.rewind();
            int ntargets = chunk_output.data.getInt();
            int table_addr = chunk_output.data.getInt();
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(table_addr, ntargets * 16 * 2));
            conn.WaitReadSync();
            ByteBuffer routing_table = conn.getMemReadBuffer();
            return routing_table;
        } else {
            return ByteBuffer.wrap(new byte[16 * 4 * 2]);
        }
    }

    private void refreshOutputs(ByteBuffer output_routing_table_data) {
        DefaultTableModel tm_out = (DefaultTableModel) table_midi_out_routing.getModel();
        table_midi_out_routing.setRowSelectionAllowed(false);
        table_midi_out_routing.setColumnSelectionAllowed(false);
        tm_out.setRowCount(0);
        tm_out.setColumnIdentifiers(CreateColumnIds());
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
        for (int vport = 0; vport < 16; vport++) {
            for (int j = 0; j < ntargets; j++) {
                byte target1 = output_routing_table_data.get();
                byte target2 = output_routing_table_data.get();
                if (target1 != 0) {
                    switch (target1) {
                        case 1: // DIN
                            tm_out.setValueAt(true, 0, target1);
                            break;
                        case 2: // USB Device port
                            tm_out.setValueAt(true, 1, target1);
                            break;
                        case 3: // USB Host port
                            tm_out.setValueAt(true, 2 + target2, target1);
                            break;
                    }
                }
            }
        }
        table_midi_out_routing.getColumnModel().getColumn(0).setPreferredWidth(150);
        for (int i = 1; i < 17; i++) {
            table_midi_out_routing.getColumnModel().getColumn(i).setPreferredWidth(25);
        }
        table_midi_out_routing.doLayout();
    }

    void refresh() {
        refreshInputs(readInputMapping());
        refreshOutputs(readOutputMapping());
    }
}
