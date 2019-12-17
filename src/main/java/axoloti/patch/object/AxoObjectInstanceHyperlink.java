/**
 * Copyright (C) 2015 Johannes Taelman
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
package axoloti.patch.object;

import axoloti.abstractui.PatchView;
import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import java.awt.Desktop;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "hyperlink")
public class AxoObjectInstanceHyperlink extends AxoObjectInstance0 {

    AxoObjectInstanceHyperlink() {
    }

    AxoObjectInstanceHyperlink(IAxoObject obj, PatchModel patch1, String InstanceName1, Point location) {
        super(obj, patch1, InstanceName1, location);
    }

    public void launch() {
        String link = getInstanceName();
        if (link.startsWith("www.")
                || link.startsWith("http://")
                || link.startsWith("https://")) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (IOException | URISyntaxException ex) {
                Logger.getLogger(AxoObjectInstanceHyperlink.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (link.endsWith(".axp") || link.endsWith(".axh") || link.endsWith(".axs")) {
            String s = getParent().getFileNamePath();
            s = s.substring(0, s.lastIndexOf(File.separatorChar));
            File f = new File(s + File.separatorChar + link);
            if (f.canRead()) {
                PatchView.openPatch(f);
            } else {
                Logger.getLogger(AxoObjectInstanceHyperlink.class.getName()).log(Level.SEVERE, "can''t read file {0}", f.getAbsolutePath());
            }
        }
    }

    @Override
    public String getCInstanceName() {
        return "";
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }
    @Override
    public void dispose() {
    }

}
