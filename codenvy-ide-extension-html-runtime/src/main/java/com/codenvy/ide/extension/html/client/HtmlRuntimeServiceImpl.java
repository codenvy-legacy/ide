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
package com.codenvy.ide.extension.html.client;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Implementation of {@link HtmlRuntimeService}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlRuntimeServiceImpl.java Jun 26, 2013 11:10:54 AM azatsarynnyy $
 */
public class HtmlRuntimeServiceImpl extends HtmlRuntimeService {

    private static final String BASE_URL         = "/html/runner";

    private static final String RUN_APPLICATION  = BASE_URL + "/run";

    private static final String STOP_APPLICATION = BASE_URL + "/stop";

    private final String        wsName;

    /** REST service context. */
    private final String        restContext;

    public HtmlRuntimeServiceImpl(String restContext, String wsName) {
        this.restContext = restContext;
        this.wsName = wsName;
    }

    /**
     * @see com.codenvy.ide.extension.html.client.HtmlRuntimeService#start(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void start(String vfsId, String projectId, AsyncRequestCallback<ApplicationInstance> callback)
                                                                                                         throws RequestException {
        String requestUrl = restContext + wsName + RUN_APPLICATION;
        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);
        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see com.codenvy.ide.extension.html.client.HtmlRuntimeService#stop(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException {
        String requestUrl = restContext + wsName + STOP_APPLICATION;
        StringBuilder params = new StringBuilder("?");
        params.append("name=").append(name);
        AsyncRequest.build(RequestBuilder.GET, requestUrl + params.toString()).send(callback);
    }
}
