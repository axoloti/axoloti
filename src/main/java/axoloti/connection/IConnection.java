package axoloti.connection;

import axoloti.chunks.ChunkParser;
import axoloti.job.IJobContext;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileInfo;
import axoloti.targetprofile.axoloti_core;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 *
 * @author jtaelman
 */
public interface IConnection {

    boolean isConnected();

    void disconnect();

    boolean connect(IDevice connectable);

    /* patch start/stop */
    void transmitStop() throws IOException;

    ILivePatch transmitStartLive(byte[] elf, String patchName, IPatchCB patchCB, IJobContext ctx) throws IOException, PatchLoadFailedException;

    ILivePatch transmitStart(String patchName, IPatchCB patchCB) throws IOException, PatchLoadFailedException;

    ILivePatch transmitStart(int patchIndex) throws IOException, PatchLoadFailedException;

    void uploadPatchToFlash(byte[] elf, String patchName) throws IOException;

    /* memory ops */
    ByteBuffer read(int addr, int length) throws IOException;

    void write(int addr, byte[] data) throws IOException;

    void uploadFirmware(byte[] data) throws IOException;

    /* File ops */
    SDCardInfo getFileList(String path) throws IOException;

    SDFileInfo getFileInfo(String filename) throws IOException;

    void upload(String filename, InputStream inputStream, Calendar cal, int size, IJobContext ctx) throws IOException;

    ByteBuffer download(String filename, IJobContext ctx) throws IOException;

    void createDirectory(String filename, Calendar date) throws IOException;

    void deleteFile(String filename) throws IOException;

    /* inject events */
    void transmitVirtualInputEvent(byte b0, byte b1, byte b2, byte b3) throws IOException;

    void sendMidi(int cable, byte m0, byte m1, byte m2) throws IOException;

    /* other */
    void transmitPing() throws IOException;

    void transmitGetFWVersion() throws IOException;

    void bringToDFU() throws IOException;

    axoloti_core getTargetProfile();

    boolean getSDCardPresent();

    ChunkParser getFWChunks();

    String getFWID();

    void transmitExtraCommand(int arg) throws IOException;

}
