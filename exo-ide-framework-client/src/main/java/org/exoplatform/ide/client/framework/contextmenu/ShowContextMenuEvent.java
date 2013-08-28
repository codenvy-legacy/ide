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
package org.exoplatform.ide.client.framework.contextmenu;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to view context menu.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 23, 2012 11:30:45 AM anya $
 */
public class ShowContextMenuEvent extends GwtEvent<ShowContextMenuHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<ShowContextMenuHandler> TYPE = new GwtEvent.Type<ShowContextMenuHandler>();

    /** X coordinate of the context menu. */
    private int x;

    /** Y coordinate of the context menu. */
    private int y;

    /** Object, on which context menu was called. */
    private Object object;

    /**
     * @param x
     *         coordinate of the context menu
     * @param y
     *         coordinate of the context menu
     * @param object
     *         object, on which context menu was called
     */
    public ShowContextMenuEvent(int x, int y, Object object) {
        this.x = x;
        this.y = y;
        this.object = object;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowContextMenuHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowContextMenuHandler handler) {
        handler.onShowContextMenu(this);
    }

    /** @return the x */
    public int getX() {
        return x;
    }

    /** @return the y */
    public int getY() {
        return y;
    }

    public Object getObject() {
        return object;
    }
}
