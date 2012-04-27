/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.exoplatform.ide.extension.cloudfoundry.shared.Runtime;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FrameworkImpl implements Framework
{
   private String name;
   private List<Runtime> runtimes;
   private String description;
   private int memory;
   private String displayName;

   public FrameworkImpl(String name, String displayName, List<Runtime> runtimes, int memory, String description)
   {
      this.name = name;
      this.displayName = displayName;
      this.runtimes = runtimes;
      this.memory = memory;
      this.description = description;
   }

   public FrameworkImpl()
   {
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public List<Runtime> getRuntimes()
   {
      return runtimes;
   }

   @Override
   public void setRuntimes(List<Runtime> runtimes)
   {
      this.runtimes = runtimes;
   }

   @Override
   public String getDescription()
   {
      return description;
   }

   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }

   @Override
   public int getMemory()
   {
      return memory;
   }

   @Override
   public void setMemory(int memory)
   {
      this.memory = memory;
   }

   @Override
   public String getDisplayName()
   {
      return displayName;
   }

   @Override
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }
}
