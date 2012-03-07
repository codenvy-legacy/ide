/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/java/debug")
public class DebuggerService
{
   @Inject
   private DebuggerRegistry debuggerRegistry;

   @GET
   @Path("connect")
   @Produces(MediaType.APPLICATION_JSON)
   public DebuggerInfo connect(@QueryParam("host") String host, @QueryParam("port") int port) throws VMConnectException
   {
      final Debugger d = Debugger.connect(host, port);
      final String key = debuggerRegistry.add(d);
      return new DebuggerInfoImpl(host, port, key, d.getVmName(), d.getVmVersion());
   }

   @POST
   @Path("breakpoints/add/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public void addBreakPoint(@PathParam("id") String id, BreakPoint breakPoint) throws InvalidBreakPoint
   {
      debuggerRegistry.get(id).addBreakPoint(breakPoint);
   }

   @GET
   @Path("breakpoints/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public List<BreakPoint> getBreakPoints(@PathParam("id") String id)
   {
      return debuggerRegistry.get(id).getBreakPoints();
   }

   @POST
   @Path("breakpoints/switch/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   public void switchBreakPoint(@PathParam("id") String id, BreakPoint breakPoint) throws InvalidBreakPoint
   {
      debuggerRegistry.get(id).switchBreakPoint(breakPoint);
   }
}
