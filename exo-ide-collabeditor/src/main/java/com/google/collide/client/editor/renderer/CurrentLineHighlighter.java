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
package com.google.collide.client.editor.renderer;

import elemental.html.Element;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.commons.shared.ListenerRegistrar;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.google.collide.client.common.ThemeConstants;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.Buffer.Resources;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.shared.document.LineInfo;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CurrentLineHighlighter {
    private int activeLineNumber;

    private final JsonArray<ListenerRegistrar.Remover> listenerRemovers = JsonCollections.createArray();

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
     * @param editor
     */
    public CurrentLineHighlighter(Buffer buffer, SelectionModel selection, Resources res) {
        this.buffer = buffer;
        listenerRemovers.add(selection.getCursorListenerRegistrar().add(cursorListener));
        lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
        lineHighlighter.addClassName(res.workspaceEditorBufferCss().currentLine());
        lineHighlighter.addClassName(ThemeConstants.ACTIVE_LINE);
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
