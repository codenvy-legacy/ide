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
package org.exoplatform.ide.editor.java;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Breakpoint {
    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }

    protected int lineNumber;

    private Type type;

    private String message;

    /**
     * @param type
     * @param lineNumber
     */
    public Breakpoint(Type type, int lineNumber) {
        this(type, lineNumber, null);
    }

    /**
     * @param type
     * @param lineNumber
     * @param message
     */
    public Breakpoint(Type type, int lineNumber, String message) {
        super();
        this.type = type;
        this.lineNumber = lineNumber;
        this.message = message;
    }

    /** @return the type */
    public Type getType() {
        return type;
    }

    /** @return the lineNumber */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

}
