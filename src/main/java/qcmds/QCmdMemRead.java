/**
 * Copyright (C) 2015 Johannes Taelman
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
import axoloti.connection.IConnection.MemReadHandler;
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdMemRead implements QCmdSerialTask {

    final int addr;
    final int length;
    ByteBuffer result = null;
    MemReadHandler doneHandler;

    static class Sync {

        boolean ready = false;
    }
    final Sync sync = new Sync();

    public QCmdMemRead(int addr, int length) {
        this.addr = addr;
        this.length = length;
        this.doneHandler = null;
    }

    public QCmdMemRead(int addr, int length, MemReadHandler doneHandler) {
        this.addr = addr;
        this.length = length;
        this.doneHandler = doneHandler;
    }

    @Override
    public QCmd performAction(IConnection connection) {
        if (length != 0) {
            connection.clearReadSync();
            connection.transmitMemoryRead(addr, length, doneHandler);
            connection.waitReadSync();
            result = connection.getMemReadBuffer();
            sync.ready = true;
            synchronized (sync) {
                sync.notifyAll();
            }
        }
        return this;
    }

    public ByteBuffer getResult() {
        synchronized (sync) {
            if (sync.ready) {
                return result;
            }
            try {
                sync.wait(1000);
                return result;
            } catch (InterruptedException ex) {
            }
        }
        if (sync.ready) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getStartMessage() {
        return null;
    }

    @Override
    public String getDoneMessage() {
        return null;
    }
}
