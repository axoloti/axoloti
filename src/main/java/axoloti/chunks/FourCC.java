package axoloti.chunks;

/**
 *
 * @author jtaelman
 */
public class FourCC implements Comparable<FourCC> {

    private final int x;

    public static String format(int v) {
        return String.format("fourcc [%c%c%c%c](0x%08x)",
                (char) (v & 0xFF),
                (char) ((v >> 8) & 0xff),
                (char) ((v >> 16) & 0xFF),
                (char) ((v >> 24) & 0xFF),
                v);
    }

    public FourCC(int x) {
        this.x = x;
    }

    public FourCC(char a, char b, char c, char d) {
        x = (a & 0xFF) | ((b & 0xFF) << 8) | ((c & 0xFF) << 16) | ((d & 0xFF) << 24);
    }

    public int getInt() {
        return x;
    }

    @Override
    public int compareTo(FourCC o) {
        return Integer.compare(o.getInt(), x);
    }

}
