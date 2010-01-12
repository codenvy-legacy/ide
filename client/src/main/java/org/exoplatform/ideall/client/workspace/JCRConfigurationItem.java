/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ideall.client.workspace;

import java.util.ArrayList;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JCRConfigurationItem
{

   private String name;

   private String icon;

   private Object entry;

   private final ArrayList<JCRConfigurationItem> children = new ArrayList<JCRConfigurationItem>();

   public JCRConfigurationItem(String name)
   {
      this.name = name;
   }

   public JCRConfigurationItem(String name, String icon)
   {
      this.name = name;
      this.icon = icon;
   }

   public JCRConfigurationItem(String name, String icon, Object entry)
   {
      this.name = name;
      this.icon = icon;
      this.entry = entry;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getIcon()
   {
      return icon;
   }

   public Object getEntry()
   {
      return entry;
   }

   public ArrayList<JCRConfigurationItem> getChildren()
   {
      return children;
   }

}
