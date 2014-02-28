/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

/**
 * Implementation of {@link ExtensionsClientService} service.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ExtensionsClientServiceImpl implements ExtensionsClientService {
    /** REST-service context. */
    private final String              restContext;
    private final String              workspaceId;
    /** Loader to be displayed. */
    private final Loader              loader;
    private final AsyncRequestFactory asyncRequestFactory;

    /**
     * Create service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected ExtensionsClientServiceImpl(@Named("restContext") String restContext, @Named("workspaceId") String workspaceId,
                                          Loader loader, AsyncRequestFactory asyncRequestFactory) {
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.loader = loader;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void launch(@NotNull String projectName, @NotNull AsyncRequestCallback<ApplicationProcessDescriptor> callback) {
        final String requestUrl = restContext + "/runner/" + workspaceId + "/run";
        String params = "project=" + projectName;
        asyncRequestFactory.createPostRequest(requestUrl + "?" + params, null).header("content-type", "application/json").data("{}")
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStatus(@NotNull Link link, @NotNull AsyncRequestCallback<ApplicationProcessDescriptor> callback) {
        asyncRequestFactory.createGetRequest(link.getHref()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) {
        loader.setMessage("Retrieving logs...");
        asyncRequestFactory.createGetRequest(link.getHref()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) {
        loader.setMessage("Stopping an application...");
        asyncRequestFactory.createPostRequest(link.getHref(), null).loader(loader).send(callback);
    }
}