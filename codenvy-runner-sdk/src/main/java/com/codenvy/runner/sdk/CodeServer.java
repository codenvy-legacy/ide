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
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.ide.commons.GwtXmlUtils;
import com.codenvy.ide.commons.MavenUtils;
import com.codenvy.ide.commons.ZipUtils;

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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.zip.ZipFile;

/**
 * GWT code server. Concrete implementations provide an implementation of methods
 * thereby controlling how the GWT code server will run, stop, get log files content.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
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
                                     Utils.ExtensionDescriptor extensionDescriptor, Path projectSourcesPath,
                                     ZipFile warFile) throws RunnerException {
        final Path javaParserWorkerPath;
        try {
            final Path warDirPath = workDirPath.resolve("war");
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), warDirPath.toFile());

            MavenUtils.addDependencyToPom(warDirPath.resolve("pom.xml"), extensionDescriptor.groupId,
                                          extensionDescriptor.artifactId,
                                          extensionDescriptor.version);
            GwtXmlUtils.inheritGwtModule(MavenUtils.findFile(SDKRunner.IDE_GWT_XML_FILE_NAME, warDirPath),
                                         extensionDescriptor.gwtModuleName);

            setCodeServerConfiguration(warDirPath.resolve("pom.xml"), workDirPath, runnerConfiguration);

            final Path extDirPath = Files.createDirectory(workDirPath.resolve("ext"));
            Files.createSymbolicLink(extDirPath.resolve("src"), projectSourcesPath.resolve("src"));
            Files.createSymbolicLink(extDirPath.resolve("pom.xml"), projectSourcesPath.resolve("pom.xml"));

            javaParserWorkerPath = workDirPath.resolve("javaParserWorker");
            Utils.unzip(new File(warFile.getName()), Paths.get("_app/javaParserWorker"), javaParserWorkerPath.toFile());
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        final CodeServerProcess codeServerProcess;
        if (SystemInfo.isUnix()) {
            codeServerProcess = startUnix(workDirPath.toFile(), runnerConfiguration, javaParserWorkerPath);
        } else {
            codeServerProcess = startWindows(workDirPath.toFile(), runnerConfiguration, javaParserWorkerPath);
        }
        return codeServerProcess;
    }

    // *nix

    private CodeServerProcess startUnix(File codeServerWorkDir, SDKRunnerConfiguration runnerConfiguration,
                                        Path javaParserWorkerPath)
            throws RunnerException {
        java.io.File startUpScriptFile = genStartUpScriptUnix(codeServerWorkDir);
        return new CodeServerProcess(runnerConfiguration.getCodeServerBindAddress(),
                                     runnerConfiguration.getCodeServerPort(), startUpScriptFile, codeServerWorkDir,
                                     pidTaskExecutor, javaParserWorkerPath);
    }

    /**
     * Set the GWT code server configuration in the specified pom.xml file.
     *
     * @param pomPath
     *         path to pom.xml that stores code server's configuration
     * @param workDir
     *         code server working directory is the root of the directory tree where the code server will write
     *         compiler output. If not supplied, a system temporary directory will be used
     * @param runnerConfiguration
     * @throws RunnerException
     *         if any error occurred while writing code server's configuration
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setCodeServerConfiguration(Path pomPath, Path workDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        final String confWorkDir = workDir == null ? "" : "<codeServerWorkDir>" + workDir + "</codeServerWorkDir>";

        final String bindAddress = runnerConfiguration.getCodeServerBindAddress();
        final String confBindAddress = bindAddress == null ? "" : "<bindAddress>" + bindAddress + "</bindAddress>";

        final int port = runnerConfiguration.getCodeServerPort();
        final String confPort = port == -1 ? "" : "<codeServerPort>" + port + "</codeServerPort>";

        final String codeServerConf =
                String.format("<configuration>%s%s%s</configuration>", confWorkDir, confBindAddress, confPort);

        try {
            Xpp3Dom additionalConfiguration = Xpp3DomBuilder.build(new StringReader(codeServerConf));
            Model pom = MavenUtils.readPom(pomPath);
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));
            MavenUtils.writePom(pom, pomPath);
        } catch (XmlPullParserException | IOException e) {
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
                .format("mvn clean generate-sources gwt:run-codeserver -Dgwt.module=com.codenvy.ide.IDEPlatform -P%s " +
                        "> stdout.log &\n",
                        ADD_SOURCES_PROFILE_ID);
    }

    // Windows

    private CodeServerProcess startWindows(java.io.File codeServerWorkDir, SDKRunnerConfiguration runnerConfiguration,
                                           Path javaParserWorkerPath) throws RunnerException {
        throw new UnsupportedOperationException();
    }

    public static class CodeServerProcess {
        private final String          bindAddress;
        private final int             port;
        private final ExecutorService pidTaskExecutor;
        private final ExecutorService copyDirExecutor;
        private final Path            javaParserWorkerPath;
        private final File            startUpScriptFile;
        private final File            workDir;
        private int pid = -1;

        protected CodeServerProcess(String bindAddress, int port, File startUpScriptFile, File workDir,
                                    ExecutorService pidTaskExecutor, Path javaParserWorkerPath) {
            this.bindAddress = bindAddress;
            this.port = port;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            this.pidTaskExecutor = pidTaskExecutor;
            copyDirExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("CopyDir", true));
            this.javaParserWorkerPath = javaParserWorkerPath;
        }

        public void start() throws RunnerException {
            if (pid != -1) {
                throw new IllegalStateException("Code server process is already started");
            }

            try {
                Runtime.getRuntime().exec(new CommandLine(startUpScriptFile.getAbsolutePath())
                                                  .toShellCommand(), null, workDir);

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

                copyDirExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new WatchDir(workDir.toPath(), javaParserWorkerPath,
                                         "com.codenvy.ide.IDEPlatform/compile-*/war/_app").processEvents();
                        } catch (IOException e) {
                        }
                    }
                });

                // TODO: wait for pre-compile or use gwt-maven-plugin 2.6.0-rc1

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

            copyDirExecutor.shutdownNow();

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
                throw new RunnerException("Unable to get code server logs. " + body == null ? "" : body);
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
