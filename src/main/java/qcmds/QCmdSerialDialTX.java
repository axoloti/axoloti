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

import axoloti.SerialConnection;
import jssc.SerialPortException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdSerialDialTX implements QCmdSerialTask {

    final byte[] b;

    public QCmdSerialDialTX(byte[] b) {
        this.b = b;
    }

    @Override
    public QCmd Do(SerialConnection serialConnection) {
        serialConnection.ClearSync();
        try {
            serialConnection.writeBytes(b);
        } catch (SerialPortException ex) {
            Logger.getLogger(QCmdPing.class.getName()).log(Level.INFO, null, ex);
            return new QCmdDisconnect();
        }
        return this;
    }

    @Override
    public String GetStartMessage() {
        return null;
    }

    @Override
    public String GetDoneMessage() {
        return null;
    }
}
