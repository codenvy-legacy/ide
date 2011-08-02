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
package org.exoplatform.ide.client.framework.vfs;

import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.xml.QName;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */
public abstract class Item
{

   private String name;
   
   private String href;

   private Collection<Property> properties = new ArrayList<Property>();

   private boolean propertiesChanged = false;

   private String icon;
   
   /**
    * Item is system or not.
    * "System" means is used and edited by application, not user.
    */
   private boolean isSystem;

   protected Item(String href)
   {
      this.href = href;
   }

   public String getHref()
   {
      return href;
   }

   public void setHref(String href)
   {
      this.href = href;
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

   public Property getProperty(QName propertyName)
   {
      for (Property property : properties)
      {
         if (propertyName.equals(property.getName()))
         {
            return property;
         }
      }

      return null;
   }

   private String getNameFromHref()
   {
      String name = href;
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

   /**
    * @return the isSystem
    */
   public boolean isSystem()
   {
      return isSystem;
   }

   /**
    * @param isSystem the isSystem to set
    */
   public void setSystem(boolean isSystem)
   {
      this.isSystem = isSystem;
   }
   
   /**
    * @return display name 
    */
   public String getName()
   {
      if (name == null)
         return getNameFromHref();
      return name;
   }
   
   /**
    * @param name
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   /**
    * Get the url to working directory of selected item,
    * that is the current directory you are working on.
    * @return href of selected item or href of parent folder (if item isn't folder)
    */
   public abstract String getWorkDir();
}
