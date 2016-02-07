/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axoloti;

import static axoloti.MainFrame.prefs;
import axoloti.dialogs.PatchBank;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author jtaelman
 */
public class FileUtils {

    public final static FileFilter axpFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".axp")) {
                return true;
            } else if (file.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Axoloti Patch";
        }
    };

    public final static FileFilter axhFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".axh")) {
                return true;
            } else if (file.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Axoloti Help";
        }
    };

    public final static FileFilter axsFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".axs")) {
                return true;
            } else if (file.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Axoloti Subpatch";
        }
    };

    public final static FileFilter axbFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".axb")) {
                return true;
            } else if (file.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Axoloti Patch Bank";
        }
    };

    public final static FileFilter axoFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.getName().endsWith(".axo")) {
                return true;
            } else if (file.isDirectory()) {
                return true;
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Axoloti Object";
        }
    };

    public static JFileChooser GetFileChooser() {
        JFileChooser fc = new JFileChooser(prefs.getCurrentFileDirectory());
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Axoloti Files", "axp", "axh", "axs", "axb"));
        fc.addChoosableFileFilter(axpFileFilter);
        fc.addChoosableFileFilter(axhFileFilter);
        fc.addChoosableFileFilter(axsFileFilter);
        fc.addChoosableFileFilter(axbFileFilter);
        return fc;
    }

    public static void Open(JFrame frame) {
        JFileChooser fc = GetFileChooser();
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            prefs.setCurrentFileDirectory(fc.getCurrentDirectory().getPath());
            prefs.SavePrefs();
            File f = fc.getSelectedFile();
            for (DocumentWindow dw : DocumentWindowList.GetList()) {
                if (f.equals(dw.getFile())) {
                    JFrame frame1 = dw.GetFrame();
                    frame1.setVisible(true);
                    frame1.setState(java.awt.Frame.NORMAL);
                    frame1.toFront();
                    return;
                }
            }
            if (axpFileFilter.accept(f)
                    || axsFileFilter.accept(f)
                    || axhFileFilter.accept(f)) {
                PatchGUI.OpenPatch(f);
                MainFrame.prefs.addRecentFile(f.getAbsolutePath());
            } else if (axbFileFilter.accept(f)) {
                PatchBank.OpenBank(f);
                MainFrame.prefs.addRecentFile(f.getAbsolutePath());
            }
        }
    }

}
