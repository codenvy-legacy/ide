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
import com.codenvy.ide.api.resources.ResourceProvider;
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

import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of {@link UnzipTemplateClientService} service.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class UnzipTemplateClientServiceImpl implements UnzipTemplateClientService {
    private static final String BASE_URL             = "/create-extension/" + Utils.getWorkspaceName();
    private static final String UNPACK_GIST_TEMPLATE = BASE_URL + "/template/gist";
    /** REST-service context. */
    private final String           restContext;
    /** Loader to be displayed. */
    private final Loader           loader;
    /** Provider of IDE resources. */
    private final ResourceProvider resourceProvider;

    /**
     * Create service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     * @param resourceProvider
     *         provider of IDE resources
     */
    @Inject
    protected UnzipTemplateClientServiceImpl(@Named("restContext") String restContext, Loader loader, ResourceProvider resourceProvider) {
        this.loader = loader;
        this.restContext = restContext;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void unzipGistTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_GIST_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void launch(@NotNull String projectName, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + "/runner/" + Utils.getWorkspaceName() + "/run";
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