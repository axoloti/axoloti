/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.Theme;
import axoloti.atom.AtomInstance;
import axoloti.datatypes.DataType;
import axoloti.iolet.IoletAbstract;
import axoloti.object.AxoObjectInstance;
import components.LabelComponent;
import components.SignalMetaDataIcon;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPopupMenu;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "source")
public class OutletInstance<T extends Outlet> extends IoletAbstract implements Comparable<OutletInstance>, AtomInstance<T> {

    @Attribute(name = "outlet", required = false)
    public String outletname;

    private final T outlet;

    public String getOutletname() {
        if (outletname != null) {
            return outletname;
        } else {
            int sepIndex = name.lastIndexOf(' ');
            return name.substring(sepIndex + 1);
        }
    }

    @Override
    public T GetDefinition() {
        return outlet;
    }

    public OutletInstance() {
        this.outlet = null;
        this.axoObj = null;
    }

    public OutletInstance(T outlet, AxoObjectInstance axoObj) {
        this.outlet = outlet;
        this.axoObj = axoObj;
        RefreshName();
        PostConstructor();
    }

    public final void RefreshName() {
        name = axoObj.getInstanceName() + " " + outlet.name;
        objname = axoObj.getInstanceName();
        outletname = outlet.name;
        name = null;
    }

    public DataType GetDataType() {
        return outlet.getDatatype();
    }

    public String GetLabel() {
        return outlet.name;
    }

    public String GetCName() {
        return outlet.GetCName();
    }

    public final void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(Box.createHorizontalGlue());
        if (axoObj.getType().GetOutlets().size() > 1) {
            add(new LabelComponent(outlet.name));
            add(Box.createHorizontalStrut(2));
        }
        add(new SignalMetaDataIcon(outlet.GetSignalMetaData()));
        jack = new components.JackOutputComponent(this);
        jack.setForeground(outlet.getDatatype().GetColor());
        add(jack);
        setToolTipText(outlet.description);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public JPopupMenu getPopup() {
        return new OutletInstancePopupMenu(this);
    }

    @Override
    public int compareTo(OutletInstance t) {
        return axoObj.compareTo(t.axoObj);
    }
}
