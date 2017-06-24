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
package axoloti.outlets;

import axoloti.NetController;
import axoloti.PatchController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Johannes Taelman
 */
public class OutletInstancePopupMenu extends JPopupMenu {

    public OutletInstancePopupMenu(OutletInstanceController outletInstanceController) {
        super();

        PatchController pc = outletInstanceController.getParent().getParent();
        NetController nc = pc.getNetFromOutlet(outletInstanceController.getModel());

        JMenuItem itemDisconnect = new JMenuItem("Disconnect inlet");
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
                    outletInstanceController.addMetaUndo("disconnect inlet");
                    pc.disconnect(outletInstanceController.getModel());
                }
            });
        }
        if (nc.getModel().getDestinations().length + nc.getModel().getSources().length > 2) {
            add(itemDisconnect);
            JMenuItem itemDelete = new JMenuItem("Delete net");
            itemDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    outletInstanceController.addMetaUndo("delete net");
                    pc.delete(nc);
                }
            });
            add(itemDelete);            
        }
    }
}
