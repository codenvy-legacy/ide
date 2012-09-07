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
package org.exoplatform.ide.security.login;

import org.exoplatform.ide.security.oauth.OAuthAuthenticationService;
import org.exoplatform.ide.security.openid.OpenIDAuthenticationService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Deploys service required for OpenID and OAuth authentication.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FederatedLoginApplication extends Application
{
   private final Set<Class<?>> classes;

   public FederatedLoginApplication()
   {
      classes = new HashSet<Class<?>>(2);
      classes.add(OpenIDAuthenticationService.class);
      classes.add(OAuthAuthenticationService.class);
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }
}
