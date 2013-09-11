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
