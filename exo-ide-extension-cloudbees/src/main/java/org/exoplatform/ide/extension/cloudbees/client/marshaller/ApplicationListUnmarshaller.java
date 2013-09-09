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
package org.exoplatform.ide.extension.cloudbees.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 */
public class ApplicationListUnmarshaller implements Unmarshallable<List<ApplicationInfo>> {

    private List<ApplicationInfo> apps;

    /** @param apps */
    public ApplicationListUnmarshaller(List<ApplicationInfo> apps) {
        this.apps = apps;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONArray value = JSONParser.parseLenient(response.getText()).isArray();

        if (value == null) {
            return;
        }

        for (int i = 0; i < value.size(); i++) {
            String payload = value.get(i).isObject().toString();

            AutoBean<ApplicationInfo> appInfoBean =
                    AutoBeanCodex.decode(CloudBeesExtension.AUTO_BEAN_FACTORY, ApplicationInfo.class, payload);
            apps.add(appInfoBean.as());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<ApplicationInfo> getPayload() {
        return apps;
    }
}
