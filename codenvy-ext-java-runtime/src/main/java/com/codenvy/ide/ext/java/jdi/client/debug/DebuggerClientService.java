/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.BreakPointList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * The client service for debug java application.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public interface DebuggerClientService {
    /**
     * Connects to application.
     *
     * @param host
     * @param port
     * @param callback
     * @throws RequestException
     */
    void connect(String host, int port, AsyncRequestCallback<DebuggerInfo> callback) throws RequestException;

    /**
     * Disconnects to application.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void disconnect(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Adds breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     * @throws RequestException
     */
    void addBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback) throws RequestException;

    /**
     * Deletes breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     * @throws RequestException
     */
    void deleteBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback) throws RequestException;

    /**
     * Returns list of breakpoints.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void getBreakPoints(String id, AsyncRequestCallback<BreakPointList> callback) throws RequestException;

    /**
     * Checks event.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void checkEvents(String id, AsyncRequestCallback<DebuggerEventList> callback) throws RequestException;

    /**
     * Creates dump.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void dump(String id, AsyncRequestCallback<StackFrameDump> callback) throws RequestException;

    /**
     * Resume process.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void resume(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Returns value of field.
     *
     * @param id
     * @param var
     * @param callback
     * @throws RequestException
     */
    void getValue(String id, Variable var, AsyncRequestCallback<Value> callback) throws RequestException;

    /**
     * Sets field's value.
     *
     * @param id
     * @param request
     * @param callback
     * @throws RequestException
     */
    void setValue(String id, UpdateVariableRequest request, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Do step into.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepInto(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Do step over.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepOver(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Do step return.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepReturn(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stops application.
     *
     * @param runningApp
     * @param callback
     * @throws RequestException
     */
    void stopApplication(ApplicationInstance runningApp, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Remove all breakpoint.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void deleteAllBreakPoint(String id, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Evaluate expression.
     *
     * @param id
     * @param expression
     * @param callback
     * @throws RequestException
     */
    void evaluateExpression(String id, String expression, AsyncRequestCallback<StringBuilder> callback) throws RequestException;
}