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

import axoloti.connection.IConnection;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdUploadFile implements QCmdSerialTask {

    InputStream inputStream;
    final String filename;
    final Calendar cal;
    File file;
    long size;
    long tsEpoch;
    boolean success = false;

    public QCmdUploadFile(InputStream inputStream, String filename) {
        this.inputStream = inputStream;
        this.filename = filename;
        this.cal = null;
    }

    public QCmdUploadFile(File file, String filename) {
        this.file = file;
        this.filename = filename;
        inputStream = null;
        this.cal = null;
    }

    public QCmdUploadFile(File file, String filename, Calendar cal) {
        this.file = file;
        this.filename = filename;
        inputStream = null;
        this.cal = cal;
    }

    @Override
    public String getStartMessage() {
        return "Start uploading file to sdcard : " + filename;
    }

    @Override
    public String getDoneMessage() {
        if (success) {
            return "Done uploading file";
        } else {
            return "Failed uploading file";
        }
    }

    @Override
    public QCmd performAction(IConnection connection) {
        connection.clearSync();
        try {
            if (inputStream == null) {
                if (!file.isFile()) {
                    Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.INFO, "file does not exist: {0}", filename);
                    success = false;
                    return this;
                }
                if (!file.canRead()) {
                    Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.INFO, "can''t read file: {0}", filename);
                    success = false;
                    return this;
                }
                inputStream = new FileInputStream(file);
            }
            Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.INFO, "uploading: {0}", filename);
            Calendar ts;
            if (cal != null) {
                ts = cal;
            } else if (file != null) {
                ts = Calendar.getInstance();
                ts.setTimeInMillis(file.lastModified());
            } else {
                ts = Calendar.getInstance();
            }
            int tlength = inputStream.available();
            int remLength = inputStream.available();
            size = tlength;
            connection.transmitCreateFile(filename, tlength, ts);
            int MaxBlockSize = 32768;
            int pct = 0;
            do {
                int l;
                if (remLength > MaxBlockSize) {
                    l = MaxBlockSize;
                    remLength -= MaxBlockSize;
                } else {
                    l = remLength;
                    remLength = 0;
                }
                byte[] buffer = new byte[l];
                int nRead = inputStream.read(buffer, 0, l);
                if (nRead != l) {
                    Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, "file size wrong?{0}", nRead);
                }
                connection.transmitAppendFile(buffer);
                int newpct = (100 * (tlength - remLength) / tlength);
                if (newpct != pct) {
                    Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.INFO, "uploading : {0}%", newpct);
                }
                pct = newpct;
                remLength = inputStream.available();
            } while (remLength > 0);

            inputStream.close();
            connection.transmitCloseFile();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        SDCardInfo sdcardinfo = TargetModel.getTargetModel().getSDCardInfo();
                        sdcardinfo.addFile(filename, (int) size, ts);
                        TargetModel.getTargetModel().setSDCardInfo(sdcardinfo);
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, null, ex);
            }
            success = true;
            return this;
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
        success = false;
        return this;
    }

}
