/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.abstractui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class DocumentWindowList {

    private DocumentWindowList() {
    }

    private final static List<DocumentWindow> list = new LinkedList<DocumentWindow>();

    static public void registerWindow(DocumentWindow w) {
        if (!list.contains(w)) {
            list.add(w);
        }
    }

    static public void unregisterWindow(DocumentWindow w) {
        list.remove(w);
    }

    static public List<DocumentWindow> getList() {
        return Collections.unmodifiableList(list);
    }

    static public boolean askCloseAll() {
        List<DocumentWindow> clone = new LinkedList<>(list);
        for (DocumentWindow dw : clone) {
            if (dw.askClose()) {
                return true;
            }
        }
        return false;
    }
}
