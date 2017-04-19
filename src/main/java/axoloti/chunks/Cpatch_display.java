package axoloti.chunks;

/**
 *
 * @author jtaelman
 */
public class Cpatch_display {

    public final int nDisplayVector;
    public final int pDisplayVector;

    public Cpatch_display(ChunkData d) {
        nDisplayVector = d.data.getInt();
        pDisplayVector = d.data.getInt();
    }
}
