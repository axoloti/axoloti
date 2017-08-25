package axoloti.chunks;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class ChunkParser {

    // this parser assumes max one chunk of a certain FourCC
//    private Map<FourCC, ChunkData> map = new HashMap<>();
    private ArrayList<ChunkData> list = new ArrayList<>();

    public ChunkParser(ByteBuffer bb) {
        FourCC hfourcc = new FourCC(bb.getInt());
        System.out.println("parser1 " + FourCC.Format(hfourcc.getInt()));
        int hlength = bb.getInt();
//        int remaininglength = hlength;
        while (bb.remaining() > 0) {
            ChunkData c = new ChunkData(bb);
            list.add(c);
            c.dumpHead();
        }
    }

    public ChunkData GetOne(FourCC fourcc) {
        for (ChunkData d : list) {
            if (d.fourcc.getInt() == fourcc.getInt()) {
                return d;
            }
        }
        return null;
    }

    public ChunkData[] GetAll(FourCC fourcc) {
        int n = 0;
        for (ChunkData d : list) {
            if (d.fourcc.getInt() == fourcc.getInt()) {
                n++;
            }
        }
        ChunkData[] r = new ChunkData[n];
        n = 0;
        for (ChunkData d : list) {
            if (d.fourcc.getInt() == fourcc.getInt()) {
                r[n] = d;
                n++;
            }
        }
        return r;
    }
}
