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

/**
 *
 * @author Johannes Taelman
 */
public class QCmdWriteMem implements QCmdSerialTask {

    private final byte[] buffer;
    private final int addr;

    public QCmdWriteMem(int addr, byte[] buffer) {
        this.addr = addr;
        this.buffer = buffer;
    }

    @Override
    public String getStartMessage() {
        return null;
    }

    @Override
    public String getDoneMessage() {
        return null;
    }

    private final static int MAXBLOCKSIZE = 32768;

    @Override
    public QCmd performAction(IConnection connection) {
        connection.clearSync();
        int offset = 0;
        int remaining = buffer.length;
        do {
            int l;
            if (remaining > MAXBLOCKSIZE) {
                l = MAXBLOCKSIZE;
                remaining -= MAXBLOCKSIZE;
            } else {
                l = remaining;
                remaining = 0;
            }
            byte[] part = new byte[l];
            for (int i = 0; i < l; i++) {
                part[i] = buffer[offset + i];
            }
            connection.uploadFragment(part, addr + offset);
            offset += l;
            remaining -= l;
        } while (remaining > 0);
        return this;
    }
}
