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
package org.exoplatform.ide.editor.shared.text;

/**
 * Indicates the attempt to access a non-existing position. The attempt has been performed on a text store such as a document or
 * string.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 */
public class BadLocationException extends Exception {

    /**
     * Serial version UID for this class.
     * <p>
     * Note: This class is not intended to be serialized.
     * </p>
     *
     * @since 3.1
     */
    private static final long serialVersionUID = 3257281452776370224L;

    /** Creates a new bad location exception. */
    public BadLocationException() {
        super();
    }

    /**
     * Creates a new bad location exception.
     *
     * @param message
     *         the exception message
     */
    public BadLocationException(String message) {
        super(message);
    }
}
