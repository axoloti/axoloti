package axoloti.chunks;

import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public class Cpatch_display {

    public final int nDisplayVector;
    public final int pDisplayVector;

    public Cpatch_display(ChunkData d) {
        ByteBuffer data = d.getData();
        nDisplayVector = data.getInt();
        pDisplayVector = data.getInt();
    }
}
