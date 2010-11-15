/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant;

import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public abstract class A 
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

   /**
    * @return the string
    */
   public String getString()
   {
      return string;
   }
   
   /**
    * @param string the string to set
    */
   public void setString(String string)
   {
      this.string = string;
   }
   
   
   /**
    * @return the integer
    */
   public Integer getInteger()
   {
      return integer;
   }

   /**
    * @param integer the integer to set
    */
   public void setInteger(Integer integer)
   {
      this.integer = integer;
   }

   /**
    * @return the l
    */
   public long getL()
   {
      return l;
   }

   /**
    * @param l the l to set
    */
   public void setL(long l)
   {
      this.l = l;
   }

   public A(String string, Integer integer, long l)
   {
      super();
      this.string = string;
      this.integer = integer;
      this.l = l;
   }
   
   

}
