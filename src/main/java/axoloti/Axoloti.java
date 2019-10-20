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

import axoloti.job.IJobContext;
import axoloti.job.JobContext;
import axoloti.objectlibrary.AxoObjects;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.preferences.Preferences;
import axoloti.shell.TestEnv;
import axoloti.swingui.MainFrame;
import axoloti.target.TargetModel;
import axoloti.utils.OSDetect;
import java.awt.EventQueue;
import java.awt.SplashScreen;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Johannes Taelman
 */
public class Axoloti {

    public final static String HOME_DIR = "axoloti_home";
    public final static String RELEASE_DIR = "axoloti_release";
    public final static String API_DIR = "axoloti_api";
    public final static String ENV_DIR = "axoloti_env";

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            initProperties();

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (System.getProperty("os.name").contains("OS X")) {
                //System.setProperty("apple.laf.useScreenMenuBar", "true");
                //
                // TODO: OSX: useScreenMenuBar
                // Unfortunately, dynamically populated menus
                // (like the Window or recent files menu)
                // do not work with useScreenMenuBar...
                // They show up but no ActionListener is called.
                // useScreenMenuBar is disabled until a fix is found
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

    static void buildEnvironment(String var, String def) {
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

    static boolean getTestDir(String var) {
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
    private static Boolean cacheDeveloper = null;

    public static boolean isDeveloper() {
        if (cacheDeveloper != null) {
            return cacheDeveloper;
        }
        String dirRelease = System.getProperty(RELEASE_DIR);
        File f = new File(dirRelease + File.separator + ".git");
        cacheDeveloper = f.exists();
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

    public static String getFirmwareFilename() {
        return System.getProperty(Axoloti.RELEASE_DIR) + "/firmware/build/axoloti.bin";
    }

    public static String getHomeDir() {
        return System.getProperty(axoloti.Axoloti.HOME_DIR);
    }

    public static String getBuildDir() {
        return System.getProperty(axoloti.Axoloti.HOME_DIR) + "/build";
    }

    public static String getReleaseDir() {
        return System.getProperty(axoloti.Axoloti.RELEASE_DIR);
    }

    public static String getAPIDir() {
        return System.getProperty(axoloti.Axoloti.API_DIR);
    }

    public static String getEnvDir() {
        return System.getProperty(axoloti.Axoloti.ENV_DIR);
    }

    public static void initProperties() throws URISyntaxException, IOException {
        String curDir = System.getProperty("user.dir");
        File jarFile = new File(Axoloti.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String jarDir = jarFile.getParentFile().getCanonicalPath();
        String defaultHome = curDir;
        String defaultRuntime = curDir;
        String defaultRelease = curDir;
        boolean versionedHome = true;

        File git = new File("." + File.separator + ".git");
        if (git.exists()) {
            // developer using git, assume they want everything local dir
            System.out.println("defaulting to developer defaults, can be overridden");
            defaultHome = ".";
            defaultRuntime = ".";
        } else {
            String docDir;
            if (null != OSDetect.getOS()) {
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
                }
            } else {
                docDir = System.getenv("HOME") + "/";
                defaultRuntime = System.getenv("HOME") + "/axoloti_runtime";
            }

            String verHomeDirname = "axoloti-" + Version.AXOLOTI_SHORT_VERSION;
            File versionHome = new File(docDir + verHomeDirname);
            if (versionHome.exists() | versionedHome) {
                defaultHome = docDir + verHomeDirname;
                versionedHome = true;
            } else {
                defaultHome = docDir + "axoloti";
            }
        }

        buildEnvironment(HOME_DIR, defaultHome);
        File homedir = new File(System.getProperty(HOME_DIR));
        if (!homedir.exists()) {
            homedir.mkdir();
        }

        File buildir = new File(System.getProperty(HOME_DIR) + File.separator + "build");
        if (!buildir.exists()) {
            buildir.mkdir();
        }
        if (!getTestDir(HOME_DIR)) {
            System.err.println("Home directory is invalid");
        }
        checkFailSafeModeActive(); // do this as as possible after home dir setup

        buildEnvironment(RELEASE_DIR, defaultRelease);
        if (!getTestDir(RELEASE_DIR)) {
            System.err.println("Release directory is invalid");
        }
        buildEnvironment(API_DIR, System.getProperty(RELEASE_DIR) + File.separator + "api");
        if (!getTestDir(API_DIR)) {
            System.err.println(API_DIR + ": directory is invalid");
        }
        buildEnvironment(ENV_DIR, System.getProperty(RELEASE_DIR) + File.separator + "env");
        if (!getTestDir(ENV_DIR)) {
            System.err.println(ENV_DIR + ": ENV directory is invalid");
        }

        Preferences prefs = Preferences.getPreferences();
        if (versionedHome) {
            AxolotiLibrary lib = prefs.getLibrary(AxolotiLibrary.FACTORY_ID);
            if (lib != null) {
                File locdir = new File(lib.getLocalLocation());
                File verdir = new File(System.getProperty(axoloti.Axoloti.HOME_DIR) + File.separator + "axoloti-factory" + File.separator);
                if (!locdir.getCanonicalPath().equals(verdir.getCanonicalPath())) {
                    System.out.println("Using versioned home, will reset libraries");
                    prefs.resetLibraries(true);
                }
            }
        }

        System.out.println("Axoloti Directories:\n"
                + "Current = " + curDir + "\n"
                + "Jar = " + jarDir + "\n"
                + "Release = " + System.getProperty(RELEASE_DIR) + "\n"
                + "AxolotiHome = " + System.getProperty(HOME_DIR) + "\n"
        );
    }

    private static void handleCommandLine(final String args[]) {
        boolean cmdLineOnly = false;
        boolean cmdRunAllTest = false;
        boolean cmdRunPatchTest = false;
        boolean cmdRunObjectTest = false;
        boolean cmdRunFileTest = false;
        boolean cmdRunUpgrade = false;
        boolean stopOnFirstFail = false;
        String cmdFile = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("-exitOnFirstFail")) {
                stopOnFirstFail = true;
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
                MainFrame frame = new MainFrame(args, TargetModel.getTargetModel());
                Tests tests = new Tests();
                AxoObjects objs = new AxoObjects();
                IJobContext progress = new JobContext();
                AxoObjects.loadAxoObjects(progress);
                if (SplashScreen.getSplashScreen() != null) {
                    SplashScreen.getSplashScreen().close();
                }

                System.out.println("Axoloti cmd line initialised");
                int exitCode = 0;
                List<String> failedPatches = null;
                if (cmdRunAllTest) {
                    failedPatches = tests.runAllTests(stopOnFirstFail);
                    exitCode = failedPatches.isEmpty() ? 0 : -1;
                } else if (cmdRunPatchTest) {
                    failedPatches = tests.runPatchTests(stopOnFirstFail);
                    exitCode = failedPatches.isEmpty() ? 0 : -1;
                } else if (cmdRunObjectTest) {
                    failedPatches = tests.runObjectTests(stopOnFirstFail);
                    exitCode = failedPatches.isEmpty() ? 0 : -1;
                } else if (cmdRunFileTest) {
                    exitCode = tests.runFileTest(cmdFile) ? 0 : -1;
                } else if (cmdRunUpgrade) {
                    exitCode = tests.runFileUpgrade(cmdFile) ? 0 : -1;
                }

                if (failedPatches != null && !failedPatches.isEmpty()) {
                    System.out.println("List of failing patches:");
                    for (String patchname : failedPatches) {
                        System.out.println(patchname);
                    }
                }

                System.out.println("Axoloti cmd line complete");
                System.exit(exitCode);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-2);
            }
        } else {
            EventQueue.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame(args, TargetModel.getTargetModel());
                    frame.setVisible(true);
                    TestEnv.test_env();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
