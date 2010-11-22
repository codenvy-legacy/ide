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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class B extends A implements I
{
   
   public List<Boolean> booleans;
   
   private final Collection<Double> doubles;
   
   public B() throws ClassFormatError, ClassNotFoundException
   {
      doubles =  new ArrayList<Double>();
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
      return new A(){};
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
   

}
