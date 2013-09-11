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
 * This event fired before closing View.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ClosingViewEvent extends GwtEvent<ClosingViewHandler> {

    /** Type of this event. */
    public static final GwtEvent.Type<ClosingViewHandler> TYPE = new GwtEvent.Type<ClosingViewHandler>();

    /** View to be closed. */
    private View view;

    /** Revoked or not closing the View. */
    private boolean closingCanceled = false;

    /**
     * Creates a new instance of this Event.
     *
     * @param view
     *         view to be closed
     */
    public ClosingViewEvent(View view) {
        this.view = view;
    }

    /**
     * Gets View to be closed.
     *
     * @return View to be closed.
     */
    public View getView() {
        return view;
    }

    /** Revoke closing View. */
    public void cancelClosing() {
        closingCanceled = true;
    }

    /**
     * Gets is closing was revoked.
     *
     * @return
     */
    public boolean isClosingCanceled() {
        return closingCanceled;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ClosingViewHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ClosingViewHandler handler) {
        handler.onClosingView(this);
    }

}
