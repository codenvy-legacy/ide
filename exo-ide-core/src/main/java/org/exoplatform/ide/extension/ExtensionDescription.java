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
package org.exoplatform.ide.extension;

import com.google.gwt.inject.client.AsyncProvider;

import java.util.List;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ExtensionDescription<T>
{
   private final String id;

   private final String version;

   private final List<String> dependencies;

   private final AsyncProvider<T> extensionProvider;

   public ExtensionDescription(String id, String version, List<String> dependencies, AsyncProvider<T> extensionProvider)
   {
      this.id = id;
      this.version = version;
      this.dependencies = dependencies;
      this.extensionProvider = extensionProvider;
   }

   public String getName()
   {
      return id;
   }

   public String getVersion()
   {
      return version;
   }

   public List<String> getDependencies()
   {
      return dependencies;
   }

   /**
    * @return the extensionProvider
    */
   public AsyncProvider<T> getExtensionProvider()
   {
      return extensionProvider;
   }

}