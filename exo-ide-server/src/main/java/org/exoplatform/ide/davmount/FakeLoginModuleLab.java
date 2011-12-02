/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.davmount;

import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.j2ee.TomcatLoginModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

public class FakeLoginModuleLab extends TomcatLoginModule
{
   /**
    * The list of users.
    */
   private List<String> users = new ArrayList<String>();
   private String _password;

   @Override
   public void afterInitialize()
   {
      super.afterInitialize();
      users.add((String)options.get("username"));
      users.add("exo");
      users.add("root");
      _password = (String)options.get("password");
      if (_password == null || _password.isEmpty())
         _password = "exo";
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean login() throws LoginException
   {
      try
      {
         Callback[] callbacks = new Callback[2];
         callbacks[0] = new NameCallback("Username");
         callbacks[1] = new PasswordCallback("Password", false);

         callbackHandler.handle(callbacks);
         String username = ((NameCallback)callbacks[0]).getName();
         String password = new String(((PasswordCallback)callbacks[1]).getPassword());
         ((PasswordCallback)callbacks[1]).clearPassword();
         if (users.contains(username))
         {
            Set<MembershipEntry> entries = new HashSet<MembershipEntry>(1);
            entries.add(new MembershipEntry(username));
            entries.add(new MembershipEntry("/ide/administrators"));
            Set<String> roles = new HashSet<String>(2);
            roles.add("administrators");
            roles.add("users");
            identity = new Identity(username, entries, roles);
            sharedState.put("javax.security.auth.login.name", username);
            subject.getPrivateCredentials().add(new PasswordCredential(password));
            subject.getPublicCredentials().add(new UsernameCredential(username));
         }
         else
         {
            throw new LoginException("Login failed for " + username + ". ");
         }
      }
      catch (UnsupportedCallbackException e)
      {
         e.printStackTrace();
         throw new LoginException(e.getMessage());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new LoginException(e.getMessage());
      }
      return true;
   }
}
