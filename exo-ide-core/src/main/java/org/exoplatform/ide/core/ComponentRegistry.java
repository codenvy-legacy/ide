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

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.event.ComponentLifecycleEvent;
import org.exoplatform.ide.core.event.ComponentLifecycleHandler;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ComponentRegistry
{
   private final EventBus eventBus;
   private JsonArray<Component> pendingComponents;

   /**
    * Instantiates Component Registry. All components should be listed in this constructor
    */
   @Inject
   public ComponentRegistry(ResourceProvider resourceManager, EventBus eventBus)
   {
      this.eventBus = eventBus;
      pendingComponents = JsonCollections.<Component> createArray();
      pendingComponents.add(resourceManager);
      
   }

   /**
    * Starts all the components listed in registry
    * 
    * @param callback
    */
   public void start(final Callback<Void, ComponentException> callback)
   {
      eventBus.addHandler(ComponentLifecycleEvent.TYPE, new ComponentLifecycleHandler()
      {

         @Override
         public void onComponentStarted(ComponentLifecycleEvent event)
         {
            pendingComponents.remove(event.getComponent());
            // services started
            if (pendingComponents.size() == 0)
            {
               GWT.log("All services initialized. Starting.");
               callback.onSuccess(null);
            }
         }

         @Override
         public void onComponentFailed(ComponentLifecycleEvent event)
         {
            callback.onFailure(new ComponentException("Failed to start component", event.getComponent()));
            GWT.log("FAILED to start service:" + event.getComponent());
         }
      });
      
      pendingComponents.asIterable();
      for (Component component : pendingComponents.asIterable())
      {
         component.start();
      }
   }
   
}
