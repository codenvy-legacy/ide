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
package org.exoplatform.ide.git.server.rest;

import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GitLocation
{
   private final String ideURL;

   /**
    * @param ideURL full URL of working directory in IDE notation
    */
   public GitLocation(String ideURL)
   {
      this.ideURL = ideURL;
   }

   public String getIdeURL()
   {
      return ideURL;
   }

   /**
    * Determine local directory where git repository located.
    * 
    * @param uriInfo UriInfo
    */
   public String getLocalPath(UriInfo uriInfo)
   {
      String baseUrl = uriInfo.getBaseUri().toString();
      baseUrl += "/jcr/";
      String localPath = System.getProperty("org.exoplatform.ide.git.repo-dir");
      if (localPath == null)
         throw new IllegalStateException("Directory for git repositories is not specified. ");
      if (!localPath.endsWith("/"))
         localPath += "/"; // unix like path only!
      localPath += ideURL.substring(baseUrl.length());
      return localPath;
   }
}
