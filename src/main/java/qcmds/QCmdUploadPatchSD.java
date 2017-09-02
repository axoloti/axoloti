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

import axoloti.IConnection;
import axoloti.PatchFileBinary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class QCmdUploadPatchSD extends QCmdUploadFile {

    public QCmdUploadPatchSD(String filename, Calendar cal) {
        super((InputStream) null, filename);
    }

    @Override
    public QCmd Do(IConnection connection) {
        try {
            inputStream = new ByteArrayInputStream(PatchFileBinary.getPatchFileBinary());
            return super.Do(connection);
        } catch (IOException ex) {
            Logger.getLogger(QCmdUploadFile.class.getName()).log(Level.SEVERE, "IOException", ex);
        }
        success = false;
        return this;
    }
}
