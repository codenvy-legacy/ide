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
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: NodeJsRuntimeServiceImpl.java Apr 18, 2013 5:02:19 PM vsvydenko $
 *
 */
public class NodeJsRuntimeServiceImpl extends NodeJsRuntimeService {
    private static final String LOGS = "/ide/node/runner/logs";

    private String restContext;

    private static final String RUN_APPLICATION = "/ide/node/runner/run";

    private static final String STOP_APPLICATION = "/ide/node/runner/stop";

    public NodeJsRuntimeServiceImpl(String restContext) {
        this.restContext = restContext;
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#start(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void start(String vfsId, ProjectModel project, AsyncRequestCallback<ApplicationInstance> callback)
            throws RequestException {
        String requestUrl = restContext + RUN_APPLICATION;

        StringBuilder params = new StringBuilder("?");
        params.append("&vfsid=").append(vfsId).append("&projectid=").append(project.getId());

        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString(), true)
                    .requestStatusHandler(new StartApplicationStatusHandler(project.getName()))
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#stop(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException {
        String requestUrl = restContext + STOP_APPLICATION;

        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString(), true)
                    .requestStatusHandler(new StopApplicationStatusHandler(name)).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.nodejs.client.NodeJsRuntimeService#getLogs(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restContext + LOGS;
        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        Loader loader = new GWTLoader();
        loader.setMessage("Retrieving logs.... ");

        AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
    }

}
