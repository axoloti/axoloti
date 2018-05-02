package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstanceController;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceView128B extends DisplayInstanceView {

    DisplayInstanceView128B(DisplayInstanceController controller) {
        super(controller);
    }

    final int n = 128;

    byte dst[] = new byte[n];
    int idst[] = new int[n];
    int[] value;

    @Override
    public String GenerateCodeInit(String vprefix) {
        String s = "{\n"
                + "   int _i;\n"
                + "   for(_i=0;_i<" + n + ";_i++)\n"
                + "   " + GetCName() + "[_i] = 0;\n"
                + "}\n";
        return s;
    }

    @Override
    public String valueName(String vprefix) {
        return "(int8_t *)(&displayVector[" + offset + "])";
    }

    @Override
    public void ProcessByteBuffer(ByteBuffer bb) {
        bb.get(dst);
        for (int i = 0; i < n; i++) {
            idst[i] = dst[i];
        }
        getController().setValue(idst);
    }

}
