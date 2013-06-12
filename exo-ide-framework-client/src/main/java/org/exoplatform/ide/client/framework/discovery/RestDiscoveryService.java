/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.discovery;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Discovery service for REST services.
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class RestDiscoveryService {
    /** Instance of the {@link RestDiscoveryService}. */
    private static RestDiscoveryService instance;

    /** Context of the REST services. */
    private final String restServiceContext;

    /** @return {@link RestDiscoveryService} */
    public static RestDiscoveryService getInstance() {
        return instance;
    }

    public RestDiscoveryService(String restServiceContext, String wsName) {
        instance = this;
        this.restServiceContext = restServiceContext + wsName;
    }

    /**
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getRestServices(AsyncRequestCallback<RestServicesList> callback) throws RequestException {
        String url = restServiceContext;
        if (!url.endsWith("/")) {
            url += "/";
        }
        AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }
}
