package axoloti.target.midirouting;

import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.job.GlobalJobProcessor;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.target.TargetModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class MidiOutputRoutingTable extends AbstractModel {

    private String portname;
    private int vports[]; // bitfield per port
    private final int addr;

    public MidiOutputRoutingTable(int address) {
        addr = address;
    }

    private int getTableAddr() {
        return addr + 8;
    }

    public void retrieve(IConnection conn) throws IOException {
        ByteBuffer mem1 = conn.read(addr, 60);
        int name_addr = mem1.getInt();

        ByteBuffer mem = conn.read(name_addr, 60);
        String c = "";
        byte b = mem.get();
        while (b != 0) {
//                            System.out.println(String.format("%02X %c",(int)b, (char)b));
            c += (char) b;
            b = mem.get();
        }
        setPortName(c);
        int nports = mem1.getInt();
        if (nports != 0) {
            ByteBuffer mem3 = conn.read(getTableAddr(), nports * 4);
            int vports1[] = new int[nports];
            for (int i = 0; i < nports; i++) {
                vports1[i] = mem3.getInt();
                System.out.println(String.format("MidiOutputRouting %s:%d map %08X ", getPortName(), i, vports1[i]));
            }
            setMapping(vports1);
        }
    }

    public void apply(IConnection conn) {
        try {
            if ((vports != null) && (vports.length != 0)) {
                byte[] b = new byte[vports.length * 4];
                ByteBuffer bb = ByteBuffer.wrap(b);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int i = 0;
                for (int v : vports) {
                    bb.putInt(v);
                    System.out.println(String.format("set outputMap %s %d %08X", portname, i++, v));
                }
                conn.write(getTableAddr(), b);
            }
        } catch (IOException ex) {
            Logger.getLogger(MidiOutputRoutingTable.class.getName()).log(Level.SEVERE, null, ex);
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
            IConnection conn = CConnection.getConnection();
            apply(conn);
        }
        firePropertyChange(
                MORT_MAPPING,
                null, vports);
    }

    public void upload() {
        if (vports == null) {
            return;
        }
        String fn = "/settings/midi-out/" + getPortName() + ".axr";
        ByteBuffer bb = ByteBuffer.allocateDirect(vports.length * 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.asIntBuffer().put(vports);
        byte b[] = new byte[vports.length * 4];
        bb.rewind();
        bb.get(b);
        InputStream is = new ByteArrayInputStream(b);
        GlobalJobProcessor.getJobProcessor().exec((ctx) -> {
            try {
                TargetModel.getTargetModel().upload(fn, is, Calendar.getInstance(), b.length, ctx);
            } catch (IOException ex) {
                ctx.reportException(ex);
            }
        });
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
