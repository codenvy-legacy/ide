/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.core;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.ComponentLifecycleEvent;
import org.exoplatform.ide.core.event.ComponentLifecycleEvent.LifecycleState;

/**
 * Abstract Component implementation, provide methods for correct notifications 
 * when project failed or started. This abstract class should be used by Components
 * as Super instead of directly implementing {@link Component}.
 * 
 * When Component successfully started it should invoke {@link ComponentImpl#onStarted()}
 * and {@link ComponentImpl#onFailed(Throwable)} when it failed to start.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public abstract class ComponentImpl implements Component
{

   private EventBus eventBus;

   /**
    * {@inheritDoc}
    */
   @Override
   public void start()
   {
      // Dummy Start() for Components that doesn't require asynchronous start
      onStarted();
   }

   /**
    * @param eventBus Application wide Event Bus
    */
   @Inject
   public ComponentImpl(EventBus eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Notifies Component failed to start
    * 
    * @param exception
    */
   protected void onFailed(Throwable exception)
   {
      eventBus.fireEvent(new ComponentLifecycleEvent(this, LifecycleState.FAILED));
      GWT.log("ResourceProviderService:ailed to start resource provider" + exception.getMessage() + ">" + exception);
   }

   /**
    * Notifies Component Started
    */
   protected void onStarted()
   {
      eventBus.fireEvent(new ComponentLifecycleEvent(this, LifecycleState.STARTED));
   }
}
