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
 * Event occurs, when Node.js application has started.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationStartedEvent.java Apr 18, 2013 5:13:44 PM vsvydenko $
 *
 */
public class ApplicationStartedEvent extends GwtEvent<ApplicationStartedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<ApplicationStartedHandler> TYPE = new GwtEvent.Type<ApplicationStartedHandler>();

    /** Started application. */
    private ApplicationInstance application;

    /**
     * @param application
     *         started application
     */
    public ApplicationStartedEvent(ApplicationInstance application) {
        this.application = application;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationStartedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationStartedHandler handler) {
        handler.onApplicationStarted(this);
    }

    /** @return the application */
    public ApplicationInstance getApplication() {
        return application;
    }
}
