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
package org.exoplatform.ide.extension.googleappengine.server;

import org.exoplatform.ide.extension.googleappengine.shared.User;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UserImpl implements User
{
   private String name;
   private boolean authenticated;
   private String type;

   public UserImpl(String name, boolean authenticated, String type)
   {
      this.name = name;
      this.authenticated = authenticated;
      this.type = type;
   }

   @Override
   public boolean isAuthenticated()
   {
      return authenticated;
   }

   @Override
   public void setAuthenticated(boolean authenticated)
   {
      this.authenticated = authenticated;
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
   public String getAuthenticationType()
   {
      return type;
   }

   @Override
   public void setAuthenticationType(String type)
   {
      this.type = type;
   }

   @Override
   public String toString()
   {
      return "UserImpl{" +
         "name='" + name + '\'' +
         ", authenticated=" + authenticated +
         ", type='" + type + '\'' +
         '}';
   }
}
