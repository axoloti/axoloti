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
package axoloti;

import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author jtaelman
 */
public class SDCardInfo {

    private final ArrayList<SDFileInfo> files = new ArrayList<SDFileInfo>();
    boolean available = false;
    int clusters = 0;
    int clustersize = 0;
    int sectorsize = 0;

    boolean busy = false;

    private static SDCardInfo instance = null;

    protected SDCardInfo() {
    }

    public synchronized static SDCardInfo getInstance() {
        if (instance == null) {
            instance = new SDCardInfo();
        }
        return instance;
    }

    public synchronized void SetInfo(int clusters, int clustersize, int sectorsize) {
        this.clusters = clusters;
        this.clustersize = clustersize;
        this.sectorsize = sectorsize;
        files.clear();
        busy = true;
        if ((MainFrame.mainframe != null) && (MainFrame.mainframe.filemanager != null)) {
            MainFrame.mainframe.filemanager.refresh();
        }
    }

    public synchronized ArrayList<SDFileInfo> getFiles() {
        return (ArrayList<SDFileInfo>) files.clone();
    }

    public int getClusters() {
        return clusters;
    }

    public int getClustersize() {
        return clustersize;
    }

    public int getSectorsize() {
        return sectorsize;
    }

    public void AddFile(String fname, int size, int timestamp) {
        int DY = 1980 + ((timestamp & 0x0FE00) >> 9);
        int DM = ((timestamp & 0x01E0) >> 5);
        int DD = (timestamp & 0x001F);
        int TH = (int) ((timestamp & 0x0F8000000l) >> 27);
        int TM = (timestamp & 0x07E00000) >> 21;
        int TS = (timestamp & 0x001F0000) >> 15;
        Calendar date = Calendar.getInstance();
        date.set(DY, DM - 1, DD, TH, TM, TS);
        AddFile(fname, size, date);
    }

    public synchronized void AddFile(String fname, int size, Calendar date) {
        if (fname.lastIndexOf(0) > 0) {
            fname = fname.substring(0, fname.lastIndexOf(0));
        }
        if (fname.equals("/")) {
            busy = false;
            return;
        }
        SDFileInfo sdf = null;
        for (SDFileInfo f : files) {
            if (f.filename.equalsIgnoreCase(fname)) {
                // already present
                sdf = f;
            }
        }
        if (sdf != null) {
            sdf.size = size;
            sdf.timestamp = date;
            MainFrame.mainframe.filemanager.refresh();
            return;
        }
        sdf = new SDFileInfo(fname, date, size);
        files.add(sdf);
//        Logger.getLogger(SDCardInfo.class.getName()).log(Level.SEVERE, "file added " + files.size() + ":" + fname);
        MainFrame.mainframe.filemanager.refresh();
    }

    public synchronized void Delete(String fname) {
        SDFileInfo f1 = null;
        for (SDFileInfo f : files) {
            if (f.filename.equalsIgnoreCase(fname)
                    || f.filename.equalsIgnoreCase(fname + "/")) {
                f1 = f;
                break;
            }
        }
        if (f1 != null) {
            files.remove(f1);
            MainFrame.mainframe.filemanager.refresh();
        }
    }

    public synchronized SDFileInfo find(String name) {
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        for (SDFileInfo f : files) {
            if (f.filename.equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public synchronized boolean exists(String name, long timestampEpoch, long size) {
        //Logger.getLogger(SDCardInfo.class.getName()).log(Level.SEVERE, "exists? " + name);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        for (SDFileInfo f : files) {
            //Logger.getLogger(SDCardInfo.class.getName()).log(Level.SEVERE, "file compare " + name + ":" + f.filename);
            if (f.filename.equalsIgnoreCase(name) && f.size == size && (Math.abs(f.timestamp.getTimeInMillis() - timestampEpoch) < 3000)) {
                return true;
            }
        }
        return false;
    }

}
