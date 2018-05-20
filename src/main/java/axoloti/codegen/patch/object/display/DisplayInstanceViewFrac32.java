package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceViewFrac32 extends DisplayInstanceView {

    DisplayInstanceViewFrac32(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public String generateCodeInit(String vprefix) {
        String s = getCName() + " = 0;\n";
        return s;
    }

    @Override
    public String valueName(String vprefix) {
        return "displayVector[" + offset + "]";
    }

    @Override
    public void processByteBuffer(ByteBuffer bb) {
        int i = bb.getInt();
        model.getController().setValue(i * 1.0 / (1 << 21));
    }
}
