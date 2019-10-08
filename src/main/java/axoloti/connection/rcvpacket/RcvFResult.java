package axoloti.connection.rcvpacket;

import axoloti.connection.FileReference;
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
public class RcvFResult implements IRcvPacketConsumer {

    private final CompletableFuture<FResult> fresultFuture = new CompletableFuture<>();

    @Override
    public boolean processPacket(ByteBuffer rbuf) throws IOException {
        int hdr = rbuf.getInt();
        if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_f_result) {
            int fref = rbuf.getInt();
            int err = rbuf.getInt();
            fresultFuture.complete(new FResult(new FileReference(fref), err));
            return true;
        } else {
            return false;
        }
    }

    public FResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return fresultFuture.get(timeout, unit);
    }
}
