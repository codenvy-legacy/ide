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
 * Variable at debuggee JVM.
 *
 * @author andrew00x
 * @see JdiField
 * @see JdiLocalVariable
 * @see JdiArrayElement
 */
public interface JdiVariable {
    /**
     * Name of variable. If this variable is element of array then name is: <i>[i]</i>, where <i>i</i> - index of element
     *
     * @return name of variable
     * @throws DebuggerException
     *         if an error occurs
     */
    String getName() throws DebuggerException;

    /**
     * Check is this variable is array.
     *
     * @return <code>true</code> if variable is array and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isArray() throws DebuggerException;

    /**
     * Check is this variable is primitive.
     *
     * @return <code>true</code> if variable is primitive and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isPrimitive() throws DebuggerException;

    /**
     * Get value of variable.
     *
     * @return value
     * @throws DebuggerException
     *         if an error occurs
     */
    JdiValue getValue() throws DebuggerException;

    /**
     * Name of variable type.
     *
     * @return type name
     * @throws DebuggerException
     *         if an error occurs
     */
    String getTypeName() throws DebuggerException;
}
