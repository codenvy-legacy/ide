/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server;

import com.codenvy.ide.commons.JsonHelper;
import com.codenvy.ide.ext.extruntime.dto.server.DtoServerImpls.ApplicationInstanceImpl;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServerLauncher;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServerConfiguration;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServerException;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTMavenCodeServerLauncher;
import com.codenvy.ide.ext.extruntime.server.tools.ProcessUtil;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.extension.maven.shared.BuildStatus.Status;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.codenvy.ide.commons.FileUtils.ANY_FILTER;
import static com.codenvy.ide.commons.FileUtils.deleteRecursive;
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.commons.ZipUtils.zipDir;
import static com.codenvy.ide.commons.ContainerUtils.readValueParam;
import static com.codenvy.ide.commons.ContainerUtils.readValuesParam;
import static com.codenvy.ide.commons.FileUtils.createTempDirectory;
import static com.codenvy.ide.commons.FileUtils.downloadFile;
import static com.codenvy.ide.commons.NameGenerator.generate;
import static com.codenvy.ide.ext.extruntime.server.Utils.addDependencyToPom;
import static com.codenvy.ide.ext.extruntime.server.Utils.addModuleToReactorPom;
import static com.codenvy.ide.ext.extruntime.server.Utils.enableSuperDevMode;
import static com.codenvy.ide.ext.extruntime.server.Utils.fixMGWT332Bug;
import static com.codenvy.ide.ext.extruntime.server.Utils.inheritGwtModule;
import static com.codenvy.ide.ext.extruntime.server.Utils.readPom;
import static com.codenvy.ide.ext.extruntime.server.Utils.setTomcatPorts;
import static com.codenvy.ide.ext.extruntime.server.Utils.writePom;
import static java.lang.Integer.parseInt;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Launcher for managing (creating/launching/getting logs/stopping) Codenvy-extension projects.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionLauncher.java Jul 7, 2013 3:17:41 PM azatsarynnyy $
 */
public class ExtensionLauncher implements Startable {
    private static final Log                         LOG                    = ExoLogger.getLogger(ExtensionLauncher.class);

    /** System property that contains build server URL. */
    public static final String                       BUILD_SERVER_BASE_URL  = "exo.ide.builder.build-server-base-url";
    /** Default name of the client module directory. */
    public static final String                       CLIENT_MODULE_DIR_NAME = "codenvy-ide-client";
    /** Id of Maven profile that used to add (re)sources of custom's extension. */
    public static final String                       ADD_SOURCES_PROFILE    = "customExtensionSources";

    /** Base URL of build server. */
    private final String                             buildServerBaseURL;

    private TomcatPortManager                        portManager;
    private CodeServerPortManager                    codeServerPortManager;

    /** Launched Codenvy applications with custom extension. */
    private final ConcurrentMap<String, Application> applications;

