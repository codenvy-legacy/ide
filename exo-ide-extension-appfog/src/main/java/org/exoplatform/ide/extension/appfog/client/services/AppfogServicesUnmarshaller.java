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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;

/**
 * Unmarshaller for Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogServicesUnmarshaller implements Unmarshallable<AppfogServices> {
    /** Appfog services (system and provisioned). */
    private AppfogServices appfogServices;

    private final class Keys {
        public static final String SYSTEM = "appfogSystemService";

        public static final String PROVISIONED = "appfogProvisionedService";
    }

    public AppfogServicesUnmarshaller() {
        appfogServices = AppfogExtension.AUTO_BEAN_FACTORY.services().as();
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
                AppfogSystemService[] services = new AppfogSystemService[systemServices.size()];
                for (int i = 0; i < systemServices.size(); i++) {
                    String value = systemServices.get(i).isObject().toString();
                    services[i] =
                            AutoBeanCodex.decode(AppfogExtension.AUTO_BEAN_FACTORY, AppfogSystemService.class, value).as();
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
                            AutoBeanCodex.decode(AppfogExtension.AUTO_BEAN_FACTORY, AppfogProvisionedService.class, value).as();
                }
                appfogServices.setAppfogProvisionedService(services);
            } else {
                appfogServices.setAppfogProvisionedService(new AppfogProvisionedService[0]);
            }
        }

    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    @Override
    public AppfogServices getPayload() {
        return appfogServices;
    }
}
