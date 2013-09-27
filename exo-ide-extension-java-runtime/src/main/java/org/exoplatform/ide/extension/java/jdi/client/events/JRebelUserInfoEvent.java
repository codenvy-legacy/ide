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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs when user tries to update projet.
 *
 * @author <a href="mailto:vsydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: JRebelUserInfoEvent.java Apr 28, 2012 10:05:37 AM vsvydenko $
 */
public class JRebelUserInfoEvent extends GwtEvent<JRebelUserInfoHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<JRebelUserInfoHandler> TYPE = new GwtEvent.Type<JRebelUserInfoHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<JRebelUserInfoHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(JRebelUserInfoHandler handler) {
        handler.onJRebelInfo(this);
    }

}
