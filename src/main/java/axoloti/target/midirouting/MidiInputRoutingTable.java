package axoloti.target.midirouting;

import axoloti.connection.CConnection;
import axoloti.connection.CompletionHandler;
import axoloti.connection.IConnection;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import qcmds.QCmdMemRead;
import qcmds.QCmdProcessor;
import qcmds.QCmdUploadFile;
import qcmds.QCmdWriteMem;

/**
 *
 * @author jtaelman
 */
public class MidiInputRoutingTable extends AbstractModel {

    String portname;
    int vports[];
    int addr;

    private int getTableAddr() {
        return addr + 8;
    }

    public void retrieve(IConnection conn, int addr, CompletionHandler completionHandler) {
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
                        System.out.println("portname1:" + c + ":" + nports);
                        if (nports == 0) {
                            completionHandler.done();
                        } else {
                            conn.AppendToQueue(new QCmdMemRead(getTableAddr(), nports * 4, new IConnection.MemReadHandler() {
                                @Override
                                public void Done(ByteBuffer mem) {
                                    int vports1[] = new int[nports];
                                    for (int i = 0; i < nports; i++) {
                                        vports1[i] = mem.getInt();
                                        System.out.println(String.format("MidiInputRouting %s:%d map %08X ", getPortName(), i, vports1[i]));
                                    }
                                    setMapping(vports1);
                                    completionHandler.done();
                                }
                            }));
                        }
                    }
                }));
            }
        }));
    }

    public void upload() {
        if (vports == null) {
            return;
        }
        String fn = "/settings/midi-in/" + getPortName() + ".axr";
        ByteBuffer bb = ByteBuffer.allocateDirect(vports.length * 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asIntBuffer().put(vports);
        byte b[] = new byte[vports.length * 4];
        bb.rewind();
        bb.get(b);
        InputStream is = new ByteArrayInputStream(b);
        QCmdProcessor p = QCmdProcessor.getQCmdProcessor();
        p.AppendToQueue(new QCmdUploadFile(is, fn));
        try {
            p.WaitQueueFinished();
        } catch (Exception ex) {
            Logger.getLogger(MidiInputRoutingTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void apply(IConnection conn) {
        if ((vports != null) && (vports.length != 0)) {
            byte[] b = new byte[vports.length * 4];
            ByteBuffer bb = ByteBuffer.wrap(b);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int i = 0;
            for (int v : vports) {
                bb.putInt(v);
                System.out.println(String.format("set inputMap %s %d %08X", portname, i++, v));
            }
            conn.AppendToQueue(new QCmdWriteMem(getTableAddr(), b));
        }
    }

    public final static Property MIRT_PORTNAME = new StringProperty("PortName", MidiInputRoutingTable.class);
    public final static Property MIRT_MAPPING = new ObjectProperty("Mapping", int[].class,  MidiInputRoutingTable.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(MIRT_PORTNAME);
        l.add(MIRT_MAPPING);
        return l;
    }

    public String getPortName() {
        return portname;
    }

    public void setPortName(String portname) {
        this.portname = portname;
        firePropertyChange(
                MIRT_PORTNAME,
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
                MIRT_MAPPING,
                null, vports);
    }

    @Override
    protected AbstractController createController() {
        return new AbstractController(this) {
        };
    }

    @Override
    public IModel getParent() {
        return null;
    }
}
