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
package org.exoplatform.ide.git.server;

/**
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class NotAuthorizedGitException extends GitException {
    protected NotAuthorizedGitException() {
    }

    /**
     * @param message error message
     */
    public NotAuthorizedGitException(String message) {
        super(message);
    }

    /**
     * @param cause cause
     */
    public NotAuthorizedGitException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message error message
     * @param cause cause
     */
    public NotAuthorizedGitException(String message, Throwable cause) {
        super(message, cause);
    }
}