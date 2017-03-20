/**
 * Copyright (C) 2017 Johannes Taelman
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class QCmdUploadPatchSD extends QCmdUploadFile {

    public QCmdUploadPatchSD(String filename, Calendar cal) {
        super((InputStream)null, filename);
    }
    
    @Override
    public QCmd Do(Connection connection) {
       try {
            String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
            int nfiles = 3;
            File f[] = new File[nfiles];
            f[0] = new File(buildDir + "/xpatch.sram1.bin");
            f[1] = new File(buildDir + "/xpatch.sram3.bin");
            f[2] = new File(buildDir + "/xpatch.sdram.bin");
            int addr[] = {
                connection.getTargetProfile().getPatchAddr(),
                connection.getTargetProfile().getSRAM3Addr(),
                connection.getTargetProfile().getSDRAMAddr()
            };
            int size_header = 20;
            int size_segmentheader = 12;
            int bbsize = size_header;
            for(int i=0;i<nfiles;i++){
                bbsize += size_segmentheader + f[i].length();
            }
            byte ba[] = new byte[bbsize];
            ByteBuffer bb = ByteBuffer.wrap(ba);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.clear();
            // header
            // signature
            bb.put((byte)'A');
            bb.put((byte)'x');
            bb.put((byte)'o');
            bb.put((byte)'h');
            // length
            bb.putInt(12);
            // arch
            bb.put((byte)'f');
            bb.put((byte)'4');
            bb.put((byte)'2');
            bb.put((byte)'7');
            // version
            bb.put((byte)0);
            bb.put((byte)0);
            bb.put((byte)0);
            bb.put((byte)0);
            // nsegments
            bb.putInt(nfiles);
            // segments...
            for(int i=0;i<nfiles;i++){
                bb.putInt(0x12345678);
                bb.putInt((int)f[i].length()+4);
                bb.putInt(addr[i]);
                bb.put(Files.readAllBytes(f[i].toPath()));
            }
            inputStream = new ByteArrayInputStream(bb.array());
            return super.Do(connection);
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
        success = false;
        return this;
     }
}
