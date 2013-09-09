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
package org.exoplatform.ide.extension.heroku.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for application information from JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 30, 2011 11:00:13 AM anya $
 */
public class ApplicationInfoUnmarshaller implements Unmarshallable<List<Property>> {
    private List<Property> properties;

    /**
     * @param applicationInfo
     *         application's information
     */
    public ApplicationInfoUnmarshaller(List<Property> properties) {
        this.properties = properties;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONValue json = JSONParser.parseStrict(response.getText());
        if (json == null)
            return;
        JSONObject jsonObject = json.isObject();
        if (jsonObject == null)
            return;

        for (String key : jsonObject.keySet()) {
            if (jsonObject.get(key).isString() != null) {
                String value = jsonObject.get(key).isString().stringValue();
                properties.add(new Property(key, value));
            }
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<Property> getPayload() {
        return properties;
    }

}
