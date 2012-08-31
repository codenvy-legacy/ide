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

import org.exoplatform.ide.client.core.runtime.ProgressMonitor;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface Folder extends Container

{
   /**
    * Creates a new folder resource as a member of this handle's parent resource.
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    * 
    * @param force a flag controlling how to deal with resources that
    *    are not in sync with the local file system
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    */
   void create(boolean force, ProgressMonitor monitor) throws Exception;

   /**
    * Returns a handle to the file with the given name in this folder.
    *
    * @param name the string name of the member file
    * @return the (handle of the) member file
    */
   File getFile(String name);

   /**
    * Returns a handle to the folder with the given name in this folder.
    *
    * @param name the string name of the member folder
    * @return the (handle of the) member folder
    */
   Folder getFolder(String name);
}
