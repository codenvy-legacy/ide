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

/**
 * Instance of class member in debuggee JVM.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JdiField extends JdiVariable {
    /**
     * Check is this field is final.
     *
     * @return <code>true</code> if field is final and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isFinal() throws DebuggerException;

    /**
     * Check is this field is static.
     *
     * @return <code>true</code> if field is static and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isStatic() throws DebuggerException;

    /**
     * Check is this transient is transient.
     *
     * @return <code>true</code> if field is transient and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isTransient() throws DebuggerException;

    /**
     * Check is this field is volatile.
     *
     * @return <code>true</code> if field is volatile and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isVolatile() throws DebuggerException;
}
