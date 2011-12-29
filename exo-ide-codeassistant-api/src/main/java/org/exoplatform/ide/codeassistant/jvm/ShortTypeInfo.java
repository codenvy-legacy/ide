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
package org.exoplatform.ide.codeassistant.jvm;

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTF;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Short information about class or interface. Contain fqn, short name,
 * modifiers Example : { "name": "java.lang.String", "modifiers": 0, "type":
 * "CLASS" }
 * 
 * 
 * Created by The eXo Platform SAS.
 * 
 */
public class ShortTypeInfo extends Member
{

   /**
    * Means this is CLASS, INTERFACE or ANNOTATION
    */
   private String type;

   public ShortTypeInfo()
   {
   }

   public ShortTypeInfo(String name, int modifiers, String type)
   {
      super(name, modifiers);
      this.type = type;
   }
   
   public String getType()
   {
      return type;
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      type = readStringUTF(in);
   }

   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(modifierToString());
      builder.append(" ");
      builder.append(type);
      builder.append(" ");
      builder.append(getName());

      return builder.toString();
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      writeStringUTF(type, out);
   }
}
