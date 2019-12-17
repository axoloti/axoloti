package axoloti.connection;

import java.nio.ByteBuffer;

/**
 *
 * @author jtaelman
 */
public interface IPatchCB {

    void openEditor();

    void patchStopped();

    void setDspLoad(int dspLoad);

    void paramChange(int index, int value);

    void distributeDataToDisplays(ByteBuffer dispData);
}
