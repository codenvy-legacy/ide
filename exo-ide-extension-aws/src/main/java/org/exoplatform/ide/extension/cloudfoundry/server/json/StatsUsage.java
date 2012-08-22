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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StatsUsage
{
   private double cpu;
   /* Used memory (in KB). */
   private long mem;
   /* Used disk (in bytes). */
   private long disk;

   public double getCpu()
   {
      return cpu;
   }

   public void setCpu(double cpu)
   {
      this.cpu = cpu;
   }

   public long getMem()
   {
      return mem;
   }

   public void setMem(long mem)
   {
      this.mem = mem;
   }

   public long getDisk()
   {
      return disk;
   }

   public void setDisk(long disk)
   {
      this.disk = disk;
   }

   @Override
   public String toString()
   {
      return "StatsUsage [cpu=" + cpu + ", mem=" + mem + ", disk=" + disk + "]";
   }
}
