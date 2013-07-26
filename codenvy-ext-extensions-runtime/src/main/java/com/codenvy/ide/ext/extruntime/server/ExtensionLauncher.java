/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.extruntime.server;

import com.codenvy.ide.commons.JsonHelper;
import com.codenvy.ide.ext.extruntime.server.codeserver.CodeServerStarter.CodeServer;
import com.codenvy.ide.ext.extruntime.server.codeserver.GwtMvnCodeServerStarter;
import com.codenvy.ide.ext.extruntime.server.tools.ProcessUtil;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
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
import static com.codenvy.ide.commons.server.FileUtils.createTempDirectory;
import static com.codenvy.ide.commons.server.FileUtils.downloadFile;
import static com.codenvy.ide.commons.server.NameGenerator.generate;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Launcher for launching Codenvy extensions in a separate Codenvy instance.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionLauncher.java Jul 7, 2013 3:17:41 PM azatsarynnyy $
 */
public class ExtensionLauncher implements Startable {
    private static final Log                                       LOG                      = ExoLogger.getLogger(ExtensionLauncher.class);

    /** System property that contains build server URL. */
    private static final String                                    BUILD_SERVER_BASE_URL    = "exo.ide.builder.build-server-base-url";
    /** The system-dependent default name-separator character. */
    private static final char                                      PS                       = File.separatorChar;
    /** Default name of the client module directory. */
    private static final String                                    CLIENT_MODULE_DIR_NAME   = "codenvy-ide-client";
    /** Directive for GWT-module descriptor to enable GWT SuperDevMode and use cross-site IFrame linker. */
    // TODO avoid to using 'failIfScriptTag' property (remove <script> tags from Commons.gwt.xml)
    private static final String                                    SUPER_DEV_MODE_DIRECTIVE =
                                                                                              "\r\n\t<add-linker name='xsiframe' />"
                                                                                                  + "\r\n\t<set-configuration-property name='devModeRedirectEnabled' value='true' />"
                                                                                                  + "\r\n\t<set-configuration-property name='xsiframe.failIfScriptTag' value='false'/>"
                                                                                                  + "\r\n\t<set-property name='compiler.useSourceMaps' value='true' />";

    /** Maven POM reader. */
    private static MavenXpp3Reader                                 pomReader                = new MavenXpp3Reader();
    /** Maven POM writer. */
    private static MavenXpp3Writer                                 pomWriter                = new MavenXpp3Writer();

    /** Base URL of build server. */
    private final String                                           baseURL;
    /** Launched extensions. */
    private final ConcurrentMap<String, CodenvyExtensionResources> extensions;

