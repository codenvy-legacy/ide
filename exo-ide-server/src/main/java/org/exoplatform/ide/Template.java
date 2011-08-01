/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide;

/**
 * Abstract template data.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: AbstractTemplate.java Jul 26, 2011 5:37:26 PM vereshchaka $
 *
 */
public abstract class Template
{
   private String name;
   
   private String description;
   
   /**
    * Flag, is template default.
    * If template is default, it can't be deleted,
    * unlike user's template
    */
   private boolean defaultTemplate;
   
   /**
    * Auxiliary field.
    * It is necessary for client, that while
    * parsing json it will be able to detect the type of child (folder or file).
    */
   private String childType;
   
   public Template(){}
   
   protected Template(String type)
   {
      this.childType = type;
   };
   
   /**
    * @return the defaultTemplate
    */
   public boolean isDefault()
   {
      return defaultTemplate;
   }
   
   /**
    * @param defaultTemplate the defaultTemplate to set
    */
   public void setDefault(boolean defaultTemplate)
   {
      this.defaultTemplate = defaultTemplate;
   }
   
   /**
    * @return the type
    */
   public String getChildType()
   {
      return childType;
   }
   
   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

}
