package axoloti.connection.rcvpacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jtaelman
 */
public class RcvPatchDisp implements IRcvPacketConsumer {

    private int memReadLength;
    private final CompletableFuture<ByteBuffer> futureByteBuffer = new CompletableFuture<>();

    private int state = 0;

    @Override
    public boolean processPacket(ByteBuffer rbuf) throws IOException {
        if (state == 0) {
            int hdr = rbuf.getInt();
            if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_patch_disp) {
                memReadLength = rbuf.getInt();
                int patch_ref = rbuf.getInt();
                if (memReadLength == 0) {
                    futureByteBuffer.complete(null);
                    state = 2;
                } else {
                    state = 1;
                }
//                if (log_rx_diagnostics) {
//                    System.out.print("rx memrd addr=" + String.format("0x%08X", memReadAddr) + " le=" + memReadLength + " [");
//                    for (int i = 12; i < size; i++) {
//                        // this would be unexpected extra data...
//                        System.out.print("|" + (char) rbuf.get(i));
//                        if (i > 100) {
//                            System.out.println("|...truncated");
//                            break;
//                        }
//                    }
//                    System.out.println("]");
//                }
                return true;
            } else {
                return false;
            }
        } else if (state == 1) {
            int size = rbuf.remaining();
            if (memReadLength != size) {
                System.out.println("memread barf:" + memReadLength + ":" + size + "<");
                rbuf.rewind();
                int i = 0;
                while (rbuf.hasRemaining()) {
                    System.out.print("|" + (char) rbuf.get());
                    i++;
                    if (i > 100) {
                        System.out.println("|...truncated");
                        break;
                    }
                }
                System.out.println(">");
                throw new IOException("unexpected packet size");
            }
            byte memr[] = new byte[memReadLength];
            rbuf.get(memr, 0, memReadLength);
            ByteBuffer mrb = ByteBuffer.wrap(memr);
            mrb.order(ByteOrder.LITTLE_ENDIAN);
            mrb.rewind();
            futureByteBuffer.complete(mrb);
            state = 2;
            return true;
        } else {
            return false;
        }
    }

    public ByteBuffer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return futureByteBuffer.get(timeout, unit);
    }
}
