package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceViewInt32 extends DisplayInstanceView {

    DisplayInstanceViewInt32(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    @Override
    public String valueName(String vprefix) {
        return "displayVector[" + offset + "]";
    }

    @Override
    public String generateCodeInit(String vprefix) {
        String s = getCName() + " = 0;\n";
        return s;
    }

    @Override
    public void processByteBuffer(ByteBuffer bb) {
        int i = bb.getInt();
        model.getController().setValue(i);
    }
}
