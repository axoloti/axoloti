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
package axoloti;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import qcmds.QCmd;
import qcmds.QCmdShellTask;

/**
 *
 * @author Johannes Taelman
 */
public class ShellProcessor extends SwingWorker<Integer, String> {

    private final BlockingQueue<QCmdShellTask> queueShellTasks;
    private final BlockingQueue<QCmd> queueResponse;

    public ShellProcessor(BlockingQueue<QCmd> queueResponse) {
        super();
        queueShellTasks = new ArrayBlockingQueue<QCmdShellTask>(10);
        this.queueResponse = queueResponse;
    }

    public boolean AppendToQueue(QCmdShellTask cmd) {
//        Logger.getLogger(ShellProcessor.class.getName()).log(Level.INFO, "ShellProcessor queue: "+ cmd.GetStartMessage());
        return queueShellTasks.add(cmd);
    }

    @Override
    public Integer doInBackground() {
        while (true) {
            //          Logger.getLogger(ShellProcessor.class.getName()).log(Level.INFO, "ShellProcessor Waiting");
            try {
                QCmdShellTask qc = queueShellTasks.take();
//                Logger.getLogger(ShellProcessor.class.getName()).log(Level.INFO, "ShellProcessor : "+ qc.GetStartMessage());
//                queueResponse.add(qc.Do(this));
            } catch (InterruptedException ex) {
                Logger.getLogger(ShellProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void println(String s) {
        publish(s);
    }

    @Override
    protected void process(List<String> chunks) {
        for (String s : chunks) {
            Logger.getLogger(ShellProcessor.class.getName()).log(Level.INFO, s);
        }
    }

    @Override
    protected void done() {
        Logger.getLogger(ShellProcessor.class.getName()).log(Level.SEVERE, "ShellProcessor Terminated!");
    }

    public void Panic() {
        queueShellTasks.clear();
    }
}
