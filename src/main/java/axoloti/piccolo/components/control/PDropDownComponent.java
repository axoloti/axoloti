package axoloti.piccolo.components.control;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceComboBox;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.patch.PatchPCanvas;
import axoloti.preferences.Theme;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.util.PPaintContext;

public class PDropDownComponent extends PCtrlComponentAbstract {

    public interface DDCListener {
        void selectionChanged();
    }

    int selectedIndex;
    List<String> items;

    final private AttributeInstanceComboBox parent;

    public PDropDownComponent(List<String> items, AttributeInstanceComboBox parent, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.parent = parent;
        selectedIndex = 0;
        setItems(items);
    }

    public final void setItems(List<String> items) {
        this.items = items;
        FontRenderContext frc = new FontRenderContext(null, true, true);
        int maxWidth = 0;
        for (String s : items) {
            TextLayout tl = new TextLayout(s, Constants.FONT, frc);
            Rectangle2D r = tl.getBounds();
            if (maxWidth < r.getWidth()) {
                maxWidth = (int) r.getWidth();
            }
        }
        Dimension d = new Dimension(maxWidth + 10, 15);
        setSize(d);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(new Dimension(5000, 15));
    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();
        g2.setStroke(strokeThin);
        if (isEnabled()) {
            g2.setPaint(Theme.getCurrentTheme().Component_Secondary);
        } else {
            g2.setPaint(Theme.getCurrentTheme().Object_Default_Background);
        }
        g2.fillRect(1, 1, (int) getWidth() - 2, (int) getHeight() - 2);
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        g2.drawRect(1, 1, (int) getWidth() - 2, (int) getHeight() - 2);
        g2.setColor(Theme.getCurrentTheme().Component_Primary);
        final int rmargin = 5;
        final int htick = 3;
        int[] xp = new int[]{(int) getWidth() - rmargin - htick * 2, (int) getWidth() - rmargin, (int) getWidth() - rmargin - htick};
        final int vmargin = 5;
        int[] yp = new int[]{vmargin, vmargin, vmargin + htick * 2};
        g2.fillPolygon(xp, yp, 3);
        PUtils.setRenderQualityToHigh(g2);
        if (items.size() > 0) {
            g2.setFont(Constants.FONT);
            g2.drawString(items.get(selectedIndex), 4, 12);
        }
        PUtils.setRenderQualityToLow(g2);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedItem(String selection) {
        int index = items.indexOf(selection);
        if ((selectedIndex != index) && (index >= 0)) {
            selectedIndex = index;
            for (DDCListener il : ddcListeners) {
                il.selectionChanged();
            }
        }
        repaint();
    }

    public String getSelectedItem() {
        return items.get(selectedIndex);
    }

    public int getItemCount() {
        return items.size();
    }

    public String getItemAt(int i) {
        return items.get(i);
    }

    ArrayList<DDCListener> ddcListeners = new ArrayList<>();

    void doPopup(PInputEvent e) {
        if (isEnabled()) {
            JPopupMenu p = new JPopupMenu();
            for (String s : items) {
                JMenuItem mi = p.add(s);
                mi.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setSelectedItem(e.getActionCommand());
                        getPatchPCanvas().clearPopupParent();
                    }
                });
            }

            PatchPCanvas canvas = getPatchPCanvas();
            if (!canvas.isPopupVisible()) {
                Point popupLocation = PUtils.getPopupLocation(e);
                p.show(axoObjectInstanceView.getCanvas(),
                        popupLocation.x,
                        popupLocation.y);
                canvas.setPopupParent(this);
            } else {
                canvas.clearPopupParent();
            }
        }
    }

    public void addItemListener(DDCListener itemListener) {
        ddcListeners.add(itemListener);
    }

    @Override
    public void mousePressed(PInputEvent e) {
        doPopup(e);
    }

    @Override
    public void setValue(double v) {
        throw new RuntimeException("setValue() unsupported");
    }

    @Override
    public double getValue() {
        throw new RuntimeException("getValue() unsupported");
    }

    protected PatchPCanvas getPatchPCanvas() {
        return (PatchPCanvas) axoObjectInstanceView.getCanvas();
    }
}
