package axoloti.utils;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class ListUtils<T> {

    private ListUtils() {
    }

    /**
     * Converts a List object or null, into a non-null unmodifiable List. Unlike
     * Collections.unmodifiableList(), it handles null input.
     *
     * @param <T> type of list elements
     * @param list java.util.List<T> or null
     * @return unmodifiable java.util.List<T>, never null
     */
    public static <T> List<T> export(List<? extends T> list) {
        if (list == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(list);
        }
    }

    /**
     * Converts a List object, into a unmodifiable List. When the list is null
     * or empty, null is returned.
     *
     * @param <T> type of list elements
     * @param list java.util.List<T> or null
     * @return unmodifiable java.util.List<T>
     */
    public static <T> List<T> emptyToNull(List<? extends T> list) {
        if ((list == null) || list.isEmpty()) {
            return null;
        } else {
            return Collections.unmodifiableList(list);
        }
    }

}
