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
