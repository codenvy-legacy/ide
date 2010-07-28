/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.framework.application;

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationConfiguration
{

   private String registryURL;

   private String defaultEntryPoint;

   private String context;

   private String loopbackServiceContext;

   private String uploadServiceContext;

   private String publicContext;

   private String gadgetURL = GWT.getModuleBaseURL();

   private String gadgetServer;

   public ApplicationConfiguration()
   {
   }

   public ApplicationConfiguration(String registryURL)
   {
      this.registryURL = registryURL;
   }

   public String getRegistryURL()
   {
      return registryURL;
   }

   public void setRegistryURL(String registryURL)
   {
      this.registryURL = registryURL;
   }

   public String getDefaultEntryPoint()
   {
      return defaultEntryPoint;
   }

   public void setDefaultEntryPoint(String defaultEntryPoint)
   {
      this.defaultEntryPoint = defaultEntryPoint;
   }

   public String getContext()
   {
      return context;
   }

   public void setContext(String context)
   {
      this.context = context;
   }

   public String getLoopbackServiceContext()
   {
      return loopbackServiceContext;
   }

   public void setLoopbackServiceContext(String loopbackServiceContext)
   {
      this.loopbackServiceContext = loopbackServiceContext;
   }

   public String getUploadServiceContext()
   {
      return uploadServiceContext;
   }

   public void setUploadServiceContext(String uploadServiceContext)
   {
      this.uploadServiceContext = uploadServiceContext;
   }

   public String getPublicContext()
   {
      return publicContext;
   }

   public void setPublicContext(String publicContext)
   {
      this.publicContext = publicContext;
   }

   public String getGadgetURL()
   {
      return gadgetURL;
   }

   public void setGadgetURL(String gadgetURL)
   {
      this.gadgetURL = gadgetURL;
   }

   public String getGadgetServer()
   {
      return gadgetServer;
   }

   public void setGadgetServer(String gadgetServer)
   {
      this.gadgetServer = gadgetServer;
   }

}
