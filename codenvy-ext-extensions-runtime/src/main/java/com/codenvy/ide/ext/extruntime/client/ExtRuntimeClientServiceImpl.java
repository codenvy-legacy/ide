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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.resources.marshal.JSONSerializer.PROPERTY_SERIALIZER;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of {@link ExtRuntimeClientService} service.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeClientServiceImpl.java Jul 3, 2013 12:50:30 PM azatsarynnyy $
 */
@Singleton
public class ExtRuntimeClientServiceImpl implements ExtRuntimeClientService {
    /** Base url. */
    private static final String BASE_URL = "/ide/extruntime";

    private static final String CREATE   = "/create";
    /** Launch method's path. */
    private static final String LAUNCH   = "/launch";
    /** Stop method's path. */
    private static final String STOP     = "/stop";
    /** Get logs method's path. */
    private static final String LOGS     = "/logs";
    /** REST-service context. */
    private String              restContext;
    /** Loader to be displayed. */
    private Loader              loader;
    /** Provider of IDE resources. */
    private ResourceProvider    resourceProvider;
    /** Message bus to communicate through WebSocket. */
    private MessageBus          wsMessageBus;

    /**
     * Create service.
     * 
     * @param wsMessageBus message bus to communicate through WebSocket
     * @param restContext REST-service context
     * @param loader loader to show on server request
     * @param resourceProvider provider of IDE resources
     */
    @Inject
    protected ExtRuntimeClientServiceImpl(MessageBus wsMessageBus,
                                          @Named("restContext") String restContext,
                                          Loader loader,
                                          ResourceProvider resourceProvider) {
        this.loader = loader;
        this.restContext = restContext;
        this.resourceProvider = resourceProvider;
        this.wsMessageBus = wsMessageBus;
    }

    /** {@inheritDoc} */
    @Override
    public void createCodenvyExtensionProject(String projectName,
                                              JsonArray<Property> properties,
                                              String groupId,
                                              String artifactId,
                                              String version,
                                              AsyncRequestCallback<Void> callback)
                                                                                  throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" + resourceProvider.getRootId()
                             + "&groupid=" + groupId + "&artifactid=" + artifactId + "&version=" + version;
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void launch(String vfsId, String projectId, RequestCallback<StringBuilder> callback)
                                                                                               throws WebSocketException {
        final String params = "?vfsid=" + vfsId + "&projectid=" + projectId;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, BASE_URL + LAUNCH + params);
        builder.header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        Message message = builder.build();
        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(String appId, AsyncRequestCallback<Void> callback) throws RequestException {
        final String url = restContext + BASE_URL + STOP + "/" + appId;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(String appId, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String url = restContext + BASE_URL + LOGS + "/" + appId;
        loader.setMessage("Retrieving logs...");
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
}
