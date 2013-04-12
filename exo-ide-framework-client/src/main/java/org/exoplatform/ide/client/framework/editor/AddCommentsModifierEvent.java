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
package org.exoplatform.ide.client.framework.editor;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user adds comments modifier.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 4:45:02 PM anya $
 */
public class AddCommentsModifierEvent extends GwtEvent<AddCommentsModifierHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<AddCommentsModifierHandler> TYPE = new GwtEvent.Type<AddCommentsModifierHandler>();

    /** MIME type, for which comments modifier can be applied to. */
    private String mimeType;

    /** Comments modifier. */
    private CommentsModifier commentsModifier;

    /**
     * @param mimeType
     *         MIME type, for which comments modifier can be applied to
     * @param commentsModifier
     *         comments modifier
     */
    public AddCommentsModifierEvent(String mimeType, CommentsModifier commentsModifier) {
        this.mimeType = mimeType;
        this.commentsModifier = commentsModifier;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AddCommentsModifierHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(AddCommentsModifierHandler handler) {
        handler.onAddCommentsModifier(this);
    }

    /** @return {@link String} MIME type */
    public String getMimeType() {
        return mimeType;
    }

    /** @return {@link CommentsModifier} comments modifier */
    public CommentsModifier getCommentsModifier() {
        return commentsModifier;
    }
}
