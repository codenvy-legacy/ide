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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.lang.reflect.Modifier;

/**
 * 
 * Member is reflects identifying information about a single member (a field or
 * a method) or a constructor.
 * 
 * @see TypeInfo
 * @see FieldInfo
 * @see MethodInfo
 * 
 *      Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public abstract class Member implements IMember 
{

   protected Integer modifiers;

   protected String name;

   public Member()
   {
   }

   public Member(Integer modifiers, String name)
   {
      this.modifiers = modifiers;
      this.name = name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Integer getModifiers()
   {
      return modifiers;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setModifiers(Integer modifiers)
   {
      this.modifiers = modifiers;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String modifierToString()
   {
      StringBuffer sb = new StringBuffer();
      int len;

      if ((modifiers & Modifier.PUBLIC) != 0)
      {
         sb.append("public ");
      }
      if ((modifiers & Modifier.PROTECTED) != 0)
      {
         sb.append("protected ");
      }
      if ((modifiers & Modifier.PRIVATE) != 0)
      {
         sb.append("private ");
      }

      /* Canonical order */
      if ((modifiers & Modifier.ABSTRACT) != 0)
      {
         sb.append("abstract ");
      }
      if ((modifiers & Modifier.STATIC) != 0)
      {
         sb.append("static ");
      }
      if ((modifiers & Modifier.FINAL) != 0)
      {
         sb.append("final ");
      }
      if ((modifiers & Modifier.TRANSIENT) != 0)
      {
         sb.append("transient ");
      }
      if ((modifiers & Modifier.VOLATILE) != 0)
      {
         sb.append("volatile ");
      }
      if ((modifiers & Modifier.SYNCHRONIZED) != 0)
      {
         sb.append("synchronized ");
      }
      if ((modifiers & Modifier.NATIVE) != 0)
      {
         sb.append("native ");
      }
      if ((modifiers & Modifier.STRICT) != 0)
      {
         sb.append("strictfp ");
      }
      if ((modifiers & Modifier.INTERFACE) != 0)
      {
         sb.append("interface ");
      }

      if ((len = sb.length()) > 0)
      {
         return sb.toString().substring(0, len - 1);
      }
      return "";
   }

}
