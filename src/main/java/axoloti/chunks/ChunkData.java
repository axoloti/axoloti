package axoloti.chunks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author jtaelman
 */
public class ChunkData {

    public FourCC fourcc;
    public int length;
    public ByteBuffer data;

    public ChunkData(ByteBuffer bb) {
        fourcc = new FourCC(bb.getInt());
        length = bb.getInt();
        byte[] b = new byte[length];
        bb.get(b, 0, length);
        data = ByteBuffer.wrap(b);
        data.order(ByteOrder.LITTLE_ENDIAN);
    }
}
