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
package org.exoplatform.ide.vfs.server.exceptions;

/**
 * Should be thrown for any errors that are not expressible by another VFS (Virtual File System) exception. Used as base class for
 * any VFS unchecked exceptions.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VirtualFileSystemRuntimeException.java 78691 2012-01-13 15:24:59Z anya $
 */
@SuppressWarnings("serial")
public class VirtualFileSystemRuntimeException extends RuntimeException {
    /**
     * @param message
     *         the detail message
     * @param cause
     *         the cause
     */
    public VirtualFileSystemRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *         the detail message
     */
    public VirtualFileSystemRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     *         the cause
     */
    public VirtualFileSystemRuntimeException(Throwable cause) {
        super(cause);
    }
}
