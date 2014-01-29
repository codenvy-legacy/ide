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
package com.codenvy.ide.projecttype;

import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;

/**
 * Client service to get information about registered project types.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTypeDescriptionClientService {
    private static final String BASE_URL         = "/project-description";
    private static final String GET_DESCRIPTIONS = BASE_URL + "/descriptions";
    private String restContext;
    private Loader loader;

    @Inject
    protected ProjectTypeDescriptionClientService(@Named("restContext") String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
    }

    /**
     * Get information about all registered project types.
     *
     * @param callback
     * @throws RequestException
     */
    public void getProjectTypes(AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + GET_DESCRIPTIONS;
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(ACCEPT, APPLICATION_JSON).loader(loader).send(callback);
    }
}