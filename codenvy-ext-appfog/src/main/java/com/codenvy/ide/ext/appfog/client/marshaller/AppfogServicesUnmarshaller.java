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