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

import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GaeUserImpl implements GaeUser
{
   private String id;
   private boolean authenticated;

   public GaeUserImpl(String id, boolean authenticated)
   {
      this.id = id;
      this.authenticated = authenticated;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public void setId(String id)
   {
      this.id = id;
   }

   @Override
   public String getEmail()
   {
      // Not need email address in this case.
      return null;
   }

   @Override
   public void setEmail(String id)
   {
      // Not need email address in this case.
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
}
