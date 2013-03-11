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
package com.codenvy.ide.client;

import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * {@link AbstractExtensionManager} responsible for bringing up Extensions. It uses ExtensionRegistry to acquire 
 * Extension description and dependencies. 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class AbstractExtensionManager
{

   @SuppressWarnings("rawtypes")
   protected final JsonArray<Provider> extensions = JsonCollections.createArray();

   protected final ExtensionRegistry extensionRegistry;

   /**
    *
    */
   @Inject
   public AbstractExtensionManager(final ExtensionRegistry extensionRegistry)
   {
      this.extensionRegistry = extensionRegistry;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("rawtypes")
   public void startExtensions()
   {
      for (Provider provider : extensions.asIterable())
      {
         // this will instantiate extension so it's get enabled
         // Order of startup is managed by GIN dependency injection framework
         provider.get();
      }
   }

   /**
    * {@inheritDoc}
    */
   public JsonStringMap<ExtensionDescription> getExtensionDescriptions()
   {
      return extensionRegistry.getExtensionDescriptions();
   }

}
