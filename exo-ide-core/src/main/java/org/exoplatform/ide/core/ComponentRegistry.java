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

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ComponentRegistry
{
   private JsonArray<Component> pendingComponents;

   /**
    * Instantiates Component Registry. All components should be listed in this constructor
    */
   @Inject
   public ComponentRegistry(ResourceProvider resourceManager)
   {
      pendingComponents = JsonCollections.<Component> createArray();
      pendingComponents.add(resourceManager);
   }

   /**
    * Starts all the components listed in reg
    * 
    * @param callback
    */
   public void start(final Callback<Void, ComponentException> callback)
   {
      Callback<Component, ComponentException> internalCallback = new Callback<Component, ComponentException>()
      {
         @Override
         public void onSuccess(Component result)
         {
            pendingComponents.remove(result);
            // all components started
            if (pendingComponents.size() == 0)
            {
               GWT.log("All services initialized. Starting.");
               callback.onSuccess(null);
            }
         }

         @Override
         public void onFailure(ComponentException reason)
         {
            callback.onFailure(new ComponentException("Failed to start component", reason.getComponent()));
            GWT.log("FAILED to start service:" + reason.getComponent());
         }
      };

      pendingComponents.asIterable();
      for (Component component : pendingComponents.asIterable())
      {
         component.start(internalCallback);
      }
   }

}
