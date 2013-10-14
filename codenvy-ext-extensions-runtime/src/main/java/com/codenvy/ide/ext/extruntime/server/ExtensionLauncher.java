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

import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.Pair;
import com.codenvy.ide.ext.extruntime.dto.server.DtoServerImpls;
import com.codenvy.ide.ext.extruntime.dto.server.DtoServerImpls.ApplicationInstanceImpl;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServer;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServerConfiguration;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTCodeServerException;
import com.codenvy.ide.ext.extruntime.server.codeserver.GWTMavenCodeServer;
import com.codenvy.ide.ext.extruntime.server.tomcatServer.TomcatServer;
import com.codenvy.ide.ext.extruntime.server.tomcatServer.TomcatServerConfiguration;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.extension.maven.shared.BuildStatus.Status;

import org.apache.maven.model.Model;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.codenvy.ide.commons.ContainerUtils.readValueParam;
import static com.codenvy.ide.commons.FileUtils.*;
import static com.codenvy.ide.commons.NameGenerator.generate;
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.commons.ZipUtils.zipDir;
import static com.codenvy.ide.ext.extruntime.server.Utils.*;
import static java.lang.Integer.parseInt;

/**
 * Class used to managing (creating/launching/getting logs/stopping) Codenvy with custom's extensions.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionLauncher.java Jul 7, 2013 3:17:41 PM azatsarynnyy $
 */
public class ExtensionLauncher implements Startable {
    /** System property that contains build server URL. */
    public static final  String BUILD_SERVER_BASE_URL               = "exo.ide.builderClient.build-server-base-url";
    /** Default name of the client module directory. */
    public static final  String CLIENT_MODULE_DIR_NAME              = "codenvy-ide-client";
    public static final  String MAIN_GWT_MODULE_DESCRIPTOR_REL_PATH =
            "src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml";
    private static final Log    LOG                                 = ExoLogger.getLogger(ExtensionLauncher.class);
    /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
    private static final int    DEFAULT_APPLICATION_LIFETIME        = 60;
    /** Default address where GWT code server should bound . */
    private static final String DEFAULT_CODE_SERVER_BIND_ADDRESS    = "localhost";
    /** Application lifetime (in milliseconds). */
    private final int                                applicationLifetime;
    /** Launched Codenvy applications with custom extension. */
    private final ConcurrentMap<String, Application> applications;
    /** Checks launched application's lifetime and terminate it if it's expired. */
    private final ScheduledExecutorService           applicationTerminator;
    /** Maven build server client. */
    private       MavenBuilderClient                 builderClient;
    /** GWT code server's bind address. */
    private       String                             codeServerBindAddress;
    /** Service to find free ports for launching Tomcat servers. */
    private       CustomPortService                  httpPortService;
    /** Service to find free ports for launching GWT code servers. */
    private       CustomPortService                  codeServerPortService;

