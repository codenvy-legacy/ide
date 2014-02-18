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
package org.exoplatform.ide.shell.server.rest;

import org.everrest.core.ObjectFactory;
import org.everrest.core.ResourceBinder;
import org.everrest.core.resource.AbstractResourceDescriptor;
import org.exoplatform.ide.shell.server.CLIResourceFactory;
import org.exoplatform.ide.shell.shared.CLIResource;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
// Never be binded to RESTful framework by using eXoContainer.
// This service must works as per-request resource.
@Path("{ws-name}/cli")
public class CLIResourcesService {
    @javax.inject.Inject
    private ResourceBinder binder;
    
    @PathParam("ws-name")
    String wsName;

    @javax.inject.Inject
    private CLIResourceFactory cliResourceFactory;

    @GET
    @Path("resources")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("rawtypes")
    @RolesAllowed({"workspace/developer"})
    public Set<CLIResource> getCLIResources() throws IOException {
        Set<CLIResource> result = new HashSet<CLIResource>();
        List<ObjectFactory<AbstractResourceDescriptor>> resources = binder.getResources();
        ObjectFactory[] array = resources.toArray(new ObjectFactory[resources.size()]);
        for (int i = 0; i < array.length; i++) {
            AbstractResourceDescriptor descriptor = (AbstractResourceDescriptor)array[i].getObjectModel();
            result.addAll(cliResourceFactory.getCLIResources(descriptor));
        }
        
        return result;
    }
}
