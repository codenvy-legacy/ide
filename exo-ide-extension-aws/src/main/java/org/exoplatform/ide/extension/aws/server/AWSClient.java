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
package org.exoplatform.ide.extension.aws.server;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class AWSClient
{
   protected final AWSAuthenticator authenticator;

   public AWSClient(AWSAuthenticator authenticator)
   {
      this.authenticator = authenticator;
   }

   //

   /**
    * Login AWS API. Specified access and secret keys stored for next usage by current user.
    *
    * @param accessKey
    *    AWS access key
    * @param secret
    *    AWS secret key
    * @throws org.exoplatform.ide.extension.aws.server.AWSException
    *    if any error occurs when attempt to login to Amazon server
    */
   public final void login(String accessKey, String secret) throws AWSException
   {
      authenticator.login(accessKey, secret);
   }

   /**
    * Remove access and secret keys previously saved for current user. User will be not able to use this class any more
    * before next login.
    *
    * @throws AWSException
    */
   public final void logout() throws AWSException
   {
      authenticator.logout();
   }
}
