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
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.util.dom.Elements;

/**
 * The renderer for the debug line in the editor.
 *
 * @author Andrey Plotnikov
 */
public class DebugLineRenderer {
    private final Element lineHighlighter;
    private final Buffer  buffer;

    /**
     * Create renderer.
     *
     * @param buffer
     * @param res
     */
    public DebugLineRenderer(Buffer buffer, Resources res) {
        this.buffer = buffer;
        lineHighlighter = Elements.createDivElement(res.workspaceEditorBufferCss().line());
        Elements.addClassName(res.workspaceEditorBufferCss().debugLine(), lineHighlighter);
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(-1), "px");
    }

    /** Update debug line. */
    public void showLine(int lineNumber) {
        if (!buffer.hasLineElement(lineHighlighter)) {
            buffer.addUnmanagedElement(lineHighlighter);
        }
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(lineNumber), "px");
    }

    /** Update debug line. */
    public void disableLine() {
        buffer.removeUnmanagedElement(lineHighlighter);
    }
}