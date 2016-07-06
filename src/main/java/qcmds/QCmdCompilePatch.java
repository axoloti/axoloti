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

import axoloti.Patch;
import axoloti.utils.OSDetect;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdCompilePatch extends QCmdShellTask {

    Patch p;

    public QCmdCompilePatch(Patch p) {
        this.p = p;
    }

    @Override
    public String GetStartMessage() {
        return "Start compiling patch";
    }

    @Override
    public String GetDoneMessage() {
        if (success) {
            return "Done compiling patch";
        } else {
            return "Compiling patch failed ( " + p.getFileNamePath() + " ) ";
        }
    }
    
    @Override
    public File GetWorkingDir() {
        return new File(FirmwareDir());
    }
    
    @Override
    String GetExec() {
        if (OSDetect.getOS() == OSDetect.OS.WIN) {
            return FirmwareDir() + "/compile_patch_win.bat";
        } else if (OSDetect.getOS() == OSDetect.OS.MAC) {
            return "/bin/sh ./compile_patch_osx.sh";
        } else if (OSDetect.getOS() == OSDetect.OS.LINUX) {
            return "/bin/sh ./compile_patch_linux.sh";
        } else {
            Logger.getLogger(QCmdCompilePatch.class.getName()).log(Level.SEVERE, "UPLOAD: OS UNKNOWN!");
            return null;
        }
    }

    @Override
    QCmd err() {
        return new QCmdShowCompileFail(p);
    }
}
