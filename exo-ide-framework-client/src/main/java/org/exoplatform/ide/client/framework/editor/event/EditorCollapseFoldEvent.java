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
package org.exoplatform.ide.client.framework.editor.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: EditorCollapseFoldEvent.java Feb 28, 2013 5:08:29 PM azatsarynnyy $
 */
public class EditorCollapseFoldEvent extends GwtEvent<EditorCollapseFoldHandler> {
    private boolean collapseAll;

    public EditorCollapseFoldEvent() {
        super();
    }

    public EditorCollapseFoldEvent(boolean collapseAll) {
        super();
        this.collapseAll = collapseAll;
    }

    public static final GwtEvent.Type<EditorCollapseFoldHandler> TYPE =
            new GwtEvent.Type<EditorCollapseFoldHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditorCollapseFoldHandler handler) {
        handler.onEditorCollapse(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorCollapseFoldHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isCollapseAll() {
        return collapseAll;
    }

}
