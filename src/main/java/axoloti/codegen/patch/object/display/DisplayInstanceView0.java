package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import java.nio.ByteBuffer;

/**
 * Dummy display, no data, only shows in GUI
 *
 * @author jtaelman
 */
class DisplayInstanceView0 extends DisplayInstanceView {

    DisplayInstanceView0(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public String valueName(String vprefix) {
        return "#error do not assign value to a display of this type";
    }

    @Override
    public String generateCodeInit(String vprefix) {
        return "";
    }

    @Override
    public void processByteBuffer(ByteBuffer bb) {
    }
}
