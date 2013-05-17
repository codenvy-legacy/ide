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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.appfog.client.AppfogAutoBeanFactory;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.ext.appfog.shared.AppfogServices;
import com.codenvy.ide.ext.appfog.shared.AppfogSystemService;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Unmarshaller for Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public class AppfogServicesUnmarshaller implements Unmarshallable<AppfogServices> {
    private final class Keys {
        public static final String SYSTEM = "appfogSystemService";

        public static final String PROVISIONED = "appfogProvisionedService";
    }

    /** Appfog services (system and provisioned). */
    private AppfogServices        appfogServices;
    private AppfogAutoBeanFactory autoBeanFactory;

    /**
     * Create unmarshaller.
     *
     * @param autoBeanFactory
     */
    public AppfogServicesUnmarshaller(AppfogAutoBeanFactory autoBeanFactory) {
        this.autoBeanFactory = autoBeanFactory;

        appfogServices = autoBeanFactory.services().as();
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
                AppfogSystemService[] services = new AppfogSystemService[systemServices.size()];
                for (int i = 0; i < systemServices.size(); i++) {
                    String value = systemServices.get(i).isObject().toString();
                    services[i] = AutoBeanCodex.decode(autoBeanFactory, AppfogSystemService.class, value).as();
                }
                appfogServices.setAppfogSystemService(services);
            } else {
                appfogServices.setAppfogSystemService(new AppfogSystemService[0]);
            }
        }

        if (jsonObject.containsKey(Keys.PROVISIONED)) {
            JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
            if (provisionedServices.size() > 0) {
                AppfogProvisionedService[] services = new AppfogProvisionedService[provisionedServices.size()];
                for (int i = 0; i < provisionedServices.size(); i++) {
                    String value = provisionedServices.get(i).isObject().toString();
                    services[i] =
                            AutoBeanCodex.decode(autoBeanFactory, AppfogProvisionedService.class, value).as();
                }
                appfogServices.setAppfogProvisionedService(services);
            } else {
                appfogServices.setAppfogProvisionedService(new AppfogProvisionedService[0]);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public AppfogServices getPayload() {
        return appfogServices;
    }
}