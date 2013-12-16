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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

/**
 * //
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public class CodeServer {
    private static final Logger LOG                    = LoggerFactory.getLogger(CodeServer.class);
    /** Id of Maven profile that used to add (re)sources of custom's extension. */
    private static final String ADD_SOURCES_PROFILE_ID = "customExtensionSources";
    protected final ExecutorService pidTaskExecutor;

    public CodeServer() {
        pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("CodeServer-", true));
    }

    CodeServerProcess prepare(Path workDirPath, SDKRunnerConfiguration runnerConfiguration,
                              SDKRunner.ExtensionDescriptor extensionDescriptor, Path projectSourcesPath)
            throws RunnerException {
        try {
            final Path warDirPath = workDirPath.resolve("war");
            ZipUtils.unzip(Utils.getCodenvyPlatformBinaryDistribution().openStream(), warDirPath.toFile());

            Utils.addDependencyToPom(warDirPath.resolve("pom.xml"), extensionDescriptor.groupId,
                                     extensionDescriptor.artifactId,
                                     extensionDescriptor.version);
            GwtXmlUtils.inheritGwtModule(Utils.findFile(Utils.IDE_GWT_XML_FILE_NAME, warDirPath),
                                         extensionDescriptor.gwtModuleName);

            setCodeServerConfiguration(warDirPath.resolve("pom.xml"), workDirPath, null, -1);

            final Path extDirPath = Files.createDirectory(workDirPath.resolve("ext"));
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
        return new CodeServerProcess(runnerConfiguration.getCodeServerPort(), startUpScriptFile, codeServerWorkDir,
                                     pidTaskExecutor);
    }

    /**
     * Set the GWT code server configuration in the specified pom.xml file.
     *
     * @param pomPath
     *         path to pom.xml that stores code server's configuration
     * @param workDir
     *         code server working directory is the root of the directory tree where the code server will write
     *         compiler output. If not supplied, a system temporary directory will be used
     * @param port
     *         port on which code server will run. If -1 supplied, a default port will be 9876
     * @throws RunnerException
     *         if any error occurred while writing code server configuration
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setCodeServerConfiguration(Path pomPath, Path workDir, String bindAddress, int port)
            throws RunnerException {
        final String confWorkDir = workDir == null ? "" : "<codeServerWorkDir>" + workDir + "</codeServerWorkDir>";
        final String confBindAddress = bindAddress == null ? "" : "<bindAddress>" + bindAddress + "</bindAddress>";
        final String confPort = port == -1 ? "" : "<codeServerPort>" + port + "</codeServerPort>";
        final String codeServerConf =
                String.format("<configuration>%s%s%s</configuration>", confWorkDir, confBindAddress, confPort);

        try {
            Xpp3Dom additionalConfiguration = Xpp3DomBuilder.build(new StringReader(codeServerConf));
            Model pom = Utils.readPom(pomPath);
            Build build = pom.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin gwtPlugin = plugins.get("org.codehaus.mojo:gwt-maven-plugin");
            Xpp3Dom existingConfiguration = (Xpp3Dom)gwtPlugin.getConfiguration();
            Xpp3Dom mergedConfiguration = Xpp3DomUtils.mergeXpp3Dom(existingConfiguration, additionalConfiguration);
            gwtPlugin.setConfiguration(mergedConfiguration);
            build.setPlugins(new ArrayList(plugins.values()));
            Utils.writePom(pom, pomPath);
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

    private CodeServerProcess startWindows(java.io.File codeServerWorkDir, SDKRunnerConfiguration runnerConfiguration)
            throws RunnerException {
        throw new UnsupportedOperationException();
    }

    static class CodeServerProcess {
        final int             port;
        final ExecutorService pidTaskExecutor;
        final File            startUpScriptFile;
        final File            workDir;
        int pid = -1;

        CodeServerProcess(int port, File startUpScriptFile, File workDir, ExecutorService pidTaskExecutor) {
            this.port = port;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            this.pidTaskExecutor = pidTaskExecutor;
        }

        void start() throws RunnerException {
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

                LOG.debug("Start GWT code server at port {}, working directory {}", port, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        void stop() {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet");
            }

            // Use ProcessUtil.kill(pid) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(pid);
            LOG.debug("Stop GWT code server at port {}, working directory {}", port, workDir);
        }

        void getLogs(Appendable output) {
            // get logs over HTTP request
        }
    }

}
