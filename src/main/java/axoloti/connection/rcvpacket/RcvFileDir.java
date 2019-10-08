package axoloti.connection.rcvpacket;

import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jtaelman
 */
public class RcvFileDir implements IRcvPacketConsumer {

    private final LinkedList<SDFileInfo> fileList = new LinkedList<>();
    private final CompletableFuture<SDCardInfo> fileListFuture = new CompletableFuture<>();
    private int clusters;
    private int clusterSize;
    private int blockSize;

    int state = 0;

    @Override
    public boolean processPacket(ByteBuffer rbuf) throws IOException {
        if (state == 0) {
            int hdr = rbuf.getInt();
            if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_f_dir) {
                clusters = rbuf.getInt();
                clusterSize = rbuf.getInt();
                blockSize = rbuf.getInt();
                state = 1;
                return true;
            } else {
                return false;
            }
        } else if (state == 1) {
            SDFileInfo sdfi = RcvFileInfo.packetToSDFileInfo(rbuf);
            if (sdfi != null) {
                fileList.add(sdfi);
                return true;
            } else {
                rbuf.rewind();
                int hdr = rbuf.getInt();
                if (hdr == axoloti.connection.USBBulkConnection_v2.rx_hdr_f_dir_end) {
                    SDCardInfo sdci = new SDCardInfo(clusters, clusterSize, blockSize, fileList);
                    fileListFuture.complete(sdci);
                    state = 2;
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public SDCardInfo get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return fileListFuture.get(timeout, unit);
    }

}
