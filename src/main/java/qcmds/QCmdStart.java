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
import axoloti.Patch;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdStart implements QCmdSerialTask {

    Patch p;
    
    public QCmdStart(Patch p) {
        this.p=p;
    }

    @Override
    public String GetStartMessage() {
        return "Start starting patch";
    }

    @Override
    public String GetDoneMessage() {
        return "Done starting patch";
    }

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();
        
        connection.setPatch(p);
        
        connection.TransmitStart();
        if (connection.WaitSync()) {
            return this;
        } else {
            return new QCmdDisconnect();
        }
    }
}
