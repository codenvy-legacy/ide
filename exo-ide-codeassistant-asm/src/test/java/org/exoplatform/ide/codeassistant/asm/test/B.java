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
package org.exoplatform.ide.codeassistant.asm.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
*/
public class B extends A implements I
{

   public List<Boolean> booleans;

   private final Collection<Double> doubles;

   public B() throws ClassFormatError, ClassNotFoundException
   {
      doubles = new ArrayList<Double>();
   }

   public B(List<Boolean> booleans, Collection<Double> doubles) throws ClassFormatError, ClassNotFoundException
   {
      this.booleans = booleans;
      this.doubles = doubles;
   }

   /**
    * @param s
    * @param ss
    * @param clazz
    * @return
    * @throws ClassFormatError
    * @throws ClassNotFoundException
    */
   public A createA(String s, List<String> ss, Class<?> clazz) throws ClassFormatError, ClassNotFoundException
   {
      return new A()
      {
      };
   }

   public Collection<Double> getDoubles()
   {
      return doubles;
   }

   public String getName()
   {
      return null;
   }

   public String[] getName(Long[] longs)
   {
      return null;
   }

   public <T extends Number, InputStream> T genericMethod(T number)
   {
      return null;
   }

}
