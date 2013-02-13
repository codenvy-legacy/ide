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

import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InstanceImpl implements Instance
{
   private String debugHost;
   private int debugPort;
   private String consoleHost;
   private int consolePort;

   public InstanceImpl(String debugHost, int debugPort, String consoleHost, int consolePort)
   {
      this.debugHost = debugHost;
      this.debugPort = debugPort;
      this.consoleHost = consoleHost;
      this.consolePort = consolePort;
   }

   public InstanceImpl()
   {
   }

   @Override
   public String getDebugHost()
   {
      return debugHost;
   }

   @Override
   public void setDebugHost(String host)
   {
      debugHost = host;
   }

   @Override
   public int getDebugPort()
   {
      return debugPort;
   }

   @Override
   public void setDebugPort(int port)
   {
      debugPort = port;
   }

   @Override
   public String getConsoleHost()
   {
      return consoleHost;
   }

   @Override
   public void setConsoleHost(String host)
   {
      consoleHost = host;
   }

   @Override
   public int getConsolePort()
   {
      return consolePort;
   }

   @Override
   public void setConsolePort(int port)
   {
      consolePort = port;
   }

   @Override
   public String toString()
   {
      return "InstanceImpl{" +
         "debugHost='" + debugHost + '\'' +
         ", debugPort=" + debugPort +
         ", consoleHost='" + consoleHost + '\'' +
         ", consolePort=" + consolePort +
         '}';
   }
}
