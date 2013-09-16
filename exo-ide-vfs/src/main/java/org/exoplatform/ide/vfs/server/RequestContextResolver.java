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
package org.exoplatform.ide.vfs.server;

import javax.ws.rs.core.*;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class RequestContextResolver implements RequestContext, ContextResolver<RequestContext> {
    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Context
    private HttpHeaders headers;

    @Context
    private SecurityContext security;

    @Context
    private Providers providers;

    @Override
    public UriInfo getUriInfo() {
        return uriInfo;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return security;
    }

    @Override
    public Providers getProviders() {
        return providers;
    }

    @Override
    public RequestContext getContext(Class<?> type) {
        return this;
    }
}
