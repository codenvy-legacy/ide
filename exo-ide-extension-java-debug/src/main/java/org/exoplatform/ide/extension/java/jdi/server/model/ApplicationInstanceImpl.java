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
   private String webURL;
   private String stopURL;
   private int lifetime = -1;

   public ApplicationInstanceImpl(String name, String webURL, String stopURL, int lifetime)
   {
      this.name = name;
      this.webURL = webURL;
      this.stopURL = stopURL;
      this.lifetime = lifetime;
   }

   public ApplicationInstanceImpl(String name, String webURL, String stopURL)
   {
      this.name = name;
      this.webURL = webURL;
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
   public String getWebURL()
   {
      return webURL;
   }

   @Override
   public void setWebURL(String webURL)
   {
      this.webURL = webURL;
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
   public String toString()
   {
      return "ApplicationInstanceImpl{" +
         "name='" + name + '\'' +
         ", webURL='" + webURL + '\'' +
         ", stopURL='" + stopURL + '\'' +
         ", lifetime=" + lifetime +
         '}';
   }
}
