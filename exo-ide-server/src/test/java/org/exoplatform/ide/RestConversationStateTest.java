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
package org.exoplatform.ide;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.SimpleSecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.services.security.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.codenvy.ide.commons.IdeUser;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RestConversationStateTest extends BaseTest {

    private SecurityContext securityContext;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Authenticator authr = (Authenticator)container.getComponentInstanceOfType(Authenticator.class);
        String validUser =
                authr.validateUser(new Credential[]{new UsernameCredential("root"), new PasswordCredential("exo")});
        Identity id = authr.createIdentity(validUser);
        Set<String> roles = new HashSet<String>();
        roles.add("users");
        roles.add("administrators");
        id.setRoles(roles);
        ConversationState s = new ConversationState(id);
        ConversationState.setCurrent(s);
    }

    @Test
    public void testWhoami() throws Exception {
        Set<String> userRoles = new HashSet<String>();
        userRoles.add("users");
        securityContext = new SimpleSecurityContext(new MockPrincipal("root"), userRoles, "BASIC", false);
        EnvironmentContext ctx = new EnvironmentContext();
        ctx.put(SecurityContext.class, securityContext);
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        ContainerResponse cres = launcher.service("POST", "/ide/conversation-state/whoami", "", headers, null, null, ctx);
        Assert.assertEquals(200, cres.getStatus());
        Assert.assertNotNull(cres.getEntity());
        Assert.assertTrue(cres.getEntity() instanceof IdeUser);
        IdeUser user = (IdeUser)cres.getEntity();
        Assert.assertEquals("root", user.getUserId());
        Assert.assertTrue(user.getRoles().contains("users"));
        Assert.assertTrue(user.getRoles().contains("administrators"));
        Assert.assertEquals(2, user.getRoles().size());
    }
}
