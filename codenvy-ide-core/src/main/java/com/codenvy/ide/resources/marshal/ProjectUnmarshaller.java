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

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.event.shared.EventBus;

public class ProjectUnmarshaller implements Unmarshallable<Project> {
    private final EventBus            eventBus;
    private final AsyncRequestFactory asyncRequestFactory;
    private       Project             item;

    public ProjectUnmarshaller(EventBus eventBus, AsyncRequestFactory asyncRequestFactory) {
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            item = new Project(eventBus, asyncRequestFactory);
            item.init(JSONParser.parseLenient(response.getText()).isObject());
        } catch (Exception exc) {
            String message = "Can't parse item " + response.getText();
            throw new UnmarshallerException(message, exc);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Project getPayload() {
        return this.item;
    }
}