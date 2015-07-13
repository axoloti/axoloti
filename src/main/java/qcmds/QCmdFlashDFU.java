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

import axoloti.utils.OSDetect;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdFlashDFU extends QCmdShellTask {

    @Override
    public String GetStartMessage() {
        return "Start flashing firmware with DFU";
    }

    @Override
    public String GetDoneMessage() {
        if (success) {
            return "Done flashing firmware with DFU.";
        } else {
            return "Flashing firmware failed!";
        }
    }
    
    @Override
    public File GetWorkingDir() {
        return new File(System.getProperty(axoloti.Axoloti.FIRMWARE_DIR));
    }
    
    
    @Override
    String GetExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return RuntimeDir() + "/platform_win/upload_fw_dfu.bat";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return "/bin/sh "+ RuntimeDir() + "/platform_osx/upload_fw_dfu.sh";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return "/bin/sh "+ RuntimeDir() + "/platform_linux/upload_fw_dfu.sh";
        } else {
            Logger.getLogger(QCmdFlashDFU.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    @Override
    QCmd err() {
        return null;
    }
}
