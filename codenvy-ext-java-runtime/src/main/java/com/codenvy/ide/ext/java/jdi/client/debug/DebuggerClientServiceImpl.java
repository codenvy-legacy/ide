/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
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

    /**
     * Create client service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected DebuggerClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        BASE_URL = restContext + '/' + Utils.getWorkspaceName() + "/java/debug";
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<DebuggerInfo> callback) throws RequestException {
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
    public void addBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<BreakPoint> callback)
            throws RequestException {
        DtoClientImpls.BreakPointImpl jso = (DtoClientImpls.BreakPointImpl)breakPoint;
        String json = DtoClientImpls.BreakPointImpl.serialize(jso);
        AsyncRequest.build(POST, BASE_URL + "/breakpoints/add/" + id).data(json)
                    .header("Content-Type", "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<BreakPoint> callback)
            throws RequestException {
        DtoClientImpls.BreakPointImpl jso = (DtoClientImpls.BreakPointImpl)breakPoint;
        String json = DtoClientImpls.BreakPointImpl.serialize(jso);
        AsyncRequest.build(POST, BASE_URL + "/breakpoints/delete/" + id).data(json)
                    .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getBreakPoints(@NotNull String id, @NotNull AsyncRequestCallback<BreakPointList> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/breakpoints/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<DebuggerEventList> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/events/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void dump(@NotNull String id, @NotNull AsyncRequestCallback<StackFrameDump> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/dump/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void resume(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(GET, BASE_URL + "/resume/" + id).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<Value> callback) throws RequestException {
        DtoClientImpls.VariablePathImpl jso = (DtoClientImpls.VariablePathImpl)var.getVariablePath();
        String json = DtoClientImpls.VariablePathImpl.serialize(jso);
        AsyncRequest.build(POST, BASE_URL + "/value/get/" + id).data(json)
                    .header("Content-Type", "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        DtoClientImpls.UpdateVariableRequestImpl jso = (DtoClientImpls.UpdateVariableRequestImpl)request;
        String json = DtoClientImpls.UpdateVariableRequestImpl.serialize(jso);
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
    public void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        AsyncRequest.build(POST, BASE_URL + "/expression/" + id).data(expression)
                    .header(ACCEPT, TEXT_PLAIN).header(CONTENTTYPE, TEXT_PLAIN)
                    .loader(new EmptyLoader()).send(callback);
    }
}