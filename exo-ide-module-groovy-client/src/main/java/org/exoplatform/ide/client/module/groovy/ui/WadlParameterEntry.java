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
package org.exoplatform.ide.client.module.groovy.ui;

import org.exoplatform.ide.client.module.groovy.service.SimpleParameterEntry;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlParameterEntry extends SimpleParameterEntry
{
   // private String name;

   // private String value;

   private String type;

   private boolean send;

   private String defaultValue;

   public WadlParameterEntry(boolean send, String name, String type, String value)
   {
      super(name, value);
      this.type = type;
      this.send = send;
      this.defaultValue = "";
   }

   public WadlParameterEntry(boolean send, String name, String type, String value, String defaultValue)
   {
      super(name, value);
      this.type = type;
      this.send = send;
      this.defaultValue = defaultValue;
   }

   /**
    * Set type
    * @param type
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * Get type
    * @return
    */
   public String getType()
   {
      return type;
   }

   /**
    * Is send header
    *  
    * @return
    */
   public boolean isSend()
   {
      return send;
   }

   /**
    * Set if isSend
    * 
    * @param send
    */
   public void setSend(boolean send)
   {
      this.send = send;
   }

   public String getDefaultValue()
   {
      return defaultValue;
   }

   public void setDefaultValue(String defaultValue)
   {
      this.defaultValue = defaultValue;
   }

}
