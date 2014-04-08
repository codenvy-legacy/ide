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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationLogger;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.NamedThreadFactory;
import com.codenvy.commons.lang.ZipUtils;
import com.google.common.io.CharStreams;
import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipFile;

/**
 * {@link ApplicationServer} implementation to deploy application to Apache Tomcat servlet container.
 *
 * @author Artem Zatsarynnyy
 * @author Eugene Voevodin
 */
@Singleton
public class TomcatServer implements ApplicationServer {
    public static final  String MEM_SIZE_PARAMETER = "runner.tomcat.memory";
    private static final Logger LOG                = LoggerFactory.getLogger(TomcatServer.class);
    private static final String SERVER_XML         =
            "<?xml version='1.0' encoding='utf-8'?>\n" +
            "<Server port=\"-1\">\n" +
            "  <Listener className=\"org.apache.catalina.core.AprLifecycleListener\" SSLEngine=\"on\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.JasperListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.JreMemoryLeakPreventionListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.mbeans.GlobalResourcesLifecycleListener\" />\n" +
            "  <Listener className=\"org.apache.catalina.core.ThreadLocalLeakPreventionListener\" />\n" +
            "  <Service name=\"Catalina\">\n" +
            "    <Connector port=\"${PORT}\" protocol=\"HTTP/1.1\"\n" +
            "               connectionTimeout=\"20000\" />\n" +
            "    <Engine name=\"Catalina\" defaultHost=\"localhost\">\n" +
            "      <Host name=\"localhost\"  appBase=\"webapps\"\n" +
            "            unpackWARs=\"true\" autoDeploy=\"true\">\n" +
            "      </Host>\n" +
            "    </Engine>\n" +
            "  </Service>\n" +
            "</Server>\n";

    private final ExecutorService            pidTaskExecutor;
    private final int                        memSize;
    private final ApplicationUpdaterRegistry applicationUpdaterRegistry;

