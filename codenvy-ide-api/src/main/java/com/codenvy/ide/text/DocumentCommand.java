/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
