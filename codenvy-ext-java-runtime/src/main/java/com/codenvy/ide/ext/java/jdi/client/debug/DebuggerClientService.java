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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.java.jdi.shared.*;
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
    void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<DebuggerInfo> callback) throws RequestException;

    /**
     * Disconnects to application.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void disconnect(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Adds breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     * @throws RequestException
     */
    void addBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<BreakPoint> callback)
            throws RequestException;

    /**
     * Deletes breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     * @throws RequestException
     */
    void deleteBreakPoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<BreakPoint> callback)
            throws RequestException;

    /**
     * Returns list of breakpoints.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void getBreakPoints(@NotNull String id, @NotNull AsyncRequestCallback<BreakPointList> callback) throws RequestException;

    /**
     * Checks event.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<DebuggerEventList> callback) throws RequestException;

    /**
     * Creates dump.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void dump(@NotNull String id, @NotNull AsyncRequestCallback<StackFrameDump> callback) throws RequestException;

    /**
     * Resume process.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void resume(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Returns value of field.
     *
     * @param id
     * @param var
     * @param callback
     * @throws RequestException
     */
    void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<Value> callback) throws RequestException;

    /**
     * Sets field's value.
     *
     * @param id
     * @param request
     * @param callback
     * @throws RequestException
     */
    void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Do step into.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepInto(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Do step over.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepOver(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Do step return.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stepReturn(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stops application.
     *
     * @param runningApp
     * @param callback
     * @throws RequestException
     */
    void stopApplication(@NotNull ApplicationInstance runningApp, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Remove all breakpoint.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void deleteAllBreakPoint(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Evaluate expression.
     *
     * @param id
     * @param expression
     * @param callback
     * @throws RequestException
     */
    void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException;
}