    @Inject
    public TomcatServer(@Named(MEM_SIZE_PARAMETER) int memSize, ApplicationUpdaterRegistry applicationUpdaterRegistry) {
        this.memSize = memSize;
        this.applicationUpdaterRegistry = applicationUpdaterRegistry;
        pidTaskExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("TomcatServer-", true));
    }

    @Override
    public final String getName() {
        return "Tomcat";
    }

    @Override
    public ApplicationProcess deploy(final java.io.File workDir,
                                     ZipFile warToDeploy,
                                     final java.io.File extensionJar,
                                     final SDKRunnerConfiguration runnerConfiguration,
                                     CodeServer.CodeServerProcess codeServerProcess,
                                     StopCallback stopCallback) throws RunnerException {
        final Path tomcatPath;
        final Path webappsPath;
        final Path apiAppContextPath;
        try {
            tomcatPath = Files.createDirectory(workDir.toPath().resolve("tomcat"));
            ZipUtils.unzip(Utils.getTomcatBinaryDistribution().openStream(), tomcatPath.toFile());
            webappsPath = tomcatPath.resolve("webapps");
            ZipUtils.unzip(new java.io.File(warToDeploy.getName()), webappsPath.resolve("ide").toFile());
            generateServerXml(tomcatPath.toFile(), runnerConfiguration);

            // add JAR with extension to 'api' application's 'lib' directory
            apiAppContextPath = webappsPath.resolve("api");
            ZipUtils.unzip(new java.io.File(webappsPath.resolve("api.war").toString()), apiAppContextPath.toFile());
            IoUtil.copy(extensionJar, apiAppContextPath.resolve("WEB-INF/lib").resolve(extensionJar.getName()).toFile(), null);
        } catch (IOException e) {
            throw new RunnerException(e);
        }

        ApplicationProcess process;
        if (SystemInfo.isUnix()) {
            process = startUnix(workDir, runnerConfiguration, codeServerProcess, stopCallback);
        } else {
            process = startWindows(workDir, runnerConfiguration, codeServerProcess, stopCallback);
        }

        // TODO: unregister updater
        registerUpdater(process, new ApplicationUpdater() {
            @Override
            public void update() throws UpdateException {
                try {
                    final ProjectDescriptor projectDescriptor = runnerConfiguration.getRequest().getProjectDescriptor();
                    final java.io.File destinationDir = Files.createTempDirectory(workDir.toPath(), "sources-").toFile();
                    final java.io.File exportProject = Utils.exportProject(projectDescriptor, destinationDir);
                    final java.io.File sourcesDir = Files.createTempDirectory(workDir.toPath(), "sources-build-").toFile();
                    ZipUtils.unzip(exportProject, sourcesDir);
                    ZipFile artifact = Utils.buildProjectFromSources(sourcesDir.toPath(), extensionJar.getName());
                    // add JAR with extension to 'api' application's 'lib' directory
                    IoUtil.copy(new java.io.File(artifact.getName()),
                                apiAppContextPath.resolve("WEB-INF/lib").resolve(extensionJar.getName()).toFile(), null);
                    LOG.debug("Extension {} updated", workDir);
                } catch (Exception e) {
                    LOG.error("Unable to update extension: {}", workDir);
                    throw new UpdateException(e);
                }
            }
        });

        return process;
    }

    private void registerUpdater(ApplicationProcess process, ApplicationUpdater updater) {
        applicationUpdaterRegistry.registerUpdater(process, updater);
    }

    protected void generateServerXml(java.io.File tomcatDir, SDKRunnerConfiguration runnerConfiguration) throws IOException {
        final String cfg = SERVER_XML.replace("${PORT}", Integer.toString(runnerConfiguration.getHttpPort()));
        final java.io.File serverXmlFile = new java.io.File(new java.io.File(tomcatDir, "conf"), "server.xml");
        Files.write(serverXmlFile.toPath(), cfg.getBytes());
    }

    public int getMemSize() {
        return memSize;
    }

    @Override
    public String toString() {
        return "Tomcat Server";
    }

    // *nix

    protected ApplicationProcess startUnix(java.io.File appDir, SDKRunnerConfiguration runnerCfg,
                                           CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback)
            throws RunnerException {
        final java.io.File startUpScriptFile;
        final java.io.File logsDir = new java.io.File(appDir, "logs");
        try {
            startUpScriptFile = genStartUpScriptUnix(appDir, runnerCfg);
            updateSetenvFileUnix(appDir, runnerCfg);
            Files.createDirectory(logsDir.toPath());
        } catch (IOException e) {
            throw new RunnerException(e);
        }
        final List<java.io.File> logFiles = new ArrayList<>(2);
        logFiles.add(new java.io.File(logsDir, "stdout.log"));
        logFiles.add(new java.io.File(logsDir, "stderr.log"));
        return new TomcatProcess(runnerCfg.getHttpPort(), logFiles, runnerCfg.getDebugPort(),
                                 startUpScriptFile, appDir, codeServerProcess, stopCallback, pidTaskExecutor);
    }

    private java.io.File genStartUpScriptUnix(java.io.File appDir, SDKRunnerConfiguration runnerConfiguration) throws IOException {
        final String startupScript = "#!/bin/sh\n" +
                                     exportEnvVariablesUnix(runnerConfiguration) +
                                     "cd tomcat\n" +
                                     "chmod +x bin/*.sh\n" +
                                     catalinaUnix(runnerConfiguration) +
                                     "PID=$!\n" +
                                     "echo \"$PID\" >> ../run.pid\n" +
                                     "wait $PID";
        final java.io.File startUpScriptFile = new java.io.File(appDir, "startup.sh");
        Files.write(startUpScriptFile.toPath(), startupScript.getBytes());
        if (!startUpScriptFile.setExecutable(true, false)) {
            throw new IOException("Unable to update attributes of the startup script");
        }
        return startUpScriptFile;
    }

    private void updateSetenvFileUnix(java.io.File tomcatDir, SDKRunnerConfiguration runnerCfg) throws IOException {
        final Path setenvShPath = tomcatDir.toPath().resolve("tomcat/bin/setenv.sh");
        final String setenvShContent =
                new String(Files.readAllBytes(setenvShPath)).replace("${PORT}", Integer.toString(runnerCfg.getHttpPort()));
        Files.write(setenvShPath, setenvShContent.getBytes());
    }

    private String exportEnvVariablesUnix(SDKRunnerConfiguration runnerConfiguration) {
        int memory = runnerConfiguration.getMemory();
        if (memory <= 0) {
            memory = getMemSize();
        }
        final StringBuilder export = new StringBuilder();
        export.append(String.format("export CATALINA_OPTS=\"-Xms%dm -Xmx%dm\"%n", memory, memory));
        export.append(String.format("export SERVER_PORT=%d%n", runnerConfiguration.getHttpPort()));
        final int debugPort = runnerConfiguration.getDebugPort();
        if (debugPort > 0) {
            /*
            From catalina.sh:
            -agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
             */
            export.append(String.format("export JPDA_ADDRESS=%d%n", debugPort));
            export.append(String.format("export JPDA_TRANSPORT=%s%n", runnerConfiguration.getDebugTransport()));
            export.append(String.format("export JPDA_SUSPEND=%s%n", runnerConfiguration.isDebugSuspend() ? "y" : "n"));
        }
        return export.toString();
    }

    private String catalinaUnix(SDKRunnerConfiguration runnerConfiguration) {
        final boolean debug = runnerConfiguration.getDebugPort() > 0;
        if (debug) {
            return "./bin/catalina.sh jpda run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
        }
        return "./bin/catalina.sh run > ../logs/stdout.log 2> ../logs/stderr.log &\n";
    }

    // Windows

    protected ApplicationProcess startWindows(java.io.File appDir, SDKRunnerConfiguration runnerConfiguration,
                                              CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback) {
        throw new UnsupportedOperationException();
    }

    private static class TomcatProcess extends ApplicationProcess {
        final int                          httpPort;
        final List<java.io.File>           logFiles;
        final int                          debugPort;
        final ExecutorService              pidTaskExecutor;
        final java.io.File                 startUpScriptFile;
        final java.io.File                 workDir;
        final CodeServer.CodeServerProcess codeServerProcess;
        final StopCallback                 stopCallback;
        int pid = -1;
        TomcatLogger logger;
        Process      process;

        TomcatProcess(int httpPort, List<java.io.File> logFiles, int debugPort, java.io.File startUpScriptFile, java.io.File workDir,
                      CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback, ExecutorService pidTaskExecutor) {
            this.httpPort = httpPort;
            this.logFiles = logFiles;
            this.debugPort = debugPort;
            this.startUpScriptFile = startUpScriptFile;
            this.workDir = workDir;
            this.codeServerProcess = codeServerProcess;
            this.stopCallback = stopCallback;
            this.pidTaskExecutor = pidTaskExecutor;
        }

        @Override
        public void start() throws RunnerException {
            if (pid != -1) {
                throw new IllegalStateException("Process is already started");
            }

            try {
                process = Runtime.getRuntime().exec(new CommandLine(startUpScriptFile.getAbsolutePath()).toShellCommand(), null, workDir);
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

                try {
                    codeServerProcess.start();
                } catch (Exception ignore) {
                }

                logger = new TomcatLogger(logFiles, codeServerProcess);
                LOG.debug("Start Tomcat at port {}, application {}", httpPort, workDir);
            } catch (IOException | InterruptedException | TimeoutException e) {
                throw new RunnerException(e);
            } catch (ExecutionException e) {
                throw new RunnerException(e.getCause());
            }
        }

        @Override
        public void stop() throws RunnerException {
            if (pid == -1) {
                throw new IllegalStateException("Process is not started yet");
            }
            // Use ProcessUtil.kill(pid) because java.lang.Process.destroy() method doesn't
            // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
            ProcessUtil.kill(pid);

            try {
                codeServerProcess.stop();
            } catch (Exception ignore) {
            }

            stopCallback.stopped();
            LOG.debug("Stop Tomcat at port {}, application {}", httpPort, workDir);
        }

        @Override
        public int waitFor() throws RunnerException {
            synchronized (this) {
                if (pid == -1) {
                    throw new IllegalStateException("Process is not started yet");
                }
            }
            try {
                process.waitFor();
            } catch (InterruptedException ignored) {
            }
            return process.exitValue();
        }

        @Override
        public int exitCode() throws RunnerException {
            if (pid == -1 || ProcessUtil.isAlive(pid)) {
                return -1;
            }
            return process.exitValue();
        }

        @Override
        public boolean isRunning() throws RunnerException {
            return ProcessUtil.isAlive(pid);
        }

        @Override
        public ApplicationLogger getLogger() throws RunnerException {
            if (logger == null) {
                // is not started yet
                return ApplicationLogger.DUMMY;
            }
            return logger;
        }

        private static class TomcatLogger implements ApplicationLogger {
            final List<java.io.File>           logFiles;
            final CodeServer.CodeServerProcess codeServerProcess;

            TomcatLogger(List<java.io.File> logFiles, CodeServer.CodeServerProcess codeServerProcess) {
                this.logFiles = logFiles;
                this.codeServerProcess = codeServerProcess;
            }

            @Override
            public void getLogs(Appendable output) throws IOException {
                for (java.io.File logFile : logFiles) {
                    output.append(String.format("%n====> %1$s <====%n%n", logFile.getName()));
                    try (FileReader r = new FileReader(logFile)) {
                        CharStreams.copy(r, output);
                    }
                    output.append(System.lineSeparator());
                }
                try {
                    codeServerProcess.getLogs(output);
                } catch (Exception ignore) {
                }
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public void writeLine(String line) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void close() throws IOException {
            }
        }
    }
}