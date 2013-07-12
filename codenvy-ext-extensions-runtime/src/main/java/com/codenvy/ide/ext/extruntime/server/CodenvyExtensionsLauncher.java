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
import com.codenvy.ide.commons.ParsingResponseException;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

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
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
 * @version $Id: CodenvyExtensionsLauncher.java Jul 7, 2013 3:17:41 PM azatsarynnyy $
 */
public class CodenvyExtensionsLauncher {
    /** System property that contains build server URL. */
    public static final String         BUILD_SERVER_BASE_URL = "exo.ide.builder.build-server-base-url";

    /** Logger. */
    private static final Log           LOG                   = ExoLogger.getLogger(CodenvyExtensionsLauncher.class);

    private static MavenXpp3Reader     pomReader             = new MavenXpp3Reader();
    private static MavenXpp3Writer     pomWriter             = new MavenXpp3Writer();

    /** Base URL of build server. */
    private final String               baseURL;

    private final Map<String, Process> applications;

    public CodenvyExtensionsLauncher(InitParams initParams) {
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
     * Constructs a new {@link CodenvyExtensionsLauncher} with the provided URL of build server.
     * 
     * @param baseURL base URL of build server
     */
    protected CodenvyExtensionsLauncher(String baseURL) {
        if (baseURL == null || baseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string. ");
        }
        this.baseURL = baseURL;
        applications = new ConcurrentHashMap<String, Process>();
    }

    /**
     * Launch Codenvy extension in a separate Codenvy instance.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of a project we want to launch
     * @return launched app id
     * @throws IOException if any i/o errors occur
     * @throws BuilderException if build request was rejected by remote build server
     * @throws VirtualFileSystemException if any error in VFS
     * @throws XmlPullParserException if any error occurred while reading/writing pom.xml
     * @throws ParsingResponseException if any error occurred while parsing JSON
     * @throws InterruptedException if any error occurred while checking build status
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String launchExtension(VirtualFileSystem vfs, String projectId) throws IOException,
                                                                          BuilderException,
                                                                          VirtualFileSystemException,
                                                                          XmlPullParserException,
                                                                          ParsingResponseException, InterruptedException {
        BuildStatusBean buildStatus = null;
        File tempDir = createTempDirectory("CodenvyExtension-");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final File appDir = createTempDirectory(tempDir, "project-");
            final String clientModuleName = "codenvy-ide-client";
            final String clientModulePomPath = appDir.getPath() + "/" + clientModuleName + "/pom.xml";

            Project project = (Project)vfs.getItem(projectId, false, PropertyFilter.NONE_FILTER);
            Item pomFile = vfs.getItemByPath(project.getName() + "/pom.xml", null, false, PropertyFilter.NONE_FILTER);
            InputStream extPomContent = vfs.getContent(pomFile.getId()).getStream();
            Model extPom = pomReader.read(extPomContent, false);
            final String extArtifactId = extPom.getArtifactId();

            // -----------------------------------------------------------
            // Unpack Codenvy Platform sources & user's
            // extension project into temporary directory.
            // -----------------------------------------------------------
            InputStream codenvySourcesStream = contextClassLoader.getResourceAsStream("conf/CodenvyPlatform.zip");
            if (codenvySourcesStream == null) {
                throw new InvalidArgumentException("Can't find Codenvy Platform sources package.");
            }
            unzip(codenvySourcesStream, appDir);
            final File extDir = new File(appDir.getPath() + "/" + extArtifactId);
            unzip(vfs.exportZip(projectId).getStream(), extDir);

            // -----------------------------------------------------------
            // Use special 'clean' pom.xml for parent & client module
            // to build Codenvy Platform.
            // -----------------------------------------------------------
            Files.move(Paths.get(appDir.getPath() + "/platform-pom.xml"), Paths.get(appDir.getPath() + "/pom.xml"), REPLACE_EXISTING);
            Files.move(Paths.get(appDir.getPath() + "/" + clientModuleName + "/platform-pom.xml"), Paths.get(clientModulePomPath),
                       REPLACE_EXISTING);

            // -----------------------------------------------------------
            // Use special ide-configuration.xml with removed
            // unnecessary components.
            // -----------------------------------------------------------
            InputStream confStream = contextClassLoader.getResourceAsStream("conf/tomcat/ide-configuration.xml");
            Files.copy(confStream,
                       Paths.get(appDir.getPath() + "/" + clientModuleName + "/src/main/webapp/WEB-INF/classes/conf/ide-configuration.xml"),
                       StandardCopyOption.REPLACE_EXISTING);

            // -----------------------------------------------------------
            // Add extension as maven-module into parent pom.xml.
            // -----------------------------------------------------------
            Model parentPom = readPom(appDir.getPath() + "/pom.xml");
            final List<String> parentPomModulesList = parentPom.getModules();
            int n = 0;
            for (String module : parentPomModulesList) {
                // insert extension module before client module
                if (module.equals(clientModuleName)) {
                    parentPom.getModules().add(n, extArtifactId);
                    break;
                }
                n++;
            }
            writePom(parentPom, appDir.getPath() + "/pom.xml");

            // -----------------------------------------------------------
            // Add extension as dependency into client module's pom.xml.
            // -----------------------------------------------------------
            Dependency extMvnDependency = new Dependency();
            extMvnDependency.setGroupId(extPom.getGroupId());
            extMvnDependency.setArtifactId(extArtifactId);
            extMvnDependency.setVersion(extPom.getVersion());
            Model clientPom = readPom(clientModulePomPath);
            clientPom.getDependencies().add(extMvnDependency);

            // -----------------------------------------------------------
            // Change output directory for the WAR.
            // -----------------------------------------------------------
            Build clientPomBuild = clientPom.getBuild();
            Map<String, Plugin> clientPomPlugins = clientPomBuild.getPluginsAsMap();
            Plugin warPlugin = clientPomPlugins.get("org.apache.maven.plugins:maven-war-plugin");
            Xpp3Dom warPluginConfiguration = Xpp3DomBuilder.build(new StringReader("<configuration><outputDirectory>./target/</outputDirectory></configuration>"));
            warPlugin.setConfiguration(warPluginConfiguration);
            clientPomBuild.setPlugins(new ArrayList(clientPomPlugins.values()));
            writePom(clientPom, clientModulePomPath);

            // -----------------------------------------------------------
            // Add GWT-module into IDEPlatform.gwt.xml.
            // -----------------------------------------------------------
            final String gwtModuleDependency = "\t<inherits name='com.codenvy.ide.extension.demo.Demo'/>";
            final String demoGWTModuleDescriptorPath = appDir.getPath()
                                                       + "/" + clientModuleName + "/src/main/resources/com/codenvy/ide/IDEPlatform.gwt.xml";
            Path gwtModuleDescriptorPath = Paths.get(demoGWTModuleDescriptorPath);
            List<String> content = Files.readAllLines(gwtModuleDescriptorPath, UTF_8);
            // insert extension dependency as last entry
            int i = 0, lastInheritsLine = 0;
            for (String str : content) {
                i++;
                if (str.contains("<inherits")) {
                    lastInheritsLine = i;
                }
            }
            content.add(lastInheritsLine, gwtModuleDependency);
            Files.write(gwtModuleDescriptorPath, content, UTF_8);

            // -----------------------------------------------------------
            // Build project.
            // -----------------------------------------------------------
            File zippedProjectFile = new File(tempDir.getPath() + "/project.zip");
            zipDir(appDir.getPath(), appDir, zippedProjectFile, ANY_FILTER);
            String buildId = run(new URL(baseURL + "/builder/maven/build"), new FileInputStream(zippedProjectFile));
            LOG.info("EVENT#project-built# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType() + "#");
            final String status = startCheckingBuildStatus(buildId, project.getName(), project.getProjectType());
            buildStatus = JsonHelper.fromJson(status, BuildStatusBean.class, null);
        } finally {
            deleteRecursive(tempDir);
        }

        InputStream tomcatStream = contextClassLoader.getResourceAsStream("conf/tomcat/tomcat.zip");
        if (tomcatStream == null) {
            throw new InvalidArgumentException("Can't find Tomcat package.");
        }
        File tomcatDir = createTempDirectory("tomcat-");
        unzip(tomcatStream, tomcatDir);

        File ideWar = downloadFile(new File(tomcatDir.getPath() + "/webapps"), "app-", ".war", new URL(buildStatus.getDownloadUrl()));
        ideWar.renameTo(new File(tomcatDir.getPath() + "/webapps/IDE.war"));

        // -----------------------------------------------------------
        // Copy additional resources into Tomcat.
        // -----------------------------------------------------------
        Map<String, String> additionalResources = new HashMap<String, String>();
        additionalResources.put("setenv.sh", "bin");
        additionalResources.put("logback.xml", "conf");
        additionalResources.put("jaas.conf", "conf");
        additionalResources.put("server.xml", "conf");
        additionalResources.put("userdb.war", "webapps");
        additionalResources.put("jul-to-slf4j.jar", "lib");
        additionalResources.put("log4j-1.2.16.jar", "lib");
        additionalResources.put("log4j-over-slf4j.jar", "lib");
        additionalResources.put("logback-classic.jar", "lib");
        additionalResources.put("logback-core.jar", "lib");
        additionalResources.put("slf4j-api.jar", "lib");
        for (Entry<String, String> entry : additionalResources.entrySet()) {
            InputStream stream = contextClassLoader.getResourceAsStream("conf/tomcat/" + entry.getKey());
            Files.copy(stream, Paths.get(tomcatDir.getPath() + "/" + entry.getValue() + "/" + entry.getKey()), REPLACE_EXISTING);
        }

        new ProcessBuilder("chmod", "+x", tomcatDir.getPath() + "/bin/catalina.sh").start();
        Process process = new ProcessBuilder(tomcatDir.getPath() + "/bin/catalina.sh", "run").start();

        String appId = generate("app-", 16);
        applications.put(appId, process);
        return appId;
    }

    /**
     * Stop application.
     * 
     * @param appId identifier of application to stop
     * @throws Exception if error occurred while stopping an application
     */
    public void stop(String appId) throws Exception {
        Process process = applications.get(appId);
        if (process != null) {
            process.destroy();
            applications.remove(appId);
        } else {
            throw new Exception("Unable stop application. Application '" + appId + "' not found. ");
        }
    }

    private static Model readPom(String path) throws IOException, XmlPullParserException {
        Path pomPath = Paths.get(path);
        return pomReader.read(Files.newInputStream(pomPath), false);
    }

    private static void writePom(Model pom, String path) throws IOException {
        Path pomPath = Paths.get(path);
        pomWriter.write(Files.newOutputStream(pomPath), pom);
    }

    private String startCheckingBuildStatus(String buildId, String projectName, String projectType) throws IOException,
                                                                                                   BuilderException, InterruptedException {
        String status = "";
        for (;;) {
            status = status(buildId);
            if (!status.contains("\"status\":\"IN_PROGRESS\"")) {
                LOG.info("EVENT#build-finished# PROJECT#" + projectName + "# TYPE#" + projectType + "#");
                return status;
            }
            Thread.sleep(1000);
        }
    }

    private String run(URL url, InputStream zippedProject) throws IOException, VirtualFileSystemException, BuilderException {
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
     * Get result of build.
     * 
     * @param buildID ID of build need to check
     * @return string that contains description of current status of build in JSON format. Do nothing with such string just re-send result
     *         to client
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    private String status(String buildID) throws IOException, BuilderException {
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

    /**
     * Add authentication info to the request. By default do nothing. May be reimplemented for particular authentication scheme.
     * 
     * @param http HTTP connection to add authentication info, e.g. Basic authentication headers.
     * @throws IOException if any i/o errors occur
     */
    protected void authenticate(HttpURLConnection http) throws IOException {
    }

    private void fail(HttpURLConnection http) throws IOException, BuilderException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new BuilderException(responseCode, body, body != null ? http.getContentType() : null);
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

}
