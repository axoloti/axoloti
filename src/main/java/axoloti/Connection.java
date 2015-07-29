package axoloti;

import axoloti.targetprofile.axoloti_core;
import java.nio.ByteBuffer;
import qcmds.QCmdSerialTask;

/**
 *
 * @author jtaelman
 */
public abstract class Connection {
    abstract public boolean isConnected();
    abstract public void disconnect();
    abstract public boolean connect();
    abstract public void SelectPort();
    abstract public void TransmitStop();
    abstract public void TransmitStart();
    abstract public void TransmitPing();
    abstract public void TransmitRecallPreset(int presetNo);
    abstract public void UploadFragment(byte[] buffer, int offset);
    abstract public void TransmitGetFileList();
    abstract public void TransmitVirtualButton(int b_or, int b_and, int enc1, int enc2, int enc3, int enc4);
    abstract public void TransmitCreateFile(String filename, int size);
    abstract public void TransmitAppendFile(byte[] buffer);
    abstract public void TransmitCloseFile();
    abstract public void TransmitMemoryRead(int addr, int length);
    abstract public void TransmitMemoryRead1Word(int addr);    
    abstract public void SendUpdatedPreset(byte[] b);
    abstract public void SendMidi(int m0, int m1, int m2);
    abstract public boolean AppendToQueue(QCmdSerialTask cmd);
    abstract public void TransmitGetFWVersion();
    abstract public void TransmitCopyToFlash();
    abstract public void BringToDFU();
    abstract public void ClearSync();
    abstract public boolean WaitSync();
    abstract public void ClearReadSync();
    abstract public boolean WaitReadSync();
    abstract public void setPatch(Patch patch);
    abstract public axoloti_core getTargetProfile();
    abstract public ByteBuffer getMemReadBuffer();
    abstract public int getMemRead1Word();
    
    @Deprecated
    abstract public void writeBytes(byte[] data);

}
