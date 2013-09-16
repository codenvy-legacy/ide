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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.DummyCredentialStore;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryLoginTest {
    private Auth                 authenticator;
    private Cloudfoundry         cloudfoundry;
    private DummyCredentialStore credentialStore;
    private final String userId = "andrew";

    @Before
    public void setUp() throws Exception {
        authenticator = new Auth();
        credentialStore = new DummyCredentialStore();
        cloudfoundry = new Cloudfoundry(authenticator, credentialStore);
        ConversationState.setCurrent(new ConversationState(new Identity(userId)));
    }

    @Test
    public void testLoginDefault() throws Exception {
        authenticator.setUsername(LoginInfo.email);
        authenticator.setPassword(LoginInfo.password);
        authenticator.setTarget(LoginInfo.target);
        // Login with username and password provided by authenticator.
        cloudfoundry.login();
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "cloudfoundry", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));
    }

    @Test
    public void testLogin() throws Exception {
        cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password, "cloudfoundry");
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "cloudfoundry", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));
    }

    @Test
    public void testLoginFail() throws Exception {
        try {
            cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password + "_wrong", "cloudfoundry");
            fail("CloudfoundryException expected");
        } catch (CloudfoundryException e) {
            assertEquals(200, e.getExitCode());
            assertEquals(403, e.getResponseStatus());
            assertEquals("Operation not permitted", e.getMessage());
            assertEquals("text/plain", e.getContentType());
        }
        Credential credential = new Credential();
        credentialStore.load(userId, "cloudfoundry", credential);
        assertNull(credential.getAttribute(LoginInfo.target));
    }

    @Test
    public void testLogout() throws Exception {
        cloudfoundry.login(LoginInfo.target, LoginInfo.email, LoginInfo.password, "cloudfoundry");
        Credential credential = new Credential();
        assertTrue(credentialStore.load(userId, "cloudfoundry", credential));
        assertNotNull(credential.getAttribute(LoginInfo.target));

        cloudfoundry.logout(LoginInfo.target, "cloudfoundry");
        credential = new Credential();
        credentialStore.load(userId, "cloudfoundry", credential);
        assertNull(credential.getAttribute(LoginInfo.target));
    }
}
