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

import java.util.Arrays;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StatsInfo
{
   private String name;
   /* IP address. */
   private String host;
   private int port;
   private String[] uris;
   private double uptime;
   /* Memory quota (in bytes). */
   private long mem_quota;
   /* Disk quota (in bytes). */
   private long disk_quota;
   /* ??? */
   private long fds_quota;
   /* CPU cores. */
   private int cores;

   private StatsUsage usage;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
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

   public double getUptime()
   {
      return uptime;
   }

   public void setUptime(double uptime)
   {
      this.uptime = uptime;
   }

   public long getMem_quota()
   {
      return mem_quota;
   }

   public void setMem_quota(long mem_quota)
   {
      this.mem_quota = mem_quota;
   }

   public long getDisk_quota()
   {
      return disk_quota;
   }

   public void setDisk_quota(long disk_quota)
   {
      this.disk_quota = disk_quota;
   }

   public long getFds_quota()
   {
      return fds_quota;
   }

   public void setFds_quota(long fds_quota)
   {
      this.fds_quota = fds_quota;
   }

   public int getCores()
   {
      return cores;
   }

   public void setCores(int cores)
   {
      this.cores = cores;
   }

   public StatsUsage getUsage()
   {
      return usage;
   }

   public void setUsage(StatsUsage usage)
   {
      this.usage = usage;
   }

   @Override
   public String toString()
   {
      return "StatsInfo [name=" + name + ", host=" + host + ", port=" + port + ", uris=" + Arrays.toString(uris)
         + ", uptime=" + uptime + ", mem_quota=" + mem_quota + ", disk_quota=" + disk_quota + ", fds_quota="
         + fds_quota + ", cores=" + cores + ", usage=" + usage + "]";
   }
}
