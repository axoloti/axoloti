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
package axoloti.swingui.patch.object.iolet;

import axoloti.mvc.FocusEdit;
import axoloti.patch.PatchController;
import axoloti.patch.net.Net;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class IoletInstancePopupMenu extends JPopupMenu {

    private String getDirectionLabel(boolean isSource) {
        return isSource ? "outlet" : "inlet";
    }

    public IoletInstancePopupMenu(IoletInstance ioletInstance, FocusEdit focusEdit) {
        super();
        initComponent(ioletInstance, focusEdit);
    }

    private void initComponent(IoletInstance ioletInstance, FocusEdit focusEdit) {
        PatchController pc = ioletInstance.getParent().getParent().getController();
        Net net = pc.getNetFromIolet(ioletInstance);
        boolean isSource = ioletInstance.isSource();

        JMenuItem itemDisconnect = new JMenuItem("Disconnect " + getDirectionLabel(isSource));
        if (net == null) {
            itemDisconnect.setEnabled(false);
            add(itemDisconnect);
            return;
        }
        if (net.getDestinations().size() + net.getSources().size() > 1) {
            add(itemDisconnect);
            itemDisconnect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ioletInstance.getController().addMetaUndo("disconnect " + getDirectionLabel(isSource), focusEdit);
                    if (ioletInstance instanceof InletInstance) {
                        pc.disconnect((InletInstance) ioletInstance);
                    } else if (ioletInstance instanceof OutletInstance) {
                        pc.disconnect((OutletInstance) ioletInstance);
                    } else {
                        throw new Error("iolet is inlet nor outlet???");
                    }
                }
            });
        }
        if (net.getDestinations().size() + net.getSources().size() > 2) {
            add(itemDisconnect);
            JMenuItem itemDelete = new JMenuItem("Delete net");
            itemDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ioletInstance.getController().addMetaUndo("delete net", focusEdit);
                    pc.delete(net);
                }
            });
            add(itemDelete);
        }
    }
}
