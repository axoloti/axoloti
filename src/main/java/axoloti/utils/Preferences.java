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
package axoloti.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class Preferences {

    @Element(required = false)
    String CurrentFileDirectory;
    @Element
    String ObjectSearchPath;
    @Element(required = false)
    String ComPortName;
    @Element(required = false)
    Integer PollInterval;
    @Element(required = false)
    Boolean MouseDialAngular;
    @Element(required = false)
    Boolean ExpertMode;

    final int minimumPollInterval = 20;

    public Preferences() {
        if (CurrentFileDirectory == null) {
            CurrentFileDirectory = "";
        }
        if (ObjectSearchPath == null) {
            ObjectSearchPath = "objects;patches/subpatch";
        }
        if (ComPortName == null) {
            ComPortName = "";
        }
        if (PollInterval == null) {
            PollInterval = 50;
        }
        if (MouseDialAngular == null) {
            MouseDialAngular = false;
        }
        if (ExpertMode == null) {
            ExpertMode = false;
        }
    }

    public String[] getObjectSearchPath() {
        return ObjectSearchPath.split(";");
    }

    public void setObjectSearchPath(String[] osp) {
        String p = "";
        for (String s : osp) {
            p += s + ";";
        }
        ObjectSearchPath = p;
    }

    public String getCurrentFileDirectory() {
        return CurrentFileDirectory;
    }

    public int getPollInterval() {
        if (PollInterval > minimumPollInterval) {
            return PollInterval;
        }
        return minimumPollInterval;
    }

    public void setPollInterval(int i) {
        if (i < minimumPollInterval) {
            i = minimumPollInterval;
        }
        PollInterval = i;
    }

    public void setCurrentFileDirectory(String CurrentFileDirectory) {
        if (this.CurrentFileDirectory.equals(CurrentFileDirectory)) {
            return;
        }
        this.CurrentFileDirectory = CurrentFileDirectory;
        SavePrefs();
    }

    static String GetPrefsFileLoc() {
        return ".preferences.xml";
    }

    public static Preferences LoadPreferences() {
        File p = new File(Preferences.GetPrefsFileLoc());
        if (p.exists()) {
            try {
                Serializer serializer = new Persister();
                Preferences prefs = serializer.read(Preferences.class, p);
                return prefs;
            } catch (Exception ex) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new Preferences();
    }

    public void SavePrefs() {
        Logger.getLogger(Preferences.class.getName()).log(Level.INFO, "Saving preferences...");
        Serializer serializer = new Persister();
        File f = new File(GetPrefsFileLoc());
        Logger.getLogger(Preferences.class.getName()).log(Level.INFO, "preferences path : " + f.getAbsolutePath());
        try {
            serializer.write(this, f);
        } catch (Exception ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getComPortName() {
        return ComPortName;
    }

    public void setComPortName(String ComPortName) {
        this.ComPortName = ComPortName;
    }

    public Boolean getMouseDialAngular() {
        return MouseDialAngular;
    }

    public void setMouseDialAngular(Boolean MouseDialAngular) {
        this.MouseDialAngular = MouseDialAngular;
    }

    public Boolean getExpertMode() {
        return ExpertMode;
    }

}
