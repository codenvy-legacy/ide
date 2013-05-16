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

import org.exoplatform.ide.extension.java.jdi.server.model.BreakPointListImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.DebuggerEventListImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.DebuggerInfoImpl;
import org.exoplatform.ide.extension.java.jdi.shared.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Provide access to {@link Debugger} through HTTP.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/java/debug")
public class DebuggerService {
    @GET
    @Path("connect")
    @Produces(MediaType.APPLICATION_JSON)
    public DebuggerInfo create(@QueryParam("host") String host,
                               @QueryParam("port") int port) throws DebuggerException {
        Debugger d = Debugger.newInstance(host, port);
        return new DebuggerInfoImpl(d.getHost(), d.getPort(), d.id, d.getVmName(), d.getVmVersion());
    }

    @GET
    @Path("disconnect/{id}")
    public void disconnect(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).disconnect();
    }

    @GET
    @Path("resume/{id}")
    public void resume(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).resume();
    }

    @POST
    @Path("breakpoints/add/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addBreakPoint(@PathParam("id") String id, BreakPoint breakPoint) throws DebuggerException {
        Debugger.getInstance(id).addBreakPoint(breakPoint);
    }

    @GET
    @Path("breakpoints/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BreakPointList getBreakPoints(@PathParam("id") String id) throws DebuggerException {
        return new BreakPointListImpl(Debugger.getInstance(id).getBreakPoints());
    }

    @POST
    @Path("breakpoints/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteBreakPoint(@PathParam("id") String id, BreakPoint breakPoint) throws DebuggerException {
        Debugger.getInstance(id).deleteBreakPoint(breakPoint);
    }

    @GET
    @Path("breakpoints/delete_all/{id}")
    public void deleteAllBreakPoint(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).deleteAllBreakPoints();
    }

    @GET
    @Path("events/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public DebuggerEventList getEvents(@PathParam("id") String id) throws DebuggerException {
        return new DebuggerEventListImpl(Debugger.getInstance(id).getEvents());
    }

    @GET
    @Path("dump/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StackFrameDump getStackFrameDump(@PathParam("id") String id) throws DebuggerException {
        return Debugger.getInstance(id).dumpStackFrame();
    }

    @POST
    @Path("value/get/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Value getValue(@PathParam("id") String id, VariablePath path) throws DebuggerException {
        return Debugger.getInstance(id).getValue(path);
    }

    @POST
    @Path("value/set/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void setValue(@PathParam("id") String id, UpdateVariableRequest request) throws DebuggerException {
        Debugger.getInstance(id).setValue(request.getVariablePath(), request.getExpression());
    }

    @GET
    @Path("step/over/{id}")
    public void stepOver(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).stepOver();
    }

    @GET
    @Path("step/into/{id}")
    public void stepInto(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).stepInto();
    }

    @GET
    @Path("step/out/{id}")
    public void stepOut(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).stepOut();
    }

    @POST
    @Path("expression/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String expression(@PathParam("id") String id, String expression) throws DebuggerException {
        return Debugger.getInstance(id).expression(expression);
    }
}
