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
package org.exoplatform.gwtframework.commons.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import java.util.*;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MimeTypeResolver {

    private static final String DEFAULT_MIMETYPE = "application/octet-stream";

    private static HashMap<String, List<String>> mimeTypes;

    private static native JavaScriptObject getMimeTypesConfig() /*-{
        return $wnd.mimeTypes;
    }-*/;

    private static void loadMimeTypes() {
        mimeTypes = new HashMap<String, List<String>>();
        try {
            JSONObject json = new JSONObject(getMimeTypesConfig());

            Iterator<String> iterator = json.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                JSONValue value = json.get(key);
                if (value.isArray() != null) {
                    JSONArray array = value.isArray();
                    List<String> types = new ArrayList<String>();
                    for (int i = 0; i < array.size(); i++) {
                        String mimeType = array.get(i).isString().stringValue();
                        types.add(mimeType);
                    }
                    mimeTypes.put(key, types);

                } else if (value.isString() != null) {
                    String mimeType = value.isString().stringValue();
                    List<String> types = new ArrayList<String>();
                    types.add(mimeType);
                    mimeTypes.put(key, types);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static List<String> getMimeTypes(String fileExtension) {
        if (mimeTypes == null) {
            loadMimeTypes();
        }

        List<String> types = mimeTypes.get(fileExtension);
        if (types == null) {
            types = new ArrayList<String>();
            types.add(DEFAULT_MIMETYPE);
        }

        return types;
    }

    public static Set<String> getAllMimeTypes() {
        if (mimeTypes == null) {
            loadMimeTypes();
        }

        Set<String> setTypes = new HashSet<String>();

        for (String key : mimeTypes.keySet()) {
            for (String t : mimeTypes.get(key)) {
//           types.add(t);
                setTypes.add(t);
            }
        }

        return setTypes;

    }

}
