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
package axoloti.target.fs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class SDCardInfo {

    private final ArrayList<SDFileInfo> files;
    boolean available = false;
    int clusters = 0;
    int clustersize = 0;
    int sectorsize = 0;

    public SDCardInfo(
            int clusters,
            int clustersize,
            int sectorsize,
            List<SDFileInfo> files
    ) {
        this.clusters = clusters;
        this.clustersize = clustersize;
        this.sectorsize = sectorsize;
        this.files = new ArrayList<>(files);
        this.files.sort(new Comparator<SDFileInfo>() {
            @Override
            public int compare(SDFileInfo o1, SDFileInfo o2) {
                return o1.getFilename().compareTo(o2.getFilename());
            }
        });
    }


    public List<SDFileInfo> getFiles() {
        return Collections.unmodifiableList(files);
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

    @Deprecated
    private void addFile(String fname, int size, Calendar date) {
        // probably broken
        if (fname.lastIndexOf(0) > 0) {
            fname = fname.substring(0, fname.lastIndexOf(0));
        }
        if (fname.equals("/")) {
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
            return;
        }
//        sdf = new SDFileInfo(fname, date, size);
        files.add(sdf);
    }

    public void removeFile(String fname) {
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
        }
    }

    public SDFileInfo find(String name) {
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

    public boolean exists(String name, long timestampEpoch, long size) {
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
