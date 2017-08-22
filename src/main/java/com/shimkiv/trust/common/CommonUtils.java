package com.shimkiv.trust.common;

import java.util.List;
import java.util.Map;

/**
 * Common Utils
 *
 * @author Serhii Shymkiv
 */

public class CommonUtils {
    // Prevents instantiation
    private CommonUtils() {}

    /**
     * Parses {@link Boolean} of provided {@link Object}
     *
     * @param object {@link Object} to parse
     * @return {@link Boolean} of provided {@link Object}
     */
    public static boolean parseBoolean(Object object) {
        return object != null &&
                (boolean) object;
    }

    /**
     * Checks whether the collection is empty
     *
     * @param collection Collection
     * @return Whether the collection is empty
     */
    public static <T> boolean collectionIsEmpty(T collection) {
        if(collection != null) {
            if(Map.class.isAssignableFrom(
                    collection.
                            getClass())) {
                return ((Map) collection).isEmpty();
            } else if(List.class.isAssignableFrom(
                    collection.
                            getClass())) {
                return ((List) collection).isEmpty();
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * Checks whether the collection is not empty
     *
     * @param collection Collection
     * @return Whether the collection is not empty
     */
    public static <T> boolean collectionIsNotEmpty(T collection) {
        return !collectionIsEmpty(collection);
    }
}
