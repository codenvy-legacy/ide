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
package org.exoplatform.ide.testframework.server;

import javax.ws.rs.core.UriInfo;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FSLocation
{
   private final String url;

   /**
    * @param url full URL of resource in IDE notation
    */
   public FSLocation(String url)
   {
      this.url = url;
   }

   public String getURL()
   {
      return url;
   }

   @Deprecated
   public String getLocalPath(UriInfo uriInfo)
   {
      return getLocalPath();
   }
   
   public String getLocalPath()
   {
      
      String localPath = getRootPath();
      if (localPath == null)
         throw new IllegalStateException("Root path may not be null. ");
      if (!localPath.endsWith("/"))
         localPath += "/"; // unix like path only!
      localPath += "repository/dev-monit" + url;
      return localPath;
   }


   protected String getRootPath()
   {
      return System.getProperty("org.exoplatform.ide.server.fs-root-path");
   }
}