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
package com.codenvy.ide.text.edits;

/**
 * Thrown to indicate that an edit got added to a parent edit but the child edit somehow conflicts with the parent or one of it
 * siblings.
 * <p>
 * This class is not intended to be serialized.
 * </p>
 *
 * @see TextEdit#addChild(TextEdit)
 * @see TextEdit#addChildren(TextEdit[])
 */
public class MalformedTreeException extends RuntimeException {

    // Not intended to be serialized
    private static final long serialVersionUID = 1L;

    private TextEdit fParent;

    private TextEdit fChild;

    /**
     * Constructs a new malformed tree exception.
     *
     * @param parent
     *         the parent edit
     * @param child
     *         the child edit
     * @param message
     *         the detail message
     */
    public MalformedTreeException(TextEdit parent, TextEdit child, String message) {
        super(message);
        fParent = parent;
        fChild = child;
    }

    /**
     * Returns the parent edit that caused the exception.
     *
     * @return the parent edit
     */
    public TextEdit getParent() {
        return fParent;
    }

    /**
     * Returns the child edit that caused the exception.
     *
     * @return the child edit
     */
    public TextEdit getChild() {
        return fChild;
    }

    void setParent(TextEdit parent) {
        fParent = parent;
    }
}
