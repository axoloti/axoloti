package axoloti.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class LivePatch implements ILivePatch {
// TODO: class not public

    private final USBBulkConnection_v2 connection;
    private final IPatchCB callbacks;
    private final int patchRef;
    private final String patchName;

    protected LivePatch(int patchRef, IPatchCB callbacks, String patchName, USBBulkConnection_v2 connection) throws IOException {
        this.patchRef = patchRef;
        this.callbacks = callbacks;
        this.connection = connection;
        this.patchName = patchName;
    }

    @Override
    public void transmitRecallPreset(int presetNo) throws IOException {
        connection.applyPreset(patchRef, presetNo);
    }

    @Override
    public void transmitStop() throws IOException {
        connection.transmitStop(patchRef);
    }

    @Override
    public void sendUpdatedPreset(byte[] b) throws IOException {
        connection.sendUpdatedPreset(patchRef, b);
    }

    @Override
    public void transmitParameterChange(byte[] data) throws IOException {
        connection.transmitParameterChange(patchRef, data);
    }

    @Override
    public void pollDisplays() {
        ByteBuffer mem;
        try {
            mem = connection.patchGetDisp(patchRef);
            if (mem != null) {
                callbacks.distributeDataToDisplays(mem);
            }
        } catch (IOException ex) {
            Logger.getLogger(LivePatch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getPatchRef() {
        return patchRef;
    }

    @Override
    public String getName() {
        return patchName;
    }

    @Override
    public IPatchCB getCallbacks() {
        return callbacks;
    }

}
