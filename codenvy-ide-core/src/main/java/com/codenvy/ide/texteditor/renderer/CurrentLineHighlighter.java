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
package com.codenvy.ide.texteditor.renderer;


import elemental.dom.Element;

import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CurrentLineHighlighter {
    private int activeLineNumber;

    private final Array<ListenerRegistrar.Remover> listenerRemovers = Collections.createArray();

    private final Element lineHighlighter;

    private final SelectionModel.CursorListener cursorListener = new SelectionModel.CursorListener() {

        @Override
        public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
            if (activeLineNumber == lineInfo.number())
                return;
            activeLineNumber = lineInfo.number();
            updateActiveLine();
        }
    };

    private final Buffer buffer;

    /**
     * @param buffer
     */
    public CurrentLineHighlighter(Buffer buffer, SelectionModel selection, Resources res) {
        this.buffer = buffer;
        listenerRemovers.add(selection.getCursorListenerRegistrar().add(cursorListener));
        lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
        Elements.addClassName(res.workspaceEditorBufferCss().currentLine(), lineHighlighter);
        lineHighlighter.getStyle().setTop(0, "PX");
        buffer.addUnmanagedElement(lineHighlighter);
    }

    /**
     *
     */
    private void updateActiveLine() {
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(activeLineNumber), "PX");
    }

    public void teardown() {
        for (int i = 0, n = listenerRemovers.size(); i < n; i++) {
            listenerRemovers.get(i).remove();
        }
    }
}
