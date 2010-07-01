/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.groovy;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SimpleParameterEntry
{

   private String name;

   private String value;

   public SimpleParameterEntry()
   {
   }

   public SimpleParameterEntry(String name, String value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Returns name.
    * 
    * @return Returns the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Sets name.
    * 
    * @param name
    *          The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Returns value.
    * 
    * @return Returns the value.
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Sets value.
    * 
    * @param value
    *          The value to set.
    */
   public void setValue(String value)
   {
      this.value = value;
   }

}
