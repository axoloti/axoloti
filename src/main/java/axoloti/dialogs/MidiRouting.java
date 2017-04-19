package axoloti.dialogs;

import axoloti.CConnection;
import axoloti.ConnectionStatusListener;
import axoloti.IConnection;
import axoloti.chunks.ChunkData;
import axoloti.chunks.FourCCs;
import java.awt.event.ActionEvent;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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

    JTable table_inputs;
    JTable table_outputs;
//    DefaultMutableTreeNode top;

    void initComponents() {
        setMinimumSize(new java.awt.Dimension(200, 160));
//        top = new DefaultMutableTreeNode("midi routing");
        table_inputs = new JTable();
        table_outputs = new JTable();
        table_inputs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_outputs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_inputs.setShowGrid(true);
        table_outputs.setShowGrid(true);
        JPanel vPane1 = new JPanel();
        vPane1.setLayout(new BoxLayout(vPane1, BoxLayout.PAGE_AXIS));
        vPane1.add(new JLabel("MIDI input routing"));
        vPane1.add(table_inputs);
        vPane1.add(new JLabel("MIDI output routing"));
        vPane1.add(table_outputs);
        vPane1.setAlignmentX(0);
        vPane1.setAlignmentY(0);
        JScrollPane scrollpane = new JScrollPane(vPane1);
        JPanel vPane = new JPanel();
        vPane.setLayout(new BoxLayout(vPane, BoxLayout.PAGE_AXIS));
        vPane.add(new JLabel("work in progress... todo: allow modification, apply changes..."));
        vPane.add(scrollpane);
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

    void refresh() {
        DefaultTableModel tm_in = (DefaultTableModel) table_inputs.getModel();
        tm_in.setRowCount(0);
        tm_in.setColumnIdentifiers(new Object[]{"input port",
            "virtual in1",
            "v 2",
            "v 3",
            "v 4",
            "v 5",
            "v 6",
            "v 7",
            "v 8",
            "v 9",
            "v 10",
            "v 11",
            "v 12",
            "v 13",
            "v 14",
            "v 15",
            "v 16"
        });
        DefaultTableModel tm_out = (DefaultTableModel) table_outputs.getModel();
        tm_out.setRowCount(0);
        tm_out.setColumnIdentifiers(new Object[]{"output port",
            "virtual out #1",
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
        });
        IConnection conn = CConnection.GetConnection();
        ChunkData[] chunk_inputs = conn.GetFWChunks().GetAll(FourCCs.FW_MIDI_INPUT_ROUTING);
        ChunkData[] chunk_outputs = conn.GetFWChunks().GetAll(FourCCs.FW_MIDI_OUTPUT_ROUTING);
//        DefaultMutableTreeNode inputs = new DefaultMutableTreeNode("inputs");
        for (ChunkData cd : chunk_inputs) {
            cd.data.rewind();
            int name_addr = cd.data.getInt();
            int nports = cd.data.getInt();
            int table_addr = cd.data.getInt();
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(name_addr, 60));
            conn.WaitReadSync();
            String c = "";
            byte b = conn.getMemReadBuffer().get();
            while (b != 0) {
                c += (char) b;
                b = conn.getMemReadBuffer().get();
            }
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(table_addr, nports * 4));
            conn.WaitReadSync();
            ByteBuffer routing_table = conn.getMemReadBuffer();
            System.out.println("input " + c + " table addr " + String.format("0x%08X", table_addr));
            for (int i = 0; i < nports; i++) {
                Object[] rowdata = new Object[17];
                for (int k = 0; k < 17; k++) {
                    rowdata[k] = ".";
                }
                if (nports > 1) {
                    rowdata[0] = c + " #" + i;
                } else {
                    rowdata[0] = c;
                }
                for (int j = 0; j < 4; j++) {
                    byte dest = routing_table.get();
                    if (dest != -1) {
                        rowdata[dest + 1] = "x";
                    }
                }
                tm_in.addRow(rowdata);
            }
        }

        String[] rowdata = new String[17];
        rowdata[0] = "DIN";
        tm_out.addRow(rowdata);
        rowdata = new String[17];
        rowdata[0] = "USB Device";
        tm_out.addRow(rowdata);
        for(int i=0;i<16;i++){
            rowdata = new String[17];
            rowdata[0] = "USB Host #" + i;
            tm_out.addRow(rowdata);            
        }
        
        for (ChunkData cd : chunk_outputs) {
            cd.data.rewind();
            int ntargets = cd.data.getInt();
            int table_addr = cd.data.getInt();
            conn.ClearReadSync();
            conn.AppendToQueue(new QCmdMemRead(table_addr, ntargets * 16 * 2));
            conn.WaitReadSync();
            ByteBuffer routing_table = conn.getMemReadBuffer();
            
            for (int vport = 0; vport < 16; vport++) {                
                for (int j = 0; j < ntargets; j++) {
                    byte target1 = routing_table.get();
                    byte target2 = routing_table.get();
                    if (target1 != 0) {
                        switch (target1) {
                            case 1: // DIN
                                tm_out.setValueAt("x", 0, target1);
                                break;
                            case 2: // USB Device port
                                tm_out.setValueAt("x", 1, target1);
                                break;
                            case 3: // USB Host port
                                tm_out.setValueAt("x", 2+target2, target1);
                                break;
                        }
                    }
                }
            }
        }
    }
}
