/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package qcmds;

import axoloti.SerialConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import jssc.SerialPortException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdUploadPatch implements QCmdSerialTask {

    File f;

    public QCmdUploadPatch() {
        f = null;
    }

    public QCmdUploadPatch(File f) {
        this.f = f;
    }

    @Override
    public String GetStartMessage() {
        return "Start uploading patch";
    }

    @Override
    public String GetDoneMessage() {
        return "Done uploading patch";
    }

    @Override
    public QCmd Do(SerialConnection serialConnection) {
        serialConnection.ClearSync();
        try {
            if (f == null) {
                f = new File("patch/xpatch.bin");
            }
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.INFO, "bin path: " + f.getAbsolutePath());
            int tlength = (int) f.length();
            FileInputStream inputStream = new FileInputStream(f);
            int offset = 0;
            int MaxBlockSize = 32768;
            do {
                int l;
                if (tlength > MaxBlockSize) {
                    l = MaxBlockSize;
                    tlength -= MaxBlockSize;
                } else {
                    l = tlength;
                    tlength = 0;
                }
                byte[] buffer = new byte[l];
                int nRead = inputStream.read(buffer, 0, l);
                if (nRead != l) {
                    Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "file size wrong?" + nRead);
                }
                serialConnection.UploadFragment(buffer, serialConnection.getTargetProfile().getPatchAddr() + offset);
                offset += nRead;
            } while (tlength > 0);
            inputStream.close();
            if (serialConnection.WaitSync()) {
                return this;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "IOException", ex);
        } catch (SerialPortException ex) {
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "SerialPortException", ex);
        }
        return new QCmdDisconnect();
    }

}
