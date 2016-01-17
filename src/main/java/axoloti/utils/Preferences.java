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

import axoloti.MainFrame;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
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
    @Deprecated
    @Element(required = false)
    String ComPortName;
    @Element(required = false)
    Integer PollInterval;
    @Element(required = false)
    Boolean MouseDialAngular;
    @Element(required = false)
    Boolean ExpertMode;
    @ElementList(required = false)
    ArrayList<String> recentFiles = new ArrayList<String>();
    
    @Deprecated
    @Element(required = false)
    String MidiInputDevice;
    @Element(required = false)
    String RuntimeDir;
    @Element(required = false)
    String FirmwareDir;
    @Element(required = false)
    String FavouriteDir;
    @Element(required = false)
    String ControllerObject;
    @Element(required = false)
    Boolean ControllerEnabled;
    
    @ElementMap(required=false, entry="Boards", key="cpuid", attribute=true, inline=true)
    HashMap<String,String> BoardNames;
    
    boolean isDirty = false;

    final int nRecentFiles = 8;

    final int minimumPollInterval = 20;

    public Preferences() {
        if (CurrentFileDirectory == null) {
            CurrentFileDirectory = "";
        }
        if (ObjectSearchPath == null) {
            ObjectSearchPath = "objects";
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
        if (FavouriteDir == null) {
            FavouriteDir = "";
        }
        if (BoardNames == null) {
            BoardNames = new HashMap<String, String>();
        }
        if (ControllerObject == null) {
            ControllerObject = "";
            ControllerEnabled = false;
        }
    }

    void SetDirty() {
        isDirty = true;
    }

    void ClearDirty() {
        isDirty = false;
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
        SetDirty();
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
        SetDirty();
    }

    public void setCurrentFileDirectory(String CurrentFileDirectory) {
        if (this.CurrentFileDirectory.equals(CurrentFileDirectory)) {
            return;
        }
        this.CurrentFileDirectory = CurrentFileDirectory;
        SavePrefs();
        SetDirty();
    }

    static String GetPrefsFileLoc() {
        return System.getProperty(axoloti.Axoloti.HOME_DIR)+File.separator+"axoloti.prefs";
    }
    
    private static Preferences singleton;

    public static Preferences LoadPreferences() {
        if (singleton == null) {
            File p = new File(Preferences.GetPrefsFileLoc());
            if (p.exists()) {
                try {
                    Serializer serializer = new Persister();
                    Preferences prefs = serializer.read(Preferences.class, p);
                    singleton = prefs;
                    if (prefs.RuntimeDir == null ) {
                        prefs.RuntimeDir = System.getProperty(axoloti.Axoloti.RUNTIME_DIR);
                        prefs.SetDirty();
                    } else {
                        System.setProperty(axoloti.Axoloti.RUNTIME_DIR, prefs.RuntimeDir);
                    }
                    if (prefs.FirmwareDir == null ) {
                        prefs.FirmwareDir = System.getProperty(axoloti.Axoloti.FIRMWARE_DIR);
                        prefs.SetDirty();
                    } else {
                        System.setProperty(axoloti.Axoloti.FIRMWARE_DIR, prefs.FirmwareDir);
                    }
                    singleton.MidiInputDevice = null; // clear it out for the future
                } catch (Exception ex) {
                    Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                singleton = new Preferences();
            }
        }
        return singleton;
    }

    public void SavePrefs() {
        Logger.getLogger(Preferences.class.getName()).log(Level.INFO, "Saving preferences...");
        Serializer serializer = new Persister();
        File f = new File(GetPrefsFileLoc());
        Logger.getLogger(Preferences.class.getName()).log(Level.INFO, "preferences path : {0}", f.getAbsolutePath());
        try {
            serializer.write(this, f);
        } catch (Exception ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClearDirty();
    }

    @Deprecated
    public String getComPortName() {
        return ComPortName;
    }

    @Deprecated
    public void setComPortName(String ComPortName) {
    }

    public Boolean getMouseDialAngular() {
        return MouseDialAngular;
    }

    public void setMouseDialAngular(boolean MouseDialAngular) {
        if (this.MouseDialAngular == MouseDialAngular) {
            return;
        }
        this.MouseDialAngular = MouseDialAngular;
        SetDirty();
    }

    public Boolean getExpertMode() {
        return ExpertMode;
    }

    public ArrayList<String> getRecentFiles() {
        return recentFiles;
    }

    public void addRecentFile(String filename) {
        for (String r : recentFiles) {
            if (r.equals(filename)) {
                return;
            }
        }
        if (recentFiles.size() == nRecentFiles) {
            recentFiles.remove(0);
        }
        recentFiles.add(filename);
        SetDirty();
    }

    public String getFavouriteDir() {
        return FavouriteDir;
    }

    public void setFavouriteDir(String favouriteDir) {
        if (this.FavouriteDir.equals(favouriteDir)) {
            return;
        }
        this.FavouriteDir = favouriteDir;
        SetDirty();
    }

    public void SetFirmwareDir(String dir) {
        FirmwareDir = dir;
        System.setProperty(axoloti.Axoloti.FIRMWARE_DIR, dir);
    }

    public void SetRuntimeDir(String dir) {
        RuntimeDir = dir;
        System.setProperty(axoloti.Axoloti.RUNTIME_DIR, dir);
    }

    public String getBoardName(String cpu) {
        if(cpu==null) return null;
        if (BoardNames.containsKey(cpu)) 
            return BoardNames.get(cpu);
        return null;
    }

    public void setBoardName(String cpuid, String name) {
        if (name == null) {
            BoardNames.remove(cpuid);
        }
        else {
            BoardNames.put(cpuid, name);
        }
        SetDirty();
    }

    public String getControllerObject() {
        return ControllerObject;
    }
    
    public void setControllerObject(String s) {
        ControllerObject = s;
    }

    public void setControllerEnabled(boolean b) {
        ControllerEnabled = b;
    }
    public boolean isControllerEnabled() {
        return ControllerEnabled;
    }
}
