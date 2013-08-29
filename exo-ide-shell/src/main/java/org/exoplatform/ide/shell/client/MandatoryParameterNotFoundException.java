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
package org.exoplatform.ide.shell.client;

/**
 * Exception is thrown, when mandatory parameter not found.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 5, 2011 3:32:00 PM anya $
 */
public class MandatoryParameterNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public MandatoryParameterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MandatoryParameterNotFoundException(String message) {
        super(message);
    }

    public MandatoryParameterNotFoundException(Throwable cause) {
        super(cause);
    }
}
