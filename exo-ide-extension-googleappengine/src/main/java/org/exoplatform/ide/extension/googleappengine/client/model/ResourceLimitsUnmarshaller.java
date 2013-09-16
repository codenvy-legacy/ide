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
package org.exoplatform.ide.extension.googleappengine.client.model;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 31, 2012 4:32:27 PM anya $
 */
public class ResourceLimitsUnmarshaller implements Unmarshallable<List<ResourceLimit>> {
    private List<ResourceLimit> resourceLimits;

    public ResourceLimitsUnmarshaller(List<ResourceLimit> resourceLimits) {
        this.resourceLimits = resourceLimits;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            if (response.getText() == null || response.getText().isEmpty()) {
                return;
            }

            JSONObject json = JSONParser.parseLenient(response.getText()).isObject();

            if (json == null) {
                return;
            }

            Iterator<String> keysIterator = json.keySet().iterator();
            while (keysIterator.hasNext()) {
                String key = keysIterator.next();
                Long value = (long)json.get(key).isNumber().doubleValue();
                resourceLimits.add(new ResourceLimit(key, value));
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't map with long values.");
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public List<ResourceLimit> getPayload() {
        return resourceLimits;
    }
}
