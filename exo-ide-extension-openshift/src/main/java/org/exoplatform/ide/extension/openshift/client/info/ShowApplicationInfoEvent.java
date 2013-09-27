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
package org.exoplatform.ide.extension.openshift.client.info;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to view the application's information. Implement {@link ShowApplicationInfoHandler} to handle
 * event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 9, 2011 3:47:35 PM anya $
 */
public class ShowApplicationInfoEvent extends GwtEvent<ShowApplicationInfoHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ShowApplicationInfoHandler> TYPE = new GwtEvent.Type<ShowApplicationInfoHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowApplicationInfoHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowApplicationInfoHandler handler) {
        handler.onShowApplicationInfo(this);
    }

}
