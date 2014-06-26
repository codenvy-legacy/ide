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
package com.codenvy.ide.texteditor.embeddedimpl.orion;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.embeddedimpl.common.EmbeddedDocument;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.CursorActivityHandler;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.HasCursorActivityHandlers;
import com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionTextViewOverlay;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * The implementation of {@link EmbeddedDocument} for Orion.
 * 
 * @author "Mickaël Leduque"
 */
public class OrionDocument implements EmbeddedDocument {

    private final OrionTextViewOverlay textViewOverlay;
    private final Document             document;

    private HasCursorActivityHandlers  hasCursorActivityHandlers;

    public OrionDocument(final OrionTextViewOverlay textViewOverlay,
                         final Document document,
                         final HasCursorActivityHandlers hasCursorActivityHandlers) {
        this.textViewOverlay = textViewOverlay;
        this.document = document;
        this.hasCursorActivityHandlers = hasCursorActivityHandlers;
    }

    @Override
    public TextPosition getPositionFromIndex(final int index) {
        final int line = this.textViewOverlay.getModel().getLineAtOffset(index);
        if (line == -1) {
            return null;
        }
        final int lineStart = this.textViewOverlay.getModel().getLineStart(line);
        if (lineStart == -1) {
            return null;
        }
        final int character = index - lineStart;
        if (character < 0) {
            return null;
        }
        return new TextPosition(line, character);
    }

    @Override
    public int getIndexFromPosition(final TextPosition position) {
        final int lineStart = this.textViewOverlay.getModel().getLineStart(position.getLine());
        if (lineStart == -1) {
            return -1;
        }

        final int result = lineStart + position.getCharacter();
        final int lineEnd = this.textViewOverlay.getModel().getLineEnd(position.getLine());

        if (lineEnd < result) {
            return -1;
        }
        return result;
    }

    @Override
    public void setCursorPosition(final TextPosition position) {
        this.textViewOverlay.setCaretOffset(getIndexFromPosition(position));

    }

    @Override
    public TextPosition getCursorPosition() {
        final int offset = this.textViewOverlay.getCaretOffset();
        return getPositionFromIndex(offset);
    }

    @Override
    public HandlerRegistration addCursorHandler(final CursorActivityHandler handler) {
        return this.hasCursorActivityHandlers.addCursorActivityHandler(handler);
    }
}
