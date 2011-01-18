/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.vfs.shared;

import org.exoplatform.ide.vfs.server.OutputProperty;

import java.util.List;

/**
 * Representation of Folder object used to interaction with client via JSON.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Folder extends Item
{
   /**
    * Instance of Folder with specified attributes.
    * 
    * @param id identifier of object
    * @param name the name of object
    * @param path path of object
    * @param creationDate creation date in long format
    * @param lastModificationDate date of last modification in long format
    * @param locked is folder locked or not
    * @param properties other properties of folder
    */
   public Folder(String id, String name, String path, long creationDate, List<OutputProperty> properties)
   {
      super(id, name, Type.FOLDER, path, creationDate, properties);
   }

   /**
    * Empty instance of Folder.
    */
   public Folder()
   {
      super();
   }
}
