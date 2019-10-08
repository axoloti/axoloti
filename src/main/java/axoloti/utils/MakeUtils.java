package axoloti.utils;

/**
 *
 * @author jtaelman
 */
public class MakeUtils {

    // replace '?' back into ' ', cfr "sq" in patch.mk
    public static String qs(String s) {
        return s.replace('?', ' ');
    }

    public static String sq(String s) {
        return s.replace(' ', '?');
    }

}
