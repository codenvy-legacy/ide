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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunOptions;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation of {@link RunnerClientService} service.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RunnerClientServiceImpl implements RunnerClientService {
    private final String              baseUrl;
    private final String              workspaceId;
    /** Loader to be displayed. */
    private final Loader              loader;
    private final AsyncRequestFactory asyncRequestFactory;

    /**
     * Create service.
     *
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected RunnerClientServiceImpl(@Named("restContext") String baseUrl, @Named("workspaceId") String workspaceId, Loader loader,
                                      AsyncRequestFactory asyncRequestFactory) {
        this.baseUrl = baseUrl;
        this.workspaceId = workspaceId;
        this.loader = loader;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void run(String projectName, RunOptions runOptions, AsyncRequestCallback<ApplicationProcessDescriptor> callback) {
        final String requestUrl = baseUrl + "/runner/" + workspaceId + "/run";
        String params = "project=" + projectName;
        asyncRequestFactory.createPostRequest(requestUrl + "?" + params, runOptions)
                           .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStatus(Link link, AsyncRequestCallback<ApplicationProcessDescriptor> callback) {
        asyncRequestFactory.createGetRequest(link.getHref()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(Link link, AsyncRequestCallback<String> callback) {
        loader.setMessage("Retrieving logs...");
        asyncRequestFactory.createGetRequest(link.getHref()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(Link link, AsyncRequestCallback<String> callback) {
        loader.setMessage("Stopping an application...");
        asyncRequestFactory.createPostRequest(link.getHref(), null).loader(loader).send(callback);
    }
}
