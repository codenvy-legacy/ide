/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.configuration;

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class IDEConfiguration
{

   private String registryURL;

   private String defaultEntryPoint;

   private String context;

   private String loopbackServiceContext;

   private String uploadServiceContext;

   private String publicContext;

   private String gadgetURL = GWT.getModuleBaseURL();

   private String gadgetServer;
   
   private int httpsPort = 0;
   
   public IDEConfiguration()
   {
   }

   public IDEConfiguration(String registryURL)
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
   
   public int getHttpsPort()
   {
      return httpsPort;
   }
   
   public void setHttpsPort(int httpsPort)
   {
      this.httpsPort = httpsPort;
   }
   
}
