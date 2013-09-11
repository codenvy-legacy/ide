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
package com.codenvy.ide.factory.client.generate;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to share opened project with Factory URL.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ShareWithFactoryUrlEvent.java Jun 11, 2013 11:27:15 AM azatsarynnyy $
 */
public class ShareWithFactoryUrlEvent extends GwtEvent<ShareWithFactoryUrlHandler> {

    /** Type used to register this event. */
    public static final Type<ShareWithFactoryUrlHandler> TYPE = new Type<ShareWithFactoryUrlHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public Type<ShareWithFactoryUrlHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShareWithFactoryUrlHandler handler) {
        handler.onShare(this);
    }

}
