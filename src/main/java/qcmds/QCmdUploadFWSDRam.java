/**
 * Copyright (C) 2015 Johannes Taelman
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
import java.util.zip.CRC32;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdUploadFWSDRam implements QCmdSerialTask {

    File f;

    public QCmdUploadFWSDRam() {
        f = null;
    }

    public QCmdUploadFWSDRam(File f) {
        this.f = f;
    }

    @Override
    public String GetStartMessage() {
        return "Start uploading firmware";
    }

    @Override
    public String GetDoneMessage() {
        return "Done uploading firmware";
    }

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();
        try {
            if (f == null) {
                String buildDir = System.getProperty(Axoloti.FIRMWARE_DIR) + "/build";
                f = new File(buildDir+"/axoloti.bin");
            }
            Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.INFO, "firmware file path: {0}", f.getAbsolutePath());
            int tlength = (int) f.length();
            FileInputStream inputStream = new FileInputStream(f);

            int offset = 0;
            byte[] header = new byte[16];
            header[0] = 'f';
            header[1] = 'l';
            header[2] = 'a';
            header[3] = 's';
            header[4] = 'c';
            header[5] = 'o';
            header[6] = 'p';
            header[7] = 'y';
            header[8] = (byte) (tlength);
            header[9] = (byte) (tlength >> 8);
            header[10] = (byte) (tlength >> 16);
            header[11] = (byte) (tlength >> 24);
            byte[] bb = new byte[tlength];
            int nRead = inputStream.read(bb, 0, tlength);
            if (nRead != tlength) {
                Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
            }
            inputStream.close();
            inputStream = new FileInputStream(f);
            Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.INFO, "firmware file size: {0}", tlength);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
            CRC32 zcrc = new CRC32();
            zcrc.update(bb);
            int zcrcv = (int) zcrc.getValue();
            Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.INFO, "firmware crc: 0x{0}", Integer.toHexString(zcrcv).toUpperCase());
            header[12] = (byte) (zcrcv);
            header[13] = (byte) (zcrcv >> 8);
            header[14] = (byte) (zcrcv >> 16);
            header[15] = (byte) (zcrcv >> 24);
            connection.UploadFragment(header, connection.getTargetProfile().getSDRAMAddr() + offset);
            offset += header.length;
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
                nRead = inputStream.read(buffer, 0, l);
                if (nRead != l) {
                    Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                }
                connection.UploadFragment(buffer, connection.getTargetProfile().getSDRAMAddr() + offset);
                offset += nRead;
            } while (tlength > 0);
            inputStream.close();
            return this;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.SEVERE, "FileNotFoundException", ex);
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadFWSDRam.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
        return new QCmdDisconnect();
    }

}
