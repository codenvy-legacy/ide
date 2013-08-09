/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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