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
package org.exoplatform.ide.editor.java.client.model;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 2, 2011 12:34:20 PM evgen $
 * 
 */
public class ShortTypeInfo
{
   private Integer modifiers;

   private String name;

   /**
    * Full Qualified Class Name
    */
   private String qualifiedName;

   /**
    * Means this is CLASS, INTERFACE or ANNOTATION
    */
   private Types type;

   /**
    * 
    */
   public ShortTypeInfo()
   {
   }

   /**
    * @param modifiers
    * @param name
    * @param qualifiedName
    * @param type
    */
   public ShortTypeInfo(Integer modifiers, String name, String qualifiedName, Types type)
   {
      super();
      this.modifiers = modifiers;
      this.name = name;
      this.qualifiedName = qualifiedName;
      this.type = type;
   }

   /**
    * @return the modifiers
    */
   public Integer getModifiers()
   {
      return modifiers;
   }

   /**
    * @param modifiers the modifiers to set
    */
   public void setModifiers(Integer modifiers)
   {
      this.modifiers = modifiers;
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
    * @return the qualifiedName
    */
   public String getQualifiedName()
   {
      return qualifiedName;
   }

   /**
    * @param qualifiedName the qualifiedName to set
    */
   public void setQualifiedName(String qualifiedName)
   {
      this.qualifiedName = qualifiedName;
   }

   /**
    * @return the type
    */
   public Types getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(Types type)
   {
      this.type = type;
   }

}
