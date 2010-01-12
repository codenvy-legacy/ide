/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ideall.client.model;

import java.util.ArrayList;
import java.util.Collection;

import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class Item
{

   private String path;

   private Collection<Property> properties = new ArrayList<Property>();
   
   private boolean propertiesChanged = false;

   private String icon;

   protected Item(String path)
   {
      this.path = path;
   }

   /**
    * @return the path
    */
   public String getPath()
   {
      return path;
   }

   /**
    * @param path the path to set
    */
   public void setPath(String path)
   {
      this.path = path;
   }

   /**
    * @return the propertiesChanged
    */
   public boolean isPropertiesChanged()
   {
      return propertiesChanged;
   }

   /**
    * @param propertiesChanged the propertiesChanged to set
    */
   public void setPropertiesChanged(boolean propertiesChanged)
   {
      this.propertiesChanged = propertiesChanged;
   }

   /**
    * @return the properties
    */
   public Collection<Property> getProperties()
   {
      return properties;
   }
   
   public String getName()
   {
      String name = path;
      if (name.endsWith("/"))
      {
         name = name.substring(0, name.length() - 1);
      }
      name = name.substring(name.lastIndexOf("/") + 1);
      return name;
   }

   /**
    * @return the icon
    */
   public String getIcon()
   {
      return icon;
   }

   /**
    * @param icon the icon to set
    */
   public void setIcon(String icon)
   {
      this.icon = icon;
   } 
   
}
