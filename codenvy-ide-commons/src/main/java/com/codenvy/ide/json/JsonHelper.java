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
package com.codenvy.ide.json;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 */
public class JsonHelper {

    public static String toJson(Map<String, String> map) {
        String json = "";
        if (map != null && !map.isEmpty()) {
            JSONObject jsonObj = new JSONObject();
            for (Map.Entry<String, String> entry: map.entrySet()) {
                jsonObj.put(entry.getKey(), new JSONString(entry.getValue()));
            }
            json = jsonObj.toString();
        }
        return json;
    }

    public static Map<String, String> toMap(String jsonStr) {
        Map<String, String> map = new HashMap<String, String>();

        JSONValue parsed = JSONParser.parseStrict(jsonStr);
        JSONObject jsonObj = parsed.isObject();
        if (jsonObj != null) {
            for (String key : jsonObj.keySet()) {
                map.put(key, jsonObj.get(key).toString());
            }
        }

        return map;
    }
}