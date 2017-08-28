package midirouting;

import axoloti.IConnection;
import axoloti.chunks.ChunkData;
import axoloti.mvc.AbstractModel;
import java.nio.ByteBuffer;
import qcmds.QCmdMemRead;

/**
 *
 * @author jtaelman
 */
public class MidiInputRoutingTable extends AbstractModel {

    final int destinations_per_port = 4;
    String portname;
    int vports[][];

    void retrieve(IConnection conn, int addr) {
        conn.AppendToQueue(new QCmdMemRead(addr, 60, new IConnection.MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem1) {
                int name_addr = mem1.getInt();
                System.out.println(String.format("name_addr = %08X", name_addr));

                conn.AppendToQueue(new QCmdMemRead(name_addr, 60, new IConnection.MemReadHandler() {
                    @Override
                    public void Done(ByteBuffer mem) {
                        String c = "";
                        byte b = mem.get();
                        while (b != 0) {
                            c += (char) b;
                            b = mem.get();
                        }
                        System.out.println(String.format("port_name = %s", c));
                        setPortName(c);

                        int nports = mem1.getInt();
                        System.out.println(String.format("nports = %d", nports));
                        int table_addr = addr + 8;
                        System.out.println(String.format("table_addr = %08X", table_addr));
                        conn.AppendToQueue(new QCmdMemRead(table_addr, nports*4, new IConnection.MemReadHandler() {
                            @Override
                            public void Done(ByteBuffer mem) {
                                int vports1[][] = new int[nports][destinations_per_port];
                                for (int i = 0; i < nports; i++) {
                                    for (int j = 0; j < destinations_per_port; j++) {
                                        vports1[i][j] = mem.get();
                                    }
                                }
                                System.out.println("input " + portname + " table addr " + String.format("0x%08X", table_addr));
                                setMapping(vports1);
                            }
                        }));
                    }
                }));
            }
        }));
    }

    public String getPortName() {
        return portname;
    }

    public void setPortName(String portname) {
        this.portname = portname;
        firePropertyChange(
                MidiInputRoutingTableController.MIRT_PORTNAME,
                null, portname);
    }

    public int[][] getMapping() {
        return vports;
    }

    public void setMapping(int[][] vports) {
        this.vports = vports;
        firePropertyChange(
                MidiInputRoutingTableController.MIRT_MAPPING,
                null, vports);
    }
}
