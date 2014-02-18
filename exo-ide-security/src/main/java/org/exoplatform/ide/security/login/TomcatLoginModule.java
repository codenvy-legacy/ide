/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.security.login;

import com.codenvy.organization.client.UserManager;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.*;
import org.exoplatform.services.security.jaas.RolePrincipal;
import org.exoplatform.services.security.jaas.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: TomcatLoginModule.java Mar 22, 2013 vetal $
 */
public class TomcatLoginModule implements LoginModule {

    /** The name of the option to use in order to specify the name of the realm */
    private static final String OPTION_REALM_NAME = "realmName";

    private static final Logger LOG = LoggerFactory.getLogger(TomcatLoginModule.class);

    private static final String DEFAULT_REALM_NAME = new String("exo-domain");

    private Subject subject;

    private CallbackHandler callbackHandler;

    private Map<String, ?> sharedState;

    private Map<String, ?> options;

    private Object realmName;

    private Identity identity;


    /** {@inheritDoc} */
    @Override
    public boolean commit() throws LoginException {

        Set<Principal> principals = subject.getPrincipals();


        principals.add(new RolePrincipal("workspace/developer"));

        Set<String> roles = new HashSet<String>(1);
        roles.add("workspace/developer");
        identity.setRoles(roles);
        ExoContainer container = ExoContainerContext.getCurrentContainer();
        IdentityRegistry identityRegistry = (IdentityRegistry)container.getComponentInstanceOfType(IdentityRegistry.class);
        identityRegistry.register(identity);
        principals.add(new UserPrincipal(identity.getUserId()));
        return true;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        this.realmName = getRealmName(options);

    }

    @SuppressWarnings("unchecked")
    private String getRealmName(Map options) {
        if (options != null) {
            String optionValue = (String)options.get(OPTION_REALM_NAME);
            if (optionValue != null && optionValue.length() > 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The " + this.getClass() + " will use the realm " + optionValue);
                }
                return optionValue;
            }
        }
        return DEFAULT_REALM_NAME;
    }

    @Override
    public boolean login() throws LoginException {
        if (LOG.isDebugEnabled())
            LOG.debug("In login of TomcatLoginModule.");

        try {
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


            ExoContainer container = ExoContainerContext.getCurrentContainer();
            UserManager userManager = (UserManager)container.getComponentInstanceOfType(UserManager.class);
            if (!userManager.authenticateUser(username, password))
                return false;


            Credential[] credentials =
                    new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};


            subject.getPrivateCredentials().add(password);
            subject.getPublicCredentials().add(new UsernameCredential(username));
            identity = new Identity(username);
            return true;

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new LoginException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    public boolean abort() throws LoginException {
        if (LOG.isDebugEnabled())
            LOG.debug("In abort of TomcatLoginModule.");
        return true;
    }

    /** {@inheritDoc} */
    public boolean logout() throws LoginException {
        if (LOG.isDebugEnabled())
            LOG.debug("In logout of TomcatLoginModule.");

        return true;
    }

}
