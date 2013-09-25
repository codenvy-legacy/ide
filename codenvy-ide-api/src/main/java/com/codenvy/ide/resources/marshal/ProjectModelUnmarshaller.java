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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


/**
 * Unmarshaller for {@link Project}
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectModelUnmarshaller implements Unmarshallable<ProjectModelProviderAdapter> {
    private       ProjectModelProviderAdapter modelProviderAdapter;
    private final ResourceProvider            resourceProvider;

    public ProjectModelUnmarshaller(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            // Read Primary nature of the project
            JSONObject jsonObject = JSONParser.parseLenient(response.getText()).isObject();
            JsonArray<Property> properties = JSONDeserializer.PROPERTY_DESERIALIZER.toList(jsonObject.get("properties"));

            // Create project instance using ModelProvider
            modelProviderAdapter = new ProjectModelProviderAdapter(resourceProvider);
            modelProviderAdapter.init(properties).init(jsonObject);
        } catch (Exception exc) {
            String message = "Can't parse item " + response.getText();
            throw new UnmarshallerException(message, exc);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ProjectModelProviderAdapter getPayload() {
        return this.modelProviderAdapter;
    }
}
