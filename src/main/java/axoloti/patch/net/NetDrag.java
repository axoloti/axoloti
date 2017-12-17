package axoloti.patch.net;

/**
 *
 * @author jtaelman
 */
public class NetDrag extends Net {

    @Override
    public void validate() {
        if (getSources() == null) {
            throw new Error("source is null, empty array required");
        }
        if (getDestinations() == null) {
            throw new Error("dest is null, empty array required");
        }
    }

}
