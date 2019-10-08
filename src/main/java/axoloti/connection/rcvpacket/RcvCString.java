package axoloti.connection.rcvpacket;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jtaelman
 */
public class RcvCString implements IRcvPacketConsumer {

    private final CompletableFuture<String> futureString = new CompletableFuture<>();

    @Override
    public boolean processPacket(ByteBuffer rbuf) {
        int hdr = rbuf.getInt();
        if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_cstring) {
            int rem = rbuf.remaining();
            char str[] = new char[rem];
            int i = 0;
            while (rbuf.remaining() > 0) {
                char c = (char) rbuf.get();
                if (c == 0) {
                    break;
                }
                str[i++] = c;
            }
            String cstr = new String(str, 0, i);
            futureString.complete(cstr);
            return true;
        } else {
            return false;
        }
    };

    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return futureString.get(timeout, unit);
    }
}
