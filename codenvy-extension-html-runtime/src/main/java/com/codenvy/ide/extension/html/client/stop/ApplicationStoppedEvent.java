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
package com.codenvy.ide.extension.html.client.stop;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event occurs, when HTML application has been stopped.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationStoppedEvent.java Jun 26, 2013 11:07:20 AM azatsarynnyy $
 */
public class ApplicationStoppedEvent extends GwtEvent<ApplicationStoppedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<ApplicationStoppedHandler> TYPE = new GwtEvent.Type<ApplicationStoppedHandler>();

    /** Stopped application. */
    private ApplicationInstance                                  application;

    public ApplicationStoppedEvent(ApplicationInstance application) {
        this.application = application;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationStoppedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationStoppedHandler handler) {
        handler.onApplicationStopped(this);
    }

    /** @return the application */
    public ApplicationInstance getApplication() {
        return application;
    }
}
