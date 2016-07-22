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

import axoloti.object.AxoObjects;
import axoloti.utils.AxolotiLibrary;
import axoloti.utils.OSDetect;
import axoloti.utils.Preferences;
import java.awt.EventQueue;
import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Johannes Taelman
 */
public class Axoloti {

    public final static String RUNTIME_DIR = "axoloti_runtime";
    public final static String HOME_DIR = "axoloti_home";
    public final static String RELEASE_DIR = "axoloti_release";
    public final static String FIRMWARE_DIR = "axoloti_firmware";
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            initProperties();

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (System.getProperty("os.name").contains("OS X")) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
        } catch (URISyntaxException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        } catch (InstantiationException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new Error(e);
        }
        System.setProperty("line.separator", "\n");

        Synonyms.instance(); // prime it
        handleCommandLine(args);
    }

    static void BuildEnv(String var, String def) {
        String ev = System.getProperty(var);
        if (ev == null) {
            ev = System.getenv(var);
            if (ev == null) {
                ev = def;
            }
        }
        File f = new File(ev);
        if (f.exists()) {
            try {
                ev = f.getCanonicalPath();
            } catch (IOException ex) {
                Logger.getLogger(Axoloti.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.setProperty(var, ev);
    }

    static boolean TestDir(String var) {
        String ev = System.getProperty(var);
        File f = new File(ev);
        if (!f.exists()) {
            System.err.println(var + " Directory does not exist " + ev);
            return false;
        }
        if (!f.isDirectory()) {
            System.err.println(var + " should be a valid directory " + ev);
            return false;
        }
        return true;
    }

    // cache this, as it linked to checks on the UI/menu
    private static String cacheFWDir = null;
    private static boolean cacheDeveloper = false;

    public static boolean isDeveloper() {
        String fwEnv = System.getProperty(FIRMWARE_DIR);
        if (cacheFWDir != null && fwEnv.equals(cacheFWDir)) {
            return cacheDeveloper;
        }
        cacheFWDir = fwEnv;
        cacheDeveloper = false;
        String dirRelease = System.getProperty(RELEASE_DIR);
        String fwRelease = dirRelease + File.separator + "firmware";
        if (!fwRelease.equals(cacheFWDir)) {
            File fR = new File(fwRelease);
            File fE = new File(fwEnv);
            try {
                cacheDeveloper = !fR.getCanonicalPath().equals(fE.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(Axoloti.class.getName()).log(Level.SEVERE, null, ex);
                cacheDeveloper = false;
            }
        } else {
            File f = new File(dirRelease + File.separator + ".git");
            if (f.exists()) {
                cacheDeveloper = true;
            }
        }
        return cacheDeveloper;
    }

    static boolean failSafeMode = false;

    static void checkFailSafeModeActive() {
        failSafeMode = false;
        String homedir = System.getProperty(HOME_DIR);
        if (homedir == null) {
            return;
        }
        try {
            File f = new File(homedir + File.separator + "failsafe");
            if (f.exists()) {
                System.err.print("fail safe mode");
                failSafeMode = true;
            }
        } catch (Throwable e) {
        }
    }

    public static boolean isFailSafeMode() {
        return failSafeMode;
    }

    private static void initProperties() throws URISyntaxException, IOException {
        String curDir = System.getProperty("user.dir");
        File jarFile = new File(Axoloti.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String jarDir = jarFile.getParentFile().getCanonicalPath();
        String defaultHome = curDir;
        String defaultRuntime = curDir;
        String defaultRelease = curDir;
        boolean versionedHome = false;

        File git = new File("." + File.separator + ".git");
        if (git.exists()) {
            // developer using git, assume they want everything local dir
            System.out.println("defaulting to developer defaults, can be overridden");
            defaultHome = ".";
            defaultRuntime = ".";
        } else {
            String docDir;
            if (null != OSDetect.getOS()) 
                switch (OSDetect.getOS()) {
                case WIN:
                    // not sure which versions of windows this is valid for, good for 8!
                    docDir = System.getenv("HOMEPATH") + File.separator + "Documents" + File.separator;
                    defaultRuntime = System.getenv("ProgramFiles") + File.separator + "axoloti_runtime";
                    break;
                case MAC:
                    docDir = System.getenv("HOME") + "/Documents/";
                    defaultRuntime = "/Applications/axoloti_runtime";
                    break;
                case LINUX:
                default:
                    docDir = System.getenv("HOME") + "/";
                    defaultRuntime = System.getenv("HOME") + "/axoloti_runtime";
                    break;
            } else {
                    docDir = System.getenv("HOME") + "/";
                    defaultRuntime = System.getenv("HOME") + "/axoloti_runtime";
            }
            
            String ver = Version.AXOLOTI_SHORT_VERSION.replace(".", "_");
            File versionHome= new File(docDir + "axoloti_"+ver);
            if(versionHome.exists()) {
                defaultHome = docDir + "axoloti_"+ver;
                versionedHome = true;
            } else {
                defaultHome = docDir + "axoloti";
            }
        }

        BuildEnv(HOME_DIR, defaultHome);
        File homedir = new File(System.getProperty(HOME_DIR));
        if (!homedir.exists()) {
            homedir.mkdir();
        }

        File buildir = new File(System.getProperty(HOME_DIR) + File.separator + "build");
        if (!buildir.exists()) {
            buildir.mkdir();
        }
        if (!TestDir(HOME_DIR)) {
            System.err.println("Home directory is invalid");
        }
        checkFailSafeModeActive(); // do this as as possible after home dir setup

        BuildEnv(RELEASE_DIR, defaultRelease);
        if (!TestDir(RELEASE_DIR)) {
            System.err.println("Release directory is invalid");
        }
        BuildEnv(RUNTIME_DIR, defaultRuntime);
        if (!TestDir(RUNTIME_DIR)) {
            System.err.println("Runtime directory is invalid");
        }

        BuildEnv(FIRMWARE_DIR, System.getProperty(RELEASE_DIR) + File.separator + "firmware");
        if (!TestDir(FIRMWARE_DIR)) {
            System.err.println("Firmware directory is invalid");
        }

        Preferences prefs = Preferences.LoadPreferences();
        if (versionedHome) {
            String fwDir = System.getProperty(axoloti.Axoloti.FIRMWARE_DIR);
            if(! fwDir.startsWith(System.getProperty(RELEASE_DIR)) && !fwDir.startsWith(System.getProperty(HOME_DIR))) {
                System.out.println("Using versioned home, will reset firmware");
                prefs.SetFirmwareDir(System.getProperty(RELEASE_DIR) + File.separator + "firmware");
            }

            AxolotiLibrary lib = prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
            if(lib != null) {
                File locdir = new File(lib.getLocalLocation());
                File verdir = new File(System.getProperty(axoloti.Axoloti.HOME_DIR) + File.separator + "axoloti-factory"+File.separator);
                if(! locdir.getCanonicalPath().equals(verdir.getCanonicalPath())) {
                    System.out.println("Using versioned home, will reset libraries");
                    prefs.ResetLibraries(true);
                }
            }
        }

        System.out.println("Axoloti Directories:\n"
                + "Current = " + curDir + "\n"
                + "Jar = " + jarDir + "\n"
                + "Release = " + System.getProperty(RELEASE_DIR) + "\n"
                + "Runtime = " + System.getProperty(RUNTIME_DIR) + "\n"
                + "Firmware = " + System.getProperty(FIRMWARE_DIR) + "\n"
                + "AxolotiHome = " + System.getProperty(HOME_DIR)
        );
    }

    private static void handleCommandLine(final String args[]) {
        boolean cmdLineOnly = false;
        boolean cmdRunAllTest = false;
        boolean cmdRunPatchTest = false;
        boolean cmdRunObjectTest = false;
        boolean cmdRunFileTest = false;
        boolean cmdRunUpgrade = false;
        String cmdFile = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("-exitOnFirstFail")) {
                MainFrame.stopOnFirstFail = true;
            }

            // exclusive options
            if (arg.equalsIgnoreCase("-runAllTests")) {
                cmdLineOnly = true;
                cmdRunAllTest = true;
            } else if (arg.equalsIgnoreCase("-runPatchTests")) {
                cmdLineOnly = true;
                cmdRunPatchTest = true;
            } else if (arg.equalsIgnoreCase("-runObjTests")) {
                cmdLineOnly = true;
                cmdRunObjectTest = true;
            } else if (arg.equalsIgnoreCase("-runTest")) {
                cmdLineOnly = true;
                cmdRunFileTest = true;
                if (i + 1 < args.length) {
                    cmdFile = args[i + 1];
                } else {
                    System.err.println("-runTest patchname/directory : missing file/dir");
                    System.exit(-1);
                }
            } else if (arg.equalsIgnoreCase("-runUpgrade")) {
                cmdLineOnly = true;
                cmdRunUpgrade = true;
                if (i + 1 < args.length) {
                    cmdFile = args[i + 1];
                } else {
                    System.err.println("-runUpgrade patchname/directory : missing file/dir");
                    System.exit(-1);
                }
            } else if (arg.equalsIgnoreCase("-help")) {
                System.out.println("Axoloti "
                        + " [-runAllTests|-runPatchTests|-runObjTests] "
                        + " [-runTest patchfile|dir]"
                        + " [-runUpgrade patchfile|dir]"
                        + " [-exitOnFirstFail");
                System.exit(0);
            }
        }

        if (cmdLineOnly) {
            try {
                MainFrame frame = new MainFrame(args);
                AxoObjects objs = new AxoObjects();
                objs.LoadAxoObjects();
                if (SplashScreen.getSplashScreen() != null) {
                    SplashScreen.getSplashScreen().close();
                }
                try {
                    objs.LoaderThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Axoloti.class.getName()).log(Level.SEVERE, null, ex);
                }

                System.out.println("Axoloti cmd line initialised");
                int exitCode = 0;
                if (cmdRunAllTest) {
                    exitCode = frame.runAllTests() ? 0 : -1;
                } else if (cmdRunPatchTest) {
                    exitCode = frame.runPatchTests() ? 0 : -1;
                } else if (cmdRunObjectTest) {
                    exitCode = frame.runObjectTests() ? 0 : -1;
                } else if (cmdRunFileTest) {
                    exitCode = frame.runFileTest(cmdFile) ? 0 : -1;
                } else if (cmdRunUpgrade) {
                    exitCode = frame.runFileUpgrade(cmdFile) ? 0 : -1;
                }
                System.out.println("Axoloti cmd line complete");
                System.exit(exitCode);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-2);
            }
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        MainFrame frame = new MainFrame(args);
                        frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
