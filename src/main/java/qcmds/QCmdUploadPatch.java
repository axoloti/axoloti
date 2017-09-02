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
import axoloti.IConnection;
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
public class QCmdUploadPatch implements QCmdSerialTask {

    final String basepath;

    public QCmdUploadPatch() {
        basepath = System.getProperty(Axoloti.HOME_DIR)+"/build/xpatch";
    }

    public QCmdUploadPatch(String basepath) {
        this.basepath = basepath;
    }

    @Override
    public String GetStartMessage() {
        return "Start uploading patch";
    }

    @Override
    public String GetDoneMessage() {
        return "Done uploading patch";
    }

    void UploadBinFile(IConnection connection, File f, int baseaddr) throws FileNotFoundException, IOException {
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
                    Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                }
                connection.UploadFragment(buffer, baseaddr + offset);
                offset += nRead;
            } while (tlength > 0);
            inputStream.close();        
    }
    
    @Override
    public QCmd Do(IConnection connection) {
        connection.ClearSync();
        try {
            // from now on there can be multiple segments!
            File f = new File(basepath + ".sram1.bin");
            File f2 = new File(basepath + ".sram3.bin");
            File f3 = new File(basepath + ".sdram.bin");
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.INFO, "bin path: {0}", f.getAbsolutePath() + " " + f2.getAbsolutePath());
            UploadBinFile(connection, f, connection.getTargetProfile().getPatchAddr());
            if (f2.length() > 0)
                UploadBinFile(connection, f2, connection.getTargetProfile().getSRAM3Addr());
            if (f3.length() > 0)
                UploadBinFile(connection, f3, connection.getTargetProfile().getSDRAMAddr());
            return this;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadPatch.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
        return new QCmdDisconnect();
    }

}
