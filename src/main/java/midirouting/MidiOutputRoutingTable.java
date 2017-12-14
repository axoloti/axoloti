package midirouting;

import axoloti.CConnection;
import axoloti.IConnection;
import axoloti.mvc.AbstractModel;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import qcmds.QCmdMemRead;
import qcmds.QCmdWriteMem;

/**
 *
 * @author jtaelman
 */
public class MidiOutputRoutingTable extends AbstractModel {

    String portname;
    int vports[]; // bitfield per port
    int addr;

    private int getTableAddr() {
        return addr + 8;
    }

    public void retrieve(IConnection conn, int addr) {
        this.addr = addr;
        conn.AppendToQueue(new QCmdMemRead(addr, 60, new IConnection.MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem1) {
                int name_addr = mem1.getInt();

                conn.AppendToQueue(new QCmdMemRead(name_addr, 60, new IConnection.MemReadHandler() {
                    @Override
                    public void Done(ByteBuffer mem) {
                        String c = "";
                        byte b = mem.get();
                        while (b != 0) {
//                            System.out.println(String.format("%02X %c",(int)b, (char)b));
                            c += (char) b;
                            b = mem.get();
                        }
                        setPortName(c);
                        int nports = mem1.getInt();
                        conn.AppendToQueue(new QCmdMemRead(getTableAddr(), nports * 4, new IConnection.MemReadHandler() {
                            @Override
                            public void Done(ByteBuffer mem) {
                                int vports1[] = new int[nports];
                                for (int i = 0; i < nports; i++) {
                                    vports1[i] = mem.getInt();
                                    System.out.println(String.format("MidiOutputRouting %s:%d map %08X ", getPortName(), i, vports1[i]));
                                }
                                setMapping(vports1);
                            }
                        }));
                    }
                }));
            }
        }));
    }

    public void apply(IConnection conn) {
        if ((vports != null) && (vports.length != 0)) {
            byte[] b = new byte[vports.length * 4];
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int i = 0;
            for (int v : vports) {
                bb.putInt(v);
                System.out.println(String.format("set outputMap %s %d %08X", portname, i++, v));
            }
            conn.AppendToQueue(new QCmdWriteMem(getTableAddr(), b));
        }
    }

    public final static Property MORT_PORTNAME = new StringProperty("PortName", MidiOutputRoutingTable.class);
    public final static Property MORT_MAPPING = new ObjectProperty("Mapping", int[].class, MidiOutputRoutingTable.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(MORT_PORTNAME);
        l.add(MORT_MAPPING);
        return l;
    }

    public String getPortName() {
        return portname;
    }

    public void setPortName(String portname) {
        this.portname = portname;
        firePropertyChange(
                MORT_PORTNAME,
                null, portname);
    }

    public int[] getMapping() {
        return vports;
    }

    public void setMapping(int[] vports) {
        if (this.vports != vports) {
            this.vports = vports;
            IConnection conn = CConnection.GetConnection();
            apply(conn);
        }
        firePropertyChange(
                MORT_MAPPING,
                null, vports);
    }
}
