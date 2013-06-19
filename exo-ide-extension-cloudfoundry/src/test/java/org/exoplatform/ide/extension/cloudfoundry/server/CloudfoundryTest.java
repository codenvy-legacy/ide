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
package org.exoplatform.ide.extension.cloudfoundry.server;

import com.codenvy.commons.lang.ZipUtils;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.DummyCredentialStore;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.commons.lang.NameGenerator.generate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryTest {
    private static Auth                 authenticator;
    private static File                 javaWebApp;
    private static DummyCredentialStore credentialStore;
    private final String userId = "andrew";
    private final String paasProvider = "cloudfoundry";

    @BeforeClass
    public static void init() throws Exception {
        authenticator = new Auth();
        authenticator.setUsername(LoginInfo.email);
        authenticator.setPassword(LoginInfo.password);
        authenticator.setTarget(LoginInfo.target);
        credentialStore = new DummyCredentialStore();
        javaWebApp = createJavaWebApplication();
    }

    private Cloudfoundry    cloudfoundry;
    private SystemResources limits;

    @Before
    public void setUp() throws Exception {
        cloudfoundry = new Cloudfoundry(authenticator, credentialStore);
        ConversationState.setCurrent(new ConversationState(new Identity("andrew")));
        cloudfoundry.login();
        limits = cloudfoundry.systemInfo(LoginInfo.target, paasProvider).getLimits();
    }

    @Test
    public void testGetTarget() throws Exception {
        assertEquals(LoginInfo.target, cloudfoundry.getTarget());
    }

    @Test
    public void testGetTargets() throws Exception {
        assertEquals(1, cloudfoundry.getTargets(paasProvider).size());
        assertTrue(cloudfoundry.getTargets(paasProvider).contains(LoginInfo.target));
    }

    @Test
    public void testSystemInfoNoAuthenticated() throws Exception {
        // --- Emulate invalid security token.
        Auth _authenticator = new Auth();
        _authenticator.setTarget(LoginInfo.target);
        Credential ok = new Credential();
        credentialStore.load(userId, "cloudfoundry", ok);

        Credential invalid = new Credential();
        invalid.setAttribute(LoginInfo.target, ok.getAttribute(LoginInfo.target) + "_wrong");
        credentialStore.save(userId, "cloudfoundry", invalid);
        cloudfoundry = new Cloudfoundry(_authenticator, credentialStore);
        // ---
        try {
            cloudfoundry.systemInfo("", paasProvider); // read server(target) name from authenticator.
            fail("CloudfoundryException expected");
        } catch (CloudfoundryException e) {
            assertEquals(200, e.getExitCode());
            assertEquals(200, e.getResponseStatus()); // anyway 200 HTTP status expected
            assertEquals("Authentication required.\n", e.getMessage());
            assertEquals("text/plain", e.getContentType());
        }
    }

    @Test
    public void testSystemInfo() throws Exception {
        SystemInfo systemInfo = cloudfoundry.systemInfo("", paasProvider);
        assertEquals(LoginInfo.email, systemInfo.getUser());
        assertNotNull(systemInfo.getUsage());
        assertNotNull(systemInfo.getLimits());
        assertNotNull(systemInfo.getFrameworks());
        // Check for null values in frameworks info.
        assertFalse(systemInfo.getFrameworks().keySet().contains(null));
        assertFalse(systemInfo.getFrameworks().values().contains(null));
    }

    @Test
    public void testCreateApplication() throws Exception {
        final String name = generate("test-", 16);
        try {
            CloudFoundryApplication app = cloudfoundry.createApplication(
                    LoginInfo.target, // CF server
                    name,             // application name
                    null,             // framework. Should be determined automatically
                    null,             // url
                    1,                // instances
                    128,              // memory
                    false,            // no-start. Will be started after creation
                    null,             // runtime
                    null,             // command. Need for standalone applications only
                    null,             // debug
                    null,
                    // vfs. Do not provide VFS instance since we provide all required info and not need to read something from VFS
                    null,             // project
                    javaWebApp.toURI().toURL(),
                    null
                                                                        );

            assertEquals(name, app.getName());
            assertEquals(1, app.getUris().size());
            assertEquals(LoginInfo.target.replace("http://api", name), app.getUris().get(0));
            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            assertNull(app.getDebug());

            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Ignore
    @Test
    public void testCreateApplicationDebug() throws Exception {
        // IGNORED since debugging is not allowed for free accounts an cloudfoundry.com
        final String name = generate("test-", 16);
        try {
            CloudFoundryApplication app = cloudfoundry.createApplication(
                    LoginInfo.target, // CF server
                    name,             // application name
                    null,             // framework. Should be determined automatically
                    null,             // url
                    1,                // instances
                    128,              // memory
                    false,            // no-start. Will be started after creation
                    null,             // runtime
                    null,             // command. Need for standalone applications only
                    new DebugMode(),  // debug
                    null,
                    // vfs. Do not provide VFS instance since we provide all required info and not need to read something from VFS
                    null,             // project
                    javaWebApp.toURI().toURL(),
                    null
                                                                        );

            assertNotNull(app.getDebug());

            Instance[] instances = cloudfoundry.applicationInstances(LoginInfo.target, name, null, null);
            assertEquals(1, instances.length);
            assertFalse(instances[0].getDebugPort() == 0);
            assertNotNull(instances[0].getDebugHost());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testCreateApplicationNotEnoughMemory() throws Exception {
        final String name = generate("test-", 16);
        final int mem = 128;
        try {
            // use all available memory for this application
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, limits.getMemory(), false, null, null,
                                           null, null, null, javaWebApp.toURI().toURL(), null);

            try {
                cloudfoundry.createApplication(LoginInfo.target, generate("test-", 16), null, null, 1, mem, false, null,
                                               null, null, null, null, javaWebApp.toURI().toURL(), null);
                fail("IllegalStateException expected.");
            } catch (IllegalStateException e) {
                // OK. All available memory used for first application
                final String expected = String.format(
                        "Not enough resources to create new application. Available memory %dM but %dM required", 0, mem);
                assertTrue(e.getMessage().matches(expected + ".*"));
            }
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testCreateApplicationMaxApplicationNumber() throws Exception {
        final int maxApp = limits.getApps();
        final List<String> apps = new ArrayList<String>(maxApp);
        // should not reach max memory limit.
        final int mem = limits.getMemory() / (limits.getApps() + 1);
        try {
            for (int i = 0; i < maxApp; i++) {
                String name = generate("test-", 16);
                cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, mem, false, null, null, null, null, null,
                                               javaWebApp.toURI().toURL(), null);
                apps.add(name);
            }

            try {
                cloudfoundry.createApplication(LoginInfo.target, generate("test-", 16), null, null, 1, mem, false, null,
                                               null, null, null, null, javaWebApp.toURI().toURL(), null);
                fail("IllegalStateException expected.");
            } catch (IllegalStateException e) {
                // OK. Max number of application reached.
                final String expected = String.format(
                        "Not enough resources to create new application. Max number of applications \\(%d\\) reached", maxApp);
                assertTrue(e.getMessage().matches(expected + ".*"));
            }
        } finally {
            for (String name : apps) {
                cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
            }
        }
    }

    @Test
    public void testCreateApplicationInvalidName() throws Exception {
        final String name = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            try {
                cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null,
                                               null, null, null, null, javaWebApp.toURI().toURL(), null);
                fail("IllegalArgumentException expected.");
            } catch (IllegalArgumentException e) {
                // OK. Application with the same name already exists.
                final String expected = String.format("Application '%s' already exists", name);
                assertTrue(e.getMessage().matches(expected + ".*"));
            }
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testApplicationInfo() throws Exception {
        final String name = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(name, app.getName());
            assertEquals(1, app.getUris().size());
            assertEquals(LoginInfo.target.replace("http://api", name), app.getUris().get(0));
            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            assertNull(app.getDebug());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testStartApplication() throws Exception {
        final String name = generate("test-", 16);
        try {
            // create application but not start it
            CloudFoundryApplication app = cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128,
                                                                         true /* disable start */, null, null, null, null, null,
                                                                         javaWebApp.toURI().toURL(), null);
            assertEquals(1, app.getInstances());
            assertEquals(0, app.getRunningInstances());
            assertEquals("STOPPED", app.getState());
            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 404); // not started yet

            app = cloudfoundry.startApplication(LoginInfo.target, name, null, null, null, "cloudfoundry");

            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            assertNull(app.getDebug());
            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testStartApplicationNotEnoughMemory() throws Exception {
        final String name1 = generate("test-", 16);
        final String name2 = generate("test-", 16);
        try {
            // create application but not start it
            cloudfoundry.createApplication(LoginInfo.target, name1, null, null, 1, 128, true /* disable start */, null,
                                           null, null, null, null, javaWebApp.toURI().toURL(), null);

            // create and start application that use all available memory
            cloudfoundry.createApplication(LoginInfo.target, name2, null, null, 1, limits.getMemory(), false, null, null,
                                           null, null, null, javaWebApp.toURI().toURL(), null);

            try {
                // start should be failed because there is no memory for start application any more
                cloudfoundry.startApplication(LoginInfo.target, name1, null, null, null, "cloudfoundry");
                fail("CloudfoundryException expected. ");
            } catch (CloudfoundryException e) {
                assertEquals(600, e.getExitCode());
                assertTrue(e.getMessage().matches(
                        "Not enough resources to create new application. Not enough memory capacity.*"));
                assertEquals(403, e.getResponseStatus());
                assertEquals("text/plain", e.getContentType());
            }

        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name1, null, null, "cloudfoundry", false);
            cloudfoundry.deleteApplication(LoginInfo.target, name2, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testStopApplication() throws Exception {
        final String name = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null,
                                           null, null, null, null, javaWebApp.toURI().toURL(), null);

            cloudfoundry.stopApplication(LoginInfo.target, name, null, null, "cloudfoundry");
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);
            assertEquals(1, app.getInstances());
            assertEquals(0, app.getRunningInstances());
            assertEquals("STOPPED", app.getState());
            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 404); // stopped
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testRestartApplication_NotStarted() throws Exception {
        final String name = generate("test-", 16);
        try {
            // create application but not start it
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, true /* disable start */, null,
                                           null, null, null, null, javaWebApp.toURI().toURL(), null);

            // even to started application may be re-started
            cloudfoundry.restartApplication(LoginInfo.target, name, null, null, null, "cloudfoundry");
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);
            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testRestartApplication_Started() throws Exception {
        final String name = generate("test-", 16);
        try {
            // create application and start
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            // restart
            cloudfoundry.restartApplication(LoginInfo.target, name, null, null, null, "cloudfoundry");
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);
            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            checkApplicationURL(new URL("http://" + app.getUris().get(0)), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testUpdateApplication() throws Exception {
        final String new_content = "<html><head><title>Hello</title></head><body><h1>Hello Andrew</h1></body></html>";
        // update content of JSP and repack war file
        File updated = new File(javaWebApp.getParentFile(), "java_web_updated");
        ZipUtils.unzip(javaWebApp, updated);
        FileWriter w = new FileWriter(new File(updated, "index.jsp"));
        w.write(new_content);
        w.flush();
        w.close();
        File new_war = new File(updated.getParentFile(), "java_web_updated.war");
        ZipUtils.zipDir(updated.getAbsolutePath(), updated, new_war, null);

        final String name = generate("test-", 16);
        try {
            // create application and start
            CloudFoundryApplication app = cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false,
                                                                         null, null, null, null, null, javaWebApp.toURI().toURL(), null);

            // update
            cloudfoundry.updateApplication(LoginInfo.target, name, null, null, null);
            assertEquals(1, app.getInstances());
            assertEquals(1, app.getRunningInstances());
            assertEquals("STARTED", app.getState());
            checkApplicationOutput(new URL("http://" + app.getUris().get(0)), new_content);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testGetFiles() throws Exception {
        final String name = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            String raw = cloudfoundry.getFiles(LoginInfo.target, name, null, "0" /* for instance with index 0 */, null, null);
            List<String> list = new ArrayList<String>();
            for (String line : raw.split("\n")) {
                list.add(line.split("\\s+")[0]);
            }
            assertTrue(list.size() >= 2);
            assertTrue(list.contains("tomcat/"));
            assertTrue(list.contains("logs/"));
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testGetLogs() throws Exception {
        final String name = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            String logs = cloudfoundry.getLogs(LoginInfo.target, name, "0" /* for instance with index 0 */, null, null);
            assertNotNull(logs);
            assertFalse(logs.isEmpty());

            System.out.println(logs);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testMapUrl() throws Exception {
        final String name = generate("test-", 16);
        try {
            final String original = LoginInfo.target.replace("http://api", name);
            final String mapped = LoginInfo.target.replace("http://api", name + "_mapped");
            cloudfoundry.createApplication(LoginInfo.target, name, null, original, 1, 128, false, null, null, null, null,
                                           null, javaWebApp.toURI().toURL(), null);

            cloudfoundry.mapUrl(LoginInfo.target, name, null, null, null);
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(2, app.getUris().size());
            assertTrue(app.getUris().contains(original));
            assertTrue(app.getUris().contains(mapped));

            checkApplicationURL(new URL("http://" + original), 200);
            checkApplicationURL(new URL("http://" + mapped), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testUnmapUrl() throws Exception {
        final String name = generate("test-", 16);
        try {
            // Create application and map one URL for it.
            final String original = LoginInfo.target.replace("http://api", name);
            final String mapped = LoginInfo.target.replace("http://api", name + "_mapped");
            cloudfoundry.createApplication(LoginInfo.target, name, null, original, 1, 128, false, null, null, null, null,
                                           null, javaWebApp.toURI().toURL(), null);
            cloudfoundry.mapUrl(LoginInfo.target, name, null, null, null);
            // ---

            cloudfoundry.unmapUrl(LoginInfo.target, name, null, null, null); // remove original URL
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(1, app.getUris().size());
            assertTrue(app.getUris().contains(mapped));

            checkApplicationURL(new URL("http://" + original), 404);
            checkApplicationURL(new URL("http://" + mapped), 200);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testMem() throws Exception {
        final String name = generate("test-", 16);
        final int mem = 128;
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, mem, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            final int usedMem_Before = cloudfoundry.systemInfo(LoginInfo.target, paasProvider).getUsage().getMemory();
            cloudfoundry.mem(LoginInfo.target, name, null, null, mem * 2); // double memory size

            final int usedMem_After = cloudfoundry.systemInfo(LoginInfo.target, paasProvider).getUsage().getMemory();
            assertEquals(usedMem_Before + mem, usedMem_After);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testInstances1() throws Exception {
        final String name = generate("test-", 16);
        final int instances = 1;
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, instances, 128, false, null, null, null, null,
                                           null, javaWebApp.toURI().toURL(), null);

            cloudfoundry.instances(LoginInfo.target, name, null, null, null); // one more instance
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(2, app.getInstances());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testInstances2() throws Exception {
        final String name = generate("test-", 16);
        final int instances = 2;
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, instances, 128, false, null, null, null, null,
                                           null, javaWebApp.toURI().toURL(), null);

            cloudfoundry.instances(LoginInfo.target, name, null, null, null); // stop one instance
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(1, app.getInstances());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testInstances3() throws Exception {
        final String name = generate("test-", 16);
        final int instances = 1;
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, instances, 128, false, null, null, null, null,
                                           null, javaWebApp.toURI().toURL(), null);

            cloudfoundry.instances(LoginInfo.target, name, null, null, null);
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);

            assertEquals(2, app.getInstances());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testDeleteApplication() throws Exception {
        final String name = generate("test-", 16);
        cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                       javaWebApp.toURI().toURL(), null);

        cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        assertTrue(cloudfoundry.listApplications(LoginInfo.target, paasProvider).length == 0);

        checkApplicationURL(new URL(LoginInfo.target.replace("api", name)), 404);
    }

    @Test
    public void testApplicationStats() throws Exception {
        final String name = generate("test-", 16);
        final int mem = 128;
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, mem, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            Map<String, CloudfoundryApplicationStatistics> stats =
                    cloudfoundry.applicationStats(LoginInfo.target, name, null, null);
            assertEquals(1, stats.size()); // application has one instance
            CloudfoundryApplicationStatistics stat = stats.get("0");
            assertNotNull(stat);

            assertEquals(name, stat.getName());
            assertEquals("RUNNING", stat.getState());
            assertNotNull(stat.getHost());
            assertFalse(stat.getPort() == 0);
            final String[] uris = stat.getUris();
            assertFalse(uris.length == 0);
            assertEquals(LoginInfo.target.replace("http://api", name), uris[0]);
            final String uptime = stat.getUptime();
            assertNotNull(uptime);
            assertTrue(uptime.matches("\\d{1,2}d:\\d{1,2}h:\\d{1,2}m:\\d{1,2}s"));
            assertFalse(stat.getMem() == 0);
            assertEquals(mem, stat.getMemLimit());
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testListApplications() throws Exception {
        final String name1 = generate("test-", 16);
        final String name2 = generate("test-", 16);
        try {
            cloudfoundry.createApplication(LoginInfo.target, name1, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);
            cloudfoundry.createApplication(LoginInfo.target, name2, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);

            CloudFoundryApplication[] apps = cloudfoundry.listApplications(LoginInfo.target, paasProvider);
            assertEquals(2, apps.length);

            Set<String> names = new HashSet<String>(2);
            for (CloudFoundryApplication app : apps) {
                names.add(app.getName());
            }
            assertTrue(names.contains(name1));
            assertTrue(names.contains(name2));
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name1, null, null, "cloudfoundry", false);
            cloudfoundry.deleteApplication(LoginInfo.target, name2, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testEnvironmentAdd() throws Exception {
        final String name = generate("test-", 16);
        final String key = "test_key";
        final String value = "test_value";
        try {
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);
            cloudfoundry.environmentAdd(LoginInfo.target, name, null, null, null, key);

            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);
            List<String> env = app.getEnv();
            assertNotNull(env);
            assertFalse(env.isEmpty());
            assertEquals(env.get(0), key + '=' + value);
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testEnvironmentDelete() throws Exception {
        final String name = generate("test-", 16);
        try {
            // create application and add environment variable
            cloudfoundry.createApplication(LoginInfo.target, name, null, null, 1, 128, false, null, null, null, null, null,
                                           javaWebApp.toURI().toURL(), null);
            cloudfoundry.environmentAdd(LoginInfo.target, name, null, null, null, "test_key");
            // ---

            cloudfoundry.environmentDelete(LoginInfo.target, name, null, null, null);
            CloudFoundryApplication app = cloudfoundry.applicationInfo(LoginInfo.target, name, null, null);
            List<String> env = app.getEnv();
            assertTrue(env == null || env.isEmpty()); // null or empty list is OK
        } finally {
            cloudfoundry.deleteApplication(LoginInfo.target, name, null, null, "cloudfoundry", false);
        }
    }

    @Test
    public void testServices() throws Exception {
        final Set<String> expectedSystemServices = new HashSet<String>(
                Arrays.asList("redis", "mongodb", "mysql", "postgresql", "rabbitmq"));
        CloudfoundryServices services = cloudfoundry.services(LoginInfo.target, paasProvider);
        assertTrue(services.getSystem().length >= expectedSystemServices.size());
        for (SystemService sys : services.getSystem()) {
            expectedSystemServices.remove(sys.getVendor());
        }

        if (!expectedSystemServices.isEmpty()) {
            fail("Not found next system services in response: " + expectedSystemServices);
        }
    }

    @Ignore
    @Test
    public void testValidateAction() throws Exception {
    }

    @Ignore
    @Test
    public void testCreateService() throws Exception {
    }

    @Ignore
    @Test
    public void testDeleteService() throws Exception {
    }

    @Ignore
    @Test
    public void testBindService() throws Exception {
    }

    @Ignore
    @Test
    public void testUnbindService() throws Exception {
    }

    ///////////////////////////////////////////////////////////////////////////

    private final static String WEB_XML   =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<web-app version=\"2.5\" xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">\n" +
            "   <display-name>Simple Web Application</display-name>\n" +
            "</web-app>";
    private final static String INDEX_JSP = "<html><head><title>Hello</title></head><body><h1>Hello world</h1></body></html>";

    private static File createJavaWebApplication() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(".");
        final File targetDir = new File(URI.create(url.toString())).getParentFile();
        File application = createFolder(targetDir, "java_web");
        createFile(application, "index.jsp", INDEX_JSP);
        File web_inf = createFolder(application, "WEB-INF");
        createFile(web_inf, "web.xml", WEB_XML);
        File war = new File(targetDir, "java_web.war");
        ZipUtils.zipDir(application.getAbsolutePath(), application, war, null);
        return war;
    }

    private static File createFolder(File parent, String name) throws IOException {
        File dir = new File(parent, name);
        if (!dir.mkdirs()) {
            throw new IOException("Unable create " + dir.getAbsolutePath());
        }
        return dir;
    }

    private static File createFile(File parent, String name, String content) throws IOException {
        File file = new File(parent, name);
        FileWriter w = new FileWriter(file);
        try {
            w.write(content);
            w.flush();
        } finally {
            w.close();
        }
        return file;
    }

    private static boolean checkApplicationURL(URL url, int expectedStatus) throws IOException {
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        int responseCode = http.getResponseCode();
        http.disconnect();
        return responseCode == expectedStatus;
    }

    private static boolean checkApplicationOutput(URL url, String expectedBody) throws IOException {
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        InputStream in = http.getInputStream();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] b = new byte[128];
        int r;
        while ((r = in.read(b)) != -1) {
            bout.write(b, 0, r);
        }
        http.disconnect();
        return expectedBody.equals(bout.toString());
    }
}
