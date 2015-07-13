/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import java.awt.EventQueue;
import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Johannes Taelman
 */
public class Axoloti {

    public final static String RUNTIME_DIR = "axoloti_runtime";
    public final static String HOME_DIR = "axoloti_home";
    public final static String RELEASE_DIR = "axoloti_release";
    public final static String FIRMWARE_DIR = "axoloti_firmware";

    static void BuildEnv(String var, String def) {
        String ev = System.getProperty(var);
        if (ev == null) {
            ev = System.getenv(var);
            if (ev == null) {
                ev = def;
            }
        }
        System.setProperty(var, ev);
    }

    static boolean TestDir(String var) {
        String ev = System.getProperty(var);
        File f = new File(var);
        if (f.exists()) {
            System.err.println(var + " Directory does not exist " + ev);
            return false;
        }
        if (f.isDirectory()) {
            System.err.println(var + " should be a valid directory " + ev);
            return false;
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // setup environment
            String curDir = System.getProperty("user.dir");

            BuildEnv(RELEASE_DIR, curDir);
            if (!TestDir(RELEASE_DIR)) {
                System.exit(-1);
            }
            BuildEnv(RUNTIME_DIR, curDir);
            if (!TestDir(RUNTIME_DIR)) {
                System.exit(-1);
            }

            BuildEnv(FIRMWARE_DIR, System.getProperty(RELEASE_DIR) + File.separator + "firmware");
            if (!TestDir(FIRMWARE_DIR)) {
                System.exit(-1);
            }

            BuildEnv(HOME_DIR, curDir);
            File homedir = new File(System.getProperty(HOME_DIR));
            if (!homedir.exists()) {
                homedir.mkdir();
            }
            File buildir = new File(System.getProperty(HOME_DIR) + File.separator + "build");
            if (!buildir.exists()) {
                buildir.mkdir();
            }

            if (!TestDir(HOME_DIR)) {
                System.exit(-1);
            }

            System.out.println("Axoloti Driectories:\n"
                    + "Current = " + curDir + "\n"
                    + "Release = " + System.getProperty(RELEASE_DIR) + "\n"
                    + "Runtime = " + System.getProperty(RUNTIME_DIR) + "\n"
                    + "Firware = " + System.getProperty(FIRMWARE_DIR) + "\n"
                    + "AxolotiHome = " + System.getProperty(HOME_DIR)
            );

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (System.getProperty("os.name").contains("OS X")) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        System.setProperty("line.separator", "\n");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
