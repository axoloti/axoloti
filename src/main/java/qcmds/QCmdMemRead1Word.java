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

/**
 *
 * @author Johannes Taelman
 */
public class QCmdMemRead1Word implements QCmdSerialTask {

    final int addr;
    int result = 0;

    class Sync {

        boolean ready = false;
    }
    final Sync sync = new Sync();

    public QCmdMemRead1Word(int addr) {
        this.addr = addr;
    }

    @Override
    public QCmd Do(Connection connection) {
        synchronized (sync) {
            connection.ClearReadSync();
            connection.TransmitMemoryRead1Word(addr);
            connection.WaitReadSync();
            result = connection.getMemRead1Word();
            sync.ready = true;
            sync.notifyAll();
        }
        return this;
    }

    public int getResult() {
        synchronized (sync) {
            if (sync.ready) {
                return result;
            }
            try {
                sync.wait(10000);
                return result;
            } catch (InterruptedException ex) {
            }
        }
        return 0;
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
