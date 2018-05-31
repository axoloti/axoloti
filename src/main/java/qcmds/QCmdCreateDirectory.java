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

import axoloti.connection.IConnection;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdCreateDirectory implements QCmdSerialTask {

    final String filename;
    final Calendar date;

    public QCmdCreateDirectory(String filename) {
        this.filename = filename;
        date = Calendar.getInstance();
    }

    public QCmdCreateDirectory(String filename, Calendar date) {
        this.filename = filename;
        this.date = date;
    }

    @Override
    public String getStartMessage() {
        return "Creating directory on sdcard : " + filename;
    }

    @Override
    public String getDoneMessage() {
        return "Done creating directory";
    }

    @Override
    public QCmd performAction(IConnection connection) {
        connection.clearSync();
        connection.transmitCreateDirectory(filename, date);
        String fn1 = filename;
        if (!fn1.endsWith("/")) {
            fn1 += "/";
        }
        final String fn = fn1;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    SDCardInfo sdcardinfo = TargetModel.getTargetModel().getSDCardInfo();
                    sdcardinfo.addFile(fn, 0, date);
                    TargetModel.getTargetModel().setSDCardInfo(sdcardinfo);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(QCmdCreateDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

}
