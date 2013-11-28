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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.shared.*;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.rest.HTTPHeader.*;
import static com.codenvy.ide.rest.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.MimeType.TEXT_PLAIN;
import static com.google.gwt.http.client.RequestBuilder.GET;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link DebuggerClientService}.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
@Singleton
public class DebuggerClientServiceImpl implements DebuggerClientService {
    private static String BASE_URL;
    private        Loader loader;
    private DtoFactory dtoFactory;

    /**
     * Create client service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected DebuggerClientServiceImpl(@Named("restContext") String restContext, Loader loader, DtoFactory dtoFactory) {
        this.dtoFactory = dtoFactory;
        BASE_URL = restContext + '/' + Utils.getWorkspaceName() + "/java/debug";
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String params = "host=" + host + "&port=" + port;
        AsyncRequest.build(GET, BASE_URL + "/connect?" + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("DisConnection... ");
        AsyncRequest.build(GET, BASE_URL + "/disconnect/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String json = dtoFactory.toJson(breakPoint);
        AsyncRequest.build(POST, BASE_URL + "/breakpoints/add/" + id).data(json)
                    .header("Content-Type", "application/json").loader(loader).send(callback);
    }


    /** {@inheritDoc} */
    @Override
    public void deleteBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        String json = dtoFactory.toJson(breakPoint);
        AsyncRequest.build(POST, BASE_URL + "/breakpoints/delete/" + id).data(json)
                    .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getBreakPoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/breakpoints/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/events/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void dump(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/dump/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void resume(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/resume/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String json = dtoFactory.toJson(var);
        AsyncRequest.build(POST, BASE_URL + "/value/get/" + id).data(json)
                    .header("Content-Type", "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String json = dtoFactory.toJson(request);
        AsyncRequest.build(POST, BASE_URL + "/value/set/" + id).data(json)
                    .header(CONTENT_TYPE, APPLICATION_JSON).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepInto(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/step/into/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepOver(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/step/over/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepReturn(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/step/out/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stopApplication(@NotNull ApplicationInstance runningApp, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        AsyncRequest.build(GET, runningApp.getStopURL()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAllBreakPoint(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/breakpoints/delete_all/" + id).loader(loader)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        AsyncRequest.build(POST, BASE_URL + "/expression/" + id).data(expression)
                    .header(ACCEPT, TEXT_PLAIN).header(CONTENTTYPE, TEXT_PLAIN)
                    .loader(new EmptyLoader()).send(callback);
    }
}