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

import com.codenvy.commons.lang.IoUtil;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.SimpleSecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.security.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 20, 2011 evgen $
 */
public class ProjectTemplateTest extends BaseTest {
    private SecurityContext securityContext;

    private VirtualFileSystem vfs;

    protected final String BASE_URI = "http://localhost.com:8080";

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
        vfs = vfsRegistry.getProvider("dev-monit").newInstance(null, null);

        ItemList<Item> children =
                vfs.getChildren(vfs.getInfo().getRoot().getId(), -1, 0, null, false, PropertyFilter.ALL_FILTER);
        for (Item i : children.getItems()) {
            vfs.delete(i.getId(), null);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetProjectTemplate() throws Exception {
        Set<String> userRoles = new HashSet<String>();
        userRoles.add("users");
        securityContext = new SimpleSecurityContext(new MockPrincipal("root"), userRoles, "BASIC", false);
        EnvironmentContext ctx = new EnvironmentContext();
        ctx.put(SecurityContext.class, securityContext);
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        ContainerResponse cres =
                launcher.service("GET", "/ide/templates/project/list", BASE_URI, headers, null, null, ctx);
        Assert.assertEquals(200, cres.getStatus());
        Assert.assertNotNull(cres.getEntity());
        List<ProjectTemplate> templates = (List<ProjectTemplate>)cres.getEntity();
        Assert.assertEquals(1, templates.size());
        ProjectTemplate proj = templates.get(0);
        Assert.assertEquals("SpringDemoProject", proj.getName());
        Assert.assertEquals("spring", proj.getType());
        Assert.assertEquals("Demo Spring Project", proj.getDescription());
    }

    @Test
    public void testCreateProjectFromTempalte() throws Exception {
        String prj = UUID.randomUUID().toString();
        Set<String> userRoles = new HashSet<String>();
        userRoles.add("users");
        securityContext = new SimpleSecurityContext(new MockPrincipal("root"), userRoles, "BASIC", false);
        EnvironmentContext ctx = new EnvironmentContext();
        ctx.put(SecurityContext.class, securityContext);
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        String rootId = vfs.getInfo().getRoot().getId();
        ContainerResponse cres =
                launcher.service("POST", BASE_URI + "/ide/templates/project/create?vfsid=dev-monit&name=" + prj + "&parentId="
                                         + rootId + "&templateName=SpringDemoProject", BASE_URI, headers, null, null, ctx);
        Assert.assertEquals(200, cres.getStatus());
        Item item = (Item)cres.getEntity();
        Assert.assertTrue(item instanceof Project);
        Project p = (Project)item;
        Assert.assertEquals("spring", p.getProjectType());
        String pom = IoUtil.readStream(vfs.getContent(p.getPath() + "/pom.xml", null).getStream());
        Assert.assertTrue(pom.contains("<artifactId>" + prj + "</artifactId>"));
        Assert.assertTrue(pom.contains("<groupId>com.localhost</groupId>"));
    }


}
