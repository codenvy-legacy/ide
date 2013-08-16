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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
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
    private static final String BASE_URL = '/' + Utils.getWorkspaceName() + "/extruntime";

    /** Create method's path. */
    private static final String CREATE   = "/create";

    /** Launch method's path. */
    private static final String LAUNCH   = "/launch";

    /** Get logs method's path. */
    private static final String LOGS     = "/logs";

    /** Stop method's path. */
    private static final String STOP     = "/stop";

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
                                              AsyncRequestCallback<Void> callback) throws RequestException {
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
    public void launch(String vfsId, String projectId, RequestCallback<ApplicationInstance> callback) throws WebSocketException {
        final String params = "?vfsid=" + vfsId + "&projectid=" + projectId;
        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, BASE_URL + LAUNCH + params);
        builder.header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON);
        Message message = builder.build();
        wsMessageBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(String appId, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String url = restContext + BASE_URL + LOGS + "/" + appId;
        loader.setMessage("Retrieving logs...");
        AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(String appId, AsyncRequestCallback<Void> callback) throws RequestException {
        final String url = restContext + BASE_URL + STOP + "/" + appId;
        loader.setMessage("Stopping an application...");
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
}
