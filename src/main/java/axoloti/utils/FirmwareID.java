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
package axoloti.utils;

import axoloti.Axoloti;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author Johannes Taelman
 */
public class FirmwareID {

    static public String getFirmwareID() {
        try {
            File f = new File(System.getProperty(Axoloti.FIRMWARE_DIR) +"/build/axoloti.bin");
            if (!f.canRead()) {
                return "Please compile the firmware first";
            }
            int tlength = (int) f.length();
            FileInputStream inputStream = new FileInputStream(f);
            byte[] bb = new byte[tlength];
            int nRead = inputStream.read(bb, 0, tlength);
            if (nRead != tlength) {
                Logger.getLogger(FirmwareID.class.getName()).log(Level.SEVERE, "file size wrong?" + nRead);
            }
            CRC32 zcrc = new CRC32();
            zcrc.update(bb);
            int zcrcv = (int) zcrc.getValue();
            return String.format("%08X", zcrcv);
        } catch (IOException ex) {
            Logger.getLogger(FirmwareID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
