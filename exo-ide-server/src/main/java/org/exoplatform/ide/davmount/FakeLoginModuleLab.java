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
package org.exoplatform.ide.davmount;

import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.j2ee.TomcatLoginModule;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakeLoginModuleLab extends TomcatLoginModule {
    /** The list of users. */
    private List<String> users = new ArrayList<String>();

    private String _password;

    @Override
    public void afterInitialize() {
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
    public boolean login() throws LoginException {
        try {
            Callback[] callbacks = new Callback[2];
            callbacks[0] = new NameCallback("Username");
            callbacks[1] = new PasswordCallback("Password", false);

            callbackHandler.handle(callbacks);
            String username = ((NameCallback)callbacks[0]).getName();
            String password = new String(((PasswordCallback)callbacks[1]).getPassword());
            ((PasswordCallback)callbacks[1]).clearPassword();
            if (users.contains(username)) {
                Set<MembershipEntry> entries = new HashSet<MembershipEntry>(1);
                entries.add(new MembershipEntry(username));
                entries.add(new MembershipEntry("/ide/administrators"));
                Set<String> roles = new HashSet<String>(2);
                roles.add("workspace/developer");
                identity = new Identity(username, entries, roles);
                sharedState.put("javax.security.auth.login.name", username);
                subject.getPrivateCredentials().add(new PasswordCredential(password));
                subject.getPublicCredentials().add(new UsernameCredential(username));
            } else {
                throw new LoginException("Login failed for " + username + ". ");
            }
        } catch (UnsupportedCallbackException e) {
            throw new LoginException(e.getMessage());
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
        return true;
    }
}
