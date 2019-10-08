package axoloti.connection;

import java.io.IOException;

/**
 *
 * @author jtaelman
 */
public interface ILivePatch {

    void sendUpdatedPreset(byte[] b) throws IOException;

    void transmitRecallPreset(int presetNo) throws IOException;

    void transmitStop() throws IOException;

    void transmitParameterChange(byte[] b) throws IOException;

    void pollDisplays();

    IPatchCB getCallbacks();

    String getName();
}
