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
package org.exoplatform.ide.codeassistant.asm.objects;

import java.util.Set;

/**
 *
 */
public class A
{
   private String string;

   protected Integer integer;

   public long l;

   public A() throws ClassNotFoundException
   {
   }

   public A(Set<Class<?>> classes) throws ClassNotFoundException, ClassFormatError
   {
   }

   private A(int k) throws ClassNotFoundException
   {
   }

   protected A(String k) throws ClassNotFoundException
   {
   }

   public A(String string, Integer integer, long l)
   {
      super();
      this.string = string;
      this.integer = integer;
      this.l = l;
   }

   public String getString()
   {
      return string;
   }

   public void setString(String string)
   {
      this.string = string;
   }

   public Integer getInteger()
   {
      return integer;
   }

   public void setInteger(Integer integer)
   {
      this.integer = integer;
   }

   public long getL()
   {
      return l;
   }

   public void setL(long l)
   {
      this.l = l;
   }

   private void method1()
   {
   }

   protected void method2()
   {
   }
}
