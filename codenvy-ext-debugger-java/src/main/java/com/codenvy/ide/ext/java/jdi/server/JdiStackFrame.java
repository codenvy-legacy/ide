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

/**
 * State of method invocation.
 *
 * @author andrew00x
 */
public interface JdiStackFrame {
    /**
     * Get all available instance or class members.
     *
     * @return list of fields. Fields should be ordered:
     *         <ul>
     *         <li>static fields should go before non-static fields</li>
     *         <li>fields of the same type should be ordered by name</li>
     *         </ul>
     * @throws DebuggerException
     *         if an error occurs
     */
    JdiField[] getFields() throws DebuggerException;

    /**
     * Get field by name.
     *
     * @return field or <code>null</code> if there is not such field
     * @throws DebuggerException
     *         if an error occurs
     */
    JdiField getFieldByName(String name) throws DebuggerException;

    /**
     * Get all available local variables.
     *
     * @return list of local variables
     * @throws DebuggerException
     *         if an error occurs
     * @throws DebuggerAbsentInformationException
     *         if an error occurs
     */
    JdiLocalVariable[] getLocalVariables() throws DebuggerException, DebuggerAbsentInformationException;

    /**
     * Get local variable by name.
     *
     * @return local variable or <code>null</code> if there is not such local variable
     * @throws DebuggerException
     *         if an error occurs
     * @throws DebuggerAbsentInformationException
     *         if an error occurs
     */
    JdiLocalVariable getLocalVariableByName(String name) throws DebuggerException, DebuggerAbsentInformationException;
}
