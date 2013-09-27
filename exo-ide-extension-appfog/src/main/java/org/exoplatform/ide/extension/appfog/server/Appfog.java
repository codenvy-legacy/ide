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

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.extension.appfog.server.json.ApplicationFile;
import org.exoplatform.ide.extension.appfog.server.json.Crashes;
import org.exoplatform.ide.extension.appfog.server.json.CreateAppfogApplication;
import org.exoplatform.ide.extension.appfog.server.json.CreateResponse;
import org.exoplatform.ide.extension.appfog.server.json.InstanceInfo;
import org.exoplatform.ide.extension.appfog.server.json.InstancesInfo;
import org.exoplatform.ide.extension.appfog.server.json.RuntimeInfo;
import org.exoplatform.ide.extension.appfog.server.json.Stats;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplicationStatistics;
import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;
import org.exoplatform.ide.extension.appfog.shared.Framework;
import org.exoplatform.ide.extension.appfog.shared.InfraDetail;
import org.exoplatform.ide.extension.appfog.shared.InfraType;
import org.exoplatform.ide.extension.appfog.shared.Instance;
import org.exoplatform.ide.extension.appfog.shared.SystemInfo;
import org.exoplatform.ide.extension.appfog.shared.SystemResources;
import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codenvy.commons.lang.IoUtil.GIT_FILTER;
import static com.codenvy.commons.lang.IoUtil.copy;
import static com.codenvy.commons.lang.IoUtil.createTempDirectory;
import static com.codenvy.commons.lang.IoUtil.downloadFile;
import static com.codenvy.commons.lang.IoUtil.list;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.NameGenerator.generate;
import static com.codenvy.commons.lang.ZipUtils.unzip;
import static com.codenvy.commons.lang.ZipUtils.zipDir;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class Appfog {
    static final class AppfogCredential {
        final String target;
        final String token;

        AppfogCredential(String target, String token) {
            this.target = target;
            this.token = token;
        }
    }

    public static final Map<String, Framework> FRAMEWORKS;

    private static final int DEFAULT_MEMORY_SIZE = 256;

    static {
        Map<String, Framework> fm = new HashMap<String, Framework>(12);
        fm.put("rails3", new FrameworkImpl("rails3", "Rails", null, 256, "Rails  Application"));
        fm.put("spring", new FrameworkImpl("spring", "Spring", null, 768, "Java SpringSource Spring Application"));
        fm.put("grails", new FrameworkImpl("grails", "Grails", null, 512, "Java SpringSource Grails Application"));
        fm.put("lift", new FrameworkImpl("lift", "Lift", null, 512, "Scala Lift Application"));
        fm.put("java_web", new FrameworkImpl("java_web", "JavaWeb", null, 768, "Java Web Application"));
        fm.put("sinatra", new FrameworkImpl("sinatra", "Sinatra", null, 128, "Sinatra Application"));
        fm.put("node", new FrameworkImpl("node", "Node", null, 64, "Node.js Application"));
        fm.put("php", new FrameworkImpl("php", "PHP", null, 128, "PHP Application"));
        fm.put("otp_rebar", new FrameworkImpl("otp_rebar", "Erlang/OTP Rebar", null, 64, "Erlang/OTP Rebar Application"));
        fm.put("wsgi", new FrameworkImpl("wsgi", "WSGI", null, 64, "Python WSGI Application"));
        fm.put("django", new FrameworkImpl("django", "Django", null, 128, "Python Django Application"));
        fm.put("standalone", new FrameworkImpl("standalone", "Standalone", null, 256, "Standalone Application"));
        FRAMEWORKS = Collections.unmodifiableMap(fm);
    }

    private static final Log LOG = ExoLogger.getLogger(Appfog.class);

    private final AppfogAuthenticator authenticator;
    private final CredentialStore     credentialStore;

    public Appfog(AppfogAuthenticator authenticator, CredentialStore credentialStore) {
        this.authenticator = authenticator;
        this.credentialStore = credentialStore;
        // Create a trust manager that does not validate certificate chains
        TrustManager trustAllManager = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{trustAllManager}, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ignored) {
        }
    }

    public void setTarget(String server) throws CredentialStoreException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "appfog", credential);
        credential.setAttribute("current_target", server);
        credentialStore.save(userId, "appfog", credential);
    }

    public String getTarget() throws CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(getUserId(), "appfog", credential);
        return credential.getAttribute("current_target");
    }

    public Collection<String> getTargets() throws CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(getUserId(), "appfog", credential);
        List<String> targets = new ArrayList<String>(2);
        for (Map.Entry<String, String> entry : credential.getAttributes().entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("http://") || key.startsWith("https://")) {
                targets.add(key);
            }
        }
        return targets;
    }

    public void login(String server, String email, String password)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "appfog", credential);
        if (server == null) {
            server = credential.getAttribute("current_target");
        }
        authenticator.login(server, email, password, credential);
        credentialStore.save(userId, "appfog", credential);
    }

    public void login() throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "appfog", credential);
        authenticator.login(credential);
        credentialStore.save(userId, "appfog", credential);
    }

    public void logout(String server) throws CredentialStoreException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "appfog", credential);
        credential.removeAttribute(server);
        credentialStore.save(userId, "appfog", credential);
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    public SystemInfo systemInfo(String server)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        return systemInfo(getCredential(server));
    }

    private SystemInfo systemInfo(AppfogCredential credential)
            throws AppfogException, ParsingResponseException, IOException {
        SystemInfoImpl systemInfo = parseJsonResponse(
                getJson(credential.target + "/info", credential.token, 200), SystemInfoImpl.class, null);

        if (systemInfo.getUser() == null) {
            throw new AppfogException(200, 200, "Authentication required.\n", "text/plain");
        }

        for (Framework framework : systemInfo.getFrameworks().values()) {
            // If known framework - try to add some additional info.
            Framework cfg = FRAMEWORKS.get(framework.getName());
            if (cfg != null) {
                framework.setDisplayName(cfg.getDisplayName());
                framework.setDescription(cfg.getDescription());
                framework.setMemory(cfg.getMemory());
            } else {
                framework.setMemory(DEFAULT_MEMORY_SIZE);
            }
        }
        return systemInfo;
    }

    public AppfogApplication applicationInfo(String server, String app, VirtualFileSystem vfs, String projectId)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return applicationInfo(
                getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
    }

    private AppfogApplication applicationInfo(AppfogCredential credential, String app)
            throws AppfogException, ParsingResponseException, IOException {
        return parseJsonResponse(
                getJson(credential.target + "/apps/" + app, credential.token, 200),
                AppfogApplication.class,
                null
                                );
    }

    public AppfogApplication createApplication(String server,
                                               String app,
                                               String framework,
                                               String url,
                                               int instances,
                                               int memory,
                                               boolean noStart,
                                               String runtime,
                                               String command,
                                               DebugMode debugMode,
                                               VirtualFileSystem vfs,
                                               String projectId,
                                               URL war,
                                               InfraType infraType)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (app == null || app.isEmpty()) {
            throw new IllegalArgumentException("Application name required. ");
        }
        if ((vfs == null || projectId == null) && war == null) {
            throw new IllegalArgumentException("Project directory or location of WAR file required. ");
        }
        if (server == null || server.isEmpty()) {
            throw new IllegalArgumentException("Location of Appfog server required. ");
        }
        AppfogCredential credential = getCredential(server);

        return createApplication(credential, app, framework, url, instances, memory, noStart, runtime, command,
                                 debugMode, vfs, projectId, war, infraType);
    }

    public static final Pattern suggestUrlPattern = Pattern.compile("(http(s)?://)?([^\\.]+)(.*)");

    private AppfogApplication createApplication(AppfogCredential credential,
                                                String app,
                                                String frameworkName,
                                                String appUrl,
                                                int instances,
                                                int memory,
                                                boolean noStart,
                                                String runtime,
                                                String command,
                                                DebugMode debugMode,
                                                VirtualFileSystem vfs,
                                                String projectId,
                                                URL url,
                                                InfraType infraType)
            throws AppfogException, ParsingResponseException, VirtualFileSystemException, IOException {
        SystemInfo systemInfo = systemInfo(credential);
        SystemResources limits = systemInfo.getLimits();
        SystemResources usage = systemInfo.getUsage();

        checkApplicationNumberLimit(limits, usage);
        checkApplicationName(credential, app);

        AppfogApplication appInfo;
        java.io.File path = null;
        boolean cleanup = false;
        try {
            if (url != null) {
                URI uri = URI.create(url.toString());
                if ("file".equals(uri.getScheme())) {
                    path = new java.io.File(uri);
                } else {
                    path = downloadFile(null, "af_" + app, ".war", url);
                    cleanup = true; // remove only downloaded file.
                }
            }

            if (frameworkName == null) {
                if (path != null) {
                    frameworkName = Utils.detectFramework(path);
                } else {
                    frameworkName = Utils.detectFramework(vfs, projectId);
                }
            }

            Framework framework;
            if (frameworkName == null) {
                throw new RuntimeException("Can't detect application type. ");
            } else if ("standalone".equals(frameworkName)) {
                // Need to some more info for standalone applications.
                if (command == null || command.isEmpty()) {
                    throw new IllegalArgumentException("Command required for standalone application. ");
                }
                Map runtimes = getRuntimes(credential);
                if (runtimes.get(runtime) == null) {
                    throw new IllegalArgumentException(
                            "Unsupported runtime '" + runtime + "'. List of supported runtimes: " + runtimes.keySet());
                }
                framework = FRAMEWORKS.get("standalone");
            } else {
                framework = getFramework(systemInfo, frameworkName);
            }

            //Need this because appfog has a problem with deploying rails apps with version 1.8
            if ("rails3".equals(frameworkName)) {
                runtime = "ruby192";
            }

            if (instances <= 0) {
                instances = 1;
            }
            if (memory <= 0) {
                memory = framework.getMemory();
            }
            // Check memory capacity.
            if (!noStart) {
                checkAvailableMemory(instances, memory, limits, usage);
            }
            if (appUrl == null || appUrl.isEmpty()) {
                Matcher m = suggestUrlPattern.matcher(credential.target);
                m.matches();
                appUrl = app + m.group(4);
            }

            CreateAppfogApplication payload = new CreateAppfogApplication(
                    app,
                    instances,
                    appUrl,
                    memory,
                    framework.getName(),
                    runtime,
                    command,
                    infraType.getInfra()
            );

            String json = postJson(credential.target + "/apps", credential.token, JsonHelper.toJson(payload), 302);
            CreateResponse resp = parseJsonResponse(json, CreateResponse.class, null);
            appInfo = parseJsonResponse(doRequest(resp.getRedirect(), "GET", credential.token, null, null, 200),
                                        AppfogApplication.class, null);

            uploadApplication(credential, app, vfs, projectId, path);

            if (vfs != null && projectId != null) {
                writeApplicationName(vfs, projectId, app);
                writeServerName(vfs, projectId, credential.target);
                writeInfraName(vfs, projectId, infraType.getInfra().getName());
            }

            if (!noStart) {
                appInfo = startApplication(credential, app, debugMode != null ? debugMode.getMode() : null, false);
            }
        } finally {
            if (path != null && cleanup) {
                deleteRecursive(path);
            }
        }
        return appInfo;
    }

    public AppfogApplication startApplication(String server,
                                              String app,
                                              DebugMode debugMode,
                                              VirtualFileSystem vfs,
                                              String projectId)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return startApplication(
                getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
                debugMode != null ? debugMode.getMode() : null,
                true
                               );
    }

    private AppfogApplication startApplication(AppfogCredential credential,
                                               String app,
                                               String debug,
                                               boolean failIfStarted)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        String name = appInfo.getName();
        if (debug != null) {
            String runtime = appInfo.getStaging().getStack();
            RuntimeInfo runtimeInfo = getRuntimeInfo(runtime, credential);
            Set<String> debugModes = runtimeInfo != null ? runtimeInfo.getDebug_modes() : Collections.<String>emptySet();
            if (!debugModes.contains(debug)) {
                StringBuilder msg = new StringBuilder();
                msg.append("Unsupported debug mode '");
                msg.append(debug);
                msg.append("' for application ");
                msg.append(name);
                if (debugModes.isEmpty()) {
                    msg.append(". Debug is not supported. ");
                } else {
                    msg.append(". Available modes: ");
                    msg.append(debugModes);
                }
                throw new IllegalArgumentException(msg.toString());
            }
        }
        if (!"STARTED".equals(appInfo.getState())) {
            appInfo.setState("STARTED"); // Update application state.
            appInfo.setDebug(debug);
            putJson(credential.target + "/apps/" + name, credential.token, JsonHelper.toJson(appInfo), 200);
            // Check is application started.
            final int attempts = 30;
            final int sleepTime = 2000;
            // 1 minute for start application.
            boolean started = false;
            for (int i = 0; i < attempts && !started; i++) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
                appInfo = applicationInfo(credential, name);
                started = appInfo.getInstances() == appInfo.getRunningInstances();
            }
            if (!started) {
                Crashes.Crash[] crashes = applicationCrashes(credential, name).getCrashes();
                if (crashes != null && crashes.length > 0) {
                    throw new AppfogException(400, "Application '" + name + "' failed to start. ", "text/plain");
                }
            }
        } else if (failIfStarted) {
            throw new AppfogException(400, "Application '" + name + "' already started. ", "text/plain");
        }
        // Send info about application to client to make possible check is application started or not.
        return appInfo;
    }

    public void stopApplication(String server,
                                String app,
                                VirtualFileSystem vfs,
                                String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        stopApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                        app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
    }

    private void stopApplication(AppfogCredential credential, String app, boolean failIfStopped)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        if (!"STOPPED".equals(appInfo.getState())) {
            appInfo.setState("STOPPED"); // Update application state.
            putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
        } else if (failIfStopped) {
            throw new AppfogException(400, "Application '" + app + "' already stopped. ", "text/plain");
        }
    }

    public AppfogApplication restartApplication(String server,
                                                String app,
                                                DebugMode debugMode,
                                                VirtualFileSystem vfs,
                                                String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        return restartApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                                  app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
                                  debugMode == null ? null : debugMode.getMode());
    }

    private AppfogApplication restartApplication(AppfogCredential credential, String app, String debug)
            throws AppfogException, ParsingResponseException, IOException {
        stopApplication(credential, app, false);
        return startApplication(credential, app, debug, false);
    }

    public void updateApplication(String server,
                                  String app,
                                  VirtualFileSystem vfs,
                                  String projectId,
                                  URL war)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if ((vfs == null || projectId == null) && war == null) {
            throw new IllegalArgumentException("Project directory or location to WAR file required. ");
        }
        updateApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                          app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
                          vfs, projectId, war);
    }

    private void updateApplication(AppfogCredential credential, String app, VirtualFileSystem vfs, String projectId, URL url)
            throws ParsingResponseException, AppfogException, VirtualFileSystemException, IOException {
        AppfogApplication appInfo = applicationInfo(credential, app);

        java.io.File path = null;
        boolean cleanup = false;
        try {
            if (url != null) {
                URI uri = URI.create(url.toString());
                if ("file".equals(uri.getScheme())) {
                    path = new java.io.File(uri);
                } else {
                    path = downloadFile(null, "af_" + app, ".war", url);
                    cleanup = true;
                }
                uploadApplication(credential, app, vfs, projectId, path);
            } else {
                uploadApplication(credential, app, vfs, projectId, null);
            }
        } finally {
            if (path != null && cleanup) {
                deleteRecursive(path);
            }
        }

        if ("STARTED".equals(appInfo.getState())) {
            restartApplication(credential, app, appInfo.getMeta().getDebug());
        }
    }

    public String getFiles(String server,
                           String app,
                           String path,
                           String instance,
                           VirtualFileSystem vfs,
                           String projectId)
            throws AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        AppfogCredential credential = getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
        if (app == null || app.isEmpty()) {
            app = detectApplicationName(vfs, projectId, true);
        }
        return getFiles(credential, app, path == null || path.isEmpty() ? "/" : path,
                        instance == null || instance.isEmpty() ? "0" : instance);
    }

    private String getFiles(AppfogCredential credential, String app, String path, String instance)
            throws AppfogException, IOException {
        return doRequest(
                credential.target + "/apps/" + app + "/instances/" + instance + "/files/" + URLEncoder.encode(path, "UTF-8"),
                "GET", credential.token,
                null,
                null,
                200
                        );
    }

    public String getLogs(String server,
                          String app,
                          String instance,
                          VirtualFileSystem vfs,
                          String projectId)
            throws AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        AppfogCredential credential = getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server);
        return getLogs(credential, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app,
                       instance == null || instance.isEmpty() ? "0" : instance);
    }

    private String getLogs(AppfogCredential credential, String app, String instance) throws AppfogException, IOException {
        String[] lines = getFiles(credential, app, "/logs", instance).split("\n");
        StringBuilder logs = new StringBuilder();
        for (String line : lines) {
            String path = "/logs/" + line.split("\\s+")[0];
            String content = "";

            try {
                content = getFiles(credential, app, path, instance);
            } catch (AppfogException e) {
                if (204 != e.getResponseStatus()) {
                    throw e;
                }
            }

            if (!(content == null || content.isEmpty())) {
                logs.append("====> ");
                logs.append(path);
                logs.append(" <====");
                logs.append('\n');
                logs.append('\n');
                logs.append(content);
            }
        }
        return logs.toString();
    }

    public void mapUrl(String server,
                       String app,
                       VirtualFileSystem vfs,
                       String projectId,
                       String url)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("URL for mapping required. ");
        }
        mapUrl(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
               app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, url);
    }

    private void mapUrl(AppfogCredential credential, String app, String url) throws IOException, ParsingResponseException,
                                                                                    AppfogException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        // Cloud foundry server send URL without schema.
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }

        boolean updated = false;
        List<String> uris = appInfo.getUris();
        if (uris == null) {
            uris = new ArrayList<String>(1);
            appInfo.setUris(uris);
            updated = uris.add(url);
        } else if (!uris.contains(url)) {
            updated = uris.add(url);
        }
        // If have something to update then do that.
        if (updated) {
            putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
        }
    }

    public void unmapUrl(String server,
                         String app,
                         VirtualFileSystem vfs,
                         String projectId,
                         String url)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("URL for unmapping required. ");
        }
        unmapUrl(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                 app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, url);
    }

    private void unmapUrl(AppfogCredential credential, String app, String url) throws IOException, ParsingResponseException,
                                                                                      AppfogException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        List<String> uris = appInfo.getUris();
        if (uris != null && uris.size() > 0 && uris.remove(url)) {
            putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
        }
    }

    public void mem(String server,
                    String app,
                    VirtualFileSystem vfs,
                    String projectId,
                    int memory)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (memory < 0) {
            throw new IllegalArgumentException("Memory reservation for application may not be negative. ");
        }
        mem(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
            app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, memory, true);
    }

    private void mem(AppfogCredential credential, String app, int memory, boolean restart)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        int currentMem = appInfo.getResources().getMemory();
        if (memory != currentMem) {
            SystemInfo systemInfo = systemInfo(credential);
            SystemResources limits = systemInfo.getLimits();
            SystemResources usage = systemInfo.getUsage();
            if (limits != null && usage != null //
                && (appInfo.getInstances() * (memory - currentMem)) > (limits.getMemory() - usage.getMemory())) {
                throw new IllegalStateException("Not enough resources. Available memory " //
                                                + ((limits.getMemory() - usage.getMemory()) + currentMem) + 'M'
                                                + " but " + (appInfo.getInstances() * memory) + "M required. ");
            }
            appInfo.getResources().setMemory(memory);
            putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(credential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public Instance[] applicationInstances(String server,
                                           String app,
                                           VirtualFileSystem vfs,
                                           String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        return applicationInstances(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                                    app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
    }

    private Instance[] applicationInstances(AppfogCredential credential, String app)
            throws ParsingResponseException, AppfogException, IOException {
        InstanceInfo[] instancesInfo =
                parseJsonResponse(getJson(credential.target + "/apps/" + app + "/instances", credential.token, 200),
                                  InstancesInfo.class, null).getInstances();
        if (instancesInfo != null && instancesInfo.length > 0) {
            Instance[] instances = new Instance[instancesInfo.length];
            for (int i = 0; i < instancesInfo.length; i++) {
                InstanceInfo info = instancesInfo[i];
                instances[i] = new InstanceImpl(info.getDebug_ip(), info.getDebug_port(), info.getConsole_ip(), info.getConsole_port());
            }
            return instances;
        }
        return new Instance[0];
    }

    public void instances(String server,
                          String app,
                          VirtualFileSystem vfs,
                          String projectId,
                          String expression)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        instances(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                  app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, expression, false);
    }

    /** Instance update expression pattern. */
    private static final Pattern instanceUpdateExpr = Pattern.compile("([+-])?(\\d+)");

    private void instances(AppfogCredential credential, String app, String expression, boolean restart) throws IOException,
                                                                                                               ParsingResponseException,
                                                                                                               AppfogException {
        Matcher m = instanceUpdateExpr.matcher(expression);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid number of instances " + expression + ". ");
        }
        String sign = m.group(1);
        String val = m.group(2);

        AppfogApplication appInfo = applicationInfo(credential, app);
        int currentInst = appInfo.getInstances();
        int newInst = sign == null //
                      ? Integer.parseInt(expression) //
                      : sign.equals("-") //
                        ? currentInst - Integer.parseInt(val) //
                        : currentInst + Integer.parseInt(val);
        if (newInst < 1) {
            throw new IllegalArgumentException("Invalid number of instances " + newInst //
                                               + ". Must be at least one instance. ");
        }
        if (currentInst != newInst) {
            appInfo.setInstances(newInst);
            putJson(credential.target + "/apps/" + app, credential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(credential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public void deleteApplication(String server,
                                  String app,
                                  VirtualFileSystem vfs,
                                  String projectId,
                                  boolean deleteServices)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        deleteApplication(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                          app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, deleteServices, vfs, projectId);
    }

    private void deleteApplication(AppfogCredential credential,
                                   String app,
                                   boolean deleteServices,
                                   VirtualFileSystem vfs,
                                   String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        AppfogApplication appInfo = applicationInfo(credential, app);
        deleteJson(credential.target + "/apps/" + app, credential.token, 200);
        if (vfs != null && projectId != null) {
            writeApplicationName(vfs, projectId, null);
            writeServerName(vfs, projectId, null);
            writeInfraName(vfs, projectId, null);
        }
        if (deleteServices) {
            List<String> services = appInfo.getServices();
            if (services != null && services.size() > 0) {
                for (String service : services) {
                    deleteService(credential.target, service);
                }
            }
        }
    }

    public Map<String, AppfogApplicationStatistics> applicationStats(String server,
                                                                     String app,
                                                                     VirtualFileSystem vfs,
                                                                     String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        return applicationStats(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                                app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app);
    }

    @SuppressWarnings({"serial", "rawtypes", "unchecked"})
    private Map<String, AppfogApplicationStatistics> applicationStats(AppfogCredential credential, String app)
            throws AppfogException, ParsingResponseException, IOException {
        Map cloudStats =
                parseJsonResponse(getJson(credential.target + "/apps/" + app + "/stats", credential.token, 200), Map.class,
                                  new HashMap<String, Stats>(0) {
                                  }.getClass().getGenericSuperclass());

        if (cloudStats != null && cloudStats.size() > 0) {
            Map<String, AppfogApplicationStatistics> stats =
                    new HashMap<String, AppfogApplicationStatistics>(cloudStats.size());
            for (Map.Entry next : (Iterable<Map.Entry>)cloudStats.entrySet()) {
                Stats s = (Stats)next.getValue();

                AppfogApplicationStatistics appStats = new AppfogApplicationStatisticsImpl();
                appStats.setState(s.getState());
                if (s.getStats() != null) {
                    appStats.setName(s.getStats().getName());
                    appStats.setHost(s.getStats().getHost());
                    appStats.setPort(s.getStats().getPort());
                    appStats.setUris(s.getStats().getUris());
                    appStats.setMemLimit(Math.round(s.getStats().getMem_quota() / (1024 * 1024)));
                    appStats.setDiskLimit(Math.round(s.getStats().getDisk_quota() / (1024 * 1024)));
                    appStats.setUptime(toUptimeString(s.getStats().getUptime()));
                    appStats.setCpuCores(s.getStats().getCores());
                    if (s.getStats().getUsage() != null) {
                        appStats.setCpu(s.getStats().getUsage().getCpu());
                        appStats.setMem(Math.round(s.getStats().getUsage().getMem() / 1024));
                        appStats.setDisk(Math.round(s.getStats().getUsage().getDisk() / (1024 * 1024)));
                    }
                }
                stats.put((String)next.getKey(), appStats);
            }
            return stats;
        }
        return Collections.emptyMap();
    }

    public AppfogApplication[] listApplications(String server)
            throws ParsingResponseException, AppfogException, CredentialStoreException, IOException {
        AppfogCredential credential = getCredential(server);
        return parseJsonResponse(getJson(credential.target + "/apps", credential.token, 200), AppfogApplication[].class, null);
    }

    public AppfogServices services(String server)
            throws AppfogException, CredentialStoreException, ParsingResponseException, IOException {
        AppfogCredential credential = getCredential(server);
        return new AppfogServicesImpl(systemServices(credential), provisionedServices(credential));
    }

    private AppfogSystemService[] systemServices(AppfogCredential credential)
            throws AppfogException, ParsingResponseException, IOException {
        try {
            JsonValue jsonServices = JsonHelper.parseJson(getJson(credential.target + "/info/services", credential.token, 200));
            List<AppfogSystemService> result = new ArrayList<AppfogSystemService>();
            for (Iterator<String> types = jsonServices.getKeys(); types.hasNext(); ) {
                String type = types.next();
                for (Iterator<String> vendors = jsonServices.getElement(type).getKeys(); vendors.hasNext(); ) {
                    String vendor = vendors.next();
                    for (Iterator<String> versions = jsonServices.getElement(type).getElement(vendor).getKeys(); versions
                            .hasNext(); ) {
                        String version = versions.next();
                        result.add(ObjectBuilder.createObject(AppfogSystemServiceImpl.class,
                                                              jsonServices.getElement(type).getElement(vendor).getElement(version)));
                    }
                }
            }
            return result.toArray(new AppfogSystemService[result.size()]);
        } catch (JsonException e) {
            throw new ParsingResponseException(e.getMessage(), e);
        } catch (JsonParseException e) {
            throw new ParsingResponseException(e.getMessage(), e);
        }
    }

    private AppfogProvisionedService[] provisionedServices(AppfogCredential credential)
            throws AppfogException, ParsingResponseException, IOException {
        return parseJsonResponse(getJson(credential.target + "/services", credential.token, 200), AppfogProvisionedService[].class, null);
    }

    public AppfogProvisionedService createService(String server,
                                                  String service,
                                                  String name,
                                                  String app,
                                                  VirtualFileSystem vfs,
                                                  String projectId,
                                                  InfraType infraType)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (service == null || service.isEmpty()) {
            throw new IllegalArgumentException("Service type required. ");
        }

        return createService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                             service, name, app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, false) : app, infraType);
    }

    private AppfogProvisionedService createService(AppfogCredential credential,
                                                   String service,
                                                   String name,
                                                   String app,
                                                   InfraType infraType)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        AppfogSystemService[] available = systemServices(credential);
        AppfogSystemService target = null;
        for (int i = 0; i < available.length && target == null; i++) {
            if (service.equals(available[i].getVendor())) {
                target = available[i];
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("Invalid service type '" + service + "'. ");
        }
        // Generate service name if not specified.
        if (name == null || name.isEmpty()) {
            name = generate(service + '-', 8);
        }

        AppfogCreateService req = new AppfogCreateService(name, target.getType(), service, target.getVersion(), infraType.getInfra());
        postJson(credential.target + "/services", credential.token, JsonHelper.toJson(req), 200);

        // Be sure service available.
        AppfogProvisionedService res = findService(credential, name);

        if (app != null) {
            bindService(credential.target, name, app, null, null);
        }

        return res;
    }

    private AppfogProvisionedService findService(AppfogCredential credential, String name)
            throws AppfogException, ParsingResponseException, IOException {
        for (AppfogProvisionedService service : provisionedServices(credential)) {
            if (name.equals(service.getName())) {
                return service;
            }
        }
        throw new IllegalArgumentException("Service '" + name + "' not found. ");
    }

    public void deleteService(String server, String name)
            throws ParsingResponseException, AppfogException, CredentialStoreException, IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name required. ");
        }
        deleteService(getCredential(server), name);
    }

    private void deleteService(AppfogCredential appfogCredential, String name)
            throws AppfogException, ParsingResponseException, IOException {
        findService(appfogCredential, name);
        deleteJson(appfogCredential.target + "/services/" + name, appfogCredential.token, 200);
    }

    public void bindService(String server,
                            String name,
                            String app,
                            VirtualFileSystem vfs,
                            String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name required. ");
        }
        bindService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name,
                    app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
    }

    private void bindService(AppfogCredential appfogCredential, String name, String app, boolean restart)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(appfogCredential, app);
        findService(appfogCredential, name);
        boolean updated = false;
        List<String> services = appInfo.getServices();
        if (services == null) {
            services = new ArrayList<String>(1);
            appInfo.setServices(services);
            updated = services.add(name);
        } else if (!services.contains(name)) {
            updated = services.add(name);
        }

        if (updated) {
            putJson(appfogCredential.target + "/apps/" + app, appfogCredential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(appfogCredential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public void unbindService(String server,
                              String name,
                              String app,
                              VirtualFileSystem vfs,
                              String projectId)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service name required. ");
        }
        unbindService(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name,
                      app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, true);
    }

    private void unbindService(AppfogCredential appfogCredential, String name, String app, boolean restart)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(appfogCredential, app);
        findService(appfogCredential, name);
        List<String> services = appInfo.getServices();
        if (services != null && services.size() > 0 && services.remove(name)) {
            putJson(appfogCredential.target + "/apps/" + app, appfogCredential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(appfogCredential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public void environmentAdd(String server,
                               String app,
                               VirtualFileSystem vfs,
                               String projectId,
                               String key,
                               String val)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key-value pair required. ");
        }
        environmentAdd(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                       app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, key, val, true);
    }

    private void environmentAdd(AppfogCredential appfogCredential, String app, String key, String val, boolean restart)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(appfogCredential, app);
        boolean updated = false;
        List<String> env = appInfo.getEnv();
        String kv = key + "=" + (val == null ? "" : val);
        if (env == null) {
            env = new ArrayList<String>(1);
            appInfo.setEnv(env);
            updated = env.add(kv);
        } else if (!env.contains(kv)) {
            updated = env.add(kv);
        }

        if (updated) {
            putJson(appfogCredential.target + "/apps/" + app, appfogCredential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(appfogCredential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public void environmentDelete(String server,
                                  String app,
                                  VirtualFileSystem vfs,
                                  String projectId,
                                  String key)
            throws ParsingResponseException, AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key required. ");
        }
        environmentDelete(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server),
                          app == null || app.isEmpty() ? detectApplicationName(vfs, projectId, true) : app, key, true);
    }

    private void environmentDelete(AppfogCredential appfogCredential, String app, String key, boolean restart)
            throws AppfogException, ParsingResponseException, IOException {
        AppfogApplication appInfo = applicationInfo(appfogCredential, app);
        boolean updated = false;
        List<String> env = appInfo.getEnv();
        if (env != null && env.size() > 0) {
            for (Iterator<String> iter = env.iterator(); iter.hasNext() && !updated; ) {
                String[] kv = iter.next().split("=");
                if (key.equals(kv[0].trim())) {
                    iter.remove();
                    updated = true; // Stop iteration here. Remove first key-value pair in the list ONLY!
                }
            }
        }

        if (updated) {
            putJson(appfogCredential.target + "/apps/" + app, appfogCredential.token, JsonHelper.toJson(appInfo), 200);
            if (restart && "STARTED".equals(appInfo.getState())) {
                restartApplication(appfogCredential, app, appInfo.getMeta().getDebug());
            }
        }
    }

    public void validateAction(String server,
                               String action,
                               String app,
                               String frameworkName,
                               String url,
                               int instances,
                               int memory,
                               boolean noStart,
                               VirtualFileSystem vfs,
                               String projectId)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        if ("create".equals(action)) {
            if (app == null || app.isEmpty()) {
                throw new IllegalArgumentException("Application name required. ");
            }

            if (server == null || server.isEmpty()) {
                throw new IllegalArgumentException("Location of Appfog server required. ");
            }
            AppfogCredential appfogCredential = getCredential(server);

            SystemInfo systemInfo = systemInfo(appfogCredential);
            SystemResources limits = systemInfo.getLimits();
            SystemResources usage = systemInfo.getUsage();

            checkApplicationNumberLimit(limits, usage);
            checkApplicationName(appfogCredential, app);

            Framework cfg = null;
            if (frameworkName != null) {
                cfg = getFramework(systemInfo, frameworkName);
            }

            if (instances <= 0) {
                instances = 1;
            }

            if (memory <= 0 && cfg != null) {
                memory = cfg.getMemory();
            }

            // Check memory capacity.
            if (!noStart) {
                checkAvailableMemory(instances, memory, limits, usage);
            }
        } else if ("update".equals(action)) {
            String name = detectApplicationName(vfs, projectId, true);
            // Throw exception if application not found.
            applicationInfo(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server), name);
        } else {
            throw new IllegalArgumentException("Unknown action '" + action + "'. ");
        }
    }

    public InfraDetail[] getInfras(String server, VirtualFileSystem vfs, String projectId)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return getInfras(getCredential(server == null || server.isEmpty() ? detectServer(vfs, projectId) : server));
    }

    private InfraDetail[] getInfras(AppfogCredential credential)
            throws AppfogException, ParsingResponseException, IOException {
        return parseJsonResponse(getJson(credential.target + "/info/infras", credential.token, 200), InfraDetail[].class, null);
    }

    //-----------------------------------------------------------------------------

    private String detectServer(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        String server = null;
        if (vfs != null && projectId != null) {
            Item item = vfs.getItem(projectId, false, PropertyFilter.valueOf("appfog-target"));
            server = item.getPropertyValue("appfog-target");
        }
        return server;
    }

    private void writeServerName(VirtualFileSystem vfs, String projectId, String server)
            throws VirtualFileSystemException {
        Property p = new PropertyImpl("appfog-target", server);
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(p);
        vfs.updateItem(projectId, properties, null);
    }

    private String detectApplicationName(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
            throws VirtualFileSystemException {
        String app = null;
        if (vfs != null && projectId != null) {
            Item item = vfs.getItem(projectId, false, PropertyFilter.valueOf("appfog-application"));
            app = item.getPropertyValue("appfog-application");
        }
        if (failIfCannotDetect && (app == null || app.isEmpty())) {
            throw new RuntimeException(
                    "Not a Appfog application. Please select root folder of Appfog project. ");
        }
        return app;
    }

    private void writeApplicationName(VirtualFileSystem vfs, String projectId, String name)
            throws VirtualFileSystemException {
        Property p = new PropertyImpl("appfog-application", name);
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(p);
        vfs.updateItem(projectId, properties, null);
    }

    private void writeInfraName(VirtualFileSystem vfs, String projectId, String name)
            throws VirtualFileSystemException {
        Property p = new PropertyImpl("appfog-infra", name);
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(p);
        vfs.updateItem(projectId, properties, null);
    }

    public void checkApplicationNumberLimit(SystemResources limits, SystemResources usage) {
        if (limits != null && usage != null && limits.getApps() == usage.getApps()) {
            throw new IllegalStateException("Not enough resources to create new application. "
                                            + "Max number of applications (" + limits.getApps() + ") reached. ");
        }
    }

    public void checkApplicationName(AppfogCredential appfogCredential, String app)
            throws AppfogException, ParsingResponseException, IOException {
        try {
            applicationInfo(appfogCredential, app);
            throw new IllegalArgumentException("Application '" + app + "' already exists. Use update or delete. ");
        } catch (AppfogException e) {
            if (!"Not Found".equals(e.getMessage())) {
                throw e;
            }
            //"Not Found" - means that it's all good, application doesn't exist, continue creating.
        }
    }

    public Framework getFramework(SystemInfo systemInfo, String frameworkName) {
        Framework framework = systemInfo.getFrameworks().get(frameworkName);
        if (framework != null) {
            return framework;
        }
        throw new IllegalArgumentException(
                "Unsupported framework '" + frameworkName + "'. List of supported frameworks: " + systemInfo.getFrameworks().keySet());
    }

    public RuntimeInfo getRuntimeInfo(String runtime, AppfogCredential appfogCredential)
            throws AppfogException, ParsingResponseException, IOException {
        return (RuntimeInfo)getRuntimes(appfogCredential).get(runtime);
    }

    public Map getRuntimes(AppfogCredential appfogCredential)
            throws AppfogException, ParsingResponseException, IOException {
        return parseJsonResponse(getJson(appfogCredential.target + "/info/runtimes", appfogCredential.token, 200), Map.class,
                                 new HashMap<String, RuntimeInfo>(0) {
                                 }.getClass().getGenericSuperclass());
    }

    public void checkAvailableMemory(int instances, int memory, SystemResources limits, SystemResources usage) {
        if (limits != null && usage != null //
            && (instances * memory) > (limits.getMemory() - usage.getMemory())) {
            throw new IllegalStateException("Not enough resources to create new application." //
                                            + " Available memory " + (limits.getMemory() - usage.getMemory()) + 'M' //
                                            + " but " + (instances * memory) + "M required. ");
        }
    }

    public Crashes applicationCrashes(AppfogCredential appfogCredential, String app)
            throws AppfogException, ParsingResponseException, IOException {
        return parseJsonResponse(getJson(appfogCredential.target + "/apps/" + app + "/crashes", appfogCredential.token, 200), Crashes.class,
                                 null);
    }

    private <O> O parseJsonResponse(String json, Class<O> clazz, Type type) throws ParsingResponseException {
        try {
            return JsonHelper.fromJson(json, clazz, type);
        } catch (JsonParseException e) {
            throw new ParsingResponseException(e.getMessage(), e);
        }
    }

    //-----------------------------------------------------------------------------

    public String getJson(String url, String authToken, int success)
            throws AppfogException, IOException {
        return doRequest(url, "GET", authToken, null, null, success);
    }

    public String postJson(String url, String authToken, String body, int success)
            throws AppfogException, IOException {
        return doRequest(url, "POST", authToken, body, "application/json", success);
    }

    public String putJson(String url, String authToken, String body, int success)
            throws AppfogException, IOException {
        return doRequest(url, "PUT", authToken, body, "application/json", success);
    }

    public String deleteJson(String url, String authToken, int success)
            throws AppfogException, IOException {
        return doRequest(url, "DELETE", authToken, null, null, success);
    }

    public String doRequest(String url, String method, String authToken, String body, String contentType, int success)
            throws AppfogException, IOException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)new URL(url).openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod(method);
            http.setRequestProperty("Authorization", authToken);
            http.setRequestProperty("Accept", "*/*");
            if (!(body == null || body.isEmpty())) {
                http.setRequestProperty("Content-type", contentType);
                http.setDoOutput(true);
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
                    writer.write(body);
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
            if (http.getResponseCode() != success) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            String result;
            try {
                result = readBody(input, http.getContentLength());
            } finally {
                input.close();
            }
            return result;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    public static AppfogException fault(HttpURLConnection http) throws IOException {
        final int responseCode = http.getResponseCode();
        if (responseCode == 504) {
            return new AppfogException(
                    504, -1, "Currently the server is overloaded, please try again later", "text/plain");
        }
        if (responseCode == 204) {
            return new AppfogException(
                    204, -1, "No Content", "text/plain"
            );
        }
        final String contentType = http.getContentType();
        final int length = http.getContentLength();
        String msg = null;
        int exitCode = -1;
        if (length != 0) {
            InputStream in = null;
            try {
                in = http.getErrorStream();
                msg = readBody(in, length);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            if (contentType.startsWith("application/json")) // May have '; charset=utf-8'
            {
                try {
                    JsonParser jsonParser = new JsonParser();
                    jsonParser.parse(new StringReader(msg));
                    JsonValue resultJson = jsonParser.getJsonObject().getElement("description");
                    if (resultJson != null) {
                        msg = resultJson.getStringValue();
                    }
                    JsonValue exitCodeJson = jsonParser.getJsonObject().getElement("code");
                    if (exitCodeJson != null) {
                        exitCode = exitCodeJson.getIntValue();
                    }
                    switch (exitCode) {
                        // Change message for known error codes, we don't like to see something like "you're allowed ...."
                        // in error messages.
                        case 504:
                            msg = "Max number of allowed Provisioned services reached. ";
                            break;
                        case 600:
                            msg = "Not enough resources to create new application. Not enough memory capacity. ";
                            break;
                        case 601:
                            msg = "Not enough resources to create new application. Max number of applications reached. ";
                            break;
                        case 602:
                            msg = "Too many URIs mapped for application. ";
                            break;
                    }
                    return new AppfogException(responseCode, exitCode, msg, "text/plain");
                } catch (JsonException ignored) {
                    // Cannot parse JSON send as is.
                }
            }
        }
        return new AppfogException(responseCode, exitCode, msg, contentType);
    }

    private static String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int point, off = 0;
            while ((point = input.read(b, off, contentLength - off)) > 0) {
                off += point;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int point;
            while ((point = input.read(buf)) != -1) {
                bout.write(buf, 0, point);
            }
            body = bout.toString();
        }
        return body;
    }

    private static String toUptimeString(double uptime) {
        int seconds = (int)uptime;
        int days = seconds / (60 * 60 * 24);
        seconds -= days * 60 * 60 * 24;
        int hours = seconds / (60 * 60);
        seconds -= hours * 60 * 60;
        int minutes = seconds / 60;
        seconds -= minutes * 60;
        return days + "d:" + hours + "h:" + minutes + "m:" + seconds + 's';
    }

    //--------------------------------------------------------------------------

    private static final byte[] NEW_LINE                        = "\r\n".getBytes();
    private static final byte[] HYPHENS                         = "--".getBytes();
    private static final byte[] CONTENT_DISPOSITION_RESOURCES   = "Content-Disposition: form-data; name=\"resources\"\r\n\r\n".getBytes();
    private static final byte[] CONTENT_DISPOSITION_METHOD      = "Content-Disposition: form-data; name=\"_method\"\r\n\r\n".getBytes();
    private static final byte[] CONTENT_DISPOSITION_APPLICATION =
            "Content-Disposition: form-data; name=\"application\"; filename=\"".getBytes();
    private static final byte[] PUT                             = "put".getBytes();
    private static final byte[] CONTENT_TYPE_ZIP                = "Content-type: application/octet-stream\r\n\r\n".getBytes();

    public void uploadApplication(AppfogCredential appfogCredential, String app, VirtualFileSystem vfs, String projectId, java.io.File path)
            throws ParsingResponseException, AppfogException, VirtualFileSystemException, IOException {
        LOG.debug("uploadApplication START");
        final long start = System.currentTimeMillis();

        java.io.File zip = null;
        HttpURLConnection http = null;
        java.io.File uploadDir = null;
        try {
            uploadDir = createTempDirectory(null, "af_" + app);

            if (path != null) {
                if (path.isFile()) {
                    String name = path.getName();
                    if (name.endsWith(".war") || name.endsWith(".zip") || name.endsWith(".jar")) {
                        unzip(path, uploadDir);
                    }
                } else {
                    copy(path, uploadDir, null);
                }
            } else {
                Utils.copy(vfs, projectId, uploadDir);
            }

            List<java.io.File> files = list(uploadDir, GIT_FILTER);

            long totalSize = 0;
            for (java.io.File f : files) {
                totalSize += f.length();
            }

            ApplicationFile[] resources = null;
            if (totalSize > 65536) {
                MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

                ApplicationFile[] fingerprints = new ApplicationFile[files.size()];
                final long startSHA1 = System.currentTimeMillis();
                for (int i = 0; i < fingerprints.length; i++) {
                    digest.reset();
                    java.io.File f = files.get(i);
                    fingerprints[i] = new ApplicationFile(f.length(), Utils.countFileHash(f, digest), f.getAbsolutePath());
                }
                final long timeSHA1 = System.currentTimeMillis() - startSHA1;
                LOG.debug("Count SHA1 for {} files in {} ms", files.size(), timeSHA1);

                resources = parseJsonResponse(postJson(appfogCredential.target + "/resources", appfogCredential.token,
                                                       JsonHelper.toJson(fingerprints), 200), ApplicationFile[].class, null);

                String uploadDirPath = uploadDir.getAbsolutePath() + '/';

                for (ApplicationFile resource : resources) {
                    java.io.File f = new java.io.File(resource.getFn());
                    f.delete(); // Remove files that we don't need to upload.
                    resource.setFn(resource.getFn().replace(uploadDirPath, ""));
                }
            }

            if (resources == null) {
                resources = new ApplicationFile[0];
            }

            final long startZIP = System.currentTimeMillis();
            zip = new java.io.File(System.getProperty("java.io.tmpdir"), app + ".zip");
            zipDir(uploadDir.getAbsolutePath(), uploadDir, zip, new FilenameFilter() {
                @Override
                public boolean accept(java.io.File parent, String name) {
                    return !(".cloudfoundry-application".equals(name)
                             || ".vmc_target".equals(name)
                             || ".project".equals(name)
                             || ".git".equals(name)
                             || name.endsWith("~")
                             || name.endsWith(".log"));
                }
            });
            final long timeZIP = System.currentTimeMillis() - startZIP;
            LOG.debug("zip application in {} ms", timeZIP);

            // Upload application data.
            http = (HttpURLConnection)new URL(appfogCredential.target + "/apps/" + app + "/application").openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod("POST");
            http.setRequestProperty("Authorization", appfogCredential.token);
            final String boundary = "----------" + System.currentTimeMillis();
            http.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
            http.setDoOutput(true);

            OutputStream output = http.getOutputStream();
            try {
                final byte[] boundaryBytes = boundary.getBytes();
                // first boundary
                output.write(HYPHENS);
                output.write(boundaryBytes);

                output.write(NEW_LINE);
                output.write(CONTENT_DISPOSITION_RESOURCES);
                output.write(JsonHelper.toJson(resources).getBytes());

                output.write(NEW_LINE);
                output.write(HYPHENS);
                output.write(boundaryBytes);

                output.write(NEW_LINE);
                output.write(CONTENT_DISPOSITION_METHOD);
                output.write(PUT);

                output.write(NEW_LINE);
                output.write(HYPHENS);
                output.write(boundaryBytes);

                if (zip != null) {
                    // Add zipped application files if any.
                    String filename = zip.getName();
                    output.write(NEW_LINE);
                    output.write(CONTENT_DISPOSITION_APPLICATION);
                    output.write(filename.getBytes());
                    output.write('"');

                    output.write(NEW_LINE);
                    output.write(CONTENT_TYPE_ZIP);

                    FileInputStream zipInput = new FileInputStream(zip);
                    try {
                        byte[] b = new byte[8192];
                        int r;
                        while ((r = zipInput.read(b)) != -1) {
                            output.write(b, 0, r);
                        }
                    } finally {
                        zipInput.close();
                    }
                    output.write(NEW_LINE);
                    output.write(HYPHENS);
                    output.write(boundaryBytes);
                }

                // finalize multi-part stream
                output.write(HYPHENS);
                output.write(NEW_LINE);
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }
        } finally {
            if (uploadDir != null) {
                deleteRecursive(uploadDir);
            }
            if (zip != null) {
                zip.delete();
            }
            if (http != null) {
                http.disconnect();
            }

            final long time = System.currentTimeMillis() - start;
            LOG.debug("uploadApplication END, time: {} ms", time);
        }
    }

    public AppfogCredential getCredential(String server) throws AppfogException, CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(getUserId(), "appfog", credential);
        if (server == null) {
            server = credential.getAttribute("current_target");
        }
        String token = credential.getAttribute(server);
        if (token == null) {
            throw new AppfogException(200, 200, "Authentication required.\n", "text/plain");
        }
        return new AppfogCredential(server, token);
    }
}
