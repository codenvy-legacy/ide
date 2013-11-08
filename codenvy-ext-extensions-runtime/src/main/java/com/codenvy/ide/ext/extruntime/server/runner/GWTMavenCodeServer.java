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

import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.ide.ext.extruntime.server.Utils;

import org.apache.maven.model.*;
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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.codehaus.plexus.util.xml.Xpp3DomBuilder.build;

/**
 * Implementation of {@link GWTCodeServer} interface that uses GWT Maven plug-in.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GWTMavenCodeServer.java Jul 26, 2013 3:15:52 PM azatsarynnyy $
 */
public class GWTMavenCodeServer implements GWTCodeServer {
    /** Id of Maven profile that used to add (re)sources of custom's extension. */
    public static final  String ADD_SOURCES_PROFILE = "customExtensionSources";
    private static final Log    LOG                 = ExoLogger.getLogger(GWTMavenCodeServer.class);
    /** Process that represents a started GWT code server. */
    private Process                    process;
    /** Configuration for launching GWT code server. */
    private GWTCodeServerConfiguration configuration;

    /** {@inheritDoc} */
    @Override
    public void start(GWTCodeServerConfiguration configuration) throws RunnerException {
        this.configuration = configuration;
        Path pom = configuration.getWorkDir().resolve("pom.xml");
        try {
            setCodeServerConfiguration(pom, configuration.getWorkDir(),
                                       configuration.getBindAddress(), configuration.getPort());

            // Add sources from custom project to allow GWT code server access it.
            fixMGWT332Bug(pom, configuration.getCustomModuleName());
        } catch (IOException e) {
            throw new RunnerException("Unable to launch GWT code server: " + e.getMessage(), e);
        }

        // Invoke 'generate-sources' phase to generate 'IDEInjector.java' and 'ExtensionManager.java'.
        // For details, see com.codenvy.util.IDEInjectorGenerator and com.codenvy.util.ExtensionManagerGenerator.
        final String[] command = new String[]{
                getMavenExecCommand(), "generate-sources",
                "gwt:run-codeserver", // org.codehaus.mojo:gwt-builder-plugin should be already described in a pom.xml
                "-P" + ADD_SOURCES_PROFILE};

        ProcessBuilder processBuilder = new ProcessBuilder(command).directory(configuration.getWorkDir().toFile());
        processBuilder.redirectOutput(configuration.getWorkDir().resolve("code-server.log").toFile());

        try {
            this.process = processBuilder.start();
        } catch (IOException e) {
            throw new RunnerException("Unable to launch GWT code server: " + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getLogs() throws RunnerException, IOException {
        try {
            final String url = configuration.getBindAddress() + ':' + configuration.getPort() + "/log/_app";
            final String logContent = sendGet(new URL(url.startsWith("http://") ? url : "http://" + url));

            StringBuilder logs = new StringBuilder();
            logs.append("========> GWT-code-server.log <========");
            logs.append("\n\n");
            logs.append(logContent);
            logs.append("\n\n");

            return logs.toString();
        } catch (RunnerException e) {
            throw new RunnerException("Unable to get GWT code server's logs: " + e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        LOG.debug("Killing process tree");
        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
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
     * It's a workaround for known bug in GWT Maven plug-in. See the https://jira.codehaus.org/browse/MGWT-332 for
     * details.
     *
     * @throws java.io.IOException
     *         error occurred while reading or writing content of file
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void fixMGWT332Bug(Path pomPath, String extensionModuleName) throws IOException {
        Model pom = Utils.readPom(pomPath);
        List<Profile> profiles = pom.getProfiles();
        Profile profile = null;
        for (Profile curProfile : profiles) {
            if (curProfile.getId().equals(ADD_SOURCES_PROFILE)) {
                profile = curProfile;
            }
        }

        if (profile == null) {
            throw new IllegalStateException(String.format("Profile %s not found in %s.", ADD_SOURCES_PROFILE, pomPath));
        }

        Map<String, Plugin> plugins = profile.getBuild().getPluginsAsMap();
        Plugin buildHelperPlugin = plugins.get("org.codehaus.mojo:build-helper-builder-plugin");
        PluginExecution execution = buildHelperPlugin.getExecutionsAsMap().get("add-extension-sources");

        final String confString = String.format("<configuration>" +
                                                "  <sources>" +
                                                "    <source>../%1$s/src/main/java</source>" +
                                                "    <source>../%1$s/src/main/resources</source>" +
                                                "  </sources>" +
                                                "</configuration>", extensionModuleName);

        try {
            Xpp3Dom configuration = build(new StringReader(confString));
            execution.setConfiguration(configuration);
            profile.getBuild().setPlugins(new ArrayList(plugins.values()));

            Utils.writePom(pom, pomPath);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Error occurred while parsing pom.xml :" + e.getMessage(), e);
        }
    }

    /**
     * Set GWT Maven plug-in configuration in the specified pom.xml file, to set a code server configuration.
     *
     * @param pomPath
     *         path to pom.xml that stores code server's configuration
     * @param workDir
     *         code server working directory is the root of the directory tree where the code server will write
     *         compiler
     *         output. If
     *         not supplied, a system temporary directory will be used
     * @param port
     *         port on which code server will run. If -1 supplied, a default port will be 9876
     * @throws IOException
     *         if any error occurred while writing a file
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setCodeServerConfiguration(Path pomPath, Path workDir, String bindAddress, int port)
            throws IOException {
        final String workDirConf = workDir == null ? "" : "<codeServerWorkDir>" + workDir + "</codeServerWorkDir>";
        final String bindAddressConf = bindAddress == null ? "" : "<bindAddress>" + bindAddress + "</bindAddress>";
        final String portConf = port == -1 ? "" : "<codeServerPort>" + port + "</codeServerPort>";
        final String codeServerConf =
                String.format("<configuration>%s%s%s</configuration>", workDirConf, bindAddressConf, portConf);

        try {
            Xpp3Dom additionalConfiguration = build(new StringReader(codeServerConf));

            Model pom = Utils.readPom(pomPath);
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-builder-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));

            Utils.writePom(pom, pomPath);
        } catch (XmlPullParserException e) {
            throw new IllegalStateException("Can't parse pom.xml.", e);
        }
    }

    private static String sendGet(URL url) throws IOException, RunnerException {
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

    private static void responseFail(HttpURLConnection http) throws IOException, RunnerException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new RunnerException(responseCode, "Unable to get logs. " + body == null ? "" : body);
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
