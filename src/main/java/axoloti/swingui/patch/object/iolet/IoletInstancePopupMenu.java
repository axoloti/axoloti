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

import axoloti.patch.net.NetController;
import axoloti.patch.PatchController;
import axoloti.patch.object.iolet.IoletInstanceController;
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

    public IoletInstancePopupMenu(IoletInstanceController ioletInstanceController) {
        super();

        PatchController pc = ioletInstanceController.getParent().getParent();
        NetController nc = pc.getNetFromIolet(ioletInstanceController.getModel());
        boolean isSource = ioletInstanceController.getModel().isSource();
        

        JMenuItem itemDisconnect = new JMenuItem("Disconnect " + getDirectionLabel(isSource));
        if (nc == null) {
            itemDisconnect.setEnabled(false);
            add(itemDisconnect);
            return;
        }
        if (nc.getModel().getDestinations().length + nc.getModel().getSources().length > 1) {
            add(itemDisconnect);
            itemDisconnect.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ioletInstanceController.addMetaUndo("disconnect " + getDirectionLabel(isSource));
                    pc.disconnect(ioletInstanceController.getModel());
                }
            });
        }
        if (nc.getModel().getDestinations().length + nc.getModel().getSources().length > 2) {
            add(itemDisconnect);
            JMenuItem itemDelete = new JMenuItem("Delete net");
            itemDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    ioletInstanceController.addMetaUndo("delete net");
                    pc.delete(nc);
                }
            });
            add(itemDelete);
        }
    }
}
