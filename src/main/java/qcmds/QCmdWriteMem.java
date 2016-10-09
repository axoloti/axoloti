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
public class QCmdWriteMem implements QCmdSerialTask {

    byte[] buffer;
    int addr;

    public QCmdWriteMem(int addr, byte[] buffer) {
        this.addr = addr;
        this.buffer = buffer;
    }

    @Override
    public String GetStartMessage() {
        return null;
    }

    @Override
    public String GetDoneMessage() {
        return null;
    }

    final int MaxBlockSize = 32768;

    @Override
    public QCmd Do(Connection connection) {
        connection.ClearSync();
        int offset = 0;
        int remaining = buffer.length;
        do {
            int l;
            if (remaining > MaxBlockSize) {
                l = MaxBlockSize;
                remaining -= MaxBlockSize;
            } else {
                l = remaining;
                remaining = 0;
            }
            byte[] part = new byte[l];
            for (int i = 0; i < l; i++) {
                part[i] = buffer[offset + i];
            }
            connection.UploadFragment(part, addr + offset);
            offset += l;
            remaining -= l;
        } while (remaining > 0);
        return this;
    }
}
