package axoloti.codegen.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
class DisplayInstanceView128B extends DisplayInstanceView {

    DisplayInstanceView128B(DisplayInstance displayInstance) {
        super(displayInstance);
    }

    private final static int N = 128;

    private final byte dst[] = new byte[N];
    private final int idst[] = new int[N];

    @Override
    public String generateCodeInit(String vprefix) {
        String s = "{\n"
                + "   int _i;\n"
                + "   for(_i=0;_i<" + N + ";_i++)\n"
                + "   " + getCName() + "[_i] = 0;\n"
                + "}\n";
        return s;
    }

    @Override
    public String valueName(String vprefix) {
        return "(int8_t *)(&displayVector[" + offset + "])";
    }

    @Override
    public void processByteBuffer(ByteBuffer bb) {
        bb.get(dst);
        for (int i = 0; i < N; i++) {
            idst[i] = dst[i];
        }
        model.getController().setValue(idst);
    }

}
