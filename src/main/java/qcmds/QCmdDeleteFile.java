/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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

import axoloti.Connection;
import axoloti.SDCardInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdDeleteFile implements QCmdSerialTask {

    final String filename;

    public QCmdDeleteFile(String filename) {
        this.filename = filename;
    }

    @Override
    public String GetStartMessage() {
        return "Start deleting file on sdcard : " + filename;
    }

    @Override
    public String GetDoneMessage() {
        SDCardInfo.getInstance().Delete(filename);
        return "Done deleting file";
    }

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();
        Logger.getLogger(QCmdDeleteFile.class.getName()).log(Level.INFO, "deleting: {0}", filename);
        connection.TransmitDeleteFile(filename);
        return this;
    }

}
