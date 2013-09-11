/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.eclipse.jdt.client.core.util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 10, 2012 5:15:22 PM evgen $
 */
public class JSONUtil {
    /**
     * @param json
     * @return
     */
    public static long[] parseJsonAsLongArray(String json) {
        JSONValue value = parse(json);
        if (value.isArray() == null)
            throw new IllegalArgumentException("'json' parameter must represent a JSON array");
        return jsonArrayToLongArray(value.isArray());
    }

    /**
     * @param json
     * @return
     */
    private static JSONValue parse(String json) {
        JSONValue value = JSONParser.parseLenient(json);
        if (value.isObject() != null) {
            value = value.isObject().get("rsc").isArray();
        }
        return value;
    }

    /**
     * @param array
     * @return
     */
    public static long[] jsonArrayToLongArray(JSONArray array) {
        long result[] = new long[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = (long)array.get(i).isNumber().doubleValue();
        }
        return result;
    }

    public static byte[] parseArrayToByteArray(String json) {
        JSONValue value = parse(json);
        if (value.isArray() == null)
            throw new IllegalArgumentException("'json' parameter must represent a JSON array");
        return jsonArrayToByteArray(value.isArray());
    }

    public static byte[] jsonArrayToByteArray(JSONArray array) {
        byte[] result = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = Byte.valueOf(array.get(i).isString().stringValue());
        }
        return result;
    }

    public static char[] parseArrayToCharArray(String json) {
        JSONValue value = parse(json);
        if (value.isArray() == null)
            throw new IllegalArgumentException("'json' parameter must represent a JSON array");
        return jsonArrayToCharArray(value.isArray());
    }

    /**
     * @param array
     * @return
     */
    private static char[] jsonArrayToCharArray(JSONArray array) {
        char[] result = new char[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).isString().stringValue().toCharArray()[0];
        }
        return result;
    }
}
