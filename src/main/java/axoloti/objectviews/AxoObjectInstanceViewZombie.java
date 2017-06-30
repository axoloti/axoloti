package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.Theme;
import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceView;
import axoloti.object.ObjectInstanceController;
import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceView;
import components.LabelComponent;
import components.PopupIcon;
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

public class AxoObjectInstanceViewZombie extends AxoObjectInstanceViewAbstract {

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

    private Map<InletInstance, IInletInstanceView> inletInstanceViews = new HashMap<>();
    private Map<OutletInstance, IOutletInstanceView> outletInstanceViews = new HashMap<>();

    @Override
    public List<IInletInstanceView> getInletInstanceViews() {
        return null; //inletInstanceViews.values();
    }

    @Override
    public List<IOutletInstanceView> getOutletInstanceViews() {
        return null; //outletInstanceViews.values();
    }

    @Override
    public void addInletInstanceView(IInletInstanceView view) {
        inletInstanceViews.put(view.getModel(), view);
        add((InletInstanceView) view);
    }

    @Override
    public void addOutletInstanceView(IOutletInstanceView view) {
        outletInstanceViews.put(view.getModel(), view);
        add((OutletInstanceView) view);
    }

    @Override
    public boolean isZombie() {
        return true;
    }

    @Override
    public IInletInstanceView getInletInstanceView(InletInstance inletInstance) {
        return inletInstanceViews.get(inletInstance);
    }

    @Override
    public IOutletInstanceView getOutletInstanceView(OutletInstance outletInstance) {
        return outletInstanceViews.get(outletInstance);
    }
}
