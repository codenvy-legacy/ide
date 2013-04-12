/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.editor.client.marking;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user makes right mouse click on line number.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 8, 2012 4:35:31 PM anya $
 */
public class EditorLineNumberContextMenuEvent extends GwtEvent<EditorLineNumberContextMenuHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<EditorLineNumberContextMenuHandler> TYPE =
            new GwtEvent.Type<EditorLineNumberContextMenuHandler>();

    /** Number of the line, where right mouse click was called. */
    private int lineNumber;

    /** Context menu coordinates. */
    private int x, y;

    /**
     * @param lineNumber
     *         number of the line, where right mouse click was called
     * @param x
     *         left corner position of the context menu
     * @param y
     *         top corner position of the context menu
     */
    public EditorLineNumberContextMenuEvent(int lineNumber, int x, int y) {
        this.lineNumber = lineNumber;
        this.x = x;
        this.y = y;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorLineNumberContextMenuHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorLineNumberContextMenuHandler handler) {
        handler.onEditorLineNumberContextMenu(this);
    }

    /** @return the lineNumber */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the x left corner position of the context menu */
    public int getX() {
        return x;
    }

    /** @return the y top corner position of the context menu */
    public int getY() {
        return y;
    }
}
