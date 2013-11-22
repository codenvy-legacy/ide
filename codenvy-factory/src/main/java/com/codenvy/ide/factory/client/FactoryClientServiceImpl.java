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
package com.codenvy.ide.factory.client;

import com.codenvy.api.factory.AdvancedFactoryUrl;
import com.codenvy.api.factory.SimpleFactoryUrl;
import com.codenvy.ide.factory.shared.CopySpec10;
import com.codenvy.ide.factory.client.marshaller.SimpleFactoryUrlMarshaller;
import com.codenvy.ide.json.client.Jso;
import com.codenvy.ide.json.client.JsoArray;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.git.client.clone.CloneRequestStatusHandler;

import java.util.List;

import static com.google.gwt.http.client.URL.encodeQueryString;

/** Implementation of {@link FactoryClientService}. */
public class FactoryClientServiceImpl extends FactoryClientService implements CopySpec10 {

    /** Base url. */
    private static final String BASE_URL = Utils.getWorkspaceName() + "/factory";

    private static final String SHARE       = BASE_URL + "/share";
    private static final String CLONE       = BASE_URL + "/clone";
    private static final String COPY        = Utils.getWorkspaceName() + "/copy/projects";
    private static final String GET_FACTORY = "/api/factory";

    /** REST-service context. */
    private String restServiceContext;

    /** WebSocket eventbus. */
    private MessageBus eventBus;

    /** Loader to be displayed. */
    private Loader loader;

    /**
     * Construct a new {@link FactoryClientServiceImpl}.
     *
     * @param loader
     *         loader to show on server request
     */
    public FactoryClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
        this.eventBus = IDE.messageBus();
    }

    /** {@inheritDoc} */
    @Override
    public void share(String recipient, String message, AsyncRequestCallback<Object> callback)
            throws RequestException {
        final String uri = restServiceContext + SHARE;
        final String params = "recipient=" + recipient + "&message=" + encodeQueryString(message);

        AsyncRequest.build(RequestBuilder.POST, uri)
                    .loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED)
                    .data(params)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getFactory(String factoryId, AsyncRequestCallback<AdvancedFactoryUrl> callback)
            throws RequestException {
        final String url = GET_FACTORY + "/" + factoryId;

        AsyncRequest.build(RequestBuilder.GET, url)
                    .loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneProjectWS(String vfsId, String projectId, SimpleFactoryUrl factoryUrl, RequestCallback<StringBuilder> callback)
            throws WebSocketException {

        final String uri = CLONE;
        final String params = "vfsid=" + vfsId + "&projectid=" + projectId;

        SimpleFactoryUrlMarshaller marshaller = new SimpleFactoryUrlMarshaller(factoryUrl);

        callback.setStatusHandler(new CloneRequestStatusHandler(factoryUrl.getProjectattributes().get("pname"), factoryUrl.getVcsurl()));

        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.POST, uri + "?" + params)
                                                      .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                                      .data(marshaller.marshal())
                                                      .getRequestMessage();

        eventBus.send(message, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cloneProject(String vfsId, String projectId, SimpleFactoryUrl factoryUrl, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        final String uri = restServiceContext + CLONE;
        final String params = "vfsid=" + vfsId + "&projectid=" + projectId;

        SimpleFactoryUrlMarshaller marshaller = new SimpleFactoryUrlMarshaller(factoryUrl);

        AsyncRequest.build(RequestBuilder.POST, uri + "?" + params)
                    .loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                    .data(marshaller.marshal())
                    .requestStatusHandler(new CloneRequestStatusHandler(factoryUrl.getProjectattributes().get("pname"),
                                                                        factoryUrl.getVcsurl()))
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void copyProjects(String downloadUrl, List<String> projects, RequestCallback<Void> callback) throws WebSocketException {
        final String uri = COPY;
        final String params = DOWNLOAD_URL + "=" + downloadUrl;

        JsoArray<String> jso = JsoArray.from(projects);

        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.POST, uri + "?" + params)
                                                      .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                                                      .data(Jso.serialize(jso))
                                                      .getRequestMessage();

        eventBus.send(message, callback);
    }
}