    public ExtensionLauncher(InitParams initParams) {
        this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)),
             readValueParam(initParams, "code-server-bind-address", DEFAULT_CODE_SERVER_BIND_ADDRESS),
             parsePortRanges(readValueParam(initParams, "http-port-range")),
             parsePortRanges(readValueParam(initParams, "code-server-port-range")),
             parseApplicationLifeTime(readValueParam(initParams, "sdk-app-lifetime")));
    }

    /** Constructs a new {@link ExtensionLauncher} with provided build server URL and server's port ranges. */
    protected ExtensionLauncher(String buildServerBaseURL, String codeServerBindAddress,
                                Pair<Integer, Integer> httpConnectorPortRange,
                                Pair<Integer, Integer> codeServerPortRange, int applicationLifetime) {
        if (codeServerBindAddress == null || codeServerBindAddress.isEmpty()) {
            throw new IllegalArgumentException("Code server bind address may not be null or empty string.");
        }

        this.builderClient = new MavenBuilderClient(buildServerBaseURL);
        this.codeServerBindAddress = codeServerBindAddress;
        this.httpPortService = new CustomPortService(httpConnectorPortRange.first,
                                                     httpConnectorPortRange.second);
        this.codeServerPortService = new CustomPortService(codeServerPortRange.first, codeServerPortRange.second);
        this.applicationLifetime = applicationLifetime * 60 * 1000;

        this.applications = new ConcurrentHashMap<>();
        this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
        this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Launch Codenvy with a custom extension. This need some preparatory operations, such as: </p>
     * <ul>
     * <li>proper setup the Maven project settings, such as dependencies and module declaration;
     * <li>add &lt;inherits&gt; to the IDEPlatform.gwt.xml in the Client project providing the logical name of
     * extension's GWT module.
     * </ul>
     *
     * @param vfs
     *         virtual file system
     * @param projectId
     *         identifier of a project we want to launch
     * @param wsMountPath
     *         mount path for the user's workspace
     * @return launched application description
     * @throws VirtualFileSystemException
     *         if any error in VFS
     * @throws ExtensionLauncherException
     *         if any error occurred while launching Codenvy app
     */
    public ApplicationInstance launch(VirtualFileSystem vfs, String projectId, String wsMountPath)
            throws VirtualFileSystemException, ExtensionLauncherException {
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project id required.");
        }

        final int httpPort = httpPortService.acquire();
        final int codeServerPort = codeServerPortService.acquire();
        if (httpPort == -1 || codeServerPort == -1) {
            httpPortService.release(httpPort);
            codeServerPortService.release(codeServerPort);
            throw new IllegalStateException(
                    "Not enough resources to launch new application. Max number of applications was reached.");
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

            if (extensionPom.getGroupId() == null || extensionPom.getArtifactId() == null ||
                extensionPom.getVersion() == null) {
                throw new Exception("Missing Maven artifact coordinates.");
            }

            // Unpack 'codenvy-ide-client' module sources and user's extension project into temporary directory.
            InputStream codenvyClientSourcesStream = Thread.currentThread().getContextClassLoader()
                                                           .getResourceAsStream("CodenvyClient.zip");
            if (codenvyClientSourcesStream == null) {
                throw new InvalidArgumentException("Can't find codenvy-ide-client module sources.");
            }

            /*********************************** Preparing ******************************************/

            unzip(codenvyClientSourcesStream, codeServerDirPath.toFile());
            Path customModulePath = codeServerDirPath.resolve(extensionPom.getArtifactId());
            unzip(vfs.exportZip(projectId).getStream(), customModulePath.toFile());

            addDependencyToPom(clientModulePomPath, extensionPom);

            // Detect DTO usage and add an appropriate sections to the codenvy-ide-client/pom.xml.
            copyDtoGeneratorInvocations(extensionPom, clientModulePomPath);

            // Inherit custom GWT module.
            Path mainGwtModuleDescriptor = clientModuleDirPath.resolve(MAIN_GWT_MODULE_DESCRIPTOR_REL_PATH);
            inheritGwtModule(mainGwtModuleDescriptor, detectGwtModuleLogicalName(customModulePath));

            // Replace src directory and pom.xml by symbolic links to an appropriate src and pom.xml
            // in 'fs-root' directory to allow GWT code server always get an actual sources.
            Path extensionDirInFSRoot = Paths.get(wsMountPath + project.getPath());
            if (!extensionDirInFSRoot.isAbsolute()) {
                extensionDirInFSRoot = extensionDirInFSRoot.toAbsolutePath();
            }
            extensionDirInFSRoot = extensionDirInFSRoot.normalize();
            deleteRecursive(customModulePath.resolve("src").toFile());
            Files.createSymbolicLink(customModulePath.resolve("src"), extensionDirInFSRoot.resolve("src"));
            Files.delete(customModulePath.resolve("pom.xml"));
            Files.createSymbolicLink(customModulePath.resolve("pom.xml"), extensionDirInFSRoot.resolve("pom.xml"));

            /*********************************** Building & Running ******************************************/

            // Deploy custom project to Maven repository.
            File zippedExtensionProjectFile = tempDir.toPath().resolve("extension-project.zip").toFile();
            zipDir(customModulePath.toString(), customModulePath.toFile(), zippedExtensionProjectFile, ANY_FILTER);
            String deployId = builderClient.deploy(zippedExtensionProjectFile);
            final String deployStatusJson = builderClient.checkStatus(deployId);
            DtoServerImpls.BuildStatusImpl deployStatus =
                    DtoServerImpls.BuildStatusImpl.fromJsonString(deployStatusJson);

            if (deployStatus.getStatus() != Status.SUCCESSFUL) {
                LOG.error("Unable to deploy maven artifact: " + deployStatus.getError());
                throw new Exception(deployStatus.getError());
            }

            // Build Codenvy platform + custom project.
            File zippedProjectFile = tempDir.toPath().resolve("project.zip").toFile();
            zipDir(clientModuleDirPath.toString(), clientModuleDirPath.toFile(), zippedProjectFile, ANY_FILTER);
            final String buildId = builderClient.build(zippedProjectFile);

            // Launch GWT code server while project is building.
            GWTCodeServer codeServer = new GWTMavenCodeServer();
            codeServer.start(new GWTCodeServerConfiguration(codeServerBindAddress, codeServerPort, clientModuleDirPath,
                                                            customModulePath.getFileName().toString()));

            final String buildStatusJson = builderClient.checkStatus(buildId);
            DtoServerImpls.BuildStatusImpl buildStatus = DtoServerImpls.BuildStatusImpl.fromJsonString(buildStatusJson);
            if (buildStatus.getStatus() != Status.SUCCESSFUL) {
                LOG.error("Unable to build project: " + buildStatus.getError());
                throw new Exception(buildStatus.getError());
            }

            // Launch Tomcat.
            TomcatServerConfiguration tomcatConf =
                    new TomcatServerConfiguration("tomcat/tomcat.zip", createTempDirectory(tempDir, "tomcat-").toPath(),
                                                  httpPort, new URL(buildStatus.getDownloadUrl()));
            TomcatServer tomcatServer = new TomcatServer();
            tomcatServer.start(tomcatConf);

            final long expirationTime = System.currentTimeMillis() + applicationLifetime;
            applications.put(appId, new Application(appId, expirationTime, tomcatServer, codeServer, tempDir));

            LOG.debug("Start Codenvy extension {}", appId);
            return ApplicationInstanceImpl.make().setId(appId).setPort(httpPort).setCodeServerPort(codeServerPort);
        } catch (Exception e) {
            LOG.warn("Codenvy extension {} failed to launch, cause: {}", appId, e);
            // ensure that ports are released
            httpPortService.release(httpPort);
            codeServerPortService.release(codeServerPort);
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
            throw new ExtensionLauncherException(e.getMessage(), e);
        }
    }

    /**
     * Get application logs.
     *
     * @param appId
     *         id of Codenvy application to get its logs
     * @return application's logs
     * @throws ExtensionLauncherException
     *         if any error occurred while retrieving logs
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
                logs.append(codeServerLogs);
            }
        } catch (IOException | GWTCodeServerException e) {
            // do nothing
        }

        try {
            final String tomcatLogs = app.tomcatServer.getLogs();
            if (!(tomcatLogs == null || tomcatLogs.isEmpty())) {
                logs.append(tomcatLogs);
            }
        } catch (IOException e) {
            // do nothing
        }

        return logs.toString();
    }

    /**
     * Stop Codenvy with custom extension.
     *
     * @param appId
     *         id of Codenvy application to stop
     * @throws ExtensionLauncherException
     *         if error occurred while stopping an app
     */
    public void stopApp(String appId) throws ExtensionLauncherException {
        Application extension = applications.get(appId);
        if (extension == null) {
            throw new ExtensionLauncherException(
                    String.format("Unable to stop Codenvy with extension %s. Application not found.", appId));
        }

        extension.tomcatServer.stop();
        extension.codeServer.stop();

        httpPortService.release(extension.tomcatServer.getConfiguration().getPort());
        codeServerPortService.release(extension.codeServer.getConfiguration().getPort());

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
        applicationTerminator.shutdownNow();
        for (String appId : applications.keySet()) {
            try {
                stopApp(appId);
            } catch (ExtensionLauncherException e) {
                LOG.error("Failed to stop Codenvy with extension {}.", appId, e);
            }
        }
    }

    private static Pair<Integer, Integer> parsePortRanges(String portRange) {
        Pair<Integer, Integer> portRangePair;
        try {
            final int hyphenIndex = portRange.indexOf('-');
            int firstPortNumber;
            int lastPortNumber;
            if (hyphenIndex != -1) {
                firstPortNumber = parseInt(portRange.substring(0, hyphenIndex));
                lastPortNumber = parseInt(portRange.substring(hyphenIndex + 1));
                if (firstPortNumber >= lastPortNumber) {
                    throw new IllegalArgumentException("Port range is incorrect.");
                }
            } else {
                firstPortNumber = parseInt(portRange);
                lastPortNumber = parseInt(portRange);
            }
            portRangePair = Pair.of(firstPortNumber, lastPortNumber);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return portRangePair;
    }

    private static int parseApplicationLifeTime(String lifeTime) {
        if (lifeTime != null) {
            return Integer.parseInt(lifeTime);
        }
        return DEFAULT_APPLICATION_LIFETIME;
    }

    private class TerminateApplicationTask implements Runnable {
        @Override
        public void run() {
            List<String> stopped = new ArrayList<>();
            for (Application app : applications.values()) {
                if (app.isExpired()) {
                    try {
                        stopApp(app.id);
                    } catch (ExtensionLauncherException e) {
                        LOG.error("Failed to stop Codenvy with extension {}.", app.id, e);
                    }
                    // Don't try to stop application twice.
                    stopped.add(app.id);
                }
            }
            applications.keySet().removeAll(stopped);
        }
    }

    /** Stores application resources. */
    private class Application {
        final String        id;
        final File          tempDir;
        final TomcatServer  tomcatServer;
        final GWTCodeServer codeServer;
        final long          expirationTime;

        Application(String id, long expirationTime, TomcatServer tomcatServer, GWTCodeServer codeServer, File tempDir) {
            this.id = id;
            this.expirationTime = expirationTime;
            this.tomcatServer = tomcatServer;
            this.codeServer = codeServer;
            this.tempDir = tempDir;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }
    }
}
