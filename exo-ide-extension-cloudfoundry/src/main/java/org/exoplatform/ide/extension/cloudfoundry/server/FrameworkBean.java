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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FrameworkBean implements Framework
{
   private String displayName;
   private String type;
   /** Default memory size in megabytes. */
   private int memory;
   private String description;

   public FrameworkBean(String type, String displayName, int memory, String description)
   {
      this.type = type;
      this.displayName = displayName;
      this.memory = memory;
      this.description = description;
   }

   public FrameworkBean()
   {
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#getType()
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#setType(java.lang.String)
    */
   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#getDisplayName()
    */
   @Override
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#setDisplayName(java.lang.String)
    */
   @Override
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#getMemory()
    */
   @Override
   public int getMemory()
   {
      return memory;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#setMemory(int)
    */
   @Override
   public void setMemory(int memory)
   {
      this.memory = memory;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#getDescription()
    */
   @Override
   public String getDescription()
   {
      return description;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.shared.Framework#setDescription(java.lang.String)
    */
   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }
}
