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
package org.exoplatform.ide.extension.gadget.client.service;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class GadgetService
{

   private static GadgetService instance;

   public static GadgetService getInstance()
   {
      return instance;
   }

   protected GadgetService()
   {
      instance = this;
   }

   /**
    * @param tokenResponse
    * @param callback
    */
   public abstract void getGadgetMetadata(TokenResponse tokenResponse, AsyncRequestCallback<GadgetMetadata>callback);
   
   /**
    * @param request
    * @param callback
    */
   public abstract void getSecurityToken(TokenRequest request, AsyncRequestCallback<TokenResponse> callback);
   
   /**
    * @param href
    * @param callback
    */
   public abstract void deployGadget(String href, AsyncRequestCallback<String> callback);
   
   /**
    * @param href
    * @param callback
    */
   public abstract void undeployGadget(String href, AsyncRequestCallback<String> callback);
   
   

}
