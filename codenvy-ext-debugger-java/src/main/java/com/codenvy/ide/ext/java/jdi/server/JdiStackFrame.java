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
     */
    JdiLocalVariable[] getLocalVariables() throws DebuggerException;

    /**
     * Get local variable by name.
     *
     * @return local variable or <code>null</code> if there is not such local variable
     * @throws DebuggerException
     *         if an error occurs
     */
    JdiLocalVariable getLocalVariableByName(String name) throws DebuggerException;
}
