/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.MimeType.TEXT_PLAIN;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;

/**
 * The implementation of {@link DebuggerClientService}.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DebuggerClientServiceImpl implements DebuggerClientService {
    /** REST-service context. */
    private final String              baseUrl;
    private final Loader              loader;
    private final AsyncRequestFactory asyncRequestFactory;

    /**
     * Create client service.
     *
     * @param baseUrl
     *         REST-service context
     * @param workspaceId
     * @param loader
     * @param asyncRequestFactory
     */
    @Inject
    protected DebuggerClientServiceImpl(@Named("restContext") String baseUrl, @Named("workspaceId") String workspaceId, Loader loader,
                                        AsyncRequestFactory asyncRequestFactory) {
        this.loader = loader;
        this.asyncRequestFactory = asyncRequestFactory;
        this.baseUrl = baseUrl + "/debug-java/" + workspaceId;
    }

    /** {@inheritDoc} */
    @Override
    public void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<DebuggerInfo> callback) {
        final String requestUrl = baseUrl + "/connect";
        final String params = "?host=" + host + "&port=" + port;
        asyncRequestFactory.createGetRequest(requestUrl + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void disconnect(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/disconnect/" + id;
        loader.setMessage("Disconnecting... ");
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/breakpoints/add/" + id;
        asyncRequestFactory.createPostRequest(requestUrl, breakPoint).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) {
        final String requestUrl = baseUrl + "/breakpoints/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/breakpoints/delete/" + id;
        asyncRequestFactory.createPostRequest(requestUrl, breakPoint).loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) {
        final String requestUrl = baseUrl + "/breakpoints/delete_all/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<DebuggerEventList> callback) {
        final String requestUrl = baseUrl + "/events/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStackFrameDump(@NotNull String id, @NotNull AsyncRequestCallback<StackFrameDump> callback) {
        final String requestUrl = baseUrl + "/dump/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void resume(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/resume/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<Value> callback) {
        final String requestUrl = baseUrl + "/value/get/" + id;
        asyncRequestFactory.createPostRequest(requestUrl, var.getVariablePath()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/value/set/" + id;
        asyncRequestFactory.createPostRequest(requestUrl, request).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepInto(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/step/into/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepOver(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/step/over/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stepReturn(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + "/step/out/" + id;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<String> callback) {
        final String requestUrl = baseUrl + "/expression/" + id;
        asyncRequestFactory.createPostRequest(requestUrl, null)
                           .data(expression)
                           .header(ACCEPT, TEXT_PLAIN)
                           .header(CONTENTTYPE, TEXT_PLAIN)
                           .loader(new EmptyLoader())
                           .send(callback);
    }
}