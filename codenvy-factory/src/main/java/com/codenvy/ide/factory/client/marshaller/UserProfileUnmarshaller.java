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

package com.codenvy.ide.factory.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.Map;

/**
 * Parsing information about current logged in user. Getting first and last name from response and construct map from theese values.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 23.09.13 vlad $
 */
public class UserProfileUnmarshaller implements Unmarshallable<Map<String, String>> {
    private Map<String, String> profileFields;

    public UserProfileUnmarshaller(Map<String, String> profileFields) {
        this.profileFields = profileFields;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject user = JSONParser.parseStrict(response.getText()).isObject();
        if (user == null || user.size() <= 0) {
            return;
        }

        JSONObject profileObject = user.get("profile").isObject();
        JSONObject profileAttributes = profileObject.get("attributes").isObject();

        profileFields.put("firstName", profileAttributes.get("firstName").isString().stringValue());
        profileFields.put("lastName", profileAttributes.get("lastName").isString().stringValue());
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getPayload() {
        return profileFields;
    }
}
