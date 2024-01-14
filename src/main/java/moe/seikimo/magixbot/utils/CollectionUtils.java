package moe.seikimo.magixbot.utils;

import java.util.ArrayList;
import java.util.List;

public interface CollectionUtils {
    /**
     * Concatenates two lists together.
     *
     * @param list1 The first list.
     * @param list2 The second list.
     * @param <T> The type of the list.
     * @return A new list containing the elements of both lists.
     */
    static <T> List<T> concat(List<T> list1, List<T> list2) {
        var list = new ArrayList<>(list1);
        list.addAll(list2);
        return list;
    }
}
