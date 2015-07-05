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
package axoloti.object;

import axoloti.MainFrame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import transitions.TransitionManager;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjects {

    public AxoObjectTreeNode ObjectTree;
    public ArrayList<AxoObjectAbstract> ObjectList;
    HashMap<String, AxoObjectAbstract> ObjectHashMap;
    HashMap<String, AxoObjectAbstract> ObjectUpgradeHashMap;
    HashMap<String, AxoObjectAbstract> ObjectUUIDMap;

    TransitionManager transitionmgr;

    public AxoObjectAbstract GetAxoObjectFromUUID(String n) {
        return ObjectUUIDMap.get(n);
    }

    public AxoObjectAbstract GetAxoObjectFromSHA(String n) {
        AxoObjectAbstract ao = transitionmgr.GetObjectFromSha(n);
        if (ao != null) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "upgraded object by SHA : " + ao.id);
            return ao;
        }
        AxoObjectAbstract r = ObjectHashMap.get(n);
        if (r == null) {
            r = ObjectUpgradeHashMap.get(n);
        }
        return r;
    }

    public ArrayList<AxoObjectAbstract> GetAxoObjectFromName(String n, String cwd) {
        String bfname = null;
        if (n.startsWith("./") && (cwd != null)) {
            bfname = cwd + "/" + n.substring(2);
        }
        if (n.startsWith("../") && (cwd != null)) {
            bfname = cwd + "/../" + n.substring(3);
        }
        if ((bfname != null) && (cwd != null)) {
            { // try object file
                ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
                String fnameA = bfname + ".axo";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from object file : " + fnameA);
                File f = new File(fnameA);
                if (f.isFile()) {
                    try {
                        AxoObjectFile of = serializer.read(AxoObjectFile.class, f);
                        AxoObjectAbstract o = of.objs.get(0);
                        o.sPath = fnameA;
                        // to be completed : loading overloaded objects too
                        o.createdFromRelativePath = true;
                        if (o != null) {
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "hit : " + fnameA);
                            set.add(o);
                            return set;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            { // try subpatch file
                ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
                String fnameP = bfname + ".axs";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from subpatch file in patch directory: " + fnameP);
                File f = new File(fnameP);
                if (f.isFile()) {
                    AxoObjectAbstract o = new AxoObjectFromPatch(f);
                    if (n.startsWith("./") || n.startsWith("../")) {
                        o.createdFromRelativePath = true;
                    }
                    o.sPath = f.getPath();
                    if (o != null) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "hit : " + fnameP);
                        set.add(o);
                        return set;
                    }
                }
            }
            { // try patch file
                ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
                String fnameP = bfname + ".axp";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from patch file in patch directory: " + fnameP);
                File f = new File(fnameP);
                if (f.isFile()) {
                    AxoObjectAbstract o = new AxoObjectFromPatch(f);
                    if (n.startsWith("./") || n.startsWith("../")) {
                        o.createdFromRelativePath = true;
                    }
                    o.sPath = f.getPath();
                    if (o != null) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "hit : " + fnameP);
                        set.add(o);
                        return set;
                    }
                }
            }
        }
        ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
        for (AxoObjectAbstract o : ObjectList) {
            if (o.id.equals(n)) {
                set.add(o);
            }
        }
        if (set.isEmpty()) {
            String spath[] = MainFrame.prefs.getObjectSearchPath();
            for (String s : spath) {
                String fsname = s + "/" + n + ".axs";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from subpatch file : " + fsname);
                File fs = new File(fsname);
                if (fs.isFile()) {
                    AxoObjectAbstract o = new AxoObjectFromPatch(fs);
//                    o.createdFromRelativePath = true;
                    if (o != null) {
                        o.sPath = n + ".axs";
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from subpatch file succeeded :" + fsname);
                        set.add(o);
                        return set;
                    }
                }
                else {
                    // Q. do we really need to load axp here, or just axs?
                    String fpname = s + "/" + n + ".axp";
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from patch file : " + fpname);
                    File fp = new File(fpname);
                    if (fp.isFile()) {
                        AxoObjectAbstract o = new AxoObjectFromPatch(fp);
    //                    o.createdFromRelativePath = true;
                        if (o != null) {
                            o.sPath = n + ".axp";
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "attempt to create object from patch file succeeded :" + fpname);
                            set.add(o);
                            return set;
                        }
                    }
                }
            }
            // last resort : transition?
            AxoObjectAbstract ao = transitionmgr.GetObjectFromName(n);
            if (ao != null) {
                set.add(ao);
                return set;
            }
            return null;
        } else {
            return set;
        }
    }
    protected Serializer serializer = new Persister();

    public AxoObjects() {
        ObjectTree = new AxoObjectTreeNode("/");
        ObjectList = new ArrayList<AxoObjectAbstract>();
        ObjectHashMap = new HashMap<String, AxoObjectAbstract>();
        ObjectUpgradeHashMap = new HashMap<String, AxoObjectAbstract>();
        ObjectUUIDMap = new HashMap<String, AxoObjectAbstract>();
    }

    public AxoObjectTreeNode LoadAxoObjectsFromFolder(File folder, String prefix) {
        AxoObjectTreeNode t = new AxoObjectTreeNode(folder.getName());
        File fdescription = new File(folder.getAbsolutePath() + "/index.html");
        if (fdescription.canRead()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(fdescription));
                StringBuilder result = new StringBuilder();
                char[] buf = new char[1024];
                int r;
                while ((r = reader.read(buf)) != -1) {
                    result.append(buf, 0, r);
                }
                t.description = result.toString();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(folder.listFiles()));
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String no1 = o1.getName();
                String no2 = o2.getName();
                if (no1.startsWith(no2)) {
                    return 1;
                } else if (no2.startsWith(no1)) {
                    return -1;
                }
                return (no1.compareTo(no2));
            }
        });
        for (final File fileEntry : fileList) {
            if (fileEntry.isDirectory()) {
                String dirname = fileEntry.getName();
                AxoObjectTreeNode s = LoadAxoObjectsFromFolder(fileEntry, prefix + "/" + dirname);
                if (s.Objects.size() > 0 || s.SubNodes.size() > 0) {
                    t.SubNodes.put(dirname, s);
                    for (AxoObjectAbstract o : t.Objects) {
                        int i = o.id.lastIndexOf('/');
                        if (i > 0) {
                            if (o.id.substring(i + 1).equals(dirname)) {
                                s.Objects.add(o);
                            }
                        }
                    }
                }
            } else {
                if (fileEntry.getName().endsWith(".axo")) {
                    try {
                        AxoObjectFile o = serializer.read(AxoObjectFile.class, fileEntry);
                        for (AxoObjectAbstract a : o.objs) {
                            a.sPath = fileEntry.getAbsolutePath();
                            if (!prefix.isEmpty()) {
                                a.id = prefix.substring(1) + "/" + a.id;
                            }
                            String ShortID = a.id;
                            int i = ShortID.lastIndexOf('/');
                            if (i > 0) {
                                ShortID = ShortID.substring(i + 1);
                            }
                            a.shortId = ShortID;
                            String uuidVerify = a.GenerateUUID();
                            if ((uuidVerify != null) && (!uuidVerify.equals(a.getUUID()))) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE,
                                        "Incorrect uuid hash detected for object: " + fileEntry.getAbsolutePath() + " , does not match its signature (" + a.getUUID() + "). True signature would be " + uuidVerify);
                            }
                            String shaVerify = a.GenerateSHA();
                            if ((shaVerify != null) && (!shaVerify.equals(a.getSHA()))) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE,
                                        "Incorrect sha hash detected for object: " + fileEntry.getAbsolutePath() + " its implementation does not match its signature.");
                            }
                            AxoObjectTreeNode s = t.SubNodes.get(ShortID);
                            if (s == null) {
                                t.Objects.add(a);
                            } else {
                                s.Objects.add(a);
                            }

                            ObjectList.add(a);
                            if ((a.getSHA() != null) && (ObjectHashMap.containsKey(a.getSHA()))) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE,
                                        "Duplicate SHA! " + fileEntry.getAbsolutePath() + "\nOriginal name: " + ObjectHashMap.get(a.getSHA()).id
                                        + "\nPath: " + ObjectHashMap.get(a.getSHA()).sPath);
                            }
                            ObjectHashMap.put(a.getSHA(), a);

                            if (a.upgradeSha != null) {
                                for (String usha : a.upgradeSha) {
                                    if (ObjectUpgradeHashMap.containsKey(usha)) {
                                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE,
                                                "Duplicate upgrade SHA! " + fileEntry.getAbsolutePath() + "\nOriginal name: " + ObjectUpgradeHashMap.get(usha).id
                                                + "\nPath: " + ObjectUpgradeHashMap.get(usha).sPath);
                                    }
                                    ObjectUpgradeHashMap.put(usha, a);
                                }
                            }

                            if ((a.getUUID() != null) && (ObjectUUIDMap.containsKey(a.getUUID()))) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE,
                                        "Duplicate UUID! " + fileEntry.getAbsolutePath() + "\nOriginal name: " + ObjectUUIDMap.get(a.getUUID()).id
                                        + "\nPath: " + ObjectUUIDMap.get(a.getUUID()).sPath);
                            }
                            ObjectUUIDMap.put(a.getUUID(), a);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, fileEntry.getAbsolutePath(), ex);
                    }
                } else if (fileEntry.getName().endsWith(".axp")) {
                    try {
                        String oname = fileEntry.getName().substring(0, fileEntry.getName().length() - 4);
                        String fullname;
                        if (prefix.isEmpty()) {
                            fullname = oname;
                        } else {
                            fullname = prefix.substring(1) + "/" + oname;
                        }
                        AxoObjectUnloaded a = new AxoObjectUnloaded(fullname, fileEntry);
                        a.sPath = fileEntry.getAbsolutePath();
                        t.Objects.add(a);
                        ObjectList.add(a);
                    } catch (Exception ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, fileEntry.getAbsolutePath(), ex);
                    }
                }
            }
        }
        return t;
    }

    public void LoadAxoObjects(String path) {
        File folder = new File(path);
        if (folder.isDirectory()) {
            AxoObjectTreeNode t = LoadAxoObjectsFromFolder(folder, "");
            if (t.Objects.size() > 0 || t.SubNodes.size() > 0) {
                ObjectTree.SubNodes.put(folder.getName(), t);
            }
        }
    }

    public Thread LoaderThread;

    public void LoadAxoObjects() {
        Runnable objloader = new Runnable() {
            @Override
            public void run() {
                ObjectTree = new AxoObjectTreeNode("/");
                ObjectList = new ArrayList<AxoObjectAbstract>();
                ObjectHashMap = new HashMap<String, AxoObjectAbstract>();
                ObjectUpgradeHashMap = new HashMap<String, AxoObjectAbstract>();
                ObjectUUIDMap = new HashMap<String, AxoObjectAbstract>();
                String spath[] = MainFrame.prefs.getObjectSearchPath();
                if (spath != null) {
                    for (String path : spath) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "search path : " + path);
                        LoadAxoObjects(path);
                    }
                } else {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, "search path empty!");
                }
                transitionmgr = new TransitionManager();
                transitionmgr.LoadTransitions();
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "finished loading objects");
            }
        };
        Thread LoaderThread = new Thread(objloader);
        LoaderThread.start();
    }
}
