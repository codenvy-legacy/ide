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
package com.codenvy.runner.sdk;

import com.codenvy.api.core.util.CommandLine;
import com.codenvy.api.core.util.ProcessUtil;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.maven.tools.MavenUtils;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * GWT code server. Concrete implementations provide an implementation of methods
 * thereby controlling how the GWT code server will run, stop, get log files content.
 *
 * @author Artem Zatsarynnyy
 */
public class CodeServer {
    private static final Logger LOG                    = LoggerFactory.getLogger(CodeServer.class);
    /** Id of Maven POM profile used to add (re)sources of custom extension to code server recompilation process. */
    private static final String ADD_SOURCES_PROFILE_ID = "customExtensionSources";
    protected final ExecutorService pidTaskExecutor;

    public CodeServer() {
        pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("CodeServer-", true));
    }

    public CodeServerProcess prepare(Path workDirPath, SDKRunnerConfiguration runnerConfiguration,
                                     Utils.ExtensionDescriptor extensionDescriptor) throws RunnerException {
        try {
            final Path warDirPath = workDirPath.resolve("war");
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), warDirPath.toFile());

            MavenUtils.addDependency(warDirPath.resolve("pom.xml").toFile(), extensionDescriptor.groupId,
                                     extensionDescriptor.artifactId,
                                     extensionDescriptor.version,
                                     null);
            GwtXmlUtils.inheritGwtModule(IoUtil.findFile(SDKRunner.IDE_GWT_XML_FILE_NAME, warDirPath.toFile()).toPath(),
                                         extensionDescriptor.gwtModuleName);

            setCodeServerConfiguration(warDirPath.resolve("pom.xml"), workDirPath, runnerConfiguration);

            // Create symbolic links to the project's sources in order
            // to provide actual sources to code server at any time.
            final Path extDirPath = Files.createDirectory(workDirPath.resolve("ext"));
            Path projectSourcesPath = Utils.getMountPath().resolve(runnerConfiguration.getRequest().getProject());
            if (!projectSourcesPath.isAbsolute()) {
                projectSourcesPath = projectSourcesPath.toAbsolutePath();
            }
            projectSourcesPath = projectSourcesPath.normalize();
            Files.createSymbolicLink(extDirPath.resolve("src"), projectSourcesPath.resolve("src"));
            Files.createSymbolicLink(extDirPath.resolve("pom.xml"), projectSourcesPath.resolve("pom.xml"));
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final CodeServerProcess codeServerProcess;
        if (SystemInfo.isUnix()) {
            codeServerProcess = startUnix(workDirPath.toFile(), runnerConfiguration);
        } else {
            codeServerProcess = startWindows(workDirPath.toFile(), runnerConfiguration);
        }
        return codeServerProcess;
    }

    // *nix

    private CodeServerProcess startUnix(java.io.File codeServerWorkDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        java.io.File startUpScriptFile = genStartUpScriptUnix(codeServerWorkDir);
        return new CodeServerProcess(runnerConfiguration.getCodeServerBindAddress(),
                                     runnerConfiguration.getCodeServerPort(), startUpScriptFile, codeServerWorkDir,
                                     pidTaskExecutor);
    }

    /** Set the GWT code server configuration in the specified pom.xml file. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setCodeServerConfiguration(Path pomPath, Path workDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        final String confWorkDir = workDir == null ? "" : "<codeServerWorkDir>" + workDir + "</codeServerWorkDir>";

        final String bindAddress = runnerConfiguration.getCodeServerBindAddress();
        final String confBindAddress = bindAddress == null ? "" : "<bindAddress>" + bindAddress + "</bindAddress>";

        final int port = runnerConfiguration.getCodeServerPort();
        final String confPort = port == -1 ? "" : "<codeServerPort>" + port + "</codeServerPort>";

        final String codeServerConf = String.format("<configuration>%s%s%s</configuration>", confWorkDir, confBindAddress, confPort);

        try {
            Model pom;
            try (Reader reader = Files.newBufferedReader(pomPath, Charset.forName("UTF-8"))) {
                pom = MavenUtils.readModel(reader);
            } catch (IOException e) {
                throw new RunnerException(String.format("Error occurred while parsing pom.xml: %s", e.getMessage()), e);
            }
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom additionalConfiguration;
            try {
                additionalConfiguration = Xpp3DomBuilder.build(new StringReader(codeServerConf));
            } catch (XmlPullParserException e) {
                throw new RunnerException(e);
            }
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));
            try (Writer writer = Files.newBufferedWriter(pomPath, Charset.forName("UTF-8"))) {
                MavenUtils.writeModel(pom, writer);
            }
        } catch (IOException e) {
            throw new RunnerException(e);
        }
    }

    private java.io.File genStartUpScriptUnix(java.io.File workDir) throws RunnerException {
        final String startupScript = "#!/bin/sh\n" +
                                     "cd war\n" +
                                     codeServerUnix() +
                                     "PID=$!\n" +
                                     "echo \"$PID\" >> ../run.pid\n" +
                                     "wait $PID";
        final java.io.File startUpScriptFile = new java.io.File(workDir, "startup.sh");
        try {
            Files.write(startUpScriptFile.toPath(), startupScript.getBytes());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new RunnerException("Unable to update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private String codeServerUnix() {
        return String
                .format("mvn clean generate-sources gwt:run-codeserver -Dgwt.module=com.codenvy.ide.IDEPlatform -P%s > stdout.log &\n",
                        ADD_SOURCES_PROFILE_ID);
    }

    // Windows

    private CodeServerProcess startWindows(java.io.File codeServerWorkDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        throw new UnsupportedOperationException();
    }

    public static class CodeServerProcess {
        private final String          bindAddress;
        private final int             port;
        private final ExecutorService pidTaskExecutor;
        private final java.io.File    startUpScriptFile;
        private final java.io.File    workDir;
        private int pid = -1;

        protected CodeServerProcess(String bindAddress, int port, java.io.File startUpScriptFile, java.io.File workDir,
                                    ExecutorService pidTaskExecutor) {
            this.bindAddress = bindAddress;
            this.port = port;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            this.pidTaskExecutor = pidTaskExecutor;
        }

        public void start() throws RunnerException {
            if (pid != -1) {
                throw new IllegalStateException("Code server process is already started");
            }

            try {
                Runtime.getRuntime().exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null, workDir);
                pid = pidTaskExecutor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        final java.io.File pidFile = new java.io.File(workDir, "run.pid");
                        final Path pidPath = pidFile.toPath();
                        synchronized (this) {
                            while (!Files.isReadable(pidPath)) {
                                wait(100);
                            }
                        }
                        final BufferedReader pidReader = new BufferedReader(new FileReader(pidFile));
                        try {
                            return Integer.valueOf(pidReader.readLine());
                        } finally {
                            try {
                                pidReader.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }).get(5, TimeUnit.SECONDS);

                LOG.debug("Start GWT code server at port {}, working directory {}", port, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        public void stop() {
            if (pid == -1) {
                throw new IllegalStateException("Code server process is not started yet");
            }

            // Use ProcessUtil.kill(pid) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(pid);

            LOG.debug("Stop GWT code server at port {}, working directory {}", port, workDir);
        }

        public void getLogs(Appendable output) throws IOException, RunnerException {
            final String url = bindAddress + ':' + port + "/log/_app";
            final String logContent = sendGet(new URL(url.startsWith("http://") ? url : "http://" + url));
            output.append(String.format("%n====> GWT-code-server.log <====%n"));
            output.append(logContent);
            output.append(System.lineSeparator());
        }

        private String sendGet(URL url) throws IOException, RunnerException {
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("GET");
                int responseCode = http.getResponseCode();
                if (responseCode != 200) {
                    responseFail(http);
                }

                try (InputStream data = http.getInputStream()) {
                    return readBodyTagContent(data);
                }
            } finally {
                if (http != null) {
                    http.disconnect();
                }
            }
        }

        private String readBodyTagContent(InputStream stream) throws IOException {
            try {
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = domFactory.newDocumentBuilder();
                Document doc = builder.parse(stream);
                return doc.getElementsByTagName("body").item(0).getTextContent();
            } catch (ParserConfigurationException | SAXException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        private void responseFail(HttpURLConnection http) throws IOException, RunnerException {
            InputStream errorStream = null;
            try {
                int length = http.getContentLength();
                errorStream = http.getErrorStream();
                String body = null;
                if (errorStream != null) {
                    body = readBody(errorStream, length);
                }
                throw new RunnerException(String.format("Unable to get code server logs. %s", body == null ? "" : body));
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
}
