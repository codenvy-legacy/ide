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
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderClientServiceImpl.java Feb 21, 2012 12:44:05 PM azatsarynnyy $
 */
@Singleton
public class BuilderClientServiceImpl implements BuilderClientService {
    /** Base url. */
    private static final String BASE_URL = Utils.getWorkspaceName() + "/builder";
    /** Build project method's path. */
    private static final String BUILD    = BASE_URL + "/build";
    /** Build project method's path. */
    private static final String DEPLOY   = BASE_URL + "/deploy";
    /** Cancel building project method's path. */
    private static final String CANCEL   = BASE_URL + "/cancel";
    /** Get result of build method's path. */
    private static final String RESULT   = BASE_URL + "/result";
    /** REST-service context. */
    private String baseUrl;
    /** Loader to be displayed. */
    private Loader loader;

    /**
     * Create service.
     *
     * @param loader
     *         loader to show on server request
     */
    @Inject
    public BuilderClientServiceImpl(@Named("restContext") String restContext,
                                    Loader loader) {
        this.loader = loader;
        this.baseUrl = restContext + "/" + Utils.getWorkspaceName();
    }

    /** {@inheritDoc} */
    @Override
    public void build(String projectName, AsyncRequestCallback<String> callback)
            throws RequestException {
        final String requestUrl = baseUrl + BUILD;

        String params = "project=" + projectName;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cancel(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = baseUrl + CANCEL + "/" + buildid;

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
    public void result(String buildid, AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + RESULT + "/" + buildid;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

}