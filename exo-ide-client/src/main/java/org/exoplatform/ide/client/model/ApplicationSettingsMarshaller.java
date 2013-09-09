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
package org.exoplatform.ide.client.model;

import com.google.gwt.json.client.*;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsMarshaller implements Marshallable {

    private ApplicationSettings applicationSettings;

    public ApplicationSettingsMarshaller(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public String marshal() {
        JSONObject settings = new JSONObject();
        Map<String, Object> valueMap = applicationSettings.getValues();
        Iterator<String> keyIter = valueMap.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();

            if (applicationSettings.getStore(key) != Store.SERVER) {
                continue;
            }

            Object value = valueMap.get(key);
            if (value instanceof String) {
                settings.put(key, new JSONString((String)value));
            } else if (value instanceof Integer) {
                settings.put(key, new JSONNumber((Double)value));
            } else if (value instanceof Boolean) {
                settings.put(key, JSONBoolean.getInstance((Boolean)value));
            } else if (value instanceof List) {
                settings.put(key, getListNode(value));
            } else if (value instanceof Map) {
                settings.put(key, getMapNode(value));
            }
        }
        return settings.toString();
    }

    @SuppressWarnings("unchecked")
    private JSONArray getListNode(Object value) {
        JSONArray array = new JSONArray();
        List<String> values = (List<String>)value;
        int index = 0;
        for (String v : values) {
            array.set(index, new JSONString(v));
            index++;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private JSONObject getMapNode(Object value) {
        JSONObject map = new JSONObject();
        Map<String, String> values = (Map<String, String>)value;
        Iterator<String> keyIter = values.keySet().iterator();
        while (keyIter.hasNext()) {
            String k = keyIter.next();
            String v = values.get(k);
            map.put(k, new JSONString(v));
        }
        return map;
    }
}
