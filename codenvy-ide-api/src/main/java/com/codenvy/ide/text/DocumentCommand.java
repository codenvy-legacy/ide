/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
