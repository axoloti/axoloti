/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdPing implements QCmdSerialTask {

    @Override
    public String GetStartMessage() {
        return null;//"Start ping";
    }

    @Override
    public String GetDoneMessage() {
        return null;//"Done ping";
    }
    private boolean noCauseDisconnect;

    public QCmdPing() {
        noCauseDisconnect = false;
    }

    public QCmdPing(boolean noCauseDisconnect) {
        this.noCauseDisconnect = noCauseDisconnect;
    }

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();
        connection.TransmitPing();
        if (connection.WaitSync() || (noCauseDisconnect)) {
            return this;
        } else {
            if (connection.isConnected()) {
                Logger.getLogger(QCmdPing.class.getName()).log(Level.SEVERE, "Ping: WaitSync Timeout, disconnecting now");
                return new QCmdDisconnect();
            } else {
                return null;
            }
        }
    }
}
