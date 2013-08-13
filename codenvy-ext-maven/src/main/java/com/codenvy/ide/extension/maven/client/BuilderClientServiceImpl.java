/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.client;

import com.codenvy.ide.extension.maven.shared.BuildStatus;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.EmptyLoader;
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
    private static final String BASE_URL = '/' + Utils.getWorkspaceName() + "/maven";
    /** Build project method's path. */
    private static final String BUILD    = BASE_URL + "/build";
    /** Build project method's path. */
    private static final String DEPLOY   = BASE_URL + "/deploy";
    /** Cancel building project method's path. */
    private static final String CANCEL   = BASE_URL + "/cancel";
    /** Get status of build method's path. */
    private static final String STATUS   = BASE_URL + "/status";
    /** Get result of build method's path. */
    private static final String RESULT   = BASE_URL + "/result";
    /** Get build log method's path. */
    private static final String LOG      = BASE_URL + "/log";
    /** REST-service context. */
    private String restServiceContext;
    /** Loader to be displayed. */
    private Loader loader;

    /**
     * Create service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected BuilderClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.loader = loader;
        this.restServiceContext = restContext;
    }

    /** {@inheritDoc} */
    @Override
    public void build(String projectId, String vfsId, String projectName, String projectType, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        final String requesrUrl = restServiceContext + BUILD;

        String params = "vfsid=" + vfsId + "&projectid=" + projectId + "&name=" + projectName + "&type=" + projectType;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void buildAndPublish(String projectId, String vfsId, String projectName, String projectType,
                                AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        final String requesrUrl = restServiceContext + DEPLOY;

        String params = "vfsid=" + vfsId + "&projectid=" + projectId + "&name=" + projectName + "&type=" + projectType;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cancel(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + CANCEL + "/" + buildid;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void status(String buildid, AsyncRequestCallback<BuildStatus> callback) throws RequestException {
        final String requestUrl = restServiceContext + STATUS + "/" + buildid;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + LOG + "/" + buildid;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void result(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String requestUrl = restServiceContext + RESULT + "/" + buildid;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void checkArtifactUrl(String url, AsyncRequestCallback<Object> callback) throws RequestException {
        final String requestUrl = restServiceContext + "/ide/maven/check_download_url?url=" + url;
        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(new EmptyLoader()).send(callback);
    }
}