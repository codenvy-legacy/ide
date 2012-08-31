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
package org.exoplatform.ide.shared.core.resources;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface WorkspaceRoot extends Container
{
   // TODO:
   // find files by path or by URI if needed?
   // get container for Path of URI location
   // get files for location
   
   /**
    * Returns a handle to the project resource with the given name
    * which is a child of this root.  The given name must be a valid
    * path.
    * <p>
    * Note: This method deals exclusively with resource handles, 
    * independent of whether the resources exist in the workspace.
    * With the exception of validating that the name is a valid path segment,
    * validation checking of the project name is not done
    * when the project handle is constructed; rather, it is done
    * automatically as the project is created.
    * </p>
    * 
    * @param name the name of the project 
    * @return a project resource handle
    */
   Project getProject(String name);

   /**
    * Returns the collection of projects which exist under this root.
    * The projects can be open or closed.
    * @return an array of projects
    */
   Project[] getProjects();
}
