package axoloti.property;

import axoloti.mvc.IModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class StringListProperty extends PropertyReadWrite<List<String>> {

    public StringListProperty(String name, Class containerClass, String friendlyName) {
        super(name, List.class, containerClass, friendlyName);
    }

    @Override
    public Class getType() {
        return List.class;
    }

    @Override
    public boolean allowNull() {
        return false;
    }

    static String StringArrayToString(List<String> va) {
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
    public String getAsString(IModel o) {
        List<String> s = get(o);
        if (s == null) {
            return "";
        } else {
            return StringArrayToString(s);
        }
    }

    static List<String> StringToStringArrayList(String s) {
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
    public List<String> StringToObj(String v) {
        return StringToStringArrayList(v);
    }
}
