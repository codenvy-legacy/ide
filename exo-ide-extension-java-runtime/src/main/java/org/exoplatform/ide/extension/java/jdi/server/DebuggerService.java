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
