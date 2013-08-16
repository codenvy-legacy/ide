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
package com.codenvy.ide.ext.extruntime.server.codeserver;

import com.codenvy.ide.ext.extruntime.server.Utils;
import com.codenvy.ide.ext.extruntime.server.tools.ProcessUtil;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static com.codenvy.ide.ext.extruntime.server.ExtensionLauncher.ADD_SOURCES_PROFILE;
import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;

/**
 * Implementation of {@link GWTCodeServerLauncher} interface that that uses GWT Maven plug-in.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GWTMavenCodeServerLauncher.java Jul 26, 2013 3:15:52 PM azatsarynnyy $
 */
public class GWTMavenCodeServerLauncher implements GWTCodeServerLauncher {
    private static final Log           LOG = ExoLogger.getLogger(GWTMavenCodeServerLauncher.class);

    /** Process that represents a started GWT code server. */
    private Process                    process;

    /** Path to code server's log file. */
    private Path                       logFilePath;

    private GWTCodeServerConfiguration configuration;

    /** {@inheritDoc} */
    @Override
    public void start(GWTCodeServerConfiguration configuration) throws GWTCodeServerException {
        this.configuration = configuration;
        this.logFilePath = configuration.getWorkDir().resolve("code-server.log");
        setCodeServerConfiguration(configuration.getWorkDir().resolve("pom.xml"), configuration.getWorkDir(),
                                   configuration.getBindAddress(), configuration.getPort());

        // need 'clean compile' to get 'IDEInjector.java' and 'ExtensionManager.java' in a target folder
        final String[] command = new String[]{
                getMavenExecCommand(),
                "clean",
                "compile",
                "gwt:run-codeserver", // org.codehaus.mojo:gwt-maven-plugin should be described in a pom.xml
                "-P" + ADD_SOURCES_PROFILE};

        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(configuration.getWorkDir().toFile());
        processBuilder.redirectOutput(logFilePath.toFile());

        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new GWTCodeServerException("Unable to start code server.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLogs() throws GWTCodeServerException {
        try {
            // It should work fine for the files less than 2GB (Integer.MAX_VALUE).
            // One recompiling procedure writes about 1KB output information to logs.
            return new String(Files.readAllBytes(logFilePath));
        } catch (IOException e) {
            throw new GWTCodeServerException("Unable to get code server's logs.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // TODO
        // Use com.codenvy.api.tools.ProcessUtil from 'codenvy-organization-api' project when it finished.

        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
        LOG.debug("Killing process tree");
        ProcessUtil.kill(process);
    }

    /** {@inheritDoc} */
    @Override
    public GWTCodeServerConfiguration getConfiguration() {
        return configuration;
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

    /**
     * Set GWT Maven plug-in configuration in the specified pom.xml file, to set a code server configuration.
     * 
     * @param pomPath pom.xml path
     * @param workDir code server working directory is the root of the directory tree where the code server will write compiler output. If
     *            not supplied, a system temporary directory will be used
     * @param port port on which code server will run. If -1 supplied, a default port will be 9876
     * @throws IllegalStateException if any error occurred while writing a file
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setCodeServerConfiguration(Path pomPath, Path workDir, String bindAddress, int port) {
        final String workDirConf = workDir == null ? "" : "<codeServerWorkDir>" + workDir + "</codeServerWorkDir>";
        final String bindAddressConf = bindAddress == null ? "" : "<bindAddress>" + bindAddress + "</bindAddress>";
        final String portConf = port == -1 ? "" : "<codeServerPort>" + port + "</codeServerPort>";
        final String codeServerConf = String.format("<configuration>%s%s%s</configuration>", workDirConf, bindAddressConf, portConf);
        try {
            Xpp3Dom additionalConfiguration = build(new StringReader(codeServerConf));

            Model pom = Utils.readPom(pomPath);
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));

            Utils.writePom(pom, pomPath);
        } catch (IOException | XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.");
        }
    }
}
