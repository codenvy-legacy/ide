/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.server;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.BreakPointList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.VariablePath;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Provide access to {@link Debugger} through HTTP.
 *
 * @author andrew00x
 */
@Path("debug-java/{ws-id}")
public class DebuggerService {
    @GET
    @Path("connect")
    @Produces(MediaType.APPLICATION_JSON)
    public DebuggerInfo create(@QueryParam("host") String host,
                               @QueryParam("port") int port) throws DebuggerException {
        Debugger d = Debugger.newInstance(host, port);
        return DtoFactory.getInstance().createDto(DebuggerInfo.class)
                         .withHost(d.getHost())
                         .withPort(d.getPort())
                         .withId(d.id)
                         .withVmName(d.getVmName())
                         .withVmVersion(d.getVmVersion());
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
    public void addBreakpoint(@PathParam("id") String id, BreakPoint breakPoint) throws DebuggerException {
        Debugger.getInstance(id).addBreakpoint(breakPoint);
    }

    @GET
    @Path("breakpoints/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BreakPointList getBreakpoints(@PathParam("id") String id) throws DebuggerException {
        return DtoFactory.getInstance().createDto(BreakPointList.class)
                         .withBreakPoints(Debugger.getInstance(id).getBreakPoints());
    }

    @POST
    @Path("breakpoints/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteBreakpoint(@PathParam("id") String id, BreakPoint breakPoint) throws DebuggerException {
        Debugger.getInstance(id).deleteBreakPoint(breakPoint);
    }

    @GET
    @Path("breakpoints/delete_all/{id}")
    public void deleteAllBreakpoint(@PathParam("id") String id) throws DebuggerException {
        Debugger.getInstance(id).deleteAllBreakPoints();
    }

    @GET
    @Path("events/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public DebuggerEventList getEvents(@PathParam("id") String id) throws DebuggerException {
        return DtoFactory.getInstance().createDto(DebuggerEventList.class).withEvents(Debugger.getInstance(id).getEvents());
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
