package axoloti.connection.rcvpacket;

import axoloti.connection.FileReference;
import java.io.IOException;

/**
 *
 * @author jtaelman
 */
public class FResult {

    public final FileReference fref;
    public final int err; // fatfs error code

    public static final int FR_DISK_ERR = 8;

    public String getErrString() {
        switch (err) {
            case 0:
                return "";
            case 1:
                return "FR_DISK_ERR";
            case 2:
                return "FR_INT_ERR";
            case 3:
                return "FR_NOT_READY";
            case 4:
                return "FR_NO_FILE";
            case 5:
                return "FR_NO_PATH";
            case 6:
                return "FR_INVALID_NAME";
            case 7:
                return "FR_DENIED";
            case 8:
                return "FR_EXIST";
            case 9:
                return "FR_INVALID_OBJECT";
            case 10:
                return "FR_WRITE_PROTECTED";
            case 11:
                return "FR_INVALID_DRIVE";
            case 12:
                return "FR_NOT_ENABLED";
            case 13:
                return "FR_NO_FILESYSTEM";
            case 14:
                return "FR_MKFS_ABORTED";
            case 15:
                return "FR_TIMEOUT";
            case 16:
                return "FR_LOCKED";
            case 17:
                return "FR_NOT_ENOUGH_CORE";
            case 18:
                return "FR_TOO_MANY_OPEN_FILES";
            case 19:
                return "FR_INVALID_PARAMETER";

            default:
                return "";
        }
    }

    public void throwErr() throws IOException {
        if (err != 0) {
            throw new IOException(getErrString());
        }
    }

    public FResult(FileReference fref, int err) {
        this.fref = fref;
        this.err = err;
    }

    public FileReference getFileRef() {
        return fref;
    }
}
