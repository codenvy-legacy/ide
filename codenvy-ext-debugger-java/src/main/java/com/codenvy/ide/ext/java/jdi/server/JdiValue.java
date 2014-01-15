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
 * Value of JdiVariable.
 *
 * @author andrew00x
 */
public interface JdiValue {
    /**
     * Get value in String representation.
     *
     * @return value in String representation
     * @throws DebuggerException
     *         if an error occurs
     */
    String getAsString() throws DebuggerException;

    /**
     * Get nested variables.
     *
     * @return nested variables. This method always returns empty array for primitive type since primitive type has not
     *         any fields. If value represents array this method returns array members
     * @throws DebuggerException
     *         if an error occurs
     */
    JdiVariable[] getVariables() throws DebuggerException;

    /**
     * Get nested variable by name.
     *
     * @param name
     *         name of variable. Typically it is name of field. If this value represents array then name should be in form:
     *         <i>[i]</i>, where <i>i</i> is index of element
     * @return nested variable with specified name or <code>null</code> if there is no such variable
     * @throws DebuggerException
     *         if an error occurs
     * @see JdiVariable#getName()
     */
    JdiVariable getVariableByName(String name) throws DebuggerException;
}
