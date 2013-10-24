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
package com.codenvy.ide.ext.extruntime.server.runner;

import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.core.util.Pair;
import com.codenvy.ide.ext.extruntime.dto.server.DtoServerImpls.ApplicationInstanceImpl;
import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;

import org.apache.maven.model.Model;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
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
import static com.codenvy.ide.commons.FileUtils.createTempDirectory;
import static com.codenvy.ide.commons.FileUtils.deleteRecursive;
import static com.codenvy.ide.commons.NameGenerator.generate;
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.ext.extruntime.server.Utils.*;
import static java.lang.Integer.parseInt;

/**
 * Runner for Codenvy extensions.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionsRunner.java Jul 7, 2013 3:17:41 PM azatsarynnyy $
 */
public class ExtensionsRunner implements Startable {
    /** Default name of the client module directory. */
    public static final  String CLIENT_MODULE_DIR_NAME              = "codenvy-ide-client";
    public static final  String MAIN_GWT_MODULE_DESCRIPTOR_REL_PATH =
            "src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml";
    private static final Log    LOG                                 = ExoLogger.getLogger(ExtensionsRunner.class);
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
    /** GWT code server's bind address. */
    private       String                             codeServerBindAddress;
    /** Service to find free ports for launching Tomcat servers. */
    private       CustomPortService                  httpPortService;
    /** Service to find free ports for launching GWT code servers. */
    private       CustomPortService                  codeServerPortService;

    public ExtensionsRunner(InitParams initParams) {
        this(readValueParam(initParams, "code-server-bind-address", DEFAULT_CODE_SERVER_BIND_ADDRESS),
             parsePortRanges(readValueParam(initParams, "http-port-range")),
             parsePortRanges(readValueParam(initParams, "code-server-port-range")),
             parseApplicationLifeTime(readValueParam(initParams, "sdk-app-lifetime")));
    }

    /** Constructs a new {@link ExtensionsRunner}. */
    protected ExtensionsRunner(String codeServerBindAddress, Pair<Integer, Integer> httpConnectorPortRange,
                               Pair<Integer, Integer> codeServerPortRange, int applicationLifetime) {
        if (codeServerBindAddress == null || codeServerBindAddress.isEmpty()) {
            throw new IllegalArgumentException("Code server bind address may not be null or empty string.");
        }

        this.codeServerBindAddress = codeServerBindAddress;
        this.httpPortService = new CustomPortService(httpConnectorPortRange.first, httpConnectorPortRange.second);
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
     * @param warUrl
     *         WAR URL
     * @param enableHotUpdate
     *         whether to enable the ability hot update or not
     * @param vfs
     *         virtual file system (makes sense only when hot update is enabled)
     * @param projectId
     *         identifier of a project we want to run (makes sense only when hot update is enabled)
     * @param wsMountPath
     *         mount path for the user's workspace
     * @return description of a launched application
     * @throws VirtualFileSystemException
     *         if an error occurs in VFS
     * @throws RunnerException
     *         if an error occurs while launching app
     */
    public ApplicationInstance run(String warUrl, boolean enableHotUpdate, VirtualFileSystem vfs, String projectId,
                                   String wsMountPath) throws VirtualFileSystemException, RunnerException {
        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project id required.");
        }

        final int httpPort = httpPortService.acquire();
        final int codeServerPort = codeServerPortService.acquire();
        if (httpPort == -1 || codeServerPort == -1) {
            httpPortService.release(httpPort);
            codeServerPortService.release(codeServerPort);
            throw new IllegalStateException(
                    "Not enough resources to run new application. Max number of applications was reached.");
        }

        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        File tempDir = null;
        final String appId = generate("app-", 16);
        try {
            tempDir = createTempDirectory("sdk-runner-");
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

            /*********************************** Preparing ******************************************/

            InputStream codenvyClientSourcesStream = getCodenvyPlatformBinaryDistribution().openStream();
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

            /************************************** Running *********************************************/

            // Launch GWT code server.
            GWTCodeServer codeServer = new GWTMavenCodeServer();
            codeServer.start(new GWTCodeServerConfiguration(codeServerBindAddress, codeServerPort, clientModuleDirPath,
                                                            customModulePath.getFileName().toString()));

            // Launch Tomcat.
            TomcatServerConfiguration tomcatConf =
                    new TomcatServerConfiguration(createTempDirectory(tempDir, "tomcat-").toPath(), httpPort,
                                                  new URL(warUrl));
            TomcatServer tomcatServer = new TomcatServer();
            tomcatServer.start(tomcatConf);

            final long expirationTime = System.currentTimeMillis() + applicationLifetime;
            applications.put(appId, new Application(appId, expirationTime, tomcatServer, codeServer, tempDir));

            LOG.debug("Start Codenvy extension {}", appId);
            return ApplicationInstanceImpl.make().setId(appId).setPort(httpPort).setCodeServerPort(codeServerPort);
        } catch (Exception e) {
            LOG.warn("Codenvy extension {} failed to start, cause: {}", appId, e);
            // ensure that ports are released
            httpPortService.release(httpPort);
            codeServerPortService.release(codeServerPort);
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
            throw new RunnerException(e.getMessage(), e);
        }
    }

    /**
     * Get application logs.
     *
     * @param appId
     *         id of Codenvy application to get its logs
     * @return application's logs
     * @throws RunnerException
     *         if any error occurred while retrieving logs
     */
    public String getLogs(String appId) throws RunnerException {
        Application app = applications.get(appId);
        if (app == null) {
            throw new RunnerException(String.format("Unable to get logs. Application %s not found.", appId));
        }

        StringBuilder logs = new StringBuilder();
        try {
            final String codeServerLogs = app.codeServer.getLogs();
            if (!(codeServerLogs == null || codeServerLogs.isEmpty())) {
                logs.append(codeServerLogs);
            }
        } catch (IOException | RunnerException e) {
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
     * @throws RunnerException
     *         if error occurred while stopping an app
     */
    public void stopApp(String appId) throws RunnerException {
        Application extension = applications.get(appId);
        if (extension == null) {
            throw new RunnerException(
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
            } catch (RunnerException e) {
                LOG.error("Failed to stop Codenvy with extension {}.", appId, e);
            }
        }
    }

    private static int parseApplicationLifeTime(String lifeTime) {
        if (lifeTime != null) {
            return Integer.parseInt(lifeTime);
        }
        return DEFAULT_APPLICATION_LIFETIME;
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

    private class TerminateApplicationTask implements Runnable {
        @Override
        public void run() {
            List<String> stopped = new ArrayList<>();
            for (Application app : applications.values()) {
                if (app.isExpired()) {
                    try {
                        stopApp(app.id);
                    } catch (RunnerException e) {
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
