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

import axoloti.Connection;
import java.nio.ByteBuffer;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdMemRead implements QCmdSerialTask {

    final int addr;
    final int length;
    ByteBuffer result = null;

    class Sync {

        boolean ready = false;
    }
    final Sync sync = new Sync();

    public QCmdMemRead(int addr, int length) {
        this.addr = addr;
        this.length = length;
    }

    @Override
    public QCmd Do(Connection connection) {
        synchronized (sync) {
            connection.ClearReadSync();
            connection.TransmitMemoryRead(addr, length);
            connection.WaitReadSync();
            result = connection.getMemReadBuffer();
            sync.ready = true;
            sync.notifyAll();
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
    public String GetStartMessage() {
        return null;
    }

    @Override
    public String GetDoneMessage() {
        return null;
    }
}
