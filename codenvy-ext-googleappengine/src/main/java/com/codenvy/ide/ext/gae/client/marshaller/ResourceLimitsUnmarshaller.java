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
package com.codenvy.ide.ext.gae.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for the list of {@link com.codenvy.ide.ext.gae.shared.ResourceLimit}.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 31, 2012 4:32:27 PM anya $
 */
public class ResourceLimitsUnmarshaller implements Unmarshallable<JsonArray<ResourceLimit>> {
    private JsonArray<ResourceLimit> resourceLimits;

    /**
     * Constructor for unmarshaller.
     */
    public ResourceLimitsUnmarshaller(JsonArray<ResourceLimit> resourceLimits) {
        this.resourceLimits = resourceLimits;
    }

    /** {@inheritDoc} */
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

            for (String key : json.keySet()) {
                Double value = json.get(key).isNumber().doubleValue();

                DtoClientImpls.ResourceLimitImpl resourceLimit = DtoClientImpls.ResourceLimitImpl.make();
                resourceLimit.setName(key);
                resourceLimit.setValue(value);

                resourceLimits.add(resourceLimit);
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't map with long values.", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<ResourceLimit> getPayload() {
        return resourceLimits;
    }
}