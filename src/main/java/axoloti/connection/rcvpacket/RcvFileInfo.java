package axoloti.connection.rcvpacket;

import axoloti.target.fs.SDFileInfo;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jtaelman
 */
public class RcvFileInfo implements IRcvPacketConsumer {

    private final CompletableFuture<SDFileInfo> sdFileInfoFuture = new CompletableFuture<>();

    static public SDFileInfo packetToSDFileInfo(ByteBuffer rbuf) {
        int hdr = rbuf.getInt();
        if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_f_info) {
            int err = rbuf.getInt();
            int fsize = rbuf.getInt();
            int timestamp = rbuf.getInt();
            CharBuffer cb = Charset.forName("ISO-8859-1").decode(rbuf);
            String fname = cb.toString();
            // strip trailing null
            if ((fname.length() > 0) && fname.charAt(fname.length() - 1) == (char) 0) {
                fname = fname.substring(0, fname.length() - 1);
            }
            if (err != 0) {
                fname = null;
            }
            return new SDFileInfo(fname, fsize, timestamp);
//            if (log_rx_diagnostics) {
//                diagnostic_println("rx_hdr_fileinfo fn:[" + fname + "], sz:" + fsize);
//            }
        } else {
            return null;
        }
    }

    @Override
    public boolean processPacket(ByteBuffer rbuf) {
        SDFileInfo sdfi = packetToSDFileInfo(rbuf);
        if (sdfi != null) {
            if (sdfi.getFilename() == null) {
                sdFileInfoFuture.complete(null);
            } else {
                sdFileInfoFuture.complete(sdfi);
            }
            return true;
        } else {
            return false;
        }
    }

    public SDFileInfo get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return sdFileInfoFuture.get(timeout, unit);
    }

}
