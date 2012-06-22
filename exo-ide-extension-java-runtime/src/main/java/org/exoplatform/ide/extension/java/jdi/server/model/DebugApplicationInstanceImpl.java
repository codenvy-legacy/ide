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

import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @deprecated
 */
public class DebugApplicationInstanceImpl extends ApplicationInstanceImpl implements DebugApplicationInstance
{
   private String debugHost;
   private int debugPort;

   public DebugApplicationInstanceImpl(String name,
                                       String webURL,
                                       String stopURL,
                                       int lifetime,
                                       String debugHost,
                                       int debugPort)
   {
      super(name, webURL, stopURL, lifetime);
      this.debugHost = debugHost;
      this.debugPort = debugPort;
   }

   public DebugApplicationInstanceImpl(String name, String webURL, String stopURL, String debugHost, int debugPort)
   {
      super(name, webURL, stopURL);
      this.debugHost = debugHost;
      this.debugPort = debugPort;
   }

   public DebugApplicationInstanceImpl()
   {
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
      return "DebugApplicationInstanceImpl{" +
         "name='" + getName() + '\'' +
         ", webURL='" + getHost() + '\'' +
         ", stopURL='" + getStopURL() + '\'' +
         ", lifetime=" + getLifetime() +
         ", debugPort=" + debugPort +
         ", debugHost='" + debugHost + '\'' +
         '}';
   }
}
