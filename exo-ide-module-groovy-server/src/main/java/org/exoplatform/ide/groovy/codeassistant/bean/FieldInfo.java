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
package org.exoplatform.ide.groovy.codeassistant.bean;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class FieldInfo extends Member
{
   
   private String type;
   
   private String declaringClass; 

   public FieldInfo(String type, Integer modifiers, String name, String declaringClass)
   {
      super(modifiers, name);
      this.type = type;
      this.declaringClass = declaringClass;
   }
   
   public FieldInfo()
   {
      
   }
   
   public String getType()
   {
      return type;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   public String getDeclaringClass()
   {
      return declaringClass;
   }
   
   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

}
