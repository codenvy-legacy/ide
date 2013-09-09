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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event called, when welcome page opened.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomePageOpenedEvent.java Dec 19, 2011 11:44:05 AM vereshchaka $
 */
public class WelcomePageOpenedEvent extends GwtEvent<WelcomePageOpenedHandler> {

    public static final GwtEvent.Type<WelcomePageOpenedHandler> TYPE = new GwtEvent.Type<WelcomePageOpenedHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<WelcomePageOpenedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(WelcomePageOpenedHandler handler) {
        handler.onWelcomePageOpened(this);
    }

}
