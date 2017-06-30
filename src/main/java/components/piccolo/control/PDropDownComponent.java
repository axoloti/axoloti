package components.piccolo.control;

import axoloti.Theme;
import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.PUtils;
import axoloti.piccolo.PatchPCanvas;
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

        public void SelectionChanged();
    }

    int SelectedIndex;
    List<String> Items;

    final private AttributeInstanceComboBox parent;

    public PDropDownComponent(List<String> Items, AttributeInstanceComboBox parent, IAxoObjectInstanceView axoObjectInstanceView) {
        super(axoObjectInstanceView);
        this.Items = Items;
        this.parent = parent;
        SelectedIndex = 0;

        FontRenderContext frc = new FontRenderContext(null, true, true);
        int maxWidth = 0;
        for (String s : Items) {
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
        if (Items.size() > 0) {
            g2.setFont(Constants.FONT);
            g2.drawString(Items.get(SelectedIndex), 4, 12);
        }
        PUtils.setRenderQualityToLow(g2);
    }

    public int getSelectedIndex() {
        return SelectedIndex;
    }

    public void setSelectedItem(String selection) {
        int index = Items.indexOf(selection);
        if ((SelectedIndex != index) && (index >= 0)) {
            SelectedIndex = index;
            for (DDCListener il : ddcListeners) {
                il.SelectionChanged();
            }
        }
        repaint();
    }

    public String getSelectedItem() {
        return Items.get(SelectedIndex);
    }

    public int getItemCount() {
        return Items.size();
    }

    public String getItemAt(int i) {
        return Items.get(i);
    }

    ArrayList<DDCListener> ddcListeners = new ArrayList<>();

    void doPopup(PInputEvent e) {
        if (isEnabled()) {
            JPopupMenu p = new JPopupMenu();
            for (String s : Items) {
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
