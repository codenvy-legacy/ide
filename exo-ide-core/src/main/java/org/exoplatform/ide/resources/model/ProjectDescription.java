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
package org.exoplatform.ide.resources.model;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringSet;
import org.exoplatform.ide.resources.properties.Property;

/**
 * Desription of the project containing nature set and it's specific properties
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectDescription
{
   
   public static final String PROPERTY_PRIMARY_NATURE = "nature.primary";
   
   
   public static final String PROPERTY_MIXIN_NATURES = "nature.mixin";
   public static final String PROPERTY_SOURCE_FOLDERS = "folders.source";
   
   /** Properties. */
   protected Project project;

   public ProjectDescription(Project project)
   {
      this.project = project;
   }

   /**
    * @return primary nature
    */
   public String getPrimaryNature()
   {
      return (String)project.getPropertyValue(PROPERTY_PRIMARY_NATURE);
   }
   
   /**
    * @return The set of Mixin natures or empty set 
    */
   public JsonStringSet getNatures()
   {
      return asStringSet(PROPERTY_MIXIN_NATURES);
   }

   /**
    * @return The set of Project's source folders or empty set.
    */
   public JsonStringSet getSourceFolders()
   {
      return asStringSet(PROPERTY_SOURCE_FOLDERS);
   }
   
   /**
    * @param property
    * @return
    */
   private JsonStringSet asStringSet(String propertyName)
   {
      @SuppressWarnings("unchecked")
      Property<String> property = project.getProperty(propertyName);
      JsonStringSet natures = JsonCollections.createStringSet();
      if (property!=null)
      {
         natures.addAll(property.getValue());
      }
      return natures;
   }
   
   
   
   
}
