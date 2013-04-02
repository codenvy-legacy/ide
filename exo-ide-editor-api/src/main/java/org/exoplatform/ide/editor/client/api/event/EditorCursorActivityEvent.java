/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.editor.client.api.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.editor.client.api.Editor;

/**
 * Fires just after some key or mouse event have been happened in editor. Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorCursorActivityEvent extends GwtEvent<EditorCursorActivityHandler> {

    public static final GwtEvent.Type<EditorCursorActivityHandler> TYPE =
            new GwtEvent.Type<EditorCursorActivityHandler>();

    /** {@link Editor} instance. */
    private Editor editor;

    /** Cursor row. */
    private int row;

    /** Cursor column. */
    private int column;

    /**
     * Creates new instance of {@link EditorCursorActivityEvent}.
     *
     * @param editor
     * @param row
     * @param column
     */
    public EditorCursorActivityEvent(Editor editor, int row, int column) {
        this.editor = editor;
        this.row = row;
        this.column = column;
    }

    /**
     * Returns {@link Editor} instance.
     *
     * @return
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Returns cursor column.
     *
     * @return
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns cursor row.
     *
     * @return
     */
    public int getRow() {
        return row;
    }

    @Override
    protected void dispatch(EditorCursorActivityHandler handler) {
        handler.onEditorCursorActivity(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorCursorActivityHandler> getAssociatedType() {
        return TYPE;
    }

}
