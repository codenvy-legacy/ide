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
package com.codenvy.ide.texteditor.renderer;

import elemental.html.Element;

import com.codenvy.ide.Resources;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.util.dom.Elements;

/**
 * The renderer for the debug line in the editor.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
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
        lineHighlighter.addClassName(res.workspaceEditorBufferCss().debugLine());
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(-1), "PX");
        buffer.addUnmanagedElement(lineHighlighter);
    }

    /** Update debug line. */
    public void updateLine(int lineNumber) {
        lineHighlighter.getStyle().setTop(buffer.calculateLineTop(lineNumber), "PX");
    }
}