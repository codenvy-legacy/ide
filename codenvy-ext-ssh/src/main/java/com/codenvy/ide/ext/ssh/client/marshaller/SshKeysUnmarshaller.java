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
package com.codenvy.ide.ext.ssh.client.marshaller;

import com.codenvy.ide.ext.ssh.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

/**
 * Unmarshaller for get All Keys request
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeysUnmarshaller May 18, 2011 4:57:42 PM evgen $
 *          <p/>
 *          Changed made for use this marshaler in JsonpRequest. We use it for cross-domain Ajax calls
 */
public class SshKeysUnmarshaller {
    public static JsonArray<KeyItem> unmarshal(JavaScriptObject response) {
        JsonArray<KeyItem> keyItems = JsonCollections.createArray();
        JSONArray array = new JSONArray(response);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();
            String value = object.toString();

            DtoClientImpls.KeyItemImpl keyItem = DtoClientImpls.KeyItemImpl.deserialize(value);
            keyItems.add(keyItem);
        }
        return keyItems;
    }
}