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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;

/**
 * Unmarshaller for CloudFoundry services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 2:07:24 PM anya $
 */
public class CloudFoundryServicesUnmarshaller implements Unmarshallable<CloudfoundryServices> {
    /** CloudFoundry services (system and provisioned). */
    private CloudfoundryServices cloudfoundryServices;

    private final class Keys {
        public static final String SYSTEM = "system";

        public static final String PROVISIONED = "provisioned";
    }

    public CloudFoundryServicesUnmarshaller() {
        cloudfoundryServices = CloudFoundryExtension.AUTO_BEAN_FACTORY.services().as();
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();

        if (jsonObject.containsKey(Keys.SYSTEM)) {
            JSONArray systemServices = jsonObject.get(Keys.SYSTEM).isArray();
            if (systemServices.size() > 0) {
                SystemService[] services = new SystemService[systemServices.size()];
                for (int i = 0; i < systemServices.size(); i++) {
                    String value = systemServices.get(i).isObject().toString();
                    services[i] =
                            AutoBeanCodex.decode(CloudFoundryExtension.AUTO_BEAN_FACTORY, SystemService.class, value).as();
                }
                cloudfoundryServices.setSystem(services);
            } else {
                cloudfoundryServices.setSystem(new SystemService[0]);
            }
        }

        if (jsonObject.containsKey(Keys.PROVISIONED)) {
            JSONArray provisionedServices = jsonObject.get(Keys.PROVISIONED).isArray();
            if (provisionedServices.size() > 0) {
                ProvisionedService[] services = new ProvisionedService[provisionedServices.size()];
                for (int i = 0; i < provisionedServices.size(); i++) {
                    String value = provisionedServices.get(i).isObject().toString();
                    services[i] =
                            AutoBeanCodex.decode(CloudFoundryExtension.AUTO_BEAN_FACTORY, ProvisionedService.class, value).as();
                }
                cloudfoundryServices.setProvisioned(services);
            } else {
                cloudfoundryServices.setProvisioned(new ProvisionedService[0]);
            }
        }

    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public CloudfoundryServices getPayload() {
        return cloudfoundryServices;
    }
}
