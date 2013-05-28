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
package com.codenvy.ide.ext.appfog.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.ext.appfog.shared.AppfogServices;
import com.codenvy.ide.ext.appfog.shared.AppfogSystemService;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public class AppfogServicesUnmarshaller implements Unmarshallable<AppfogServices> {
    private final class Keys {
        public static final String SYSTEM      = "appfogSystemService";
        public static final String PROVISIONED = "appfogProvisionedService";
    }

    /** Appfog services (system and provisioned). */
    private DtoClientImpls.AppfogServicesImpl appfogServices;

    /** Create unmarshaller. */
    public AppfogServicesUnmarshaller() {
        appfogServices = DtoClientImpls.AppfogServicesImpl.make();
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();

        if (jsonObject.containsKey(Keys.SYSTEM)) {
            JSONArray systemServices = jsonObject.get(Keys.SYSTEM).isArray();
            if (systemServices.size() > 0) {
                JsonArray<AppfogSystemService> services = JsonCollections.createArray();
                for (int i = 0; i < systemServices.size(); i++) {
                    String value = systemServices.get(i).isObject().toString();
                    DtoClientImpls.AppfogSystemServiceImpl service = DtoClientImpls.AppfogSystemServiceImpl.deserialize(value);
                    services.add(service);
                }
                appfogServices.setAppfogSystemService(services);
            } else {
                appfogServices.setAppfogSystemService(JsonCollections.<AppfogSystemService>createArray());
            }
        }

        if (jsonObject.containsKey(Keys.PROVISIONED)) {
            JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
            if (provisionedServices.size() > 0) {
                JsonArray<AppfogProvisionedService> services = JsonCollections.createArray();
                for (int i = 0; i < provisionedServices.size(); i++) {
                    String value = provisionedServices.get(i).isObject().toString();
                    DtoClientImpls.AppfogProvisionedServiceImpl service = DtoClientImpls.AppfogProvisionedServiceImpl.deserialize(value);
                    services.add(service);
                }
                appfogServices.setAppfogProvisionedService(services);
            } else {
                appfogServices.setAppfogProvisionedService(JsonCollections.<AppfogProvisionedService>createArray());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public AppfogServices getPayload() {
        return appfogServices;
    }
}