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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.AppfogApplicationStatistics;
import java.util.Arrays;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogApplicationStatisticsImpl implements AppfogApplicationStatistics
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

   static class Builder
   {
      private String name;
      private String state;
      private String host;
      private int port;
      private String[] uris;
      private String uptime;
      private int cpuCores;
      private double cpu;
      private int mem;
      private int disk;
      private int memLimit;
      private int diskLimit;

      public Builder setName(String name)
      {
         this.name = name;
         return this;
      }

      public Builder setState(String state)
      {
         this.state = state;
         return this;
      }

      public Builder setHost(String host)
      {
         this.host = host;
         return this;
      }

      public Builder setPort(int port)
      {
         this.port = port;
         return this;
      }

      public Builder setUris(String[] uris)
      {
         this.uris = uris;
         return this;
      }

      public Builder setUptime(String uptime)
      {
         this.uptime = uptime;
         return this;
      }

      public Builder setCpuCores(int cpuCores)
      {
         this.cpuCores = cpuCores;
         return this;
      }

      public Builder setCpu(double cpu)
      {
         this.cpu = cpu;
         return this;
      }

      public Builder setMem(int mem)
      {
         this.mem = mem;
         return this;
      }

      public Builder setDisk(int disk)
      {
         this.disk = disk;
         return this;
      }

      public Builder setMemLimit(int memLimit)
      {
         this.memLimit = memLimit;
         return this;
      }

      public Builder setDiskLimit(int diskLimit)
      {
         this.diskLimit = diskLimit;
         return this;
      }

      public AppfogApplicationStatistics build()
      {
         return new AppfogApplicationStatisticsImpl(this);
      }
   }

   private AppfogApplicationStatisticsImpl(Builder builder)
   {
      this.name = builder.name;
      this.state = builder.state;
      this.host = builder.host;
      this.port = builder.port;
      this.uris = builder.uris;
      this.uptime = builder.uptime;
      this.cpuCores = builder.cpuCores;
      this.cpu = builder.cpu;
      this.mem = builder.mem;
      this.disk = builder.disk;
      this.memLimit = builder.memLimit;
      this.diskLimit = builder.diskLimit;
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
   public String getState()
   {
      return state;
   }

   @Override
   public void setState(String state)
   {
      this.state = state;
   }

   @Override
   public String getHost()
   {
      return host;
   }

   @Override
   public void setHost(String host)
   {
      this.host = host;
   }

   @Override
   public int getPort()
   {
      return port;
   }

   @Override
   public void setPort(int port)
   {
      this.port = port;
   }

   @Override
   public String[] getUris()
   {
      return uris;
   }

   @Override
   public void setUris(String[] uris)
   {
      this.uris = uris;
   }

   @Override
   public String getUptime()
   {
      return uptime;
   }

   @Override
   public void setUptime(String uptime)
   {
      this.uptime = uptime;
   }

   @Override
   public int getCpuCores()
   {
      return cpuCores;
   }

   @Override
   public void setCpuCores(int cores)
   {
      this.cpuCores = cores;
   }

   @Override
   public double getCpu()
   {
      return cpu;
   }

   @Override
   public void setCpu(double cpu)
   {
      this.cpu = cpu;
   }

   @Override
   public int getMem()
   {
      return mem;
   }

   @Override
   public void setMem(int mem)
   {
      this.mem = mem;
   }

   @Override
   public int getDisk()
   {
      return disk;
   }

   @Override
   public void setDisk(int disk)
   {
      this.disk = disk;
   }

   @Override
   public int getMemLimit()
   {
      return memLimit;
   }

   @Override
   public void setMemLimit(int memLimit)
   {
      this.memLimit = memLimit;
   }

   @Override
   public int getDiskLimit()
   {
      return diskLimit;
   }

   @Override
   public void setDiskLimit(int diskLimit)
   {
      this.diskLimit = diskLimit;
   }

   @Override
   public String toString()
   {
      return "CloudfoundryApplicationStatisticsImpl{" +
         "name='" + name + '\'' +
         ", state='" + state + '\'' +
         ", host='" + host + '\'' +
         ", port=" + port +
         ", uris=" + (uris == null ? null : Arrays.asList(uris)) +
         ", uptime='" + uptime + '\'' +
         ", cpuCores=" + cpuCores +
         ", cpu=" + cpu +
         ", mem=" + mem +
         ", disk=" + disk +
         ", memLimit=" + memLimit +
         ", diskLimit=" + diskLimit +
         '}';
   }
}
