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
package axoloti.object;

import axoloti.MainFrame;
import axoloti.Patch;
import axoloti.PatchFrame;
import axoloti.PatchGUI;
import java.awt.Rectangle;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectFromPatch extends AxoObject {

    Patch p;
    PatchGUI pg;
    PatchFrame pf;
    File f;

    public AxoObjectFromPatch(File f) {
        this.f = f;
        Serializer serializer = new Persister();
        try {
            p = serializer.read(Patch.class, f);
            p.setFileNamePath(f.getAbsolutePath());
            p.PostContructor();
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
        shortId = f.getName().substring(0, f.getName().lastIndexOf("."));
        sPath = f.getAbsolutePath();
        UpdateObject();
        MainFrame.axoObjects.ObjectList.add(this);
        // strip file extension
    }

    final public void UpdateObject() {
        AxoObject o;
        if (pg == null) {
            o = p.GenerateAxoObj(new AxoObject());
        } else {
            o = pg.GenerateAxoObj(new AxoObject());
        }
        attributes = o.attributes;
        depends = o.depends;
        displays = o.displays;
        id = f.getName().substring(0, f.getName().length() - 4);
        includes = o.includes;
        inlets = o.inlets;
        outlets = o.outlets;
        params = o.params;
        sAuthor = o.sAuthor;
        sDescription = o.sDescription;
        sDisposeCode = o.sDisposeCode;
        sInitCode = o.sInitCode;
        sKRateCode = o.sKRateCode;
        sLicense = o.sLicense;
        sLocalData = o.sLocalData;
        sMidiCode = o.sMidiCode;
        sSRateCode = o.sSRateCode;
        helpPatch = o.helpPatch;

        FireObjectModified(this);
    }

    @Override
    public void OpenEditor(Rectangle editorBounds, Integer editorActiveTabIndex) {
        if (pg == null) {
            Strategy strategy = new AnnotationStrategy();
            Serializer serializer = new Persister(strategy);
            try {
                pg = serializer.read(PatchGUI.class, f);
                pf = new PatchFrame((PatchGUI) pg, MainFrame.mainframe.getQcmdprocessor());
                pg.setFileNamePath(f.getPath());
                pg.PostContructor();
                pg.ObjEditor = this;
            } catch (Exception ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (pf == null) {
            pf = new PatchFrame((PatchGUI) pg, MainFrame.mainframe.getQcmdprocessor());
            pg.setFileNamePath(id);
            pg.PostContructor();
        }
        pf.setState(java.awt.Frame.NORMAL);
        pf.setVisible(true);
    }

}
