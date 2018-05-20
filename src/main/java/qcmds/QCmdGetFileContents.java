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

/**
 *
 * @author Johannes Taelman
 */
public class QCmdGetFileContents implements QCmdSerialTask {

    final String filename;
    IConnection.MemReadHandler doneHandler;

    public QCmdGetFileContents(String filename, IConnection.MemReadHandler doneHandler) {
        this.filename = filename;
        this.doneHandler = doneHandler;
    }

    @Override
    public String getStartMessage() {
        return "Start get file contents : " + filename;
    }

    @Override
    public String getDoneMessage() {
        return "Done getting file contents";
    }

    @Override
    public QCmd performAction(IConnection connection) {
        connection.clearSync();
        connection.transmitGetFileContents(filename, doneHandler);
        return this;
    }

}
