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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.DummyCredentialStore;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppfogLoginTest {
    private Auth                 authenticator;
    private Appfog               appfog;
    private DummyCredentialStore credentialStore;
    private final String userId = "andrew";

    @Before
    public void setUp() throws Exception {
        authenticator = new Auth();
        credentialStore = new DummyCredentialStore();
        appfog = new Appfog(authenticator, credentialStore);
        ConversationState.setCurrent(new ConversationState(new Identity(userId)));
    }

    @Ignore
    @Test
    public void testLoginDefault() throws Exception {
        authenticator.setUsername(LoginInfo.email);
        authenticator.setPassword(LoginInfo.password);
        authenticator.setTarget(LoginInfo.target);
        // Login with username and password provided by authenticator.
        appfog.login();
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "appfog", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));
    }

    @Ignore
    @Test
    public void testLogin() throws Exception {
        appfog.login(LoginInfo.target, LoginInfo.email, LoginInfo.password);
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "appfog", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));
    }

    @Ignore
    @Test
    public void testLoginFail() throws Exception {
        try {
            appfog.login(LoginInfo.target, LoginInfo.email, LoginInfo.password + "_wrong");
            fail("AppfogException expected");
        } catch (AppfogException e) {
            assertEquals(200, e.getExitCode());
            assertEquals(403, e.getResponseStatus());
            assertEquals("Operation not permitted", e.getMessage());
            assertEquals("text/plain", e.getContentType());
        }
        Credential credential = new Credential();
        assertFalse(credentialStore.load(userId, "appfog", credential));
        assertNull(credential.getAttribute(LoginInfo.target));
    }

    @Ignore
    @Test
    public void testLogout() throws Exception {
        appfog.login(LoginInfo.target, LoginInfo.email, LoginInfo.password);
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "appfog", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));

        appfog.logout(LoginInfo.target);
        credential = new Credential();
        credentialStore.load(userId, "appfog", credential);
        assertNull(credential.getAttribute(LoginInfo.target));
    }
}
