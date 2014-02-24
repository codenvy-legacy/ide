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
package com.codenvy.ide.extension.builder.client;

import com.codenvy.api.builder.dto.BuildOptions;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation of {@link BuilderClientService} service.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class BuilderClientServiceImpl implements BuilderClientService {
    /** REST-service context. */
    private final String              baseUrl;
    /** Loader to be displayed. */
    private final Loader              loader;
    private final AsyncRequestFactory asyncRequestFactory;

    /** Create service. */
    @Inject
    public BuilderClientServiceImpl(@Named("restContext") String baseUrl, @Named("workspaceId") String workspaceId, Loader loader,
                                    AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.baseUrl = baseUrl + "/builder/" + workspaceId;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void build(String projectName, AsyncRequestCallback<BuildTaskDescriptor> callback) {
        build(projectName, null, callback);
    }

    @Override
    public void build(String projectName, BuildOptions buildOptions, AsyncRequestCallback<BuildTaskDescriptor> callback) {
        final String requestUrl = baseUrl + "/build";
        String params = "project=" + projectName;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        asyncRequestFactory.createPostRequest(requestUrl + "?" + params, buildOptions).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cancel(String buildId, AsyncRequestCallback<StringBuilder> callback) {
        final String requestUrl = baseUrl + "/cancel/" + buildId;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader)
                           .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(Link link, AsyncRequestCallback<String> callback) {
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        asyncRequestFactory.createGetRequest(link.getHref())
                           .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(Link link, AsyncRequestCallback<String> callback) {
        asyncRequestFactory.createGetRequest(link.getHref()).loader(loader)
                           .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void result(String buildId, AsyncRequestCallback<String> callback) {
        final String requestUrl = baseUrl + "/result/" + buildId;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        asyncRequestFactory.createGetRequest(requestUrl)
                           .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                           .send(callback);
    }

}