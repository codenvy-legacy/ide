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
import static com.codenvy.ide.commons.ZipUtils.unzip;
import static com.codenvy.ide.commons.ZipUtils.zipDir;
import static com.codenvy.ide.commons.server.FileUtils.createTempDirectory;
import static com.codenvy.ide.commons.server.FileUtils.deleteRecursive;
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
    private static final String                                    CLIENT_MODULE_DIR_NAME   = "codenvy-ide-client";
    /** Directive for GWT-module descriptor to enable GWT SuperDevMode. */
    // TODO avoid using failIfScriptTag property and remove <script> tags from Commons.gwt.xml
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
    /** Runned extensions. */
    private final ConcurrentMap<String, CodenvyExtensionResources> applications;

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
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string. ");
        }
        this.baseURL = baseURL;
        applications = new ConcurrentHashMap<String, CodenvyExtensionResources>();
    }

    /**
     * Launch Codenvy extension in a separate Codenvy instance.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of a project we want to launch
     * @param wsMountPath mount path for the project's workspace
     * @return launched app id
     * @throws VirtualFileSystemException if any error in VFS
     * @throws ExtensionLauncherException if any error occurred while launching extension
     */
    public String launchExtension(VirtualFileSystem vfs, String projectId, String wsMountPath) throws VirtualFileSystemException,
                                                                                              ExtensionLauncherException {
        Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
        File tempDir = null;
        try {
            tempDir = createTempDirectory("CodenvyExtension-");
            final Path appDirPath = createTempDirectory(tempDir, "project-").toPath();
            final Path clientModuleDirPath = appDirPath.resolve(CLIENT_MODULE_DIR_NAME);
            final Path clientModulePomPath = clientModuleDirPath.resolve("pom.xml");

            Item pomFile = vfs.getItemByPath(project.getName() + PS + "pom.xml", null, false, PropertyFilter.NONE_FILTER);
            InputStream extPomContent = vfs.getContent(pomFile.getId()).getStream();
            Model extPom = pomReader.read(extPomContent, false);
            final String extArtifactId = extPom.getArtifactId();

            // -----------------------------------------------------------
            // Unpack Codenvy Platform sources & user's
            // extension project into temporary directory.
            // -----------------------------------------------------------
            InputStream codenvyPlatformSourcesStream = Thread.currentThread().getContextClassLoader()
                                                             .getResourceAsStream("conf/CodenvyPlatform.zip");
            if (codenvyPlatformSourcesStream == null) {
                throw new InvalidArgumentException("Can't find Codenvy Platform sources package.");
            }
            unzip(codenvyPlatformSourcesStream, appDirPath.toFile());
            Path extensionPath = appDirPath.resolve(extArtifactId);
            unzip(vfs.exportZip(projectId).getStream(), extensionPath.toFile());

            // TODO Temporary decision
            File zippedExtensionProjectFile = tempDir.toPath().resolve("extension-project.zip").toFile();
            zipDir(extensionPath.toString(), extensionPath.toFile(), zippedExtensionProjectFile, ANY_FILTER);
            startCheckingBuildStatus(deploy(zippedExtensionProjectFile));

            // -----------------------------------------------------------
            // Use special 'clean' pom.xml for parent & client module
            // to build Codenvy Platform.
            // -----------------------------------------------------------
            Files.move(appDirPath.resolve("platform-pom.xml"), appDirPath.resolve("pom.xml"), REPLACE_EXISTING);
            Files.move(clientModuleDirPath.resolve("platform-pom.xml"), clientModulePomPath, REPLACE_EXISTING);

            // -----------------------------------------------------------
            // Use special ide-configuration.xml with removed
            // unnecessary components.
            // -----------------------------------------------------------
            InputStream confStream =
                                     Thread.currentThread().getContextClassLoader()
                                           .getResourceAsStream("conf/tomcat/ide-configuration.xml");
            Files.copy(confStream, clientModuleDirPath.resolve("src/main/webapp/WEB-INF/classes/conf/ide-configuration.xml"),
                       REPLACE_EXISTING);

            // -----------------------------------------------------------
            // Add extension as maven-module into parent pom.xml.
            // -----------------------------------------------------------
            Model parentPom = readPom(appDirPath.resolve("pom.xml"));
            final List<String> parentPomModulesList = parentPom.getModules();
            int n = 0;
            for (String module : parentPomModulesList) {
                // insert extension module before client module
                if (module.equals(CLIENT_MODULE_DIR_NAME)) {
                    parentPom.getModules().add(n, extArtifactId);
                    break;
                }
                n++;
            }
            writePom(parentPom, appDirPath.resolve("pom.xml"));

            // -----------------------------------------------------------
            // Add extension as dependency into client module's pom.xml.
            // -----------------------------------------------------------
            Dependency extMvnDependency = new Dependency();
            extMvnDependency.setGroupId(extPom.getGroupId());
            extMvnDependency.setArtifactId(extArtifactId);
            extMvnDependency.setVersion(extPom.getVersion());
            Model clientPom = readPom(clientModulePomPath);
            clientPom.getDependencies().add(extMvnDependency);
            writePom(clientPom, clientModulePomPath);

            // Change output directory for the WAR to allow builder return link to download WAR.
            fixWarPlugin(clientModulePomPath);

            // Add sources from user's project to allow code server access it.
            fixGwtSources(clientModulePomPath, extArtifactId);

            // -----------------------------------------------------------
            // Add GWT-module into IDEPlatform.gwt.xml.
            // -----------------------------------------------------------
            final String gwtModuleDependency = "\t<inherits name='com.codenvy.ide.extension.demo.Demo'/>";
            Path gwtModuleDescriptorPath = appDirPath.resolve(CLIENT_MODULE_DIR_NAME)
                                                     .resolve("src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml");
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            // insert extension dependency as last entry
            int i = 0, lastInheritsLine = 0;
            for (String str : content) {
                i++;
                if (str.contains("<inherits")) {
                    lastInheritsLine = i;
                }
            }
            content.add(lastInheritsLine, SUPER_DEV_MODE_DIRECTIVE);
            content.add(lastInheritsLine, gwtModuleDependency);
            Files.write(gwtModuleDescriptorPath, content, UTF_8);


            // create symbolic links to user's project sources to allow code server always get the actual sources
            Path extensionDirInFSRoot = Paths.get(wsMountPath + project.getPath());
            if (!extensionDirInFSRoot.isAbsolute()) {
                extensionDirInFSRoot = extensionDirInFSRoot.toAbsolutePath();
            }
            extensionDirInFSRoot = extensionDirInFSRoot.normalize();

            // replace src and pom.xml by sym-links to appropriate src and pom.xml in fs-root directory
            deleteRecursive(extensionPath.resolve("src").toFile());
            Files.delete(extensionPath.resolve("pom.xml"));
            Files.createSymbolicLink(extensionPath.resolve("src"), extensionDirInFSRoot.resolve("src"));
            Files.createSymbolicLink(extensionPath.resolve("pom.xml"), extensionDirInFSRoot.resolve("pom.xml"));

            // -----------------------------------------------------------
            // Build project.
            // -----------------------------------------------------------
            File zippedProjectFile = tempDir.toPath().resolve("project.zip").toFile();
            zipDir(appDirPath.toString(), appDirPath.toFile(), zippedProjectFile, ANY_FILTER);
            final String buildId = build(zippedProjectFile);


            // -----------------------------------------------------------
            // Run code server.
            // -----------------------------------------------------------
            Process codeServerProcess = runCodeServer(appDirPath, extensionDirInFSRoot);

            final String status = startCheckingBuildStatus(buildId);
            BuildStatusBean buildStatus = JsonHelper.fromJson(status, BuildStatusBean.class, null);
            if (buildStatus.getStatus() != Status.SUCCESSFUL) {
                throw new ExtensionLauncherException(String.format("Unable to build application %s. %s ", project.getName(),
                                                                   buildStatus.getError()));
            }

            // -----------------------------------------------------------
            // Run Tomcat.
            // -----------------------------------------------------------
            Process tomcatProcess = runTomcat(tempDir, new URL(buildStatus.getDownloadUrl()));

            // TODO wait while Tomcat & code server will start and check that they started successfully

            final String appId = generate("app-", 16);
            applications.put(appId, new CodenvyExtensionResources(appId, tempDir, tomcatProcess, codeServerProcess));
            return appId;
        } catch (Exception e) {
            if (tempDir != null && tempDir.exists()) {
                deleteRecursive(tempDir);
            }
            throw new ExtensionLauncherException(String.format("Unable to launch application %s. ", project.getName()));
        }
    }

    private void fixWarPlugin(Path pomPath) {
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
            // TODO
        }
    }

    private void fixGwtSources(Path pomPath, String extensionModuleName) {
        // fix known bug https://jira.codehaus.org/browse/MGWT-332
        try {
            Model clientPom = readPom(pomPath);
            // Build clientPomBuild = clientPom.getBuild();
            // Map<String, Plugin> clientPomPlugins = clientPomBuild.getPluginsAsMap();
            // Plugin buildHelperPlugin = clientPomPlugins.get("org.codehaus.mojo:build-helper-maven-plugin");
            //
            // PluginExecution execution = new PluginExecution();
            // execution.setId("add-extension-sources");
            // execution.setPhase("generate-sources");
            // execution.addGoal("add-source");

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
                                                    + "<sources><source>../%s/src/main/java</source></sources>"
                                                    + "<resources><resource>../%s/src/main/r</resource></resources>"
                                                    + "</configuration>", extensionModuleName, extensionModuleName);
            Xpp3Dom configuration = Xpp3DomBuilder.build(new StringReader(confString));
            execution.setConfiguration(configuration);

            // buildHelperPlugin.addExecution(execution);
            // clientPomBuild.setPlugins(new ArrayList(clientPomPlugins.values()));
            superDevModeProfile.getBuild().setPlugins(new ArrayList(plugins.values()));
            writePom(clientPom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            // TODO
        }
    }

    /**
     * Stop application.
     * 
     * @param appId identifier of application to stop
     * @throws ExtensionLauncherException if error occurred while stopping an application
     */
    public void stopExtension(String appId) throws ExtensionLauncherException {
        CodenvyExtensionResources app = applications.get(appId);
        if (app == null) {
            throw new ExtensionLauncherException(String.format("Unable to stop application %s. Application not found. ", appId));
        }
        app.tomcatProcess.destroy();
        app.codeServerProcess.destroy();
        try {
            app.tomcatProcess.waitFor();
            app.codeServerProcess.waitFor();
        } catch (InterruptedException e) {
            // do nothing
        }
        deleteRecursive(app.tempDir);
        applications.remove(appId);
    }

    /** @see org.picocontainer.Startable#start() */
    @Override
    public void start() {
    }

    /** @see org.picocontainer.Startable#stop() */
    @Override
    public void stop() {
        for (String appId : applications.keySet()) {
            try {
                stopExtension(appId);
            } catch (Exception e) {
                LOG.error("Unable to stop Codenvy extension {}.", appId, e);
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
                // do nothing
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

    private String result(String buildID) throws IOException, ExtensionLauncherException {
        URL url = new URL(baseURL + "/builder/maven/result/" + buildID);
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
            throw new ExtensionLauncherException(String.format("Unable to build project. "));
        }
    }

    private String deploy(File zippedProjectFile) throws ExtensionLauncherException {
        try {
            return run(new URL(baseURL + "/builder/maven/deploy"), new FileInputStream(zippedProjectFile));
        } catch (Exception e) {
            throw new ExtensionLauncherException(String.format("Unable to build project. "));
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

    private Process runCodeServer(Path appDir, Path extensionDirInFSRoot) throws ExtensionLauncherException {
        try {
            // TODO clean compile it's a temporary fix to get IDEInjector.java in target folder
            // TODO consider to add GWT maven plugin to pom.xml if need to set additional configuration
            // TODO set code server's temp directory (not system /temp)
            ProcessBuilder processBuilder =
                                            new ProcessBuilder(getMavenExecCommand(), "clean", "compile",
                                                               "org.codehaus.mojo:gwt-maven-plugin:2.5.1:run-codeserver",
                                                               "-PdevMode").directory(appDir.resolve(CLIENT_MODULE_DIR_NAME)
                                                                                            .toFile());
            // TODO only for debug purpose
            processBuilder.redirectOutput(appDir.resolve("codeServerOut.txt").toFile());

            return processBuilder.start();
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch application. ");
        }
    }

    private Process runTomcat(File tempDir, URL ideWarUrl) throws ExtensionLauncherException {
        InputStream tomcatBundleStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/tomcat/tomcat.zip");
        if (tomcatBundleStream == null) {
            throw new ExtensionLauncherException("Can't find Tomcat package.");
        }

        try {
            File tomcatDir = createTempDirectory(tempDir, "tomcat-");
            unzip(tomcatBundleStream, tomcatDir);
            File ideWar = downloadFile(new File(tomcatDir.getPath() + PS + "webapps"), "app-", ".war", ideWarUrl);
            ideWar.renameTo(tomcatDir.toPath().resolve("webapps/IDE.war").toFile());

            final Path catalinaPath = tomcatDir.toPath().resolve("bin/catalina.sh");
            Files.setPosixFilePermissions(catalinaPath, PosixFilePermissions.fromString("rwxr--r--"));
            return new ProcessBuilder(catalinaPath.toString(), "run").start();
        } catch (IOException e) {
            throw new ExtensionLauncherException("Unable to launch application. ");
        }
    }

    private String getMavenExecCommand() {
        final File mvnHome = getMavenHome();
        if (mvnHome != null) {
            final String mvn = "bin" + File.separatorChar + "mvn";
            return new File(mvnHome, mvn).getAbsolutePath(); // use Maven home directory if it's set
        } else {
            return "mvn"; // otherwise 'mvn' should be in PATH variable
        }
    }

    private File getMavenHome() {
        final String m2HomeEnv = System.getenv("M2_HOME");
        if (m2HomeEnv == null) {
            return null;
        }
        final File m2Home = new File(m2HomeEnv);
        return m2Home.exists() ? m2Home : null;
    }

    private class CodenvyExtensionResources {
        final String id;
        File         tempDir;
        Process      tomcatProcess;
        Process      codeServerProcess;

        CodenvyExtensionResources(String id, File tempDir, Process tomcatProcess, Process codeServerProcess) {
            this.id = id;
            this.tempDir = tempDir;
            this.tomcatProcess = tomcatProcess;
            this.codeServerProcess = codeServerProcess;
        }
    }

}
