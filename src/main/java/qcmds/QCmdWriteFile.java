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

import axoloti.Axoloti;
import axoloti.Connection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
@Deprecated // replace with QCMDUploadFile
public class QCmdWriteFile implements QCmdSerialTask {

    final String Filename;

    public QCmdWriteFile(String Filename) {
        this.Filename = Filename;
    }

    public String GetFilename() {
        return Filename;
    }

    @Override
    public String GetStartMessage() {
        return "Start writing file to SDCard";
    }

    @Override
    public String GetDoneMessage() {
        return "Done writing file to SDCard";
    }

    @Override
    public QCmd Do(Connection Connection) {
        Connection.ClearSync();
        try {
            Thread.sleep(100);
            String buildDir=System.getProperty(Axoloti.HOME_DIR)+"/build";;
            File f = new File(buildDir + "/xpatch.bin");
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.INFO, "bin path: {0}", f.getAbsolutePath());
            byte[] buffer = new byte[(int) f.length()];
            FileInputStream inputStream = new FileInputStream(f);
            int nRead = inputStream.read(buffer);
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.INFO, "file size: {0}", nRead);
            inputStream.close();
            byte[] data = new byte[12];
            data[0] = 'A';
            data[1] = 'x';
            data[2] = 'o';
            data[3] = 'w';
            int tvalue = Connection.getTargetProfile().getPatchAddr();
            data[4] = (byte) tvalue;
            data[5] = (byte) (tvalue >> 8);
            data[6] = (byte) (tvalue >> 16);
            data[7] = (byte) (tvalue >> 24);
            data[8] = (byte) (nRead);
            data[9] = (byte) (nRead >> 8);
            data[10] = (byte) (nRead >> 16);
            data[11] = (byte) (nRead >> 24);
            byte[] filename = new byte[12];
            int i = 0;
            String fn = GetFilename().toLowerCase();
            int extensionIndex = fn.lastIndexOf(".");
            if (extensionIndex > 0) {
                fn = fn.substring(0, extensionIndex);
            }
            if (fn.length() > 7) {
                fn = fn.substring(0, 7);
            }
            fn = fn + ".bin";
            int length = fn.length();
            for (; i < length; i++) {
                filename[i] = (byte) fn.charAt(i);
            }
            for (; i < 8; i++) {
                filename[i] = 0;
            }
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.INFO, "filename on SD: {0}", new String(filename));
            Connection.ClearSync();
            Connection.writeBytes(data);
            Connection.writeBytes(filename);
            Connection.writeBytes(buffer);
            if (Connection.WaitSync()) {
                return this;
            } else {
                return new QCmdDisconnect();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QCmdWriteFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new QCmdDisconnect();
    }
}
