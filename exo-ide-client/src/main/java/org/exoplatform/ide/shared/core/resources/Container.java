/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero GeneralLicense
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU GeneralLicense for more details.
 *
 * You should have received a copy of the GNU GeneralLicense
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.shared.core.resources;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface Container extends Resource
{

   /**
    * Returns whether a resource of some type with the given path 
    * exists relative to this resource.
    * The supplied path may be absolute or relative; in either case, it is
    * interpreted as relative to this resource.  Trailing separators are ignored.
    * If the path is empty this container is checked for existence.
    *
    * @param path the path of the resource
    * @return <code>true</code> if a resource of some type with the given path 
    *     exists relative to this resource, and <code>false</code> otherwise
    */
   boolean exists(String path);

   /**
    * Returns a handle to the file identified by the given path in this
    * container.
    *
    * @param path the path of the member file
    * @return the (handle of the) member file
    */
   File getFile(String path);

   /**
    * Returns a handle to the folder identified by the given path in this
    * container.
   least two segments.
    * </p>
    *
    * @param path the path of the member folder
    * @return the (handle of the) member folder
    */
   Folder getFolder(String path);

   /**
    * Returns a list of existing member resources (projects, folders and files)
    * in this resource, in no particular order.
    * Note that the members of a project or folder are the files and folders
    * immediately contained within it.  The members of the workspace root
    * are the projects in the workspace.
    * </p>
    *
    * @return an array of members of this resource
    * @exception CoreException if this request fails.
    */
   Resource[] members() throws Exception;

   /**
    * Adds a new filter to this container. Filters restrict the set of files and directories
    * in the underlying file system that will be included as members of this container.
    * <p> 
    * This operation changes resources; these changes will be reported
    * in a subsequent resource change event that will include an indication of any 
    * resources that have been removed as a result of the new filter.
    * </p>
    * <p>
    * This operation is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    * 
    */
   //public ResourceFilterDescription createFilter(boolean include, FileInfoMatcherDescription matcherDescription, int updateFlags, ProgressMonitor monitor) throws Exception; // TODO ?

   /**
    * Retrieve all filters on this container.
    * If no filters exist for this resource, an empty array is returned.
    * 
    * @return an array of filters
    * @exception Exception if this resource's filters could not be retrieved. 
    */
   //public ResourceFilterDescription[] getFilters() throws Exception; // TODO ?

}
