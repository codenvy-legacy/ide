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
package org.exoplatform.ide.shared.model;

import java.io.Serializable;

/**
 * Simple Dummy File model for testing purposes, to be replaced with real model object
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 26, 2012  
 */
public class File implements Serializable
{
   private static final long serialVersionUID = 1L;

   String name;

   public File()
   {
   }

   public File(String name)
   {
      this.name = name;
   }

   /**
   * {@inheritDoc}
   */
   public String getMimeType()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return name;
   }

   /**
   * {@inheritDoc}
   */
   public String getPath()
   {
      return null;
   }
}
