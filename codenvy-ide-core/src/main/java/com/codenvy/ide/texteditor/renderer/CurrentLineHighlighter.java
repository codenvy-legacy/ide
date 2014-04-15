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
