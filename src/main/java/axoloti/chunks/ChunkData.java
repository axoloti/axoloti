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

    public void dumpHead() {
        System.out.println("chunk head " + FourCC.Format(fourcc.getInt()));
        System.out.print("chunk data: ");
        ByteBuffer d = data.duplicate();
        d.rewind();
        d.order(ByteOrder.LITTLE_ENDIAN);
        for (int j = 0; j < 4; j++) {
            if (d.remaining() < 4) {
                break;
            }
            System.out.print(String.format("%08X ", d.getInt()));
        }
        if (d.remaining() < 4) {
            System.out.println();
        } else {
            System.out.println("...");
        }
    }
}
