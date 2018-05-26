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
import axoloti.live.patch.parameter.ParameterInstanceLiveView;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdGuiDialTx implements QCmdGUITask {

    @Override
    public void performGUIAction(QCmdProcessor processor) {
        if (processor.isQueueEmpty()) {
            PatchViewLive patchViewLive = processor.getPatchController();
            if (patchViewLive != null) {
                for (ParameterInstanceLiveView p : patchViewLive.getParameterInstances()) {
                    if (p.getNeedsTransmit()) {
                        if (processor.hasQueueSpaceLeft()) {
                            processor.appendToQueue(new QCmdSerialDialTX(p.TXData()));
                            //processor.println("tx dial " + p.getName());
                        } else {
                            break;
                        }
                    }
                }
                if (patchViewLive.getNeedsPresetUpdate() && processor.hasQueueSpaceLeft()) {
                    byte[] pb = patchViewLive.getUpdatedPresetTable();
                    patchViewLive.clearNeedsPresetUpdate();
                    processor.appendToQueue(new QCmdUpdatePreset(pb));
                }
            }
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
