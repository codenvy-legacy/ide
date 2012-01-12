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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

/**
 * Describes an account held by this Person, 
 * which MAY be on the Service Provider's service, or MAY be on a different service.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public class Account
{
   /**
    * The top-most authoritative domain for this account.
    */
   private String domain;

   /**
    * An alphanumeric user name, usually chosen by the user.
    */
   private String userName;

   /**
    *A user ID associated with this account. 
    */
   private String userId;

   /**
    * @return the domain
    */
   public String getDomain()
   {
      return domain;
   }

   /**
    * @param domain the domain to set
    */
   public void setDomain(String domain)
   {
      this.domain = domain;
   }

   /**
    * @return the userName
    */
   public String getUserName()
   {
      return userName;
   }

   /**
    * @param userName the userName to set
    */
   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   /**
    * @return the userId
    */
   public String getUserId()
   {
      return userId;
   }

   /**
    * @param userId the userId to set
    */
   public void setUserId(String userId)
   {
      this.userId = userId;
   }
}
