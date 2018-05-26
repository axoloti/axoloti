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
package generatedobjects;

import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectFile;
import axoloti.object.attribute.AxoAttribute;
import axoloti.object.inlet.Inlet;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.Outlet;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.Parameter;
import axoloti.object.parameter.ParameterFrac32UMap;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.preferences.Preferences;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 *
 * @author Johannes Taelman
 */
@Deprecated
class GenTools {

    static protected Serializer serializer = new Persister();
    static String unstable = "unstable";

    static String convertToLegalFilename(String s) {
        s = s.replaceAll("<", "LT");
        s = s.replaceAll(">", "GT");
        s = s.replaceAll("\\*", "STAR");
        s = s.replaceAll("~", "TILDE");
        s = s.replaceAll("\\+", "PLUS");
        s = s.replaceAll("-", "MINUS");
        s = s.replaceAll("/", "SLASH");
        //if (!cn.equals(o.id)) o.sCName = cn;
        return s;
    }

    @Deprecated
    static void checkString(AxoObject o, String s) {
        for (Parameter p : o.params) {
            s = s.replaceAll("%" + p.getName() + "%", "");
            s = s.replaceAll("_" + p.getName(), "");
        }
        for (Inlet p : o.inlets) {
            s = s.replaceAll("%" + p.getName() + "%", "");
            s = s.replaceAll("_" + p.getName(), "");
        }
        for (Outlet p : o.outlets) {
            s = s.replaceAll("%" + p.getName() + "%", "");
            s = s.replaceAll("_" + p.getName(), "");
        }
        for (axoloti.object.display.Display p : o.displays) {
            s = s.replaceAll("%" + p.getName() + "%", "");
            s = s.replaceAll("_" + p.getName(), "");
        }

        s = s.replaceAll("ntrig", "");
        s = s.replaceAll("rtrig", "");
        s = s.replaceAll("_trig", "");
        s = s.replaceAll("starttrig", "");
        s = s.replaceAll("stoptrig", "");
        s = s.replaceAll("ltrig", "");
        s = s.replaceAll("int", "");
        s = s.replaceAll("default", "");
        for (Parameter p : o.params) {
            if (s.contains(p.getName())) {
                Logger.getLogger(axoloti.patch.PatchModel.class.getName()).log(Level.SEVERE, "Object {0}: contains unmarked string {1}\n{2}", new Object[]{o.id, p.getName(), s});
            }
        }
        for (Inlet p : o.inlets) {
            if (s.contains(p.getName())) {
                Logger.getLogger(axoloti.patch.PatchModel.class.getName()).log(Level.SEVERE, "Object {0}: contains unmarked string {1}\n{2}", new Object[]{o.id, p.getName(), s});
            }
        }
        for (Outlet p : o.outlets) {
            if (s.contains(p.getName())) {
                Logger.getLogger(axoloti.patch.PatchModel.class.getName()).log(Level.SEVERE, "Object {0}: contains unmarked string {1}\n{2}", new Object[]{o.id, p.getName(), s});
            }
        }

    }

