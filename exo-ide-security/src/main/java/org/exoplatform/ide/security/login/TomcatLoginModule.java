/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.jaas.RolePrincipal;
import org.exoplatform.services.security.jaas.UserPrincipal;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: TomcatLoginModule.java Mar 22, 2013 vetal $
 *
 */
public class TomcatLoginModule implements LoginModule
{

   /**
    * The name of the option to use in order to specify the name of the realm
    */
   private static final String OPTION_REALM_NAME = "realmName";

   private static final Logger LOG = LoggerFactory.getLogger(TomcatLoginModule.class);

   private static final String DEFAULT_REALM_NAME = new String("exo-domain");

   private Subject subject;

   private CallbackHandler callbackHandler;

   private Map<String, ?> sharedState;

   private Map<String, ?> options;

   private Object realmName;

   private Identity identity;

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean commit() throws LoginException
   {

      Set<Principal> principals = subject.getPrincipals();


      principals.add(new RolePrincipal("developer"));

      Set<String> roles = new HashSet<String>(1);
      roles.add("developer");
      identity.setRoles(roles);
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      IdentityRegistry identityRegistry = (IdentityRegistry)container.getComponentInstanceOfType(IdentityRegistry.class);
      identityRegistry.register(identity);
      principals.add(new UserPrincipal(identity.getUserId()));
      return true;
   }

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
      Map<String, ?> options)
   {
      this.subject = subject;
      this.callbackHandler = callbackHandler;
      this.sharedState = sharedState;
      this.options = options;
      this.realmName = getRealmName(options);

   }

   @SuppressWarnings("unchecked")
   private String getRealmName(Map options)
   {
      if (options != null)
      {
         String optionValue = (String)options.get(OPTION_REALM_NAME);
         if (optionValue != null && optionValue.length() > 0)
         {
            if (LOG.isDebugEnabled())
            {
               LOG.debug("The " + this.getClass() + " will use the realm " + optionValue);
            }
            return optionValue;
         }
      }
      return DEFAULT_REALM_NAME;
   }

   @Override
   public boolean login() throws LoginException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In login of DefaultLoginModule.");

      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Try create identity");
         Callback[] callbacks = new Callback[2];
         callbacks[0] = new NameCallback("Username");
         callbacks[1] = new PasswordCallback("Password", false);

         callbackHandler.handle(callbacks);
         String username = ((NameCallback)callbacks[0]).getName();
         String password = new String(((PasswordCallback)callbacks[1]).getPassword());
         ((PasswordCallback)callbacks[1]).clearPassword();
         if (username == null || password == null)
            return false;

         Credential[] credentials =
            new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};

    

         subject.getPrivateCredentials().add(password);
         subject.getPublicCredentials().add(new UsernameCredential(username));
         identity = new Identity(username);
         return true;

      }
      catch (final Exception e)
      {
         if (LOG.isDebugEnabled())
         {
            LOG.debug(e.getMessage());
         }

         throw new LoginException(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean abort() throws LoginException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In abort of DefaultLoginModule.");
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean logout() throws LoginException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In logout of DefaultLoginModule.");

      return true;
   }

}
