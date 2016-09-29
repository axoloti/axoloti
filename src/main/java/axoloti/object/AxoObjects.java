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
import axoloti.utils.AxolotiLibrary;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjects {

    public AxoObjectTreeNode ObjectTree;
    public ArrayList<AxoObjectAbstract> ObjectList;
    HashMap<String, AxoObjectAbstract> ObjectUUIDMap;

    public AxoObjectAbstract GetAxoObjectFromUUID(String n) {
        return ObjectUUIDMap.get(n);
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
                Logger.getLogger(AxoObjects.class.getName()).log(Level.FINE, "attempt to create object from object file : {0}", fnameA);
                File f = new File(fnameA);
                if (f.isFile()) {
                    boolean loadOK = false;
                    AxoObjectFile of = null;
                    try {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.FINE, "hit : {0}", fnameA);
                        of = serializer.read(AxoObjectFile.class, f);
                        loadOK = true;
                    } catch (Exception ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                        try {
                            of = serializer.read(AxoObjectFile.class, f, false);
                            loadOK = true;
                        } catch (Exception ex1) {
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    if (loadOK) {
                        AxoObjectAbstract o = of.objs.get(0);
                        if (o != null) {
                            o.sPath = fnameA;
                            // to be completed : loading overloaded objects too
                            o.createdFromRelativePath = true;
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "loaded : {0}", fnameA);
                            set.add(o);
                            return set;
                        }
                    }
                }
            }
            { // try subpatch file
                ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
                String fnameP = bfname + ".axs";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.FINE, "attempt to create object from subpatch file in patch directory: {0}", fnameP);
                File f = new File(fnameP);
                if (f.isFile()) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.FINE, "hit : {0}", fnameP);
                    AxoObjectAbstract o = new AxoObjectFromPatch(f);
                    if (n.startsWith("./") || n.startsWith("../")) {
                        o.createdFromRelativePath = true;
                    }
                    o.sPath = f.getPath();
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "loaded : {0}", fnameP);
                    set.add(o);
                    return set;
                }
            }
        }
        ArrayList<AxoObjectAbstract> set = new ArrayList<AxoObjectAbstract>();
        // need to clone ObjectList to avoid a ConcurrentModificationException?
        for (AxoObjectAbstract o : (ArrayList<AxoObjectAbstract>)ObjectList.clone()) {
            if (o.id.equals(n)) {
                set.add(o);
            }
        }
        if (set.isEmpty()) {
            String spath[] = MainFrame.prefs.getObjectSearchPath();
            for (String s : spath) {
                String fsname = s + "/" + n + ".axs";
                Logger.getLogger(AxoObjects.class.getName()).log(Level.FINE, "attempt to create object from subpatch file : {0}", fsname);
                File fs = new File(fsname);
                if (fs.isFile()) {
                    AxoObjectAbstract o = new AxoObjectFromPatch(fs);
//                    o.createdFromRelativePath = true;
                    o.sPath = n + ".axs";
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "loaded :{0}", fsname);
                    set.add(o);
                    return set;
                }
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
        ObjectUUIDMap = new HashMap<String, AxoObjectAbstract>();
    }

    public AxoObjectTreeNode LoadAxoObjectsFromFolder(File folder, String prefix) {

        String id = folder.getName();
        // is this objects in a library, if so use the library name
        if (prefix.length() == 0 && folder.getName().equals("objects")) {
            try {
                String libpath = folder.getParentFile().getCanonicalPath() + File.separator;
                for (AxolotiLibrary lib : MainFrame.prefs.getLibraries()) {
                    if (lib.getLocalLocation().equals(libpath)) {
                        id = lib.getId();
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        AxoObjectTreeNode t = new AxoObjectTreeNode(id);
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
                    AxoObjectFile o = null;
                    try {
                         o = serializer.read(AxoObjectFile.class, fileEntry);
                    } catch (java.lang.reflect.InvocationTargetException ite) {
                        if(ite.getTargetException() instanceof AxoObjectFile.ObjectVersionException) {
                            AxoObjectFile.ObjectVersionException ove = (AxoObjectFile.ObjectVersionException) ite.getTargetException();
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, "Object produced with newer version of Axoloti {0} {1}", 
                                                                            new Object[]{fileEntry.getAbsoluteFile(), ove.getMessage()});
                        } else {
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, fileEntry.getAbsolutePath(), ite);
                            try {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO,"Error reading object, try relaxed mode {0}",fileEntry.getAbsolutePath());
                                o = serializer.read(AxoObjectFile.class, fileEntry, false);
                            } catch (Exception ex1) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, fileEntry.getAbsolutePath(), ex);
                        try {
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO,"Error reading object, try relaxed mode {0}",fileEntry.getAbsolutePath());
                            o = serializer.read(AxoObjectFile.class, fileEntry, false);
                        } catch (Exception ex1) {
                            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    if (o!=null) {
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
                            AxoObjectTreeNode s = t.SubNodes.get(ShortID);
                            if (s == null) {
                                t.Objects.add(a);
                            } else {
                                s.Objects.add(a);
                            }

                            ObjectList.add(a);

                            if ((a.getUUID() != null) && (ObjectUUIDMap.containsKey(a.getUUID()))) {
                                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, "Duplicate UUID! {0}\nOriginal name: {1}\nPath: {2}", new Object[]{fileEntry.getAbsolutePath(), ObjectUUIDMap.get(a.getUUID()).id, ObjectUUIDMap.get(a.getUUID()).sPath});
                            }
                            ObjectUUIDMap.put(a.getUUID(), a);
                        }
                    }
                } else if (fileEntry.getName().endsWith(".axs")) {
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
                String dirname = folder.getName();
                if (!ObjectTree.SubNodes.containsKey(dirname)) {
                    ObjectTree.SubNodes.put(dirname, t);
                } else {
                    // it should be noted, here , we never see this name...
                    // it just needs to be unique, so not to overwirte the map
                    // but just in case it becomes relevant in the future
                    String pname = dirname;
                    try {
                        pname = folder.getCanonicalFile().getParent();
                    } catch (IOException ex) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (!ObjectTree.SubNodes.containsKey(pname)) {
                        ObjectTree.SubNodes.put(pname, t);
                    } else {
                        // hmm, lets use the orig name with number
                        int i = 1;
                        dirname = folder.getName() + "#" + i;
                        while (ObjectTree.SubNodes.containsKey(dirname)) {
                            i++;
                            dirname = folder.getName() + "#" + i;
                        }
                        ObjectTree.SubNodes.put(dirname, t);
                    }
                }
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
                ObjectUUIDMap = new HashMap<String, AxoObjectAbstract>();
                String spath[] = MainFrame.prefs.getObjectSearchPath();
                if (spath != null) {
                    for (String path : spath) {
                        Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "search path : {0}", path);
                        LoadAxoObjects(path);
                    }
                } else {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, "search path empty!");
                }
                Logger.getLogger(AxoObjects.class.getName()).log(Level.INFO, "finished loading objects");
            }
        };
        LoaderThread = new Thread(objloader);
        LoaderThread.start();
    }
    
    public static String ConvertToLegalFilename(String s) {
        s = s.replaceAll("<", "LT");
        s = s.replaceAll(">", "GT");
        s = s.replaceAll("\\*", "STAR");
        s = s.replaceAll("~", "TILDE");
        s = s.replaceAll("\\+", "PLUS");
        s = s.replaceAll("-", "MINUS");
        s = s.replaceAll("/", "SLASH");
        s = s.replaceAll(":", "COLON");
        //if (!cn.equals(o.id)) o.sCName = cn;        
        return s;
    }
    
    void PostProcessObject(AxoObjectAbstract o) {
        if (o instanceof AxoObject) {
            // remove labels when there's only a single parameter
            AxoObject oo = (AxoObject) o;
            if ((oo.params != null) && (oo.params.size() == 1)) {
                oo.params.get(0).noLabel = true;
            }
            if ((oo.displays != null) && (oo.displays.size() == 1)) {
                oo.displays.get(0).noLabel = true;
            }
            if (oo.depends == null) {
                oo.depends = new HashSet<String>();
            }
            String c = oo.sSRateCode + oo.sKRateCode + oo.sInitCode + oo.sLocalData;
            if (c.contains("f_open")) {
                oo.depends.add("fatfs");
            }
            if (c.contains("ADAU1961_WriteRegister")) {
                oo.depends.add("ADAU1361");
            }
            if (c.contains("PWMD1")) {
                oo.depends.add("PWMD1");
            }
            if (c.contains("PWMD2")) {
                oo.depends.add("PWMD2");
            }
            if (c.contains("PWMD3")) {
                oo.depends.add("PWMD3");
            }
            if (c.contains("PWMD4")) {
                oo.depends.add("PWMD4");
            }
            if (c.contains("PWMD5")) {
                oo.depends.add("PWMD5");
            }
            if (c.contains("PWMD6")) {
                oo.depends.add("PWMD6");
            }
            if (c.contains("SD1")) {
                oo.depends.add("SD1");
            }
            if (c.contains("SD2")) {
                oo.depends.add("SD2");
            }
            if (c.contains("SPID1")) {
                oo.depends.add("SPID1");
            }
            if (c.contains("SPID2")) {
                oo.depends.add("SPID2");
            }
            if (c.contains("I2CD1")) {
                oo.depends.add("I2CD1");
            }
            if (oo.depends.isEmpty()) {
                oo.depends = null;
            }

            if (oo.sInitCode != null && oo.sInitCode.isEmpty()) {
                oo.sInitCode = null;
            }
            if (oo.sLocalData != null && oo.sLocalData.isEmpty()) {
                oo.sLocalData = null;
            }
            if (oo.sKRateCode != null && oo.sKRateCode.isEmpty()) {
                oo.sKRateCode = null;
            }
            if (oo.sSRateCode != null && oo.sSRateCode.isEmpty()) {
                oo.sSRateCode = null;
            }
            if (oo.sDisposeCode != null && oo.sDisposeCode.isEmpty()) {
                oo.sDisposeCode = null;
            }
            if (oo.sMidiCode != null && oo.sMidiCode.isEmpty()) {
                oo.sMidiCode = null;
            }
        }
        if (o.sLicense == null) {
            o.sLicense = "GPL";
        }
        if (o.GetIncludes() == null) {
            o.SetIncludes(null);
        }
        if ((o.GetIncludes() != null) && o.GetIncludes().isEmpty()) {
            o.SetIncludes(null);
        }
    }

     public void WriteAxoObject(String path, AxoObjectAbstract o) {
        File f =new File(path);

        AxoObjectFile a = new AxoObjectFile();
        a.objs = new ArrayList<AxoObjectAbstract>();
        a.objs.add(o);
        for (AxoObjectAbstract oa : a.objs) {
            PostProcessObject(oa);
        }
        // arghh, in memory use /midi/in/cc persist as cc !
        String id = o.id;
        o.id = o.shortId;
        if (f.exists()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            try {
                serializer.write(a, os);
            } catch (Exception ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean identical = false;
            try {
                InputStream is1 = new FileInputStream(f);
                byte[] bo = os.toByteArray();
                InputStream is2 = new ByteArrayInputStream(bo);
                while (true) {
                    int i1 = is1.read();
                    int i2 = is2.read();
                    if ((i2 == -1) && (i1 == -1)) {
                        identical = true;
                        break;
                    }
                    if (i1 == -1) {
                        break;
                    }
                    if (i2 == -1) {
                        break;
                    }
                    if (i1 != i2) {
                        break;
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!identical) {
                // overwrite with new
                try {
                    System.out.println("object file changed : " + f.getName());
                    serializer.write(a, f);
                } catch (Exception ex) {
                    Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("object file unchanged : " + f.getName());
            }
        } else {
            // just write a new one
            try {
                serializer.write(a, f);
                System.out.println("object file created : " + f.getName());
            } catch (Exception ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        o.id = id;
    }
    
    
}
