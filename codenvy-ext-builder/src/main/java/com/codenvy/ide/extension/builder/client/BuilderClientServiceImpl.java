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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
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
    private final String baseUrl;
    /** Loader to be displayed. */
    private final Loader loader;

    /**
     * Create service.
     *
     * @param loader
     *         loader to show on server request
     */
    @Inject
    public BuilderClientServiceImpl(@Named("restContext") String baseUrl, Loader loader) {
        this.baseUrl = baseUrl+ "/builder/" + Utils.getWorkspaceId();
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void build(String projectName, AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/build";
        String params = "project=" + projectName;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cancel(String buildId, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = baseUrl + "/cancel/" + buildId;
        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(Link link, AsyncRequestCallback<String> callback) throws RequestException {
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(Link link, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void result(String buildId, AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/result/" + buildId;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

}