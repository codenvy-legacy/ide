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
package org.exoplatform.ide.client.framework.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationSettings {

    public enum Store {

        COOKIES, SERVER, NONE

    }

    private HashMap<String, Store> stores = new HashMap<String, Store>();

    /*
     * Values can be only next types: String, Integer, Boolean, Map, List
     */
    private HashMap<String, Object> values = new HashMap<String, Object>();

    public Map<String, Object> getValues() {
        return values;
    }

    public boolean containsKey(String key) {
        return values.containsKey(key);
    }

    public void setValue(String key, Object value, Store store) {
        values.put(key, value);
        stores.put(key, store);
    }

    public Object getValueAsObject(String key) {
        return values.get(key);
    }

    public String getValueAsString(String key) {
        return (String)values.get(key);
    }

    public Integer getValueAsInteger(String key) {
        if (values.get(key) == null) {
            return 0;
        }

        return Integer.valueOf((String)values.get(key));
    }

    public Boolean getValueAsBoolean(String key) {
        if (values.get(key) == null) {
            return null;
        }

        return (Boolean)values.get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getValueAsList(String key) {
        if (values.get(key) == null) {
            return null;
        }

        return (List<String>)(values.get(key));
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getValueAsMap(String key) {
        if (values.get(key) == null) {
            return null;
        }

        return (Map<String, String>)(values.get(key));
    }

    public Store getStore(String key) {
        if (stores.get(key) == null) {
            return Store.NONE;
        }

        return stores.get(key);
    }

}
