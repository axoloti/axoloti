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

import axoloti.IConnection;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdBringToDFUMode implements QCmdSerialTask {

    @Override
    public String GetStartMessage() {
        return "Start enabling DFU";
    }

    @Override
    public String GetDoneMessage() {
        return "Done enabling DFU. The regular USB communication will now abort, "
                + "and Axoloti Core will restart itself in \"rescue\" (DFU) mode."
                + "\"Flash (rescue)\" will restart Axoloti Core again in normal mode when completed."
                + "To leave \"rescue\" mode, power-cycle your Axoloti Core.";
    }

    @Override
    public QCmd Do(IConnection connection) {
        connection.BringToDFU();
        return this;
    }
}
