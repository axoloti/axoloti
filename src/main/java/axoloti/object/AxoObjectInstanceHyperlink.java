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
package axoloti.object;

import axoloti.Patch;
import axoloti.PatchGUI;
import components.LabelComponent;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.PulseButtonComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "hyperlink")
public class AxoObjectInstanceHyperlink extends AxoObjectInstanceAbstract {

    public AxoObjectInstanceHyperlink() {
    }

    public AxoObjectInstanceHyperlink(AxoObjectAbstract type, Patch patch1, String InstanceName1, Point location) {
        super(type, patch1, InstanceName1, location);
    }

    @Override
    public boolean IsLocked() {
        return false;
    }

    private PulseButtonComponent button;

    void Lauch() {
        String link = getInstanceName();
        if (link.startsWith("www.")
                || link.startsWith("http://")
                || link.startsWith("https://")) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (IOException ex) {
                Logger.getLogger(AxoObjectInstanceHyperlink.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(AxoObjectInstanceHyperlink.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (link.endsWith(".axp") || link.endsWith(".axh") || link.endsWith(".axs")) {
            String s = getPatch().getFileNamePath();
            s = s.substring(0, s.lastIndexOf(File.separatorChar));
            File f = new File(s + File.separatorChar + link);
            if (f.canRead()) {
                PatchGUI.OpenPatch(f);
            } else {
                Logger.getLogger(AxoObjectInstanceHyperlink.class.getName()).log(Level.SEVERE, "can''t read file {0}", f.getAbsolutePath());
            }
        }
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        button = new PulseButtonComponent();
        button.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                if (e.getValue() == 1.0) {
                    Lauch();
                }
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
            }
        });
        add(button);
        add(Box.createHorizontalStrut(5));
        InstanceLabel = new LabelComponent(getInstanceName());
        InstanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        InstanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                AxoObjectInstanceHyperlink.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                AxoObjectInstanceHyperlink.this.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        InstanceLabel.addMouseMotionListener(this);
        add(InstanceLabel);
        setLocation(x, y);

        resizeToGrid();
    }

    @Override
    public void setInstanceName(String s) {
        super.setInstanceName(s);
        resizeToGrid();
    }

    @Override
    public String getCInstanceName() {
        return "";
    }
}
