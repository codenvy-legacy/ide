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
package org.exoplatform.ide.client.framework.ui.api.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.ui.api.View;

/**
 * This event generates after changing of view visibility.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewVisibilityChangedEvent extends GwtEvent<ViewVisibilityChangedHandler> {

    /** Type of this event. */
    public static final GwtEvent.Type<ViewVisibilityChangedHandler> TYPE =
            new GwtEvent.Type<ViewVisibilityChangedHandler>();

    /** View which visibility was changed. */
    private View view;

    /**
     * Creates a new instance of this event.
     *
     * @param view
     *         view which visibility was changed
     */
    public ViewVisibilityChangedEvent(View view) {
        this.view = view;
    }

    /**
     * Gets view which visibility was changed.
     *
     * @return view instance of view which visibility was changed
     */
    public View getView() {
        return view;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ViewVisibilityChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ViewVisibilityChangedHandler handler) {
        handler.onViewVisibilityChanged(this);
    }

}
