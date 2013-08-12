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
package com.codenvy.ide.factory.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * Implementation of {@link FactoryClientService}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryClientServiceImpl.java Jun 25, 2013 11:33:15 PM azatsarynnyy $
 */
public class FactoryClientServiceImpl extends FactoryClientService {

    /** Base url. */
    private static final String BASE_URL = Utils.getWorkspaceName() + "/factory";

    private static final String SHARE    = BASE_URL + "/share";

    private static final String FACTORY_CREATED    = BASE_URL + "/log-factory-created";

    /** REST-service context. */
    private String              restServiceContext;

    /** Loader to be displayed. */
    private Loader              loader;

    /**
     * Construct a new {@link FactoryClientServiceImpl}.
     * 
     * @param restContext REST-service context
     * @param loader loader to show on server request
     */
    public FactoryClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /**
     * Sends e-mail message to share Factory URL.
     * 
     * @throws RequestException
     */
    public void share(String recipient, String message, AsyncRequestCallback<Object> callback)
                                                                                                     throws RequestException {
        final String requesrUrl = restServiceContext + SHARE;

        String params = "recipient=" + recipient + "&message=" + message;
        AsyncRequest.build(RequestBuilder.POST, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    @Override
    public void logFactoryCreated(String vfsId, String projectId, String factoryUrl, AsyncRequestCallback<StringBuilder> callback) throws RequestException{
        String url = restServiceContext + FACTORY_CREATED;
        url += "?vfsid=" + vfsId + "&projectid=" + projectId + "&factoryurl=" + factoryUrl;
        AsyncRequest.build(RequestBuilder.GET, url).send(callback);
    }
}
