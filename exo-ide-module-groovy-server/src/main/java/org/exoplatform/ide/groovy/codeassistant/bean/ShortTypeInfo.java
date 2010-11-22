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
public class ShortTypeInfo extends Member
{
   
   private String qualifiedName;
   
   private Types type;
   
   public ShortTypeInfo()
   {
   }

   public ShortTypeInfo(Integer modifiers, String name, String qualifiedName, Types type)
   {
      super(modifiers, name);
      this.qualifiedName = qualifiedName;
      this.type = type;
   }
   
   public void setQualifiedName(String qualifiedName)
   {
      this.qualifiedName = qualifiedName;
   }
   
   public String getQualifiedName()
   {
      return qualifiedName;
   }
   
   public Types getType()
   {
      return type;
   }
   
   public void setType(Types type)
   {
      this.type = type;
   }
   
   
   
   
   
   

}
