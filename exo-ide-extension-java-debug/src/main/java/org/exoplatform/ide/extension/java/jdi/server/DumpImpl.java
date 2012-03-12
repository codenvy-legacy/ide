/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.Dump;
import org.exoplatform.ide.extension.java.jdi.shared.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DumpImpl implements Dump
{
   private List<Value> fields;
   private List<Value> localVariables;

   public DumpImpl(List<Value> fields, List<Value> localVariables)
   {
      this.fields = fields;
      this.localVariables = localVariables;
   }

   public DumpImpl()
   {
   }

   @Override
   public List<Value> getFields()
   {
      return fields;
   }

   @Override
   public List<Value> getLocalVariables()
   {
      return localVariables;
   }

   @Override
   public void setFields(List<Value> fields)
   {
      this.fields = fields;
   }

   @Override
   public void setLocalVariables(List<Value> localVariables)
   {
      this.localVariables = localVariables;
   }

   //

   void addLocalVariable(Value value)
   {
      if (localVariables == null)
      {
         localVariables = new ArrayList<Value>();
      }
      localVariables.add(value);
   }

   void addField(Value value)
   {
      if (fields == null)
      {
         fields = new ArrayList<Value>();
      }
      fields.add(value);
   }

   @Override
   public String toString()
   {
      return "DumpImpl{\n" +
         "====== FIELDS =====\n" + toString(fields) +
         "\n===== LOCAL_VARIABLES======\n" + toString(localVariables) +
         "\n}";
   }

   private <E extends Value> String toString(Collection<E> collection)
   {
      Iterator<E> i = collection.iterator();
      if (!i.hasNext())
      {
         return "";
      }

      StringBuilder sb = new StringBuilder();
      while (true)
      {
         E e = i.next();
         sb.append(e == collection ? "(this Collection)" : e);
         if (!i.hasNext())
         {
            return sb.toString();
         }
         sb.append(',');
         sb.append('\n');
      }
   }
}
