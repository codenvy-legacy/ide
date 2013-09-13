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
package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event, occurs after pressing Initialize Application button (initialize java web application).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: InitializeApplicationEvent.java Jun 23, 2011 12:44:46 PM vereshchaka $
 */
public class InitializeApplicationEvent extends GwtEvent<InitializeApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<InitializeApplicationHandler> TYPE =
            new GwtEvent.Type<InitializeApplicationHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<InitializeApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(InitializeApplicationHandler handler) {
        handler.onInitializeApplication(this);
    }

}