    public ExtensionLauncher(InitParams initParams) {
        this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)),
             parsePortRanges(readValuesParam(initParams, "catalina-shutdown-port-ranges")),
             parsePortRanges(readValuesParam(initParams, "http-connector-port-ranges")),
             parsePortRanges(readValuesParam(initParams, "ajp-connector-port-ranges")),
             parsePortRanges(readValuesParam(initParams, "code-server-port-ranges")));
    }

    private static List<Integer> parsePortRanges(List<String> portRanges) {
        List<Integer> portList = new ArrayList<Integer>(portRanges.size());
        try {
            for (String portRange : portRanges) {
                if (portRange.contains("-")) {
                    final int hyphenIndex = portRange.indexOf('-');
                    final int firstPortNumber = parseInt(portRange.substring(0, hyphenIndex));
                    final int lastPortNumber = parseInt(portRange.substring(hyphenIndex + 1));
                    if (firstPortNumber >= lastPortNumber) {
                        throw new IllegalArgumentException("Port range is incorrect.");
                    }
                    for (int portNumber = firstPortNumber; portNumber <= lastPortNumber; portNumber++) {
                        portList.add(portNumber);
                    }
                } else {
                    portList.add(parseInt(portRange));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return portList;
    }

    /** Constructs a new {@link ExtensionLauncher} with provided build server URL and server's port ranges. */
    protected ExtensionLauncher(String buildServerBaseURL,
                                List<Integer> catalinaShutdownPortList,
                                List<Integer> httpConnectorPortList,
                                List<Integer> ajpConnectorPortList,
                                List<Integer> codeServerPortList) {
        if (buildServerBaseURL == null || buildServerBaseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string.");
        }
        if (httpConnectorPortList == null || httpConnectorPortList.isEmpty() ||
            catalinaShutdownPortList == null || catalinaShutdownPortList.isEmpty() ||
            ajpConnectorPortList == null || ajpConnectorPortList.isEmpty()) {
            throw new IllegalArgumentException("Port range may not be null or empty list.");
        }
        this.buildServerBaseURL = buildServerBaseURL;
        this.portManager = new TomcatPortManager(catalinaShutdownPortList, httpConnectorPortList, ajpConnectorPortList);
        this.codeServerPortManager = new CodeServerPortManager(codeServerPortList);

        applications = new ConcurrentHashMap<String, Application>();
    }

    /**
     * Launch Codenvy with a custom extension.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of a project we want to launch
     * @param wsMountPath mount path for the user's workspace
     * @return launched application description
     * @throws VirtualFileSystemException if any error in VFS
     * @throws ExtensionLauncherException if any error occurred while launching Codenvy app
     */
    public ApplicationInstance launch(VirtualFileSystem vfs, String projectId, String wsMountPath) throws VirtualFileSystemException,
                                                                                                  ExtensionLauncherException {
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project id required.");
        }

        final int shutdownPort = portManager.nextShutdownPort();
        final int httpPort = portManager.nextHttpPort();
        final int ajpPort = portManager.nextAjpPort();
        final int codeServerPort = codeServerPortManager.nextPort();
        if (shutdownPort == -1 || httpPort == -1 || ajpPort == -1 || codeServerPort == -1) {
            portManager.releasePorts(shutdownPort, httpPort, ajpPort);
            codeServerPortManager.releasePort(codeServerPort);
            throw new IllegalStateException("Not enough resources to launch new application. Max number of applications was reached.");
        }

        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        File tempDir = null;
        final String appId = generate("app-", 16);
        try {
            tempDir = createTempDirectory("Extension-");
            final Path codeServerDirPath = createTempDirectory(tempDir, "code-server-").toPath();
            final Path clientModuleDirPath = codeServerDirPath.resolve(CLIENT_MODULE_DIR_NAME);
            final Path clientModulePomPath = clientModuleDirPath.resolve("pom.xml");

            Item pomFile = vfs.getItemByPath(project.getName() + "/pom.xml", null, false, PropertyFilter.NONE_FILTER);
            InputStream extPomContent = vfs.getContent(pomFile.getId()).getStream();
            Model extensionPom = readPom(extPomContent);

            // Unpack Codenvy Platform sources & user's extension project into temporary directory.
            InputStream codenvyPlatformSourcesStream = Thread.currentThread().getContextClassLoader()
                                                             .getResourceAsStream("CodenvyPlatform.zip");
            if (codenvyPlatformSourcesStream == null) {
                throw new InvalidArgumentException("Can't find Codenvy Platform sources package.");
            }
            unzip(codenvyPlatformSourcesStream, codeServerDirPath.toFile());
            Path customModulePath = codeServerDirPath.resolve(extensionPom.getArtifactId());
            unzip(vfs.exportZip(projectId).getStream(), customModulePath.toFile());

            // Use special ide-configuration.xml with removed unnecessary components.
            InputStream confStream = Thread.currentThread().getContextClassLoader()
                                           .getResourceAsStream("tomcat/ide-configuration.xml");
            Files.copy(confStream, clientModuleDirPath.resolve("src/main/webapp/WEB-INF/classes/conf/ide-configuration.xml"),
                       REPLACE_EXISTING);

            // Use special 'clean' pom.xml for parent & client module to build Codenvy Platform (without any applications).
            Files.move(codeServerDirPath.resolve("platform-pom.xml"), codeServerDirPath.resolve("pom.xml"), REPLACE_EXISTING);
            Files.move(clientModuleDirPath.resolve("platform-pom.xml"), clientModulePomPath, REPLACE_EXISTING);

            addModuleToReactorPom(codeServerDirPath.resolve("pom.xml"), customModulePath.getFileName().toString(),
                                  CLIENT_MODULE_DIR_NAME);
            addDependencyToPom(clientModulePomPath, extensionPom.getGroupId(), extensionPom.getArtifactId(), extensionPom.getVersion());

            // Change output directory for the WAR to allow builder return link to download WAR.
            configureWarPlugin(clientModulePomPath);

            // Add sources from custom's project to allow code server access it.
            fixMGWT332Bug(clientModulePomPath, customModulePath.getFileName().toString(), ADD_SOURCES_PROFILE);

            Path gwtModuleDescriptorPath = codeServerDirPath.resolve(CLIENT_MODULE_DIR_NAME)
                                                            .resolve("src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml");
            // TODO detect logical name of custom GWT module
            inheritGwtModule(gwtModuleDescriptorPath, "com.codenvy.ide.extension.demo.Demo");
            enableSuperDevMode(gwtModuleDescriptorPath);

            // Replace src and pom.xml by symlinks to an appropriate src and pom.xml
            // in 'fs-root' directory to allow code server always get the actual sources.
            Path extensionDirInFSRoot = Paths.get(wsMountPath + project.getPath());
            if (!extensionDirInFSRoot.isAbsolute()) {
                extensionDirInFSRoot = extensionDirInFSRoot.toAbsolutePath();
            }
            extensionDirInFSRoot = extensionDirInFSRoot.normalize();

            deleteRecursive(customModulePath.resolve("src").toFile());
            Files.delete(customModulePath.resolve("pom.xml"));
            Files.createSymbolicLink(customModulePath.resolve("src"), extensionDirInFSRoot.resolve("src"));
            Files.createSymbolicLink(customModulePath.resolve("pom.xml"), extensionDirInFSRoot.resolve("pom.xml"));

            Files.write(codeServerDirPath.resolve("codenvy-ide-core/src/main/resources/com/codenvy/ide/codesrv"),
                        (":" + codeServerPort).getBytes());

            // Deploy custom project to maven repository.
            File zippedExtensionProjectFile = tempDir.toPath().resolve("extension-project.zip").toFile();
            zipDir(customModulePath.toString(), customModulePath.toFile(), zippedExtensionProjectFile, ANY_FILTER);
            startCheckingBuildStatus(deploy(zippedExtensionProjectFile));

            // Build Codenvy platform + custom project.
            File zippedProjectFile = tempDir.toPath().resolve("project.zip").toFile();
            zipDir(codeServerDirPath.toString(), codeServerDirPath.toFile(), zippedProjectFile, ANY_FILTER);
            final String buildId = build(zippedProjectFile);

            // Run code server while project is building.
            GWTCodeServerLauncher codeServer = new GWTMavenCodeServerLauncher();
            codeServer.start(new GWTCodeServerConfiguration(codeServerPort, clientModuleDirPath));

            final String status = startCheckingBuildStatus(buildId);
            BuildStatusBean buildStatus = JsonHelper.fromJson(status, BuildStatusBean.class, null);
            if (buildStatus.getStatus() != Status.SUCCESSFUL) {
                throw new Exception("Unable to build project: " + buildStatus.getError());
            }

            File tomcatDir = createTempDirectory(tempDir, "tomcat-");
            Process tomcatProcess = runTomcat(tomcatDir.toPath(), new URL(buildStatus.getDownloadUrl()), shutdownPort, httpPort, ajpPort);

            applications.put(appId, new Application(appId, codeServer, tomcatProcess, shutdownPort, httpPort, ajpPort, tomcatDir, tempDir));
            LOG.debug("Start Codenvy extension {}", appId);
            return ApplicationInstanceImpl.make().setId(appId).setPort(httpPort).setCodeServerPort(codeServerPort);
        } catch (Exception e) {
            LOG.warn("Codenvy extension {} failed to start, cause: {}", appId, e);
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
            throw new ExtensionLauncherException(String.format("Unable to launch Codenvy with extension %s.", project.getName()), e);
        }
    }

    /**
     * Get application logs.
     * 
     * @param appId id of Codenvy application to get its logs
     * @return application's logs
     * @throws ExtensionLauncherException if any error occurred while getting logs
     */
    public String getLogs(String appId) throws ExtensionLauncherException {
        Application app = applications.get(appId);
        if (app == null) {
            throw new ExtensionLauncherException(String.format("Unable to get logs. Application %s not found.", appId));
        }

        StringBuilder logs = new StringBuilder();

        try {
            final String codeServerLogs = app.codeServer.getLogs();
            if (!(codeServerLogs == null || codeServerLogs.isEmpty())) {
                logs.append("====> code-server.log <====");
                logs.append("\n\n");
                logs.append(codeServerLogs);
                logs.append("\n\n");
            }

            // read all catalina*.log files
            File logsDir = app.tomcatDir.toPath().resolve("logs").toFile();
            File[] catalinaLogFiles = logsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("catalina") && name.endsWith(".log");
                }
            });

            for (File catalinaLogFile : catalinaLogFiles) {
                final String catalinaLogs = new String(Files.readAllBytes(catalinaLogFile.toPath()));
                if (!(catalinaLogs == null || catalinaLogs.isEmpty())) {
                    logs.append("====> ");
                    logs.append(catalinaLogFile.getName());
                    logs.append(" <====");
                    logs.append("\n\n");
                    logs.append(catalinaLogs);
                    logs.append("\n\n");
                }
            }
        } catch (IOException | GWTCodeServerException e) {
            throw new ExtensionLauncherException(String.format("Unable to get logs of application %s.", appId), e);
        }

        return logs.toString();
    }

    /**
     * Stop Codenvy with custom extension.
     * 
     * @param appId id of Codenvy application to stop
     * @throws ExtensionLauncherException if error occurred while stopping an app
     */
    public void stop(String appId) throws ExtensionLauncherException {
        Application extension = applications.get(appId);
        if (extension == null) {
            throw new ExtensionLauncherException(String.format("Unable to stop Codenvy with extension %s. Application not found.", appId));
        }

        // TODO
        // Use com.codenvy.api.tools.ProcessUtil from 'codenvy-organization-api' project when it finished.

        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
        LOG.debug("Killing process tree");
        ProcessUtil.kill(extension.tomcatProcess);
        portManager.releasePorts(extension.shutdownPort, extension.httpPort, extension.ajpPort);
        codeServerPortManager.releasePort(extension.codeServer.getConfiguration().getPort());

        extension.codeServer.stop();

        deleteRecursive(extension.tempDir, false);
        applications.remove(appId);
        LOG.debug("Stop Codenvy extension {}.", appId);
    }

    /** @see org.picocontainer.Startable#start() */
    @Override
    public void start() {
    }

    /** @see org.picocontainer.Startable#stop() */
    @Override
    public void stop() {
        for (String extId : applications.keySet()) {
            try {
                stop(extId);
            } catch (Exception e) {
                LOG.error("Failed to stop Codenvy with extension {}.", extId, e);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void configureWarPlugin(Path pomPath) {
        try {
            Model clientPom = readPom(pomPath);
            Build clientPomBuild = clientPom.getBuild();
            Map<String, Plugin> clientPomPlugins = clientPomBuild.getPluginsAsMap();
            Plugin warPlugin = clientPomPlugins.get("org.apache.maven.plugins:maven-war-plugin");
            Xpp3Dom warPluginConfiguration =
                                             Xpp3DomBuilder.build(new StringReader(
                                                                                   "<configuration><outputDirectory>./target/</outputDirectory></configuration>"));
            warPlugin.setConfiguration(warPluginConfiguration);
            clientPomBuild.setPlugins(new ArrayList(clientPomPlugins.values()));
            writePom(clientPom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.", e);
        }
    }

    private String startCheckingBuildStatus(String buildId) throws IOException, ExtensionLauncherException {
        String status = "";
        for (;;) {
            status = status(buildId);
            if (!status.contains("\"status\":\"IN_PROGRESS\"")) {
                return status;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing. Continue executing.
            }
        }
    }

    /**
     * Get result of build.
     * 
     * @param buildID ID of build need to check
     * @return string that contains description of current status of build in JSON format
     * @throws IOException if any i/o errors occurs
     * @throws ExtensionLauncherException any other errors related to build server internal state or parameter of client request
     */
    private String status(String buildID) throws IOException, ExtensionLauncherException {
        URL url = new URL(buildServerBaseURL + "/builder/maven/status/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                responseFail(http);
            }

            InputStream data = http.getInputStream();
            try {
                return readBody(data, http.getContentLength());
            } finally {
                data.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private String build(File zippedProjectFile) throws ExtensionLauncherException {
        try {
            return run(new URL(buildServerBaseURL + "/builder/maven/build"), new FileInputStream(zippedProjectFile));
        } catch (Exception e) {
            throw new ExtensionLauncherException(String.format("Unable to build project."), e);
        }
    }

    private String deploy(File zippedProjectFile) throws ExtensionLauncherException {
        try {
            return run(new URL(buildServerBaseURL + "/builder/maven/deploy"), new FileInputStream(zippedProjectFile));
        } catch (Exception e) {
            throw new ExtensionLauncherException(String.format("Unable to deploy project."), e);
        }
    }

    private String run(URL url, InputStream zippedProject) throws IOException, VirtualFileSystemException, ExtensionLauncherException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/zip");
            http.setDoOutput(true);
            byte[] buff = new byte[8192];
            InputStream data = null;
            OutputStream out = null;
            try {
                data = zippedProject;
                out = http.getOutputStream();
                int r;
                while ((r = data.read(buff)) != -1) {
                    out.write(buff, 0, r);
                }
            } finally {
                if (data != null) {
                    data.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            int responseCode = http.getResponseCode();
            if (responseCode != 202) // 202 (Accepted) response is expected.
            {
                responseFail(http);
            }
            String location = http.getHeaderField("location");
            return location.substring(location.lastIndexOf('/') + 1);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private void responseFail(HttpURLConnection http) throws IOException, ExtensionLauncherException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new ExtensionLauncherException(responseCode, "Unable to build project. " + body == null ? "" : body);
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }

    private String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int off = 0;
            int i;
            while ((i = input.read(b, off, contentLength - off)) > 0) {
                off += i;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int i;
            while ((i = input.read(buf)) != -1) {
                bout.write(buf, 0, i);
            }
            body = bout.toString();
        }
        return body;
    }

    private Process runTomcat(Path tomcatDir, URL ideWarUrl, int shutdownPort, int httpPort, int ajpPort) throws ExtensionLauncherException {
        InputStream tomcatBundleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("tomcat/tomcat.zip");
        if (tomcatBundleStream == null) {
            throw new ExtensionLauncherException("Unable to launch Codenvy with extension.");
        }

        try {
            unzip(tomcatBundleStream, tomcatDir.toFile());
            setTomcatPorts(tomcatDir, shutdownPort, httpPort, ajpPort);

            File ideWar = downloadFile(new File(tomcatDir + "/webapps"), "app-", ".war", ideWarUrl);
            ideWar.renameTo(tomcatDir.resolve("webapps/ide.war").toFile());

            final Path catalinaPath = tomcatDir.resolve("bin/catalina.sh");
            Files.setPosixFilePermissions(catalinaPath, PosixFilePermissions.fromString("rwxr--r--"));

            return new ProcessBuilder(catalinaPath.toString(), "run").start();
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch Codenvy with extension.", e);
        }
    }

    /** Stores application resources. */
    private class Application {
        final String id;
        GWTCodeServerLauncher   codeServer;
        Process      tomcatProcess;
        int          shutdownPort;
        int          httpPort;
        int          ajpPort;
        File         tomcatDir;
        File         tempDir;

        Application(String id,
                    GWTCodeServerLauncher codeServer,
                    Process tomcatProcess,
                    int shutdownPort,
                    int httpPort,
                    int ajpPort,
                    File tomcatDir,
                    File tempDir) {
            this.id = id;
            this.codeServer = codeServer;
            this.tomcatProcess = tomcatProcess;
            this.shutdownPort = shutdownPort;
            this.httpPort = httpPort;
            this.ajpPort = ajpPort;
            this.tomcatDir = tomcatDir;
            this.tempDir = tempDir;
        }
    }
}
