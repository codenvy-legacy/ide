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
package com.codenvy.ide.commons.exception;

/**
 * Created by The eXo Platform SAS. Notifies about unmarshalling error accured.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@SuppressWarnings("serial")
public class UnmarshallerException extends Exception {

    /**
     * Creates an Instance of {@link UnauthorizedException} with message and root cause
     *
     * @param message
     * @param cause
     */
    public UnmarshallerException(String message, Throwable cause) {
        super(message, cause);
    }

}
