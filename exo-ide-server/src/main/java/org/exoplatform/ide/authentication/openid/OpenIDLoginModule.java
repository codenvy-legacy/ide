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
package org.exoplatform.ide.authentication.openid;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.jaas.AbstractLoginModule;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

/**
 * Propagate openid user login to JAAS.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OpenIDLoginModule extends AbstractLoginModule
{
   private static final Log LOG = ExoLogger.getLogger(OpenIDLoginModule.class);

   @Override
   protected Log getLogger()
   {
      return LOG;
   }

   @Override
   public boolean login() throws LoginException
   {
      Callback[] callbacks = new Callback[2];
      callbacks[0] = new NameCallback("UserID");
      callbacks[1] = new PasswordCallback("OpenID", false);

      try
      {
         callbackHandler.handle(callbacks);

         final String userId = ((NameCallback)callbacks[0]).getName();
         final char[] password = ((PasswordCallback)callbacks[1]).getPassword();
         if (userId == null || password == null)
         {
            return false;
         }

         ((PasswordCallback)callbacks[1]).clearPassword();

         UserStore userStore = (UserStore)getContainer().getComponentInstanceOfType(UserStore.class);
         if (userStore == null)
         {
            return false;
         }

         Authenticator authenticator = (Authenticator)getContainer().getComponentInstanceOfType(Authenticator.class);
         if (authenticator == null)
         {
            throw new LoginException("No Authenticator component found, check your configuration");
         }

         OpenIDUser user = userStore.get(userId);
         if (user != null)
         {
            if (new String(password).equals(user.getIdentifier().getIdentifier()))
            {
               Identity identity = authenticator.createIdentity(userId);
               sharedState.put("javax.security.auth.login.name", userId);
               sharedState.put("exo.security.identity", identity);
               subject.getPublicCredentials().add(new UsernameCredential(userId));
               return true;
            }
         }

         return false;
      }
      catch (Exception e)
      {
         throw new LoginException(e.getMessage());
      }
   }

   @Override
   public boolean commit() throws LoginException
   {
      return true;
   }

   @Override
   public boolean abort() throws LoginException
   {
      return true;
   }

   @Override
   public boolean logout() throws LoginException
   {
      try
      {
         UserStore userStore = null;
         try
         {
            userStore = (UserStore)getContainer().getComponentInstanceOfType(UserStore.class);
         }
         catch (IllegalStateException e)
         {
            // May happen if stopping of ExoContainer in progress.
         }

         if (userStore != null)
         {
            for (UsernameCredential c : subject.getPublicCredentials(UsernameCredential.class))
            {
               subject.getPublicCredentials().remove(c);
               userStore.remove(c.getUsername());
            }
         }
         return true;
      }
      catch (Exception e)
      {
         throw new LoginException(e.getMessage());
      }
   }
}
