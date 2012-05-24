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
package org.exoplatform.ide.extension.googleappengine.client.model;

/**
 * Represents user's credentials.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 11:13:17 AM anya $
 * 
 */
public interface Credentials
{
   /**
    * Returns user's email.
    * 
    * @return {@link String} user's email
    */
   String getEmail();

   /**
    * Sets user's email.
    * 
    * @param email user's email
    */
   void setEmail(String email);

   /**
    * Returns user's password.
    * 
    * @return {@link String} user's password
    */
   String getPassword();

   /**
    * Sets user's password.
    * 
    * @param password user's password
    */
   void setPassword(String password);
}
