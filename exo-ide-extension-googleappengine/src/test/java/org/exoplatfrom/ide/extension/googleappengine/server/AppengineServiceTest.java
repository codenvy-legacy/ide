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
package org.exoplatfrom.ide.extension.googleappengine.server;

import org.apache.commons.io.IOUtils;
import org.everrest.core.RequestHandler;
import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.*;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class AppengineServiceTest {
    protected final String BASE_URI = "http://localhost/service";

    protected final String SERVICE_URI = BASE_URI + "/ide/appengine/";

    private VirtualFileSystem vfs;

    private Folder testFolder;

    protected ResourceLauncher launcher;

    protected StandaloneContainer container;

    @Before
    public void setUp() throws Exception {
        System.setProperty("org.exoplatform.mimetypes", "conf/mimetypes.properties");
        String conf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
        StandaloneContainer.setConfigurationURL(conf);
        container = StandaloneContainer.getInstance();

        String loginConf = getClass().getResource("/login.conf").toString();
        if (System.getProperty("java.security.auth.login.config") == null)
            System.setProperty("java.security.auth.login.config", loginConf);

        // REST
        RequestHandler requestHandler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
        ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, ProviderBinder.getInstance()));
        launcher = new ResourceLauncher(requestHandler);

        // RUNTIME VARIABLES
        ConversationState user = new ConversationState(new Identity("john"));
        ConversationState.setCurrent(user);
        VirtualFileSystemRegistry vfsRegistry =
                (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
        vfs = vfsRegistry.getProvider("db1").newInstance(null, null);

        testFolder = vfs.createFolder(vfs.getInfo().getRoot().getId(), "test");
    }

    @Test
    @Ignore
    public void testChangeAppengineWebXml() throws Exception {
        // create AppEngine Project
        URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("google-app-engine.zip");
        java.io.File f = new java.io.File(testZipResource.toURI());
        FileInputStream in = new FileInputStream(f);
        vfs.importZip(testFolder.getId(), in, false);
        // Update app_id
        String path = SERVICE_URI + "change-appid/db1/" + testFolder.getId() + "?app_id=test";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("GET", path, BASE_URI, h, null, null);
        // Check app_id is changed
        Assert.assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String content =
                IOUtils.toString(vfs.getContent("test/src/main/webapp/WEB-INF/appengine-web.xml", null).getStream());
        Assert.assertTrue(content.contains("<application>test</application>"));
    }

    @Test
    @Ignore
    public void testChangeAppEngineWebYaml() throws Exception {
        // create AppEngine Project
        URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("gae-python.zip");
        java.io.File f = new java.io.File(testZipResource.toURI());
        FileInputStream in = new FileInputStream(f);
        vfs.importZip(testFolder.getId(), in, true);
        // Update app_id
        String path = SERVICE_URI + "change-appid/db1/" + testFolder.getId() + "?app_id=test";
        Map<String, List<String>> h = new HashMap<String, List<String>>(1);
        h.put("Content-Type", Arrays.asList("application/json"));
        ContainerResponse response = launcher.service("GET", path, BASE_URI, h, null, null);
        // Check app_id is changed
        Assert.assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
        String content = IOUtils.toString(vfs.getContent("test/app.yaml", null).getStream());
        Assert.assertTrue(content.contains("application: test"));
    }

    @After
    public void tearDown() throws Exception {
        vfs.delete(testFolder.getId(), null);
    }
}
