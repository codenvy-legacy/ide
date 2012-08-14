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
package org.exoplatform.ide.security.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication service provider. Allow store and provide services which implements OAuthService.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: $
 */
public class OAuthServiceProvider
{
   private OAuthServiceProvider()
   {
   }

   private static Map<String, OAuthService> authServices = new HashMap<String, OAuthService>();

   /**
    * Get authentication service by name.
    *
    * @param name
    *     name of service
    * @return
    *     authentication service represent by interface module
    */
   public static OAuthService getAuthService(String name)
   {
      try
      {
         OAuthServiceProvider.registerAuthService("google", new OAuthAuthenticatorGoogle());
         OAuthServiceProvider.registerAuthService("github", new OAuthAuthenticatorGithub());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return authServices.get(name);
   }

   /**
    * Register authentication service by interface.
    *
    * @param name
    *     name of authentication service
    * @param service
    *     class of service which implement OAuthService interface
    * @return
    *     false - if service with specified name already exists, otherwise - true
    */
   public static boolean registerAuthService(String name, OAuthService service)
   {
      if (authServices.containsKey(name))
      {
         return false;
      }
      authServices.put(name, service);

      return true;
   }
}
