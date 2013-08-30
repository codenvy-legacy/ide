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
 * This event generates after activating of any View.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewActivatedEvent extends GwtEvent<ViewActivatedHandler> {

    /** Type of this Event. */
    public static final GwtEvent.Type<ViewActivatedHandler> TYPE = new GwtEvent.Type<ViewActivatedHandler>();

    /** View which was activated. */
    private View view;

    /**
     * Creates a new instance of this Event.
     *
     * @param view
     *         View which was activated
     */
    public ViewActivatedEvent(View view) {
        this.view = view;
    }

    /**
     * Gets a view which was activated.
     *
     * @return view which was activated
     */
    public View getView() {
        return view;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ViewActivatedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ViewActivatedHandler handler) {
        handler.onViewActivated(this);
    }

}
