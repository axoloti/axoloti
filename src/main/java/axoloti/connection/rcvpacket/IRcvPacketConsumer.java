package axoloti.connection.rcvpacket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public interface IRcvPacketConsumer {

    boolean processPacket(ByteBuffer rbuf) throws IOException;
}
