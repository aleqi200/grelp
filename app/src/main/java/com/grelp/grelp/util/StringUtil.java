package com.grelp.grelp.util;

import java.util.Collection;

public final class StringUtil {

    private StringUtil() {
    }

    /**
     * join the array string using the given separator
     *
     * @param separator the separator
     * @param values    the values to join the strings
     * @return the String joined with the separator.
     * <br>
     * ex:
     * <pre>
     * {@code
     * join(""); // null
     * join("-","a"); // a
     * join("-","a", "b"); // a-b
     * }
     * </pre>
     */
    public static String join(CharSequence separator, Object... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Object value : values) {
            if (value != null) {
                if (i > 0 && i < values.length) {
                    builder.append(separator);
                }
                i++;
                builder.append(value);
            }
        }
        return builder.toString();
    }

    /**
     * join the collection string using the given separator
     *
     * @param separator  the separator
     * @param collection the collection containing the collection to join the strings
     * @return the String joined with the separator.
     * <br>
     * ex:
     * <pre>
     * {@code
     * join("-", new ArrayList<>()); // null
     * join("-", Arrays.asList("a")); // a
     * join("-", Arrays.asList("a", "b")); // a-b
     * }
     * </pre>
     */
    public static String join(CharSequence separator, Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return null;
        }
        return join(separator, collection.toArray(new Object[collection.size()]));
    }
}
