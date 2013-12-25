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
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Implementation of {@link RunnerClientService} service.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnerClientServiceImpl.java Jul 3, 2013 12:50:30 PM azatsarynnyy $
 */
@Singleton
public class RunnerClientServiceImpl implements RunnerClientService {
    private final String baseUrl;
    /** Loader to be displayed. */
    private       Loader loader;

    /**
     * Create service.
     *
     * @param loader
     *         loader to show on server request
     */
    @Inject
    protected RunnerClientServiceImpl(@Named("restContext") String restContext,
                                      Loader loader) {
        this.loader = loader;
        this.baseUrl = restContext + "/" + Utils.getWorkspaceName();
    }

    /** {@inheritDoc} */
    @Override
    public void run(String projectName, AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = baseUrl + "/runner/run";

        String params = "project=" + projectName;
        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStatus(Link link, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(Link link, AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Retrieving logs...");
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(Link link, AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Stopping an application...");
        AsyncRequest.build(RequestBuilder.POST, link.getHref()).loader(loader).send(callback);
    }
}