    public ExtensionLauncher(InitParams initParams) {
        this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)));
    }

    private static String readValueParam(InitParams initParams, String paramName, String defaultValue) {
        if (initParams != null) {
            ValueParam vp = initParams.getValueParam(paramName);
            if (vp != null) {
                return vp.getValue();
            }
        }
        return defaultValue;
    }

    /**
     * Constructs a new {@link ExtensionLauncher} with the provided URL of build server.
     * 
     * @param baseURL base URL of build server
     */
    protected ExtensionLauncher(String baseURL) {
        if (baseURL == null || baseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string.");
        }
        this.baseURL = baseURL;
        extensions = new ConcurrentHashMap<String, CodenvyExtensionResources>();
    }

    /**
     * Launch Codenvy extension in a separate Codenvy instance.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of a project we want to launch
     * @param wsMountPath mount path for the project's workspace
     * @return id of launched extension
     * @throws VirtualFileSystemException if any error in VFS
     * @throws ExtensionLauncherException if any error occurred while launching extension
     */
    public String launchExtension(VirtualFileSystem vfs, String projectId, String wsMountPath) throws VirtualFileSystemException,
                                                                                              ExtensionLauncherException {
        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        File tempDir = null;
        final String extId = generate("ext-", 16);
        try {
            tempDir = createTempDirectory("Extension-");
            final Path codeServerDirPath = createTempDirectory(tempDir, "code-server-").toPath();
            final Path clientModuleDirPath = codeServerDirPath.resolve(CLIENT_MODULE_DIR_NAME);
            final Path clientModulePomPath = clientModuleDirPath.resolve("pom.xml");

            Item pomFile = vfs.getItemByPath(project.getName() + PS + "pom.xml", null, false, PropertyFilter.NONE_FILTER);
            InputStream extPomContent = vfs.getContent(pomFile.getId()).getStream();
            Model extPom = pomReader.read(extPomContent, true);

            // Unpack Codenvy Platform sources & user's extension project into temporary directory.
            InputStream codenvyPlatformSourcesStream = Thread.currentThread().getContextClassLoader()
                                                             .getResourceAsStream("conf/CodenvyPlatform.zip");
            if (codenvyPlatformSourcesStream == null) {
                throw new InvalidArgumentException("Can't find Codenvy Platform sources package.");
            }
            unzip(codenvyPlatformSourcesStream, codeServerDirPath.toFile());
            Path customModulePath = codeServerDirPath.resolve(extPom.getArtifactId());
            unzip(vfs.exportZip(projectId).getStream(), customModulePath.toFile());

            // Use special ide-configuration.xml with removed unnecessary components.
            InputStream confStream = Thread.currentThread().getContextClassLoader()
                                           .getResourceAsStream("conf/tomcat/ide-configuration.xml");
            Files.copy(confStream, clientModuleDirPath.resolve("src/main/webapp/WEB-INF/classes/conf/ide-configuration.xml"),
                       REPLACE_EXISTING);

            // Use special 'clean' pom.xml for parent & client module to build Codenvy Platform (without any extensions).
            Files.move(codeServerDirPath.resolve("platform-pom.xml"), codeServerDirPath.resolve("pom.xml"), REPLACE_EXISTING);
            Files.move(clientModuleDirPath.resolve("platform-pom.xml"), clientModulePomPath, REPLACE_EXISTING);

            // Add extension as maven-module into parent reactor pom.xml.
            addModuleToReactorPom(codeServerDirPath.resolve("pom.xml"), customModulePath.getFileName().toString());

            // Add extension as dependency into client module's pom.xml.
            addDependencyToPom(clientModulePomPath, extPom.getGroupId(), extPom.getArtifactId(), extPom.getVersion());

            // Change output directory for the WAR to allow builder return link to download WAR.
            configureWarPlugin(clientModulePomPath);

            // Add sources from user's project to allow code server access it.
            // It's a workaround for known bug in GWT Maven plug-in.
            // See https://jira.codehaus.org/browse/MGWT-332.
            fixGwtMavenPluginBug(clientModulePomPath, customModulePath.getFileName().toString());

            // Set code server's own working directory instead of system temp directory.
            changeCodeServerWorkDir(clientModulePomPath, codeServerDirPath);

            // Add custom GWT-module into IDEPlatform.gwt.xml and enable SuperDevMode.
            Path gwtModuleDescriptorPath = codeServerDirPath.resolve(CLIENT_MODULE_DIR_NAME)
                                                            .resolve("src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml");
            addGwtModuleToGwtModuleDescriptor(gwtModuleDescriptorPath, "com.codenvy.ide.extension.demo.Demo");
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

            // Deploy custom project to maven repository.
            File zippedExtensionProjectFile = tempDir.toPath().resolve("extension-project.zip").toFile();
            zipDir(customModulePath.toString(), customModulePath.toFile(), zippedExtensionProjectFile, ANY_FILTER);
            startCheckingBuildStatus(deploy(zippedExtensionProjectFile));

            // Build Codenvy platform + custom project.
            File zippedProjectFile = tempDir.toPath().resolve("project.zip").toFile();
            zipDir(codeServerDirPath.toString(), codeServerDirPath.toFile(), zippedProjectFile, ANY_FILTER);
            final String buildId = build(zippedProjectFile);

            // Run code server while project is building.
            CodeServer codeServer = new GwtMvnCodeServerStarter().start(codeServerDirPath.resolve(CLIENT_MODULE_DIR_NAME));

            final String status = startCheckingBuildStatus(buildId);
            BuildStatusBean buildStatus = JsonHelper.fromJson(status, BuildStatusBean.class, null);
            if (buildStatus.getStatus() != Status.SUCCESSFUL) {
                throw new ExtensionLauncherException(String.format("Unable to build Codenvy extension %s. %s ", project.getName(),
                                                                   buildStatus.getError()));
            }

            File tomcatDir = createTempDirectory(tempDir, "tomcat-");
            Process tomcatProcess = runTomcat(tomcatDir, new URL(buildStatus.getDownloadUrl()), true);

            // TODO wait while Tomcat & code server will start and check that they started successfully

            extensions.put(extId, new CodenvyExtensionResources(extId, codeServer, tomcatProcess, tomcatDir, tempDir));
            LOG.debug("Start Codenvy extension {}", extId);
            return extId;
        } catch (Exception e) {
            LOG.warn("Codenvy extension {} failed to start, cause: {}", extId, e.getMessage());
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir, false);
            }
            throw new ExtensionLauncherException(String.format("Unable to launch Codenvy extension %s.", project.getName()));
        }
    }

    /**
     * Get extension logs.
     * 
     * @param extId id of extension to get its logs
     * @return extension's logs
     * @throws ExtensionLauncherException if any error occurred while getting logs
     */
    public String getLogs(String extId) throws ExtensionLauncherException {
        CodenvyExtensionResources extension = extensions.get(extId);
        if (extension == null) {
            throw new ExtensionLauncherException(String.format("Unable to get logs. Extension %s not found.", extId));
        }

        StringBuilder logs = new StringBuilder();

        final String codeServerLogs = extension.codeServer.getLogs();
        if (!(codeServerLogs == null || codeServerLogs.isEmpty())) {
            logs.append("====> code-server.log <====");
            logs.append("\n\n");
            logs.append(codeServerLogs);
            logs.append("\n\n");
        }

        // read all catalina*.log files
        File logsDir = extension.tomcatDir.toPath().resolve("logs").toFile();
        File[] catalinaLogFiles = logsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("catalina") && name.endsWith(".log");
            }
        });
        try {
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
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to get logs.");
        }

        return logs.toString();
    }

    /**
     * Stop Codenvy extension.
     * 
     * @param extId id of extension to stop
     * @throws ExtensionLauncherException if error occurred while stopping an extension
     */
    public void stopExtension(String extId) throws ExtensionLauncherException {
        CodenvyExtensionResources extension = extensions.get(extId);
        if (extension == null) {
            throw new ExtensionLauncherException(String.format("Unable to stop Codenvy extension %s. Extension not found.", extId));
        }

        // TODO
        // Use com.codenvy.api.tools.ProcessUtil from 'codenvy-organization-api' project when it finished.

        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
        ProcessUtil.kill(extension.tomcatProcess);
        extension.codeServer.stop();

        deleteRecursive(extension.tempDir, false);
        extensions.remove(extId);
        LOG.debug("Stop Codenvy extension {}.", extId);
    }

    /** @see org.picocontainer.Startable#start() */
    @Override
    public void start() {
    }

    /** @see org.picocontainer.Startable#stop() */
    @Override
    public void stop() {
        for (String extId : extensions.keySet()) {
            try {
                stopExtension(extId);
            } catch (Exception e) {
                LOG.error("Failed to stop extension {}.", extId, e);
            }
        }
    }

    private static Model readPom(Path path) {
        try {
            return pomReader.read(Files.newInputStream(path), false);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Error occurred while reading pom.xml file.");
        }
    }

    private static void writePom(Model pom, Path path) {
        try {
            pomWriter.write(Files.newOutputStream(path), pom);
        } catch (IOException e) {
            throw new IllegalStateException("Error occurred while writing pom.xml file.");
        }
    }

    private void addModuleToReactorPom(Path reactorPomPath, String moduleName) {
        Model parentPom = readPom(reactorPomPath);
        final List<String> parentPomModulesList = parentPom.getModules();
        int n = 0;
        for (String module : parentPomModulesList) {
            // insert custom module before module 'client' module
            if (module.equals(CLIENT_MODULE_DIR_NAME)) {
                parentPom.getModules().add(n, moduleName);
                break;
            }
            n++;
        }
        writePom(parentPom, reactorPomPath);
    }

    private void addDependencyToPom(Path pomPath, String groupId, String artifactId, String version) {
        Dependency extMvnDependency = new Dependency();
        extMvnDependency.setGroupId(groupId);
        extMvnDependency.setArtifactId(artifactId);
        extMvnDependency.setVersion(version);
        Model clientPom = readPom(pomPath);
        clientPom.getDependencies().add(extMvnDependency);
        writePom(clientPom, pomPath);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void configureWarPlugin(Path pomPath) throws ExtensionLauncherException {
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
            throw new ExtensionLauncherException("Unable to launch extension.");
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void fixGwtMavenPluginBug(Path pomPath, String extensionModuleName) throws ExtensionLauncherException {
        try {
            Model clientPom = readPom(pomPath);
            List<Profile> profiles = clientPom.getProfiles();
            Profile superDevModeProfile = null;
            for (Profile profile : profiles) {
                if (profile.getId().equals("devMode")) {
                    superDevModeProfile = profile;
                }
            }
            Map<String, Plugin> plugins = superDevModeProfile.getBuild().getPluginsAsMap();
            Plugin buildHelperPlugin = plugins.get("org.codehaus.mojo:build-helper-maven-plugin");
            PluginExecution execution = buildHelperPlugin.getExecutionsAsMap().get("add-extension-sources");

            final String confString = String.format("<configuration>"
                                                    + "<sources><source>../%1$s/src/main/java</source></sources>"
                                                    + "<resources><resource>../%1$s/src/main/r</resource></resources>"
                                                    + "</configuration>", extensionModuleName);
            Xpp3Dom configuration = Xpp3DomBuilder.build(new StringReader(confString));
            execution.setConfiguration(configuration);

            superDevModeProfile.getBuild().setPlugins(new ArrayList(plugins.values()));
            writePom(clientPom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new ExtensionLauncherException("Unable to launch extension.");
        }
    }

    private void changeCodeServerWorkDir(Path pomPath, Path dirPath) throws ExtensionLauncherException {
        try {
            final String configString = String.format("<configuration>"
                                                      + "<codeServerWorkDir>%s</codeServerWorkDir>"
                                                      + "</configuration>", dirPath);
            Xpp3Dom additionalConfiguration = Xpp3DomBuilder.build(new StringReader(configString));

            Model clientPom = readPom(pomPath);
            Build clientPomBuild = clientPom.getBuild();
            Map<String, Plugin> clientPomPlugins = clientPomBuild.getPluginsAsMap();
            Plugin gwtPlugin = clientPomPlugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom configuration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(configuration);
            clientPomBuild.setPlugins(new ArrayList(clientPomPlugins.values()));
            writePom(clientPom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new ExtensionLauncherException("Unable to launch extension.");
        }
    }

    private void addGwtModuleToGwtModuleDescriptor(Path gwtModuleDescriptorPath, String gwtModulePath) throws ExtensionLauncherException {
        try {
            final String gwtModuleDependency = "\t<inherits name='" + gwtModulePath + "'/>";
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            // insert custom module as last 'inherits' entry
            int i = 0, lastInheritsLine = 0;
            for (String str : content) {
                i++;
                if (str.contains("<inherits")) {
                    lastInheritsLine = i;
                }
            }
            content.add(lastInheritsLine, gwtModuleDependency);
            Files.write(gwtModuleDescriptorPath, content, UTF_8);
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch extension.");
        }
    }

    private void enableSuperDevMode(Path gwtModuleDescriptorPath) throws ExtensionLauncherException {
        try {
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            int penultimateLine = 0;
            for (String str : content) {
                penultimateLine++;
                if (str.contains("</module>")) {
                    break;
                }
            }
            content.add(penultimateLine - 1, SUPER_DEV_MODE_DIRECTIVE);
            Files.write(gwtModuleDescriptorPath, content, UTF_8);
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch extension.");
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
        URL url = new URL(baseURL + "/builder/maven/status/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                fail(http);
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
            return run(new URL(baseURL + "/builder/maven/build"), new FileInputStream(zippedProjectFile));
        } catch (Exception e) {
            throw new ExtensionLauncherException(String.format("Unable to build project."));
        }
    }

    private String deploy(File zippedProjectFile) throws ExtensionLauncherException {
        try {
            return run(new URL(baseURL + "/builder/maven/deploy"), new FileInputStream(zippedProjectFile));
        } catch (Exception e) {
            throw new ExtensionLauncherException(String.format("Unable to build project."));
        }
    }

    private String run(URL url, InputStream zippedProject) throws IOException, VirtualFileSystemException, ExtensionLauncherException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/zip");
            authenticate(http);
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
                fail(http);
            }
            String location = http.getHeaderField("location");
            return location.substring(location.lastIndexOf('/') + 1);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Add authentication info to the request. By default do nothing. May be reimplemented for particular authentication scheme.
     * 
     * @param http HTTP connection to add authentication info, e.g. Basic authentication headers.
     * @throws IOException if any i/o errors occur
     */
    protected void authenticate(HttpURLConnection http) throws IOException {
    }

    private void fail(HttpURLConnection http) throws IOException, ExtensionLauncherException {
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

    private Process runTomcat(File tomcatDir, URL ideWarUrl, boolean waitForStarting) throws ExtensionLauncherException {
        InputStream tomcatBundleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/tomcat/tomcat.zip");
        if (tomcatBundleStream == null) {
            throw new ExtensionLauncherException("Can't find Tomcat package.");
        }

        try {
            unzip(tomcatBundleStream, tomcatDir);
            File ideWar = downloadFile(new File(tomcatDir.getPath() + PS + "webapps"), "app-", ".war", ideWarUrl);
            ideWar.renameTo(tomcatDir.toPath().resolve("webapps/IDE.war").toFile());

            final Path catalinaPath = tomcatDir.toPath().resolve("bin/catalina.sh");
            Files.setPosixFilePermissions(catalinaPath, PosixFilePermissions.fromString("rwxr--r--"));
            Process process = new ProcessBuilder(catalinaPath.toString(), "run").start();
            if (waitForStarting) {
                // TODO
            }
            return process;
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch extension.");
        }
    }

    private class CodenvyExtensionResources {
        final String id;
        CodeServer   codeServer;
        Process      tomcatProcess;
        File         tomcatDir;
        File         tempDir;

        CodenvyExtensionResources(String id, CodeServer codeServer, Process tomcatProcess, File tomcatDir, File tempDir) {
            this.id = id;
            this.codeServer = codeServer;
            this.tomcatProcess = tomcatProcess;
            this.tomcatDir = tomcatDir;
            this.tempDir = tempDir;
        }
    }
}
