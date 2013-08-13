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
package com.codenvy.ide.extension.cloudfoundry.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemService;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for CloudFoundry services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 2:07:24 PM anya $
 */
public class CloudFoundryServicesUnmarshaller implements Unmarshallable<CloudFoundryServices> {
    private final class Keys {
        public static final String SYSTEM      = "system";
        public static final String PROVISIONED = "provisioned";
    }

    /** CloudFoundry services (system and provisioned). */
    private DtoClientImpls.CloudFoundryServicesImpl cloudfoundryServices;

    /** Create unmarshaller. */
    public CloudFoundryServicesUnmarshaller() {
        this.cloudfoundryServices = DtoClientImpls.CloudFoundryServicesImpl.make();
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
                JsonArray<SystemService> services = JsonCollections.createArray();
                for (int i = 0; i < systemServices.size(); i++) {
                    String value = systemServices.get(i).isObject().toString();
                    DtoClientImpls.SystemServiceImpl service = DtoClientImpls.SystemServiceImpl.deserialize(value);
                    services.add(service);
                }
                cloudfoundryServices.setSystem(services);
            } else {
                cloudfoundryServices.setSystem(JsonCollections.<SystemService>createArray());
            }
        }

        if (jsonObject.containsKey(Keys.PROVISIONED)) {
            JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
            if (provisionedServices.size() > 0) {
                JsonArray<ProvisionedService> services = JsonCollections.createArray();
                for (int i = 0; i < provisionedServices.size(); i++) {
                    String value = provisionedServices.get(i).isObject().toString();
                    DtoClientImpls.ProvisionedServiceImpl service = DtoClientImpls.ProvisionedServiceImpl.deserialize(value);
                    services.add(service);
                }
                cloudfoundryServices.setProvisioned(services);
            } else {
                cloudfoundryServices.setProvisioned(JsonCollections.<ProvisionedService>createArray());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public CloudFoundryServices getPayload() {
        return cloudfoundryServices;
    }
}
