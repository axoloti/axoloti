package axoloti.swingui.patch.object;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Theme;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.components.PopupIcon;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.object.inlet.InletInstanceView;
import axoloti.swingui.patch.object.outlet.OutletInstanceView;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class AxoObjectInstanceViewZombie extends AxoObjectInstanceViewAbstract {

    public AxoObjectInstanceViewZombie(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    public void PostConstructor() {
        super.PostConstructor();
        LabelComponent idlbl = new LabelComponent("zombie"); // FIXME: getController().getModel().typeName);
        idlbl.setAlignmentX(LEFT_ALIGNMENT);
        idlbl.setForeground(Theme.getCurrentTheme().Object_TitleBar_Foreground);

        final PopupIcon popupIcon = new PopupIcon();
        popupIcon.setPopupIconListener(
                new PopupIcon.PopupIconListener() {
                    @Override
                    public void ShowPopup() {
                        JPopupMenu popup = CreatePopupMenu();
                        popupIcon.add(popup);
                        popup.show(popupIcon,
                                0, popupIcon.getHeight());
                    }
                });
        Titlebar.add(popupIcon);
        Titlebar.add(idlbl);

        Titlebar.setToolTipText("<html>" + "Unresolved object!");

        Titlebar.setAlignmentX(LEFT_ALIGNMENT);
        add(Titlebar);

        setOpaque(true);
        setBackground(Theme.getCurrentTheme().Object_Zombie_Background);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        InstanceLabel = new LabelComponent(getController().getModel().getInstanceName());
        InstanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        InstanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        add(InstanceLabel);
        setLocation(getController().getModel().getX(), getController().getModel().getY());

        resizeToGrid();
    }

    @Override
    JPopupMenu CreatePopupMenu() {
        JPopupMenu popup = super.CreatePopupMenu();
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

    @Override
    public void showInstanceName(String s) {
        super.showInstanceName(s);
        resizeToGrid();
        repaint();
    }

    private Map<InletInstance, IIoletInstanceView> inletInstanceViews = new HashMap<>();
    private Map<OutletInstance, IIoletInstanceView> outletInstanceViews = new HashMap<>();

    @Override
    public List<IIoletInstanceView> getInletInstanceViews() {
        return null; //inletInstanceViews.values();
    }

    @Override
    public List<IIoletInstanceView> getOutletInstanceViews() {
        return null; //outletInstanceViews.values();
    }

    @Override
    public void addInletInstanceView(IIoletInstanceView view) {
        inletInstanceViews.put((InletInstance) view.getModel(), view);
        add((InletInstanceView) view);
    }

    @Override
    public void addOutletInstanceView(IIoletInstanceView view) {
        outletInstanceViews.put((OutletInstance) view.getModel(), view);
        add((OutletInstanceView) view);
    }

    @Override
    public boolean isZombie() {
        return true;
    }

    @Override
    public IIoletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return inletInstanceViews.get(inletInstance);
    }

    @Override
    public IIoletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
        return outletInstanceViews.get(outletInstance);
    }
}
