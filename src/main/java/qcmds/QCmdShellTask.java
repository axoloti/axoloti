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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public abstract class QCmdShellTask implements QCmd {

    abstract String GetExec();
    boolean success;

    class StreamHandlerThread implements Runnable {

        InputStream in;
        QCmdProcessor shellProcessor;

        public StreamHandlerThread(QCmdProcessor shellProcessor, InputStream in) {
            this.in = in;
            this.shellProcessor = shellProcessor;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            try {
                line = br.readLine();
                while (line != null) {
                    Logger.getLogger(QCmdCompilePatch.class.getName()).info(line);
                    line = br.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(QCmdCompilePatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public QCmd Do(QCmdProcessor shellProcessor) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p1;
            p1 = runtime.exec(GetExec());

            Thread thd_out = new Thread(new StreamHandlerThread(shellProcessor, p1.getInputStream()));
            thd_out.start();
            Thread thd_err = new Thread(new StreamHandlerThread(shellProcessor, p1.getErrorStream()));
            thd_err.start();
            p1.waitFor();
            thd_out.join();
            thd_err.join();
            if (p1.exitValue() == 0) {
                success = true;
            } else {
                Logger.getLogger(QCmdCompilePatch.class.getName()).log(Level.SEVERE, "shell task failed, exit value: " + p1.exitValue());
                success = false;
                return err();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(QCmdCompilePatch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QCmdCompilePatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    abstract QCmd err();
}
