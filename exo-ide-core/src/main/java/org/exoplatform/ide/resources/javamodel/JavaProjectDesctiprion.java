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
package org.exoplatform.ide.resources.javamodel;

import org.exoplatform.ide.json.JsonStringSet;
import org.exoplatform.ide.resources.model.ProjectDescription;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class JavaProjectDesctiprion extends ProjectDescription
{

   public static final String PROPERTY_SOURCE_FOLDERS = "folders.source";
   
   /**
    * @param project
    */
   public JavaProjectDesctiprion(JavaProject project)
   {
      super(project);
   }


   /**
    * @return The set of Project's source folders or empty set.
    */
   public JsonStringSet getSourceFolders()
   {
      return asStringSet(PROPERTY_SOURCE_FOLDERS);
   }
}
