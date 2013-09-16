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
package org.exoplatform.ide.extension.ssh.client.marshaller;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Unmarshaller for get All Keys request
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeysUnmarshaller May 18, 2011 4:57:42 PM evgen $
 *          <p/>
 *          Changed made for use this marshaler in JsonpRequest. We use it for cross-domain Ajax calls
 */
public class SshKeysUnmarshaller {

    public static List<KeyItem> unmarshal(JavaScriptObject response) throws UnmarshallerException {
        try {
            List<KeyItem> keyItems = new ArrayList<KeyItem>();
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.get(i).isObject();
                KeyItem keyItem = new KeyItem();
                keyItem.setHost(object.get("host").isString().stringValue());
                // check of "publicKeyURL" field, may be null
                if (object.containsKey("publicKeyURL") && object.get("publicKeyURL").isNull() == null) {
                    keyItem.setPublicKeyURL(object.get("publicKeyURL").isString().stringValue());
                }

                if (object.containsKey("removeKeyURL") && object.get("removeKeyURL").isNull() == null)
                    keyItem.setRemoveKeyURL(object.get("removeKeyURL").isString().stringValue());
                keyItems.add(keyItem);
            }
            return keyItems;
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse SSH Keys");
        }
    }

}
