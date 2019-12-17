package axoloti.connection.rcvpacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jtaelman
 */
public class RcvPtr implements IRcvPacketConsumer {

    private final CompletableFuture<Integer> futurePtr = new CompletableFuture<>();

    @Override
    public boolean processPacket(ByteBuffer rbuf) throws IOException {
        int hdr = rbuf.getInt();
        if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_result_ptr) {
            int result_ptr = rbuf.getInt();
            futurePtr.complete(result_ptr);
            return true;
        } else {
            return false;
        }
    }

    public int get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return futurePtr.get(timeout, unit);
    }
}
