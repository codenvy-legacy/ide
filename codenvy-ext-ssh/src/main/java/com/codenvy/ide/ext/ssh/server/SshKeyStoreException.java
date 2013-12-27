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
package com.codenvy.ide.ext.ssh.server;

/** @author andrew00x */
@SuppressWarnings("serial")
public final class SshKeyStoreException extends Exception {
    /**
     * @param message
     *         the detail message
     * @param cause
     *         the cause
     */
    public SshKeyStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *         the detail message
     */
    public SshKeyStoreException(String message) {
        super(message);
    }
}
