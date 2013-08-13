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
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for CloudFoundry application list.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 18, 2011 evgen $
 */
public class ApplicationListUnmarshaller implements Unmarshallable<JsonArray<CloudFoundryApplication>> {
    private JsonArray<CloudFoundryApplication> apps;

    /**
     * Create unmarshaller.
     *
     * @param apps
     */
    public ApplicationListUnmarshaller(JsonArray<CloudFoundryApplication> apps) {
        this.apps = apps;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getText() == null || response.getText().isEmpty()) {
            return;
        }

        JSONArray array = JSONParser.parseLenient(response.getText()).isArray();

        if (array == null) {
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.get(i).isObject();
            String value = (jsonObject.isObject() != null) ? jsonObject.isObject().toString() : "";

            DtoClientImpls.CloudFoundryApplicationImpl application = DtoClientImpls.CloudFoundryApplicationImpl.deserialize(value);

            apps.add(application);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<CloudFoundryApplication> getPayload() {
        return apps;
    }
}