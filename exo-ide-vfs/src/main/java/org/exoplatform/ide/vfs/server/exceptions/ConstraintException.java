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
 * If operation fails cause to any constraints.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ConstraintException.java 68071 2011-04-07 13:11:47Z vitalka $
 */
@SuppressWarnings("serial")
public class ConstraintException extends VirtualFileSystemException {
    /**
     * @param message
     *         the message
     */
    public ConstraintException(String message) {
        super(message);
    }

    /**
     * @param message
     *         the message
     * @param cause
     *         the cause
     */
    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *         the cause
     */
    public ConstraintException(Throwable cause) {
        super(cause);
    }
}