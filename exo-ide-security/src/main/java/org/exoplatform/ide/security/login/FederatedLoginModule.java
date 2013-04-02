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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FederatedLoginModule extends AbstractLoginModule {
    private static final Log LOG = ExoLogger.getLogger(FederatedLoginModule.class);

    @Override
    protected Log getLogger() {
        return LOG;
    }

    @Override
    public boolean login() throws LoginException {
        try {
            FederatedLoginList loginList = (FederatedLoginList)getContainer().getComponentInstanceOfType(FederatedLoginList.class);
            if (loginList == null) {
                // Do nothing if federated login (OpenID or OAuth) not configured.
                return false;
            }

            Callback[] callbacks = new Callback[2];
            callbacks[0] = new NameCallback("UserID");
            callbacks[1] = new PasswordCallback("Password", false);

            callbackHandler.handle(callbacks);

            final String userId = ((NameCallback)callbacks[0]).getName();
            final char[] password = ((PasswordCallback)callbacks[1]).getPassword();
            if (userId == null || password == null) {
                return false;
            }

            ((PasswordCallback)callbacks[1]).clearPassword();

            final String passwordStr = new String(password);
            if (loginList.contains(userId, passwordStr)) {
                loginList.remove(userId, passwordStr);

                Authenticator authenticator = (Authenticator)getContainer().getComponentInstanceOfType(Authenticator.class);
                if (authenticator == null) {
                    throw new LoginException("No Authenticator component found, check your configuration");
                }

                Identity identity = authenticator.createIdentity(userId);
                // NOTE : Since OrganizationAuthenticatorImpl authenticator do not check is user exists in user database
                // and in other hand we do not have user password we cannot trust user without roles.
                // Check user roles here and if user's roles is empty do not trust such user.
                // Any way if we create identity for the user it may not login without roles.
                if (identity.getRoles().isEmpty()) {
                    return false;
                }

                // Otherwise save Identity in shared state. Next LoginModule will use it.
                sharedState.put("javax.security.auth.login.name", userId);
                sharedState.put("exo.security.identity", identity);
                subject.getPublicCredentials().add(new UsernameCredential(userId));
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        return true;
    }
}
