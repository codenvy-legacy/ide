/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
