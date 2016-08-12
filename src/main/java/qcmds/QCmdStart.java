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

import axoloti.Connection;
import axoloti.PatchController;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdStart implements QCmdSerialTask {

    PatchController patchController;

    static int patch_start_timeout = 10000; //msec

    public QCmdStart(PatchController patchController) {
        this.patchController = patchController;
    }

    @Override
    public String GetStartMessage() {
        return "Starting patch...";
    }

    @Override
    public String GetDoneMessage() {
        return "Done starting patch";
    }

    public String GetTimeOutMessage() {
        return "patch start taking too long, disconnecting";
    }

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();

        connection.setPatchController(patchController);

        connection.TransmitStart();
        if (connection.WaitSync(patch_start_timeout)) {
            return this;
        } else {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, GetTimeOutMessage());
            return new QCmdDisconnect();
        }
    }
}
