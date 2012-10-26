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
package org.exoplatform.ide.extension.java.jdi.server.model;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationInstanceImpl implements ApplicationInstance
{
   private String name;
   private String host;

   private int port = 80;
   private String stopURL;
   private int lifetime = -1;
   private String debugHost;
   private int debugPort = -1;

   public ApplicationInstanceImpl(String name,
                                  String host,
                                  String stopURL,
                                  int lifetime,
                                  String debugHost,
                                  int debugPort)
   {
      this.name = name;
      this.host = host;
      this.stopURL = stopURL;
      this.lifetime = lifetime;
      this.debugHost = debugHost;
      this.debugPort = debugPort;
   }

   public ApplicationInstanceImpl(String name, String host, String stopURL, int lifetime)
   {
      this.name = name;
      this.host = host;
      this.stopURL = stopURL;
      this.lifetime = lifetime;
   }

   public ApplicationInstanceImpl(String name, String host, String stopURL)
   {
      this.name = name;
      this.host = host;
      this.stopURL = stopURL;
   }

   public ApplicationInstanceImpl()
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
   public String getStopURL()
   {
      return stopURL;
   }

   @Override
   public void setStopURL(String stopURL)
   {
      this.stopURL = stopURL;
   }

   @Override
   public int getLifetime()
   {
      return lifetime;
   }

   @Override
   public void setLifetime(int lifetime)
   {
      this.lifetime = lifetime;
   }

   @Override
   public String getDebugHost()
   {
      return debugHost;
   }

   @Override
   public void setDebugHost(String debugHost)
   {
      this.debugHost = debugHost;
   }

   @Override
   public int getDebugPort()
   {
      return debugPort;
   }

   @Override
   public void setDebugPort(int debugPort)
   {
      this.debugPort = debugPort;
   }

   @Override
   public String toString()
   {
      return "ApplicationInstanceImpl{" +
         "name='" + name + '\'' +
         ", host='" + host + '\'' +
         ", port=" + port +
         ", stopURL='" + stopURL + '\'' +
         ", lifetime=" + lifetime +
         ", debugHost='" + debugHost + '\'' +
         ", debugPort=" + debugPort +
         '}';
   }
}
