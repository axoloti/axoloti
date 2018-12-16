package axoloti;

import axoloti.abstractui.PatchView;
import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.preferences.Preferences;
import axoloti.swingui.MainFrame;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class Tests {

    public List<String> runAllTests(boolean stopOnFirstFail) {
        List<String> failingPatches1 = runPatchTests(stopOnFirstFail);
        if (!failingPatches1.isEmpty() && stopOnFirstFail) {
            return failingPatches1;
        }
        List<String> failingPatches2 = runObjectTests(stopOnFirstFail);
        if (!failingPatches2.isEmpty() && stopOnFirstFail) {
            return failingPatches2;
        }
        List<String> failingPatches = new LinkedList<>();
        failingPatches.addAll(failingPatches1);
        failingPatches.addAll(failingPatches2);
        return failingPatches;
    }

    public List<String> runPatchTests(boolean stopOnFirstFail) {
        AxolotiLibrary fLib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            throw new IllegalStateException("no factory library?");
        }
        File testDirName = new File("test");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId());
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId() + "/patches/");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        return runTestDir(new File(fLib.getLocalLocation() + "patches"), "test/" + fLib.getId(), stopOnFirstFail);
    }

    public List<String> runObjectTests(boolean stopOnFirstFail) {
        AxolotiLibrary fLib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
        if (fLib == null) {
            throw new IllegalStateException("no factory library?");
        }
        File testDirName = new File("test");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId());
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        testDirName = new File("test/" + fLib.getId() + "/objects/");
        if (!testDirName.isDirectory()) {
            testDirName.mkdir();
        }
        return runTestDir(new File(fLib.getLocalLocation() + "objects"), "test/" + fLib.getId(), stopOnFirstFail);
    }

    public boolean runFileTest(String patchName) {
        List<String> failingPatches = runTestDir(new File(patchName), "", false);
        return failingPatches.isEmpty();
    }

    private List<String> runTestDir(File f, String targetPath, boolean stopOnFirstFail) {
        if (!f.exists()) {
            return Collections.emptyList();
        }
        if (f.isDirectory()) {
            targetPath += File.separator + f.getName();
            File testDirName = new File(targetPath);
            if (!testDirName.isDirectory()) {
                testDirName.mkdir();
            }
            File[] files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    File t = new File(f + File.separator + name);
                    if (t.isDirectory()) {
                        return true;
                    }

                    if (name.length() < 4) {
                        return false;
                    }
                    String extension = name.substring(name.length() - 4);
                    boolean b = (extension.equals(".axh") || extension.equals(".axp"));
                    return b;
                }
            });
            List<String> l = new LinkedList<>();
            for (File s : files) {
                List<String> ln = runTestDir(s, targetPath, stopOnFirstFail);
                l.addAll(ln);
                if (!ln.isEmpty() && stopOnFirstFail) {
                    return l;
                }
            }
            return l;
        } else {
            // it's a file...
            boolean result = runTestCompile(f, targetPath);
            if (result) {
                return Collections.emptyList();
            } else {
                List<String> l = new LinkedList<>();
                l.add(f.getPath());
                return l;
            }
        }
    }

    private boolean runTestCompile(File f, String destinationPath) {
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "testing {0}", f.getPath());

        try {
            PatchModel patchModel = PatchModel.open(f);
            String basename = f.getName();
            File testDirName = new File(destinationPath);
            if (!testDirName.isDirectory()) {
                testDirName.mkdir();
            }
//            String outFileName = destinationPath + File.separator + basename.substring(0, basename.lastIndexOf('.'));
//            PatchViewCodegen pvcg = patchController.writeCode(outFileName);
            PatchController patchController = patchModel.getController();
            PatchViewCodegen pvcg = patchController.writeCode();
            Thread.sleep(1000); // TODO: fix testing without sleep()..
            patchController.compile();
            Thread.sleep(5000);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "PATCH FAILED: " + f.getPath(), ex);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return false;
        }
    }

    public boolean runFileUpgrade(String patchName) {
        return runUpgradeDir(new File(patchName));
    }

    private boolean runUpgradeDir(File f) {
        if (!f.exists()) {
            return true;
        }
        if (f.isDirectory()) {
            File[] files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    File t = new File(f + File.separator + name);
                    if (t.isDirectory()) {
                        return true;
                    }

                    if (name.length() < 4) {
                        return false;
                    }
                    String extension = name.substring(name.length() - 4);
                    boolean b = (extension.equals(".axh") || extension.equals(".axp") || extension.equals(".axs"));
                    return b;
                }
            });
            for (File s : files) {
                if (!runUpgradeDir(s)) {
                    return false;
                }
            }
            return true;
        }

        return runUpgradeFile(f);
    }

    private boolean runUpgradeFile(File f) {
        Logger.getLogger(MainFrame.class.getName()).log(Level.INFO, "upgrading {0}", f.getPath());
        try {
            boolean status;
            PatchModel patchModel = PatchModel.open(f);
            PatchController patchController = patchModel.getController();
            PatchFrame patchFrame = new PatchFrame(patchModel);
            PatchView patchView = PatchViewFactory.patchViewFactory(patchModel);
            patchController.addView(patchFrame);
            status = patchModel.save(f);
            if (status == false) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "UPGRADING FAILED: {0}", f.getPath());
            }
            return status;
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, "UPGRADING FAILED: " + f.getPath(), ex);
            return false;
        }
    }
}
