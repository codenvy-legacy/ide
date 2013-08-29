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
package org.exoplatform.ide.project;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@SuppressWarnings("serial")
public class ProjectPrepareException extends Exception {
    private int status = 500;

    /**
     * @param message
     *         the detail message about exception
     * @param cause
     *         the cause
     */
    public ProjectPrepareException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     *         the detail message about exception
     */
    public ProjectPrepareException(String message) {
        super(message);
    }

    /**
     * @param status
     *         the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
     *         this exception
     * @param message
     *         the detail message about exception
     * @param cause
     *         the cause
     */
    public ProjectPrepareException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * @param status
     *         the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for
     *         this exception
     * @param message
     *         the detail message about exception
     */
    public ProjectPrepareException(int status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * @return the HTTP status that should be used in response if any {@link javax.ws.rs.ext.ExceptionMapper} available for this
     *         exception
     */
    public int getStatus() {
        return status;
    }
}
