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
package com.codenvy.ide.java.client.projectmodel;

import com.codenvy.ide.resources.model.Folder;

import com.google.gwt.json.client.JSONObject;


/**
 * Presents Java Package model
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class Package extends Folder
{
   public static final String TYPE = "java.package";

   /**
    * Default constructor
    */
   protected Package()
   {
      super(TYPE, FOLDER_MIME_TYPE);
   }

   /**
    * Init Java Package from JSon Object
    * 
    * @param itemObject
    */
   protected Package(JSONObject itemObject, String name)
   {
      this();
      init(itemObject);
      this.name = name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getPath()
   {
      if (parent != null)
      {
         return parent.getPath() + "/" + name.replace(".", "/");
      }
      else
      {
         return "/" + name.replace(".", "/");
      }
   }

}
