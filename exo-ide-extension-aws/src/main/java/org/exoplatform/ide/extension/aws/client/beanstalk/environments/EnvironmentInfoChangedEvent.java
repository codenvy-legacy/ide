/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when AWS application's environment info were changed.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EnvironmentStatusChangedEvent.java Oct 2, 2012 10:50:18 AM azatsarynnyy $
 */
public class EnvironmentInfoChangedEvent extends GwtEvent<EnvironmentInfoChangedHandler> {

    /** Environment. */
    private EnvironmentInfo environment;

    /** Type used to register event. */
    public static final GwtEvent.Type<EnvironmentInfoChangedHandler> TYPE =
            new GwtEvent.Type<EnvironmentInfoChangedHandler>();

    /**
     * @param environment
     *         environment
     */
    public EnvironmentInfoChangedEvent(EnvironmentInfo environment) {
        this.environment = environment;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EnvironmentInfoChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EnvironmentInfoChangedHandler handler) {
        handler.onEnvironmentInfoChanged(this);
    }

    /** @return the environment */
    public EnvironmentInfo getEnvironment() {
        return environment;
    }

}
