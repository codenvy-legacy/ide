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
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.security.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2011 evgen $
 */
public class FileTemplateTest extends BaseTest {

    private SecurityContext securityContext;

    private VirtualFileSystem vfs;

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

        VirtualFileSystemRegistry vfsRegistry =
                (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
        vfs = vfsRegistry.getProvider("ws2").newInstance(null, null);
    }

    @Test
    public void testAddFileTemplate() throws Exception {
        Set<String> userRoles = new HashSet<String>();
        userRoles.add("users");
        securityContext = new SimpleSecurityContext(new MockPrincipal("root"), userRoles, "BASIC", false);
        EnvironmentContext ctx = new EnvironmentContext();
        ctx.put(SecurityContext.class, securityContext);
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.add("Content-type", MediaType.APPLICATION_JSON);
        URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("template.js");
        java.io.File f = new java.io.File(testZipResource.toURI());
        FileInputStream in = new FileInputStream(f);
        byte[] b = new byte[(int)f.length()];
        in.read(b);
        in.close();
        ContainerResponse cres = launcher.service("PUT", "/ide/templates/file/add", "", headers, b, null, ctx);
        Assert.assertEquals(204, cres.getStatus());
        Assert
                .assertTrue(vfs.getItemByPath("/ide-home/templates/fileTemplates", null, false, PropertyFilter.NONE_FILTER) instanceof File);
    }

    @After
    public void after() throws InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException {
        ItemList<Item> children =
                vfs.getChildren(vfs.getInfo().getRoot().getId(), -1, 0, null, false, PropertyFilter.NONE_FILTER);
        for (Item i : children.getItems()) {
            if (i.getName().equals("ide-home")) {
                try {
                    vfs.delete(i.getId(), null);
                } catch (ItemNotFoundException e) {
                    // Nothing to do
                }
                break;
            }
        }
    }

}
