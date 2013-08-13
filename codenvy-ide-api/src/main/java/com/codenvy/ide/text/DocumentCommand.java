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
 * Represents a text modification as a document replace command.
 * <p>
 * A document command can also represent a list of related changes.</p>
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DocumentCommand {
    //this fields is public for performance reason
    /** The offset of the command. */
    public int     offset;
    /** The length of the command */
    public int     length;
    /** The text to be inserted */
    public String  text;
    /** The caret offset with respect to the document before the document command is executed. */
    public int     caretOffset;
    /** Indicates whether the caret should be shifted by this command. */
    public boolean shiftsCaret;

    /** Must the command be updated */
    public boolean doit = false;

    public DocumentCommand() {
    }

    public DocumentCommand(int offset, int length, String text) {
        initialize(offset, length, text);
    }

    public void initialize(int offset, int length, String text) {
        this.offset = offset;
        this.length = length;
        this.text = text;
        caretOffset = -1;
        shiftsCaret = true;
        doit = true;
    }
}
