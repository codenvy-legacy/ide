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
package com.codenvy.ide.text;

/**
 * Indicates the attempt to access a non-existing position category in a document.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 *
 * @see Document
 */
public class BadPositionCategoryException extends Exception {

    /**
     * Serial version UID for this class.
     * <p>
     * Note: This class is not intended to be serialized.
     * </p>
     */
    private static final long serialVersionUID = 3761405300745713206L;

    /** Creates a new bad position category exception. */
    public BadPositionCategoryException() {
        super();
    }

    /**
     * Creates a new bad position category exception.
     *
     * @param message
     *         the exception's message
     */
    public BadPositionCategoryException(String message) {
        super(message);
    }
}
