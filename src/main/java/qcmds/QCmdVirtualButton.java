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
public class QCmdVirtualButton implements QCmdSerialTask {

    int b_or;
    int b_and;
    int enc1;
    int enc2;
    int enc3;
    int enc4;

    public QCmdVirtualButton(int b_or, int b_and) {
        this.b_or = b_or;
        this.b_and = b_and;
        enc1 = 0;
        enc2 = 0;
        enc3 = 0;
        enc4 = 0;
    }

    public QCmdVirtualButton(int enc1, int enc2, int enc3, int enc4) {
        this.enc1 = enc1;
        this.enc2 = enc2;
        this.enc3 = enc3;
        this.enc4 = enc4;
        this.b_or = 0;
        this.b_and = ~0;
    }

    @Override
    public String GetStartMessage() {
        return null;
    }

    @Override
    public String GetDoneMessage() {
        return null;
    }

    @Override
    public QCmd Do(SerialConnection serialConnection) {
        serialConnection.ClearSync();
        try {
            serialConnection.TransmitVirtualButton(b_or, b_and, enc1, enc2, enc3, enc4);
            return this;
        } catch (SerialPortException ex) {
            Logger.getLogger(QCmdPing.class.getName()).log(Level.SEVERE, null, ex);
            return new QCmdDisconnect();
        }
    }
}
