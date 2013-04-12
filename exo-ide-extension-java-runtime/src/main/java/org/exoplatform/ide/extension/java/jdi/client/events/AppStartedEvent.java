/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class AppStartedEvent extends GwtEvent<AppStartedHandler> {

    public static final GwtEvent.Type<AppStartedHandler> TYPE = new GwtEvent.Type<AppStartedHandler>();

    /** Started application. */
    private ApplicationInstance application;

    /**
     * @param application
     *         started application
     */
    public AppStartedEvent(ApplicationInstance application) {
        this.application = application;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AppStartedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppStartedHandler handler) {
        handler.onAppStarted(this);
    }

    /** @return {@link ApplicationInstance} started application */
    public ApplicationInstance getApplication() {
        return application;
    }
}
