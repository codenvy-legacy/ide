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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerEventList;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.ext.java.jdi.shared.UpdateVariableRequest;
import com.codenvy.ide.ext.java.jdi.shared.Value;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.rest.AsyncRequestCallback;

import javax.validation.constraints.NotNull;

/**
 * The client service for debug java application.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public interface DebuggerClientService {
    /**
     * Attach debugger.
     *
     * @param host
     * @param port
     * @param callback
     */
    void connect(@NotNull String host, int port, @NotNull AsyncRequestCallback<DebuggerInfo> callback);

    /**
     * Disconnect debugger.
     *
     * @param id
     * @param callback
     */
    void disconnect(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Adds breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     */
    void addBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Returns list of breakpoints.
     *
     * @param id
     * @param callback
     */
    void getAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback);

    /**
     * Deletes breakpoint.
     *
     * @param id
     * @param breakPoint
     * @param callback
     */
    void deleteBreakpoint(@NotNull String id, @NotNull BreakPoint breakPoint, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Remove all breakpoints.
     *
     * @param id
     * @param callback
     */
    void deleteAllBreakpoints(@NotNull String id, @NotNull AsyncRequestCallback<String> callback);

    /**
     * Checks event.
     *
     * @param id
     * @param callback
     */
    void checkEvents(@NotNull String id, @NotNull AsyncRequestCallback<DebuggerEventList> callback);

    /**
     * Get dump of fields and local variable of current stack frame.
     *
     * @param id
     * @param callback
     */
    void getStackFrameDump(@NotNull String id, @NotNull AsyncRequestCallback<StackFrameDump> callback);

    /**
     * Resume process.
     *
     * @param id
     * @param callback
     */
    void resume(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Returns value of a variable.
     *
     * @param id
     * @param var
     * @param callback
     */
    void getValue(@NotNull String id, @NotNull Variable var, @NotNull AsyncRequestCallback<Value> callback);

    /**
     * Sets value of a variable.
     *
     * @param id
     * @param request
     * @param callback
     */
    void setValue(@NotNull String id, @NotNull UpdateVariableRequest request, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Do step into.
     *
     * @param id
     * @param callback
     */
    void stepInto(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Do step over.
     *
     * @param id
     * @param callback
     */
    void stepOver(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Do step return.
     *
     * @param id
     * @param callback
     */
    void stepReturn(@NotNull String id, @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Evaluate an expression.
     *
     * @param id
     * @param expression
     * @param callback
     */
    void evaluateExpression(@NotNull String id, @NotNull String expression, @NotNull AsyncRequestCallback<String> callback);
}