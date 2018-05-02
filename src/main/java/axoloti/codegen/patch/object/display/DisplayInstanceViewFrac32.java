package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstanceController;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceViewFrac32 extends DisplayInstanceView {

    DisplayInstanceViewFrac32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public String GenerateCodeInit(String vprefix) {
        String s = GetCName() + " = 0;\n";
        return s;
    }

    @Override
    public String valueName(String vprefix) {
        return "displayVector[" + offset + "]";
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        int i = bb.getInt();
        getController().setValue(i * 1.0 / (1 << 21));
    }
}
