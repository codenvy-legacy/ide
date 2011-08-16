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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationStatistics
{
   /** Application name. */
   private String name;
   /** Application state. */
   private String state;
   /** IP address. */
   private String host;
   /** Port. */
   private int port = -1;
   /** Application URLs. */
   private String[] uris;

   /** Application uptime. If format X?d:XXh:XXm:XXs. */
   private String uptime;
   /** CPU cores. */
   private int cpuCores = -1;

   /** CPU usage in percents. */
   private double cpu = -1;
   /** Used memory (in MB). */
   private int mem = -1;
   /** Used disk (in MB). */
   private int disk = -1;

   /** Memory limit (in MB). */
   private int memLimit = -1;
   /** Disk limit (in MB). */
   private int diskLimit = -1;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getState()
   {
      return state;
   }

   public void setState(String state)
   {
      this.state = state;
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public String[] getUris()
   {
      return uris;
   }

   public void setUris(String[] uris)
   {
      this.uris = uris;
   }

   public String getUptime()
   {
      return uptime;
   }

   public void setUptime(String uptime)
   {
      this.uptime = uptime;
   }

   public int getCpuCores()
   {
      return cpuCores;
   }

   public void setCpuCores(int cores)
   {
      this.cpuCores = cores;
   }

   public double getCpu()
   {
      return cpu;
   }

   public void setCpu(double cpu)
   {
      this.cpu = cpu;
   }

   public int getMem()
   {
      return mem;
   }

   public void setMem(int mem)
   {
      this.mem = mem;
   }

   public int getDisk()
   {
      return disk;
   }

   public void setDisk(int disk)
   {
      this.disk = disk;
   }

   public int getMemLimit()
   {
      return memLimit;
   }

   public void setMemLimit(int memLimit)
   {
      this.memLimit = memLimit;
   }

   public int getDiskLimit()
   {
      return diskLimit;
   }

   public void setDiskLimit(int diskLimit)
   {
      this.diskLimit = diskLimit;
   }
}
