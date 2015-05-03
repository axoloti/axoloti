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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class FirmwareID {

    static public String getFirmwareID() {
        String fw1 = "";
        String fw2 = "";
        try {
            File f = new File(Constants.firmwaredir + "/build/axoloti.dmp");
            if (!f.canRead()) {
                return "Please compile the firmware first";
            }
            BufferedReader br = new BufferedReader(new FileReader(f));
            for (String line; (line = br.readLine()) != null;) {
                if (line.contains("patchMeta")) {
                    fw1 = line.substring(4, 8);
                }
                if (line.contains("KVP_RegisterObject")) {
                    fw2 = line.substring(4, 8);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FirmwareID.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (fw1 + fw2).toUpperCase();
    }
}
