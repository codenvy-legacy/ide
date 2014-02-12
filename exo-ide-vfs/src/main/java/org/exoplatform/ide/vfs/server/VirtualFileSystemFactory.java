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

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides access to virtual file systems which have registered providers in VirtualFileSystemRegistry.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/vfs")
public class VirtualFileSystemFactory {
    @Inject
    private VirtualFileSystemRegistry registry;

    @Inject
    private EventListenerList listeners;

    @Inject
    private RequestValidator requestValidator;

    @Context
    private Providers providers;

    @Context
    private javax.servlet.http.HttpServletRequest request;

    @Path("v2")
    public VirtualFileSystem getFileSystem() throws VirtualFileSystemException {
        validateRequest();
        final String vfsId = EnvironmentContext.getCurrent().getWorkspaceId();
        VirtualFileSystemProvider provider = registry.getProvider(vfsId);
        return provider.newInstance(getContext(), listeners);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<VirtualFileSystemInfo> getAvailableFileSystems() throws VirtualFileSystemException {
        validateRequest();
        Collection<VirtualFileSystemProvider> vfsProviders = registry.getRegisteredProviders();
        List<VirtualFileSystemInfo> result = new ArrayList<VirtualFileSystemInfo>(vfsProviders.size());
        RequestContext context = getContext();
        for (VirtualFileSystemProvider p : vfsProviders) {
            VirtualFileSystem fs = p.newInstance(context, listeners);
            result.add(fs.getInfo());
        }
        return result;
    }

    private void validateRequest() {
        if (requestValidator != null) {
            requestValidator.validate(request);
        }
    }

    protected RequestContext getContext() {
        ContextResolver<RequestContext> contextResolver = providers.getContextResolver(RequestContext.class, null);
        if (contextResolver != null) {
            return contextResolver.getContext(RequestContext.class);
        }
        return null;
    }
}
