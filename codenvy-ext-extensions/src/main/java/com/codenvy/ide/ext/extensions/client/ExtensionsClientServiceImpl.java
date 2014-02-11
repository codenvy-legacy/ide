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
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
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
    private final String restContext;
    /** Loader to be displayed. */
    private final Loader loader;

    /**
     * Create service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected ExtensionsClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.loader = loader;
        this.restContext = restContext;
    }

    /** {@inheritDoc} */
    @Override
    public void launch(@NotNull String projectName, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + "/runner/" + Utils.getWorkspaceId() + "/run";
        String params = "project=" + projectName;
        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStatus(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Retrieving logs...");
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Stopping an application...");
        AsyncRequest.build(RequestBuilder.POST, link.getHref()).loader(loader).send(callback);
    }
}