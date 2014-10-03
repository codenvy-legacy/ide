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
package com.codenvy.ide.jseditor.client.document;


import com.codenvy.ide.jseditor.client.events.CursorActivityHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * An abstraction over the editor representatin of the document.
 *
 * @author "Mickaël Leduque"
 */
public interface EmbeddedDocument {

    /**
     * Returns a line/character position for the given offset position.
     *
     * @param index
     *         the position from the start in the document
     * @return the line/character position
     */
    TextPosition getPositionFromIndex(int index);

    /**
     * Get linear position in the editor from a line/character position.
     *
     * @param position
     *         the line/character position
     * @return the offset from the document start
     */
    int getIndexFromPosition(TextPosition position);

    /**
     * Changes the cursor position.
     *
     * @param position
     *         the new position
     */
    void setCursorPosition(TextPosition position);

    /**
     * Returns the curosr position in the editor.
     *
     * @return the cursor position
     */
    TextPosition getCursorPosition();

    /**
     * Returns the number of lines in the document.
     *
     * @return the number of lines
     */
    int getLineCount();

    /**
     * Returns the contents of the editor.
     *
     * @return the contents
     */
    String getContents();

    /**
     * A position in the text editor.
     *
     * @author "Mickaël Leduque"
     */
    public class TextPosition {
        /** The line in the text. */
        private final int line;

        /** The character position on the line. */
        private final int character;

        public TextPosition(int line, int character) {
            this.line = line;
            this.character = character;
        }

        public int getLine() {
            return line;
        }

        public int getCharacter() {
            return character;
        }
    }

    /**
     * Adds a cursor handler.
     *
     * @param handler
     *         the added handler
     * @return a handle to remove the handler
     */
    HandlerRegistration addCursorHandler(CursorActivityHandler handler);
}
