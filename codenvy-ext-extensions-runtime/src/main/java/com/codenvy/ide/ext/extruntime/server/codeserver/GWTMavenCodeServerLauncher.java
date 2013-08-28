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

import com.codenvy.ide.ext.extruntime.server.ExtensionLauncherException;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        try {
            setCodeServerConfiguration(configuration.getWorkDir().resolve("pom.xml"), configuration.getWorkDir(),
                                       configuration.getBindAddress(), configuration.getPort());
        } catch (IOException e) {
            throw new GWTCodeServerException("Unable to launch GWT code server: " + e.getMessage(), e);
        }

        // Call 'generate-sources' phase to generate 'IDEInjector.java' and 'ExtensionManager.java'.
        // For details, see com.codenvy.util.IDEInjectorGenerator and com.codenvy.util.ExtensionManagerGenerator.
        final String[] command = new String[]{
                getMavenExecCommand(),
                "generate-sources",
                "gwt:run-codeserver", // org.codehaus.mojo:gwt-maven-plugin should be described in a pom.xml
                "-P" + ADD_SOURCES_PROFILE};

        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(configuration.getWorkDir().toFile());
        processBuilder.redirectOutput(logFilePath.toFile());

        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new GWTCodeServerException("Unable to launch GWT code server: " + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLogs() throws GWTCodeServerException {
        try {
            final String url = configuration.getBindAddress() + ':' + configuration.getPort() + "/log/_app";
            return sendGet(new URL(url.startsWith("http://") ? url : "http://" + url));
        } catch (IOException | ExtensionLauncherException e) {
            throw new GWTCodeServerException("Unable to get GWT code server's logs: " + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
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
     * @throws IOException if any error occurred while writing a file
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setCodeServerConfiguration(Path pomPath, Path workDir, String bindAddress, int port) throws IOException {
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
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.", e);
        }
    }

    private static String sendGet(URL url) throws IOException, ExtensionLauncherException {
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
                return readBodyTagContent(data);
            } finally {
                data.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private static String readBody(InputStream input, int contentLength) throws IOException {
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

    private static void responseFail(HttpURLConnection http) throws IOException, ExtensionLauncherException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new ExtensionLauncherException(responseCode, "Unable to get logs. " + body == null ? "" : body);
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }

    private static String readBodyTagContent(InputStream stream) throws IOException {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            return doc.getElementsByTagName("body").item(0).getTextContent();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
