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

import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.live.patch.PatchViewLive;
import axoloti.preferences.Preferences;
import axoloti.swingui.MainFrame;
import axoloti.target.PollHandler;
import axoloti.target.TargetModel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Johannes Taelman
 */
public class QCmdProcessor implements Runnable {

    private final BlockingQueue<QCmd> queue;
    private final BlockingQueue<QCmd> queueResponse;
    protected IConnection serialconnection;
    private PatchViewLive patchViewLive;
    private final PeriodicPinger pinger;
    private final Thread pingerThread;
    private final PeriodicDialTransmitter dialTransmitter;
    private final Thread dialTransmitterThread;

    class PeriodicPinger implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(Preferences.getPreferences().getPollInterval());
                } catch (InterruptedException ex) {
                    Logger.getLogger(QCmdProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (queue.isEmpty() && serialconnection.isConnected()) {
                    queue.add(new QCmdPing());

                    for (PollHandler poller : TargetModel.getTargetModel().getPollers()) {
                        poller.operation();
                    }
                }
            }
        }
    }

    class PeriodicDialTransmitter implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (queue.isEmpty() && serialconnection.isConnected()) {
                    queue.add(new QCmdGuiDialTx());
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QCmdProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    protected QCmdProcessor() {
        queue = new ArrayBlockingQueue<>(16);
        queueResponse = new ArrayBlockingQueue<>(16);
        serialconnection = CConnection.getConnection();
        pinger = new PeriodicPinger();
        pingerThread = new Thread(pinger);
        dialTransmitter = new PeriodicDialTransmitter();
        dialTransmitterThread = new Thread(dialTransmitter);
    }

    private static QCmdProcessor singleton = null;

    public static QCmdProcessor getQCmdProcessor() {
        if (singleton == null)
            singleton = new QCmdProcessor();
        return singleton;
    }

    public PatchViewLive getPatchController() {
        return patchViewLive;
    }

    public boolean appendToQueue(QCmd cmd) {
        return queue.add(cmd);
    }

    public void abort() {
        queue.clear();
        queueResponse.clear();
    }

    public void panic() {
        queue.clear();
//        shellprocessor.Panic();
//        serialconnection.Panic();
    }

    private void publish(final QCmd cmd) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (QCmdGUITask.class.isInstance(cmd)) {
                    ((QCmdGUITask) cmd).performGUIAction(QCmdProcessor.this);
                }
                String m = ((QCmd) cmd).getDoneMessage();
                if (m != null) {
                    MainFrame.mainframe.setProgressMessage(m);
                }
            }
        });
    }

    private void publish(final String m) {
//        println("publish: " + m);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame.mainframe.setProgressMessage(m);
            }
        });
    }

    public void waitQueueFinished() throws Exception {
        int t = 0;
        while (true) {
            if (queue.isEmpty() && queueResponse.isEmpty()) {
                break;
            }
            try {
                Thread.sleep(10);
                t += 10;
                if (t > 8000) {
                    System.out.println("flushing..., current = " + currentcmd);
                    while (!queue.isEmpty()) {
                        System.out.println("queue timeout : " + queue.take());
                    }
                    while (!queueResponse.isEmpty()) {
                        System.out.println("queue timeout : " + queueResponse.take());
                    }
                    throw new Exception("Queue timeout " + currentcmd);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(QCmdProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    QCmd currentcmd = null;

    @Override
    public void run() {
        pingerThread.setName("PingerThread");
        pingerThread.start();
        dialTransmitterThread.setName("DialTransmitter");
        dialTransmitterThread.start();
        while (true) {
            try {
                queueResponse.clear();
                currentcmd = queue.take();
                if (!((currentcmd instanceof QCmdPing) || (currentcmd instanceof QCmdGuiDialTx))) {
                    //System.out.println(cmd);
                    //setProgress((100 * (queue.size() + 1)) / (queue.size() + 2));
                }
                String m = currentcmd.getStartMessage();
                if (m != null) {
                    publish(m);
                    println(m);
                }
                if (QCmdShellTask.class.isInstance(currentcmd)) {
                    //                shellprocessor.AppendToQueue((QCmdShellTask)cmd);
                    //                publish(queueResponse.take());
                    QCmd response = ((QCmdShellTask) currentcmd).performShellTask(this);
                    if ((response != null)) {
                        ((QCmdGUITask) response).performGUIAction(this);
                    }
                }
                if (QCmdSerialTask.class.isInstance(currentcmd)) {
                    if (serialconnection.isConnected()) {
                        serialconnection.appendToQueue((QCmdSerialTask) currentcmd);
                        QCmd response = queueResponse.take();
                        publish(response);
                        if (response instanceof QCmdDisconnect) {
                            queue.clear();
                        }
                    }
                }
                if (QCmdGUITask.class.isInstance(currentcmd)) {
                    publish(currentcmd);
                }
                m = currentcmd.getDoneMessage();
                if (m != null) {
                    println(m);
                    publish(m);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(QCmdProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            // TODO: progress reporting
            // setProgress(0);
        }
    }

    public void println(final String s) {
        if ((s == null) || s.isEmpty()) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Logger.getLogger(QCmdProcessor.class.getName()).log(Level.INFO, s);
            }
        });
    }

    public void setPatchController(PatchViewLive patchViewLive) {
        if (this.patchViewLive != null) {
            this.patchViewLive.getDModel().getController().setLocked(false);
        }
        this.patchViewLive = patchViewLive;
    }

    public BlockingQueue<QCmd> getQueueResponse() {
        return queueResponse;
    }

    public void clearQueue() {
        queue.clear();
    }

    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    public boolean hasQueueSpaceLeft() {
        return (queue.remainingCapacity()>3);
    }

}
