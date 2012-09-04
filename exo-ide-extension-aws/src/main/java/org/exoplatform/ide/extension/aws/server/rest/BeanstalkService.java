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
package org.exoplatform.ide.extension.aws.server.rest;

import org.exoplatform.ide.extension.aws.server.AmazonWebServiceException;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: BeanstalkService.java Aug 23, 2012
 * 
 */
@Path("ide/cloudfoundry")
public class BeanstalkService
{

   @javax.inject.Inject
   private VirtualFileSystemRegistry vfsRegistry;

   public BeanstalkService()
   {
   }

   protected BeanstalkService(VirtualFileSystemRegistry vfsRegistry)
   {
      this.vfsRegistry = vfsRegistry;
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws AmazonWebServiceException, IOException,
      VirtualFileSystemException
   {

   }

   @Path("logout")
   @POST
   public void logout() throws IOException, VirtualFileSystemException
   {

   }

}
