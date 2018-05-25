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

import axoloti.live.patch.PatchViewLive;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdLock implements QCmdGUITask {

    private final PatchViewLive patchViewLive;

    public QCmdLock(PatchViewLive patchViewLive) {
        this.patchViewLive = patchViewLive;
    }

    @Override
    public String getStartMessage() {
        return "Start locking";
    }

    @Override
    public String getDoneMessage() {
        return "Done locking";
    }

    @Override
    public void performGUIAction(QCmdProcessor processor) {
        processor.setPatchController(patchViewLive);
        patchViewLive.getDModel().getController().setLocked(true);
    }
}
