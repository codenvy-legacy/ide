/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.core.util;

import com.google.gwt.json.client.JSONArray;
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
    public static long[] parseJsonAsLongArray(JSONValue json) {
        JSONValue value = parse(json);
        if (value.isArray() == null)
            throw new IllegalArgumentException("'json' parameter must represent a JSON array");
        return jsonArrayToLongArray(value.isArray());
    }

    /**
     * @param json
     * @return
     */
    private static JSONValue parse(JSONValue value) {
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

    public static byte[] parseArrayToByteArray(JSONValue value) {
        value = parse(value);
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

    public static char[] parseArrayToCharArray(JSONValue json) {
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
