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
package org.exoplatform.ide.extension.samples.client.oauth;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to login to GiHub.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 30, 2012 10:33:01 AM anya $
 */
public class GithubLoginFinishedEvent extends GwtEvent<GithubLoginFinishedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<GithubLoginFinishedHandler> TYPE = new GwtEvent.Type<GithubLoginFinishedHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public GwtEvent.Type<GithubLoginFinishedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(GithubLoginFinishedHandler handler) {
        handler.onGithubLoginFinished(this);
    }

}
