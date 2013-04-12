/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.cloudshell.server;

import junit.framework.TestCase;

import org.everrest.core.ComponentLifecycleScope;
import org.everrest.core.impl.RuntimeDelegateImpl;
import org.everrest.core.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.ide.shell.server.CLIResourceFactory;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CLIResourseFactoryTest extends TestCase {
    @Path("a/{b}")
    public static class Resource1 {
        @GET
        public void method0(@Context UriInfo uriInfo) {
        }

        @POST
        @Consumes("application/json")
        @Path("c")
        public void method1(@HeaderParam("content-type") String contentType, String cmd) {
        }

        @PUT
        @Path("/{c}/{d}/")
        public void method2(@MatrixParam("id") int id, @PathParam("b") String b, @PathParam("c") String c) {
        }

        @Path("sub/{x}")
        public Resource2 method3() {
            return new Resource2Impl();
        }
    }

    public static interface Resource2 {
        @GET
        @Produces("text/plain")
        public String method0(@PathParam("x") String x);
    }

    public static class Resource2Impl implements Resource2 {
        public String method0(@PathParam("x") String x) {
            return x;
        }
    }

    public void test1() throws Exception {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
        AbstractResourceDescriptorImpl resource =
                new AbstractResourceDescriptorImpl(Resource1.class, ComponentLifecycleScope.PER_REQUEST);
        CLIResourceFactory cliResourceFactory = new CLIResourceFactory();
        Set<CLIResource> cliResources = cliResourceFactory.getCLIResources(resource);

        Set<CLIResourceParameter> expectedParams = new HashSet<CLIResourceParameter>();

        // method0 in Resource1
        assertTrue(cliResources.contains(new CLIResource(new HashSet<String>(Arrays.asList("command1")), "/a/{b}", "GET",
                                                         new HashSet<String>(Arrays.asList("*/*")),
                                                         new HashSet<String>(Arrays.asList("*/*")), expectedParams)));

        // method1 in Resource1
        expectedParams.add(new CLIResourceParameter("content-type", new HashSet<String>(Arrays.asList("-content")),
                                                    CLIResourceParameter.Type.HEADER, true, true));
        expectedParams.add(new CLIResourceParameter("cmd", new HashSet<String>(Arrays.asList("-my-cmd")),
                                                    CLIResourceParameter.Type.BODY, true, true));
        assertTrue(cliResources.contains(new CLIResource(new HashSet<String>(Arrays.asList("command2")), "/a/{b}/c",
                                                         "POST", new HashSet<String>(Arrays.asList("application/json")),
                                                         new HashSet<String>(Arrays.asList("*/*")),
                                                         expectedParams)));

        // method2 in Resource1
        expectedParams.clear();
        expectedParams.add(new CLIResourceParameter("id", new HashSet<String>(Arrays.asList("-id")),
                                                    CLIResourceParameter.Type.MATRIX, false, true));
        expectedParams.add(new CLIResourceParameter("b", new HashSet<String>(Arrays.asList("-B")),
                                                    CLIResourceParameter.Type.PATH, false, true));
        expectedParams.add(new CLIResourceParameter("c", new HashSet<String>(Arrays.asList("-C")),
                                                    CLIResourceParameter.Type.PATH, false, true));
        assertTrue(cliResources.contains(new CLIResource(new HashSet<String>(Arrays.asList("command3")),
                                                         "/a/{b}/{c}/{d}", "PUT", new HashSet<String>(Arrays.asList("*/*")),
                                                         new HashSet<String>(Arrays.asList("*/*")),
                                                         expectedParams)));

        // method3 in Resource1
        expectedParams.clear();
        expectedParams.add(new CLIResourceParameter("x", new HashSet<String>(Arrays.asList("-x")),
                                                    CLIResourceParameter.Type.PATH, false, true));
        assertTrue(cliResources.contains(new CLIResource(new HashSet<String>(Arrays.asList("command4")),
                                                         "/a/{b}/sub/{x}", "GET", new HashSet<String>(Arrays.asList("*/*")),
                                                         new HashSet<String>(Arrays
                                                                                     .asList("text/plain")), expectedParams)));
    }
}
