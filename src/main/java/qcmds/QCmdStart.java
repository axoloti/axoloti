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
import axoloti.live.patch.PatchViewLive;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdStart implements QCmdSerialTask {

    final PatchViewLive patchViewLive;
    final Integer patchIndex;
    final String patchName;

    static int patch_start_timeout = 10000; //msec

    public QCmdStart() {
        patchIndex = null;
        patchName = null;
        patchViewLive = null;
    }

    public QCmdStart(PatchViewLive patchViewLive) {
        patchIndex = null;
        patchName = null;
        this.patchViewLive = patchViewLive;
    }

    public QCmdStart(String patchName) {
        patchViewLive = null;
        patchIndex = null;
        this.patchName = patchName;
    }

    public QCmdStart(int patchIndex) {
        patchViewLive = null;
        patchName = null;
        this.patchIndex = patchIndex;
    }

    @Override
    public String getStartMessage() {
        return "Starting patch...";
    }

    @Override
    public String getDoneMessage() {
        return "Done starting patch";
    }

    public String getTimeOutMessage() {
        return "patch start taking too long, disconnecting";
    }

    @Override
    public QCmd performAction(IConnection connection) {
        connection.clearSync();

        connection.setPatch(patchViewLive);

        if (patchName != null) {
            connection.transmitStart(patchName);
        } else if (patchIndex != null) {
            connection.transmitStart(patchIndex);
        } else {
            connection.transmitStart();
        }
        if (connection.waitSync(patch_start_timeout)) {
            return this;
        } else {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, getTimeOutMessage());
            return new QCmdDisconnect();
        }
    }
}
