package axoloti.swingui.patch.object;

import axoloti.patch.object.ObjectInstanceController;
import axoloti.preferences.Theme;
import axoloti.swingui.patch.PatchViewSwing;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class AxoObjectInstanceViewZombie extends AxoObjectInstanceView {

    public AxoObjectInstanceViewZombie(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
        initComponents3();
    }

    // TODO: zombie objects do not adapt their size to inlets/outlets???

    private void initComponents3() {
        setBackground(Theme.getCurrentTheme().Object_Zombie_Background);
        p_ioletViews.setBackground(Theme.getCurrentTheme().Object_Zombie_Background);
        p_inletViews.setBackground(Theme.getCurrentTheme().Object_Zombie_Background);
        p_outletViews.setBackground(Theme.getCurrentTheme().Object_Zombie_Background);
    }

    @Override
    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem popm_substitute = new JMenuItem("replace");
        popm_substitute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getPatchView().ShowClassSelector(AxoObjectInstanceViewZombie.this.getLocation(), AxoObjectInstanceViewZombie.this, null);
            }
        });
        popup.add(popm_substitute);
        JMenuItem popm_editInstanceName = new JMenuItem("edit instance name");
        popm_editInstanceName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addInstanceNameEditor();
            }
        });
        popup.add(popm_editInstanceName);
        return popup;
    }

}
