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

import com.codenvy.ide.extension.demo.DemoExtension;

import com.codenvy.ide.extension.css.CssExtension;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.extension.tasks.TasksExtension;
import com.codenvy.ide.java.client.JavaExtension;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;

/**
 * {@link ExtensionManager} responsible for bringing up Extensions. It uses ExtensionRegistry to acquire 
 * Extension description and dependencies. 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class ExtensionManager
{

   @SuppressWarnings("rawtypes")
   private final JsonArray<Provider> extensions;

   private final ExtensionRegistry extensionRegistry;

   /**
    *
    */
   @Inject
   public ExtensionManager(final ExtensionRegistry extensionRegistry, final Provider<DemoExtension> demoExt,
      final Provider<CssExtension> cssExt, final Provider<JavaExtension> javaExt,  final Provider<TasksExtension> tasksExt, 
      Provider<CloudFoundryExtension> cloudFoundryExt)
   {
      this.extensions = JsonCollections.createArray();
      this.extensionRegistry = extensionRegistry;
      this.extensions.add(demoExt);
      this.extensions.add(cssExt);
      this.extensions.add(javaExt);
      this.extensions.add(cloudFoundryExt);
      this.extensions.add(tasksExt);
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
