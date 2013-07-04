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
package org.exoplatform.ide.extension.nodejs.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: NodeJsRuntimeServiceImpl.java Apr 18, 2013 5:02:19 PM vsvydenko $
 *
 */
public class NodeJsRuntimeServiceImpl extends NodeJsRuntimeService {
    
    private static final String BASE_URL = "/node/runner"; 
    
    private static final String LOGS = BASE_URL + "/logs";

    private String restContext;

    private String wsName;

    private MessageBus wsMessageBus;

    private static final String RUN_APPLICATION = BASE_URL + "/run";

    private static final String STOP_APPLICATION = BASE_URL + "/stop";

    public NodeJsRuntimeServiceImpl(String restContext, String wsName, MessageBus wsMessageBus) {
        this.restContext = restContext;
        this.wsName = wsName;
        this.wsMessageBus = wsMessageBus;
        
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#start(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void start(String vfsId, ProjectModel project, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException {
        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(project.getId());
        RequestMessage message =
            RequestMessageBuilder.build(RequestBuilder.GET, wsName + RUN_APPLICATION + params).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#stop(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException {
        String requestUrl = restContext + wsName + STOP_APPLICATION;

        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString())
                    .requestStatusHandler(new StopApplicationStatusHandler(name)).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#getLogs(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restContext + wsName + LOGS;
        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        Loader loader = new GWTLoader();
        loader.setMessage("Retrieving logs.... ");

        AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
    }

}
