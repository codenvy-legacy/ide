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
package org.exoplatform.ide.extension.nodejs.client.run.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance;

/**
 * Event occurs, when Node.js application has been stopped.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationStoppedEvent.java Apr 18, 2013 5:10:13 PM vsvydenko $
 *
 */
public class ApplicationStoppedEvent extends GwtEvent<ApplicationStoppedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<ApplicationStoppedHandler> TYPE = new GwtEvent.Type<ApplicationStoppedHandler>();

    /** Stopped application. */
    private ApplicationInstance application;

    /** If <code>true</code>, then application was stopped manually. */
    private boolean manually;

    public ApplicationStoppedEvent(ApplicationInstance application, boolean manually) {
        this.application = application;
        this.manually = manually;
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

    /** @return the manually */
    public boolean isManually() {
        return manually;
    }
}
