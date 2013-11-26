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
package com.codenvy.ide.ext.git.server;


/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NotAuthorizedException extends GitException {
    /**
     * @param message error message
     */
    public NotAuthorizedException(String message) {
        super(message);
    }

    /**
     * @param cause cause
     */
    public NotAuthorizedException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message error message
     * @param cause cause
     */
    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}