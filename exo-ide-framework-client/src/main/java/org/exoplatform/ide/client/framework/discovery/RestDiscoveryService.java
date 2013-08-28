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
