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
package transitions;

import axoloti.MainFrame;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjects;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
@Deprecated
public class TransitionManager {

    Transitions transitions;

    String filename = "objects/transitions.xml";

    public void CreateTransitions() {
        transitions = new Transitions();
        AxoObjects oldObjects = new AxoObjects();
        oldObjects.LoadAxoObjects("obsolete_objects");
        for (AxoObjectAbstract oOld : oldObjects.ObjectList) {
            AxoObjectAbstract oNew = MainFrame.axoObjects.GetAxoObjectFromSHA(oOld.getSHA());
            if (oNew != null) {
                if (!oOld.id.equals(oNew.id)) {
                    //transitions.transitions.put(oOld.id, oNew.id);
                    NameTransition tr = new NameTransition(oNew.id);
                    transitions.nametransitions.put(oOld.shortId, tr);
                }
            } else {
                System.out.println(oOld.shortId);
            }
        }
        Serializer serializer = new Persister();
        try {
            serializer.write(transitions, new File(filename));
        } catch (Exception ex) {
            Logger.getLogger(TransitionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void LoadTransitions() {
        Serializer serializer = new Persister();
        try {
            transitions = serializer.read(Transitions.class, new File(filename));
        } catch (Exception ex) {
            Logger.getLogger(TransitionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AxoObjectAbstract GetObjectFromName(String n) {
        NameTransition ntr = transitions.nametransitions.get(n);
        if (ntr == null) {
            return null;
        }
        for (AxoObjectAbstract o : MainFrame.axoObjects.ObjectList) {
            if (o.id.equals(ntr.NewName)) {
                return o;
            }
        }
        return null;
    }

    public AxoObjectAbstract GetObjectFromSha(String sha) {
        ShaTransition str = transitions.shatransitions.get(sha);
        if (str == null) {
            return null;
        }
        AxoObjectAbstract r = MainFrame.axoObjects.GetAxoObjectFromSHA(str.NewSha);
        return r;
    }
}
