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
package com.codenvy.ide.jseditor.client.texteditor;

import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument.TextPosition;
import com.codenvy.ide.jseditor.client.events.CursorActivityEvent;
import com.codenvy.ide.jseditor.client.events.CursorActivityHandler;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.texteditor.selection.CursorModelWithHandler;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar.Remover;

/**
 * {@link CursorModelWithHandler} implementation for the embedded editors.
 * 
 * @author "Mickaël Leduque"
 */
class EmbeddedEditorCursorModel implements CursorModelWithHandler, CursorActivityHandler {

    private final EmbeddedDocument               document;
    private final ListenerManager<CursorHandler> cursorHandlerManager = ListenerManager.create();

    public EmbeddedEditorCursorModel(final EmbeddedDocument document) {
        this.document = document;
        this.document.addCursorHandler(this);
    }

    @Override
    public void setCursorPosition(int offset) {
        TextPosition position = document.getPositionFromIndex(offset);
        document.setCursorPosition(position);
    }

    @Override
    public Position getCursorPosition() {
        TextPosition position = document.getCursorPosition();
        int offset = document.getIndexFromPosition(position);
        return new Position(offset);
    }

    @Override
    public Remover addCursorHandler(CursorHandler handler) {
        return this.cursorHandlerManager.add(handler);
    }

    private void dispatchCursorChange(final boolean isExplicitChange) {
        final TextPosition position = this.document.getCursorPosition();


        cursorHandlerManager.dispatch(new Dispatcher<CursorModelWithHandler.CursorHandler>() {
            @Override
            public void dispatch(CursorHandler listener) {
                listener.onCursorChange(position.getLine(), position.getCharacter(), isExplicitChange);
            }
        });
    }

    @Override
    public void onCursorActivity(final CursorActivityEvent event) {
        dispatchCursorChange(true);
    }
}
