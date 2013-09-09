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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;
import org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable;

import java.util.List;

/**
 * Unmarshaller for application information from JSON format from {@link ResponseMessage}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfoUnmarshallerWS.java Nov 29, 2012 12:43:13 PM azatsarynnyy $
 */
public class ApplicationInfoUnmarshallerWS implements Unmarshallable<List<Property>> {
    private List<Property> properties;

    /**
     * @param applicationInfo
     *         application's information
     */
    public ApplicationInfoUnmarshallerWS(List<Property> properties) {
        this.properties = properties;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket
     * .rest.ResponseMessage) */
    @Override
    public void unmarshal(ResponseMessage response) throws UnmarshallerException {
        if (response.getBody() == null || response.getBody().isEmpty()) {
            return;
        }

        JSONValue json = JSONParser.parseStrict(response.getBody());
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

    /** @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#getPayload() */
    @Override
    public List<Property> getPayload() {
        return properties;
    }

}
