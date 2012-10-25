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

import org.exoplatform.ide.json.JsonArray;

/**
 * Provides Extension information:
 * <ul>
 * <li>id - unique String id;</li>
 * <li>version - version of the Extension;</li>
 * <li>title - brief description of the Extension;</li>
 * <li>dependencies - the list of required dependencies</li>
 * </ul>
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ExtensionDescription
{
   private final String id;

   private final String version;

   private final JsonArray<DependencyDescription> dependencies;

   private final String title;

   /**
    * Construct {@link ExtensionDescription}
    * 
    * @param id
    * @param version
    * @param title
    * @param dependencies
    */
   public ExtensionDescription(String id, String version, String title, JsonArray<DependencyDescription> dependencies)
   {
      this.id = id;
      this.version = version;
      this.title = title;
      this.dependencies = dependencies;
   }

   /**
    * Get Extension ID
    * 
    * @return
    */
   public String getId()
   {
      return id;
   }

   /**
    * Get Extension Version
    * 
    * @return
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * Get Extension title
    * 
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Get the list of {@link DependencyDescription}
    * 
    * @return
    */
   public JsonArray<DependencyDescription> getDependencies()
   {
      return dependencies;
   }
}