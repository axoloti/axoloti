package axoloti.property;

import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class StringListProperty extends PropertyReadWrite<ArrayList<String>> {

    public StringListProperty(String name, Class containerClass, String friendlyName) {
        super(name, ArrayList.class, containerClass, friendlyName);
    }

    @Override
    public Class getType() {
        return ArrayList.class;
    }

    @Override
    public boolean allowNull() {
        return false;
    }

    static String StringArrayToString(ArrayList<String> va) {
        // items quoted, separated by comma
        // quote characters escaped with backslash
        String s = "";
        boolean first = true;
        for (String s1 : va) {
            if (!first) {
                s += ", ";
            }
            String s2 = s1.replaceAll("\\\\", "\\\\\\");
            s2 = s2.replaceAll("\"", "\\\\\"");
            s += "\"" + s2 + "\"";
            first = false;
        }
        return s;
    }

    @Override
    public String getAsString(Object o) {
        ArrayList<String> s = get(o);
        if (s == null) {
            return "";
        } else {
            return StringArrayToString(s);
        }
    }

    static ArrayList<String> StringToStringArrayList(String s) {
        // items separated by comma
        // items can be within quotes
        // backlash to escape quote character
        ArrayList<String> l = new ArrayList<String>();
        int si = 0;
        int se = 0;
        boolean quoted = false;
        boolean escaped = false;
        String e = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!escaped) {
                switch (c) {
                    case '\"':
                        if (!quoted) {
                            quoted = true;
                            e = e.trim();
                            si = i;
                        } else {
                            quoted = false;
                            se = i;
                        }
                        break;
                    case ',':
                        if (!quoted) {
                            if (i == 0) {
                                l.add("");
                                si = 1;
                            } else if (se > si) {
                                // quoted
                                l.add(e);
                                si = i + 1;
                                e = "";
                            } else {
                                l.add(e);
                                si = i + 1;
                                e = "";
                            }
                        } else {
                            e += c;
                        }
                        break;
                    case '\\':
                        escaped = true;
                        break;
                    default:
                        e += c;
                }
            } else {
                e += c;
                escaped = false;
            }
        }
        if (e.length() > 0) {
            l.add(e);
        }
        return l;
    }

    @Override
    public ArrayList<String> StringToObj(String v) {
        return StringToStringArrayList(v);
    }
}
