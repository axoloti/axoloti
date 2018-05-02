package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstanceController;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceViewInt32 extends DisplayInstanceView {

    DisplayInstanceViewInt32(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    public String valueName(String vprefix) {
        return "displayVector[" + offset + "]";
    }

    @Override
    public String GenerateCodeInit(String vprefix) {
        String s = GetCName() + " = 0;\n";
        return s;
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        int i = bb.getInt();
        getController().setValue(i);
    }
}