    static void postProcessObject(AxoObjectAbstract o, String catname, String fn) {
        String relativeID = o.id;
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
                oo.depends = new LinkedList<>();
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

            /*
             if (oo.sKRateCode!=null)
             CheckString(oo,oo.sKRateCode);
             if (oo.sSRateCode!=null)
             CheckString(oo,oo.sSRateCode);
             */
        }
        if (o.getAuthor().isEmpty()) {
            o.setAuthor("Johannes Taelman");
        }
        if (o.getLicense().isEmpty()) {
            o.setLicense("BSD");
        }
        if (o.getIncludes() == null) {
            o.setIncludes(null);
        }
        if ((o.getIncludes() != null) && o.getIncludes().isEmpty()) {
            o.setIncludes(null);
        }
        o.id = catname + "/" + relativeID; // uuid based on full name
//        String upgradeSha = o.GenerateSHA();
        o.getUUID();
        o.id = relativeID;
        if (o instanceof AxoObject) {
            // remove labels when there's only a single parameter
            AxoObject oo = (AxoObject) o;
            for (Parameter p : oo.params) {
                if (oo.sKRateCode != null) {
                    oo.sKRateCode = oo.sKRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sSRateCode != null) {
                    oo.sSRateCode = oo.sSRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
            }
            for (AxoAttribute p : oo.attributes) {
                if (oo.sInitCode != null) {
                    oo.sInitCode = oo.sInitCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sDisposeCode != null) {
                    oo.sDisposeCode = oo.sDisposeCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sLocalData != null) {
                    oo.sLocalData = oo.sLocalData.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sMidiCode != null) {
                    oo.sMidiCode = oo.sMidiCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sKRateCode != null) {
                    oo.sKRateCode = oo.sKRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sSRateCode != null) {
                    oo.sSRateCode = oo.sSRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
            }
            for (Inlet p : oo.inlets) {
                if (oo.sKRateCode != null) {
                    oo.sKRateCode = oo.sKRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sSRateCode != null) {
                    oo.sSRateCode = oo.sSRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
            }
            for (Outlet p : oo.outlets) {
                if (oo.sKRateCode != null) {
                    oo.sKRateCode = oo.sKRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sSRateCode != null) {
                    oo.sSRateCode = oo.sSRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
            }
            for (axoloti.object.display.Display p : oo.displays) {
                if (oo.sInitCode != null) {
                    oo.sInitCode = oo.sInitCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
                if (oo.sKRateCode != null) {
                    oo.sKRateCode = oo.sKRateCode.replaceAll("%" + p.getName() + "%", p.getCName());
                }
            }
            if (oo.sInitCode != null) {
                oo.sInitCode = oo.sInitCode.replaceAll("%midichannel%", "attr_midichannel");
            }
            if (oo.sKRateCode != null) {
                oo.sKRateCode = oo.sKRateCode.replaceAll("%midichannel%", "attr_midichannel");
            }
            if (oo.sSRateCode != null) {
                oo.sSRateCode = oo.sSRateCode.replaceAll("%midichannel%", "attr_midichannel");
            }
            if (oo.sMidiCode != null) {
                oo.sMidiCode = oo.sMidiCode.replaceAll("%midichannel%", "attr_midichannel");
            }
            if (oo.sDisposeCode != null) {
                oo.sDisposeCode = oo.sDisposeCode.replaceAll("%midichannel%", "attr_midichannel");
            }

            if (oo.helpPatch == null) {
                File f = new File(getObjDir() + catname + "/" + fn + ".axh");
                if (f.exists()) {
                    oo.helpPatch = fn + ".axh";
                } else {
                    String fcatname = catname.replaceAll("/", "_");
                    File fcat = new File(getObjDir() + catname + "/" + fcatname + ".axh");
                    if (fcat.exists()) {
                        oo.helpPatch = fcatname + ".axh";
                    }
                }
            }
        }

//        String sha = o.GenerateSHA();
//        o.setSHA(sha);
//        if ((upgradeSha != null) && (!upgradeSha.equals(sha))) {
//            o.addUpgradeSHA(upgradeSha);
//        }
    }

    static public void writeAxoObject(String path, AxoObjectAbstract o) {
        File f;
        String fn;
        if (!path.endsWith(".axo")) {
            fn = convertToLegalFilename(o.id);

            int i = fn.lastIndexOf('.');
            if (i > 0) {
                path = path + "." + fn.substring(0, i);
                fn = fn.substring(i);
            }
            path = path.replace('\\', '/');
            fn = fn.replace('\\', '/');

            o.id = o.id.replace('\\', '/');
            i = o.id.lastIndexOf('/');
            if (i > 0) {
                o.id = o.id.substring(i + 1);
            }

            File fd = new File(getObjDir() + path);
            if (!fd.isDirectory()) {
                fd.mkdirs();
            }
            f = new File(getObjDir() + path + "/" + fn + ".axo");
        } else {
            f = new File(path);
            fn = f.getName();
            String objPath = null;
            for (String s : Preferences.getPreferences().getObjectSearchPath()) {
                if (path.startsWith(s)) {
                    objPath = path.substring(s.length() + 1);
                    break;
                }
                File f2 = new File(s);
                String s2 = f2.getAbsolutePath();
                if (path.startsWith(s2)) {
                    objPath = path.substring(s2.length() + 1);
                    break;
                }
            }
            if (objPath != null) {
                fn = fn.replace('\\', '/');
                int ii = fn.lastIndexOf('/');
                if (ii < 0) {
                    ii = 0;
                }
                fn = fn.substring(ii, fn.length() - 4);
                objPath = objPath.replace('\\', '/');
                //System.out.printf("1 path %s objPath %s\n", path, objPath);
                path = objPath.substring(0, objPath.lastIndexOf('/'));
                //fn = objPath.substring(0,fn.length()-4);
                System.out.printf("2 path %s objPath %s fn %s%n", path, objPath, fn);
                o.id = o.id.substring(o.id.lastIndexOf('/') + 1);
            }
        }
        AxoObjectFile a = new AxoObjectFile();
        a.objs = new ArrayList<>();
        a.objs.add(o);
        for (AxoObjectAbstract oa : a.objs) {
            postProcessObject(oa, path, fn);
        }
        if (f.exists()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            try {
                serializer.write(a, os);
            } catch (Exception ex) {
                Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean identical = false;
            try (InputStream is1 = new FileInputStream(f)) {
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
                Logger.getLogger(GenTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GenTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!identical) {
                // overwrite with new
                try {
                    System.out.println("object file changed : " + f.getName());
                    File f2 = new File(getObjDir() + path + "/" + fn + ".axo");
                    serializer.write(a, f2);
                } catch (Exception ex) {
                    Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static public void writeAxoObject(String path, AxoObjectAbstract o[]) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        Collections.addAll(a, o);
        GenTools.writeAxoObject(path, a);
    }

    static void writeAxoObject(String path, ArrayList<AxoObjectAbstract> o) {
        String fn = convertToLegalFilename(o.get(0).id);
        int i = fn.lastIndexOf('.');
        if (i > 0) {
            path = path + "." + fn.substring(0, i);
            fn = fn.substring(i);
        }
        path = path.replace('\\', '/');
        fn = fn.replace('\\', '/');

        File fd = new File(getObjDir() + path);
        if (!fd.isDirectory()) {
            fd.mkdirs();
        }
        File f = new File(getObjDir() + path + "/" + fn + ".axo");
        AxoObjectFile a = new AxoObjectFile();
        a.objs = o;
        for (AxoObjectAbstract oa : a.objs) {
            oa.id = oa.id.replace('\\', '/');
            i = oa.id.lastIndexOf('/');
            if (i > 0) {
                oa.id = oa.id.substring(i + 1);
            }
            postProcessObject(oa, path, fn);
        }

        if (f.exists()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            try {
                serializer.write(a, os);
            } catch (Exception ex) {
                Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean identical = false;
            try (InputStream is1 = new FileInputStream(f)) {
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
                Logger.getLogger(GenTools.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GenTools.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (!identical) {
                // overwrite with new
                try {
                    System.out.println("object file changed : " + f.getName());
                    File f2 = new File(getObjDir() + path + "/" + fn + ".axo");
                    serializer.write(a, f2);
                } catch (Exception ex) {
                    Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(GeneratedObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static AxoObject createKRateBinaryOpI32Object(String name, String op) {
        AxoObject o = new AxoObject(name, name + "s two k-rate signals");
        o.outlets.add(new OutletFrac32("result", "a " + op + " b"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.inlets.add(new InletFrac32("b", "b"));
        o.sKRateCode = "%result%= %a% " + op + " %b%;";
        return o;
    }

    static AxoObject createSRateBinaryOpI32Object(String name, String op) {
        AxoObject o = new AxoObject(name, name + "s two s-rate signals");
        o.outlets.add(new OutletFrac32Buffer("result", "a " + op + " b"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.inlets.add(new InletFrac32Buffer("b", "b"));
        o.sSRateCode = "%result%= %a% " + op + " %b%;";
        return o;
    }

    static AxoObject createSKBinaryOpI32Object(String name, String op) {
        AxoObject o = new AxoObject(name + "~", name + "s a s-rate and a k-rate signal, the k-rate signal is not smoothed or interpolated");
        o.outlets.add(new OutletFrac32Buffer("result", "a " + op + " b"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.inlets.add(new InletFrac32("b", "b"));
        o.sSRateCode = "%result%= %a% " + op + " %b%;";
        return o;
    }

    static ArrayList<AxoObjectAbstract> createKIFracTwoOpLogicOut(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_i = new AxoObject(name, description);
        o_i.outlets.add(new OutletBool32("out", "out"));
        o_i.inlets.add(new InletInt32("in1", "in1"));
        o_i.inlets.add(new InletInt32("in2", "in2"));
        o_i.sKRateCode = expr;
        a.add(o_i);
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletBool32("out", "out"));
        o_k.inlets.add(new InletFrac32("in1", "in1"));
        o_k.inlets.add(new InletFrac32("in2", "in2"));
        o_k.sKRateCode = expr;
        a.add(o_k);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createKFracTwoOpLogicOut(String name, String description, String op_prefix, String op_midfix, String op_suffix) {
        return createKIFracTwoOpLogicOut(name, description, "%out%= " + op_prefix + "%in1%" + op_midfix + "%in2% " + op_suffix + ";");
    }

    static ArrayList<AxoObjectAbstract> createSKFracTwoOp(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in1", "in1"));
        o_k.inlets.add(new InletFrac32("in2", "in2"));
        o_k.sKRateCode = expr;
        a.add(o_k);
        AxoObject o_s = new AxoObject(name, description);
        o_s.outlets.add(new OutletFrac32Buffer("out", "out"));
        o_s.inlets.add(new InletFrac32Buffer("in1", "in2"));
        o_s.inlets.add(new InletFrac32Buffer("in2", "in2"));
        o_s.sKRateCode = expr;
        a.add(o_s);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKFracTwoOp(String name, String description, String op_prefix, String op_midfix, String op_suffix) {
        return GenTools.createSKFracTwoOp(name, description, "%out%= " + op_prefix + "%in1%" + op_midfix + "%in2% " + op_suffix + ";");
    }

    static AxoObjectAbstract createKFracTwoOp(String name, String description, String expr) {
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in1", "in1"));
        o_k.inlets.add(new InletFrac32("in2", "in2"));
        o_k.sKRateCode = expr;
        return o_k;
    }

    static AxoObjectAbstract createSFracTwoOp(String name, String description, String expr) {
        AxoObject o_s = new AxoObject(name, description);
        o_s.outlets.add(new OutletFrac32Buffer("out", "out"));
        o_s.inlets.add(new InletFrac32Buffer("in1", "in2"));
        o_s.inlets.add(new InletFrac32Buffer("in2", "in2"));
        o_s.sSRateCode = expr;
        return o_s;
    }

    static AxoObjectAbstract createIFracTwoOp(String name, String description, String expr) {
        AxoObject o_i = new AxoObject(name, description);
        o_i.outlets.add(new OutletInt32("out", "out"));
        o_i.inlets.add(new InletInt32("in1", "in1"));
        o_i.inlets.add(new InletInt32("in2", "in2"));
        o_i.sKRateCode = expr;
        return o_i;
    }

    static ArrayList<AxoObjectAbstract> createSKIFracTwoOp(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        a.add(createKFracTwoOp(name, description, expr));
        a.add(createSFracTwoOp(name, description, expr));
        a.add(createIFracTwoOp(name, description, expr));
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKIFracTwoOp(String name, String description, String op_prefix, String op_midfix, String op_suffix) {
        return GenTools.createSKIFracTwoOp(name, description, "%out%= " + op_prefix + "%in1%" + op_midfix + "%in2% " + op_suffix + ";");
    }

    static ArrayList<AxoObjectAbstract> createIKFracOneOp(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in", "in"));
        o_k.sKRateCode = expr;
        a.add(o_k);
        AxoObject o_s = new AxoObject(name, description);
        o_s.outlets.add(new OutletInt32("out", "out"));
        o_s.inlets.add(new InletInt32("in", "in"));
        o_s.sSRateCode = expr;
        a.add(o_s);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKFracOneOp(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in", "in"));
        o_k.sKRateCode = expr;
        a.add(o_k);
        AxoObject o_s = new AxoObject(name, description);
        o_s.outlets.add(new OutletFrac32Buffer("out", "out"));
        o_s.inlets.add(new InletFrac32Buffer("in", "in"));
        o_s.sSRateCode = expr;
        a.add(o_s);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKIFracOneOp(String name, String description, String expr) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in", "in"));
        o_k.sKRateCode = expr;
        a.add(o_k);
        AxoObject o_s = new AxoObject(name, description);
        o_s.outlets.add(new OutletFrac32Buffer("out", "out"));
        o_s.inlets.add(new InletFrac32Buffer("in", "in"));
        o_s.sSRateCode = expr;
        a.add(o_s);
        AxoObject o_i = new AxoObject(name, description);
        o_i.outlets.add(new OutletInt32("out", "out"));
        o_i.inlets.add(new InletInt32("in", "in"));
        o_i.sKRateCode = expr;
        a.add(o_i);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKFracOneOp(String name, String description, String op_prefix, String op_suffix) {
        return GenTools.createSKFracOneOp(name, description, "%out%= " + op_prefix + "%in%" + op_suffix + ";");
    }

    static ArrayList<AxoObjectAbstract> createSKIFracOneOp(String name, String description, String op_prefix, String op_suffix) {
        return createSKIFracOneOp(name, description, "%out%= " + op_prefix + "%in%" + op_suffix + ";");
    }

    static ArrayList<AxoObjectAbstract> createKFracOneOpFracCLogicOut(String name, String description, String op_prefix, String op_suffix) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.params.add(new ParameterFrac32UMap("c"));
        o_k.outlets.add(new OutletBool32("out", "out"));
        o_k.inlets.add(new InletFrac32("in", "in"));
        o_k.sKRateCode = "%out%= " + op_prefix + "%in%" + op_suffix + ";";
        a.add(o_k);
        return a;
    }

    static ArrayList<AxoObjectAbstract> createSKFracOneOpFracC(String name, String description, String op_prefix, String op_suffix) {
        ArrayList<AxoObjectAbstract> a = new ArrayList<>();
        AxoObject o_k = new AxoObject(name, description);
        o_k.params.add(new ParameterFrac32UMap("c"));
        o_k.outlets.add(new OutletFrac32("out", "out"));
        o_k.inlets.add(new InletFrac32("in", "in"));
        o_k.sKRateCode = "%out%= " + op_prefix + "%in%" + op_suffix + ";";
        a.add(o_k);
        AxoObject o_s = new AxoObject(name, description);
        o_s.params.add(new ParameterFrac32UMap("c"));
        o_s.outlets.add(new OutletFrac32Buffer("out", "out"));
        o_s.inlets.add(new InletFrac32Buffer("in", "in"));
        o_s.sSRateCode = "%out%= " + op_prefix + "%in%" + op_suffix + ";";
        a.add(o_s);
        return a;
    }

    static String getObjDir() {
        AxolotiLibrary lib = Preferences.getPreferences().getLibrary(AxolotiLibrary.FACTORY_ID);
        String objdir = "objects/";
        if (lib != null) {
            objdir = lib.getLocalLocation() + objdir;
        }
        return objdir;
    }
}
