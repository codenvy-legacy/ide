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
package org.exoplatform.ide.extension.appfog.shared;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface AppfogApplicationStatistics
{
   String getName();

   void setName(String name);

   String getState();

   void setState(String state);

   String getHost();

   void setHost(String host);

   int getPort();

   void setPort(int port);

   String[] getUris();

   void setUris(String[] uris);

   String getUptime();

   void setUptime(String uptime);

   int getCpuCores();

   void setCpuCores(int cores);

   double getCpu();

   void setCpu(double cpu);

   int getMem();

   void setMem(int mem);

   int getDisk();

   void setDisk(int disk);

   int getMemLimit();

   void setMemLimit(int memLimit);

   int getDiskLimit();

   void setDiskLimit(int diskLimit);
}
