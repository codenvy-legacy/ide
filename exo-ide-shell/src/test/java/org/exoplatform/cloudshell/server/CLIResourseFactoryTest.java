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

import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.cloudshell.shared.CLIResourceParameter;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CLIResourseFactoryTest extends TestCase
{
   @Path("a/{b}")
   public static class Resource1
   {
      @GET
      public void method0(@Context UriInfo uriInfo)
      {
      }

      @POST
      @Consumes("application/json")
      @Path("c")
      public void method1(@HeaderParam("content-type") String contentType, String cmd)
      {
      }

      @PUT
      @Path("{c}/{d}")
      public void method2(@MatrixParam("id") int id, @PathParam("b") String b, @PathParam("c") String c)
      {
      }

      @Path("sub/{x}")
      public Resource2 method3()
      {
         return new Resource2Impl();
      }
   }

   public static interface Resource2
   {
      @GET
      @Produces("text/plain")
      public String method0(@PathParam("x") String x);
   }

   public static class Resource2Impl implements Resource2
   {
      public String method0(@PathParam("x") String x)
      {
         return x;
      }
   }

   public void test1() throws Exception
   {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
      AbstractResourceDescriptorImpl resource = new AbstractResourceDescriptorImpl(Resource1.class);
      CLIResourceFactory cliResourceFactory = new CLIResourceFactory();
      Map<String, CLIResource> cliResources = cliResourceFactory.getCLIResources(resource);

      Map<String, CLIResourceParameter> expectedParams = new HashMap<String, CLIResourceParameter>();

      // method0 in Resource1
      assertEquals(new CLIResource("a/{b}", "GET", new HashSet<MediaType>(Arrays.asList(new MediaType())),
         new HashSet<MediaType>(Arrays.asList(new MediaType())), expectedParams), //
         cliResources.get("command1"));

      // method1 in Resource1
      expectedParams.put("-content",
         new CLIResourceParameter("content-type", new HashSet<String>(Arrays.asList("-content")),
            CLIResourceParameter.Type.HEADER, true));
      expectedParams.put("-my-cmd", new CLIResourceParameter("cmd", new HashSet<String>(Arrays.asList("-my-cmd")),
         CLIResourceParameter.Type.BODY, true));
      assertEquals(
         new CLIResource("a/{b}/c", "POST",
            new HashSet<MediaType>(Arrays.asList(new MediaType("application", "json"))), new HashSet<MediaType>(
               Arrays.asList(new MediaType())), expectedParams), //
         cliResources.get("command2"));

      // method2 in Resource1
      expectedParams.clear();
      expectedParams.put("-id", new CLIResourceParameter("id", new HashSet<String>(Arrays.asList("-id")),
         CLIResourceParameter.Type.MATRIX, true));
      expectedParams.put("-B", new CLIResourceParameter("b", new HashSet<String>(Arrays.asList("-B")),
         CLIResourceParameter.Type.PATH, false));
      expectedParams.put("-C", new CLIResourceParameter("c", new HashSet<String>(Arrays.asList("-C")),
         CLIResourceParameter.Type.PATH, false));
      assertEquals(new CLIResource("a/{b}/{c}/{d}", "PUT", new HashSet<MediaType>(Arrays.asList(new MediaType())),
         new HashSet<MediaType>(Arrays.asList(new MediaType())), expectedParams), //
         cliResources.get("command3"));

      // method3 in Resource1
      expectedParams.clear();
      expectedParams.put("-x", new CLIResourceParameter("x", new HashSet<String>(Arrays.asList("-x")),
         CLIResourceParameter.Type.PATH, true));
      assertEquals(new CLIResource("a/{b}/sub/{x}", "GET", new HashSet<MediaType>(Arrays.asList(new MediaType())),
         new HashSet<MediaType>(Arrays.asList(new MediaType("text", "plain"))), expectedParams), //
         cliResources.get("command4"));
   }
}
